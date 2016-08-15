/*
 * Copyright 2015, Hridesh Rajan, Robert Dyer, Hoan Nguyen
 *                 and Iowa State University of Science and Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package boa.datagen;

import boa.datagen.bugForge.BugForge;
import boa.datagen.candoia.CandoiaConfiguration;
import boa.datagen.scm.AbstractConnector;
import boa.datagen.scm.GitConnector;
import boa.datagen.util.FileIO;
import boa.datagen.util.Properties;
import boa.types.Code.CodeRepository;
import boa.types.Code.Revision;
import boa.types.Issues.IssueRepository;
import boa.types.Toplevel.Project;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author hoan
 * @author rdyer
 * @author hridesh
 * 
 */
public class SeqRepoImporter {
	private final static boolean debug = Properties.getBoolean("debug", boa.datagen.DefaultProperties.DEBUG);

	private final static String keyDelim = Properties.getProperty("hbase.delimiter",
			boa.datagen.DefaultProperties.HBASE_DELIMITER);

	private final static File jsonCacheDir = new File(
			Properties.getProperty("gh.json.cache.path", boa.datagen.DefaultProperties.GH_JSON_CACHE_PATH));
	private final static File gitRootPath = new File(
			Properties.getProperty("gh.svn.path", boa.datagen.DefaultProperties.GH_GIT_PATH));

	private static final HashMap<String, String[]> repoInfo = new HashMap<String, String[]>();

	private final static ArrayList<byte[]> cacheOfProjects = new ArrayList<byte[]>();
	private final static HashSet<String> processedProjectIds = new HashSet<String>();
	private final static int poolSize = Integer
			.parseInt(Properties.getProperty("num.threads", boa.datagen.DefaultProperties.NUM_THREADS));
	private final static AtomicInteger numOfProcessedProjects = new AtomicInteger(0), listId = new AtomicInteger(0);
	private final static int maxListId = 16;
	private static Configuration conf = null;
	private static FileSystem fileSystem = null;
	private static String base = null;

	// This methods work for data generation from local systems
	public static void importFromLocalSystem() throws IOException, InterruptedException {
		conf = new Configuration();
		fileSystem = FileSystem.get(conf);
		base = Properties.getProperty("gh.json.cache.path", DefaultProperties.GH_JSON_CACHE_PATH);
		for (File file : jsonCacheDir.listFiles()) {
			if (file.getName().endsWith("buf-map")) {
				@SuppressWarnings("unchecked")
				HashMap<String, byte[]> repos = (HashMap<String, byte[]>) FileIO
						.readObjectFromFile(file.getAbsolutePath());
				ArrayList<Thread> workers = new ArrayList<>();
				for (String key : repos.keySet()) {
					ImportTaskLocal t = new ImportTaskLocal(repos.get(key));
					workers.add(t);
					t.start();
				}
				for (Thread t : workers) {
					t.join();
				}
			}
		}
	}

	private static void getProcessedProjects() throws IOException {
		FileStatus[] files = fileSystem.listStatus(new Path(base));
		String hostname = InetAddress.getLocalHost().getHostName();
		for (int i = 0; i < files.length; i++) {
			FileStatus file = files[i];
			String prefix = "projects-" + hostname + "-";
			String name = file.getPath().getName();
			int index1 = name.indexOf(prefix);
			if (index1 > -1) {
				try {
					SequenceFile.Reader r = new SequenceFile.Reader(fileSystem, file.getPath(), conf);
					final Text key = new Text();
					while (r.next(key)) {
						processedProjectIds.add(key.toString());
					}
					r.close();
				} catch (EOFException e) {
					printError(e, "EOF Exception in " + file.getPath().getName());
					fileSystem.delete(file.getPath(), false);
				}
			}
		}
		System.out.println("Got processed projects: " + processedProjectIds.size());
	}

	private static void buildCacheOfProjects(int listId) {
		cacheOfProjects.clear();
		for (File file : jsonCacheDir.listFiles()) {
			if (file.getName().endsWith("-" + listId + "-buf-map")) {
				@SuppressWarnings("unchecked")
				HashMap<String, byte[]> repos = (HashMap<String, byte[]>) FileIO
						.readObjectFromFile(file.getAbsolutePath());
				for (String key : repos.keySet()) {
					byte[] bs = repos.get(key);
					if (poolSize > 1)
						cacheOfProjects.add(bs);
					else {
						try {
							Project p = Project.parseFrom(bs);
							if (processedProjectIds.contains(p.getId()))
								continue;
							String name = p.getName();
							String[] info = repoInfo.get(name);
							if (info != null && exists(name, info[1]) != null)
								cacheOfProjects.add(bs);
						} catch (InvalidProtocolBufferException e) {
							e.printStackTrace();
						}
					}
				}
				repos.clear();
			}
		}
		System.out.println("Got cached projects: " + cacheOfProjects.size());
	}


	private static File exists(String name, String listId) {
		for (int i = 2; i <= 4; i++) {
			File dir = new File("/hadoop" + i + "/" + gitRootPath + "/" + listId + "/" + name);
			if (dir.exists())
				return dir;
		}
		return null;
	}

	@SuppressWarnings("unused")
	private static void print(String id, Project p) {
		System.out.print(id);
		System.out.print(" " + p.getId());
		System.out.print(" " + p.getName());
		System.out.print(" " + p.getHomepageUrl());
		if (p.getProgrammingLanguagesCount() > 0) {
			System.out.print(" Programming languages:" + p.getProgrammingLanguagesCount());
			for (int i = 0; i < p.getProgrammingLanguagesCount(); i++)
				System.out.print(" " + p.getProgrammingLanguages(i));
		}
		System.out.println();
	}

	private static void printError(final Throwable e, final String message) {
		e.printStackTrace();
	}

	public static class ImportTaskLocal extends Thread {
		private static final int MAX_COUNTER = 10000;
		byte[] bs = null;
		SequenceFile.Writer projectWriter, astWriter;
		private int id;

		public ImportTaskLocal(byte[] bs) throws IOException {
			this.bs = bs;
			this.id = (int) Thread.currentThread().getId();
		}

		public void openWriters(String name) {
			long time = System.currentTimeMillis() / 1000;
			String hostname = "" + time;
			for (int i = 0; i < 3; i++) {
				try {
					hostname = InetAddress.getLocalHost().getHostName();
					break;
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
			}
			String suffix = ".seq";
			while (true) {
				try {
					projectWriter = SequenceFile.createWriter(fileSystem, conf,
							new Path(base + "/" + name + "projects" + suffix), Text.class, BytesWritable.class);
					astWriter = SequenceFile.createWriter(fileSystem, conf,
							new Path(base + "/" + name + "ast" + suffix), Text.class, BytesWritable.class);
					break;
				} catch (Throwable t) {
					t.printStackTrace();
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
				}
			}
		}

		public void closeWriters() {
			while (true) {
				try {
					projectWriter.close();
					astWriter.close();
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
					break;
				} catch (Throwable t) {
					t.printStackTrace();
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
				}
			}
		}

		@Override
		public void run() {
			openWriters(Thread.currentThread().getName() + this.id);
			try {
				Project cachedProject = null;
				try {
					cachedProject = Project.parseFrom(bs);
				} catch (InvalidProtocolBufferException e) {
					e.printStackTrace();
				}
				final String dirName = cachedProject.getName();
				String repoPath = DefaultProperties.GH_GIT_PATH + "/" + dirName;
				File repo = new File(repoPath);
				/*
				 * Following code is a hack in to check all the sub repository to be proper vcs.
				 * This is done to handle source forge clones
				 */
				if (repo.isDirectory() && repo.listFiles().length == 1
						&& !CandoiaConfiguration.isProperVCSDir(repo.getAbsolutePath())) {
					repoPath = repo.listFiles()[0].getAbsolutePath();
				}
				Project project = null;
				if (new File(repoPath).isDirectory()) {
					project = storeRepositoryFrom(cachedProject, 0, new File(repoPath));

				} else {
					project = storeRepositoryFrom(cachedProject, 0, new File(cachedProject.getProjectUrl()));
				}

				for(IssueRepository ir: project.getIssueRepositoriesList()){
					if(ir.getUrl().trim().length() > 0)
					  project = storeIsseuesFrom(project, ir.getUrl());
				}

				try {
					projectWriter.append(new Text(project.getId()), new BytesWritable(project.toByteArray()));
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
			closeWriters();
		}

		private Project storeRepositoryFrom(final Project project, final int i, File repoDir) {
			final CodeRepository repo = project.getCodeRepositories(i);
			final Project.Builder projBuilder = Project.newBuilder(project);

			final String name = project.getName();
			final File gitDir = repoDir;

			final AbstractConnector conn = CandoiaConfiguration.getVCS(gitDir.getAbsolutePath());
			try {
				final CodeRepository.Builder repoBuilder = CodeRepository.newBuilder(repo);
				final String repoKey = "g:" + project.getId() + keyDelim + repo.getKind().getNumber();
				for (final Revision rev : conn.getCommits(true, astWriter, repoKey, keyDelim)) {
					final Revision.Builder revBuilder = Revision.newBuilder(rev);
					repoBuilder.addRevisions(revBuilder);
				}

				projBuilder.setCodeRepositories(i, repoBuilder);
				return projBuilder.build();
			} catch (final Exception e) {
				e.printStackTrace();
			} finally {
				conn.clear();
				try {
					if (conn != null)
						conn.close();
				} catch (Exception e) {
					printError(e, "error closing GitConnector");
					e.printStackTrace();
				}
			}

			return project;
		}

		private Project storeRepository(final Project project, final int i) {
			final CodeRepository repo = project.getCodeRepositories(i);
			final Project.Builder projBuilder = Project.newBuilder(project);

			final String name = project.getName();
			final File gitDir = exists(name, repoInfo.get(name)[1]);

			if (debug)
				System.out.println("Has repository: " + name);

			// final AbstractConnector conn = new
			// GitConnector(gitDir.getAbsolutePath());
			final AbstractConnector conn = CandoiaConfiguration.getVCS(gitDir.getAbsolutePath());
			try {
				final CodeRepository.Builder repoBuilder = CodeRepository.newBuilder(repo);
				final String repoKey = "g:" + project.getId() + keyDelim + repo.getKind().getNumber();
				for (final Revision rev : conn.getCommits(true, astWriter, repoKey, keyDelim)) {
					final Revision.Builder revBuilder = Revision.newBuilder(rev);
					repoBuilder.addRevisions(revBuilder);
				}
				projBuilder.setCodeRepositories(i, repoBuilder);
				return projBuilder.build();
			} catch (final Exception e) {
				printError(e, "unknown error");
			} finally {
				try {
					if (conn != null) {
						conn.close();
					}
				} catch (Exception e) {
					printError(e, "error closing GitConnector");
				}
			}

			return project;
		}

		private Project storeIsseuesFrom(final Project project, String URL) {
			final Project.Builder projBuilder = Project.newBuilder(project);
			final IssueRepository IRepo = project.getIssueRepositories(0);
			final IssueRepository.Builder iRepoBuilder = IssueRepository.newBuilder(IRepo);
			BugForge bugForge = CandoiaConfiguration.getBugForge(URL);
			bugForge.buildIssue(projBuilder, URL);
			return projBuilder.build();
		}
	}

}
