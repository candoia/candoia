/*
 * Copyright 2015, Hridesh Rajan, Robert Dyer,
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

import boa.datagen.candoia.CandoiaConfiguration;
import boa.datagen.candoia.CandoiaUtilities;
import boa.datagen.forges.AbstractForge;
import boa.datagen.util.FileIO;
import boa.datagen.util.Properties;
import boa.types.Issues;
import boa.types.Toplevel.Project;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * The main entry point for Boa tools for generating datasets.
 *
 * @author hridesh
 *
 */
public class BoaGenerator {
	private int maxThread = Integer
			.parseInt(Properties.getProperty("num.threads", boa.datagen.DefaultProperties.NUM_THREADS));
	ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(maxThread);

	public BoaGenerator() {
		executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(maxThread);
	}

	public static void main(final String[] args) throws IOException {
		final Options options = new Options();
		BoaGenerator generator = new BoaGenerator();
		generator.addOptions(options);

		final CommandLine cl;
		try {
			cl = new PosixParser().parse(options, args);
		} catch (final org.apache.commons.cli.ParseException e) {
			System.err.println(e.getMessage());
			new HelpFormatter().printHelp("BoaCompiler", options);
			return;
		}
		if (generator.handleCmdOptions(cl, options, args)) {
			if (cl.hasOption("help")) {
				return;
			} else { // remote repository analysis
				String[] local = new String[0];
				String[] clone = new String[0];
				String[] bugs = new String[0];
				;
				if (cl.hasOption("clone")) {
					generator.getProjectFromRemote(cl.getOptionValue("clone").split(","));
				} else if (cl.hasOption("repo")) {
					local = cl.getOptionValue("repo").split(",");
				}
				generator.buildProject(clone, local, bugs);
			}
		} else {
			System.err.println(
					"Given command is not supported by Boa/Candoia. Please see the list of available commands.");
			new HelpFormatter().printHelp("BoaCompiler", options);
		}
	}

	public void generate(String[] clone, String[] local, String[] bugs) {
		ArrayList<String> actualCloning = CandoiaUtilities.getToBeCloned(
				DefaultProperties.GH_JSON_CACHE_PATH + "/" + DefaultProperties.CLONE_DIR_NAME,
				new ArrayList<String>(Arrays.asList(clone)));
		getProjectFromRemote(actualCloning.toArray(new String[0]));
		buildProject(clone, local, bugs);
	}

	private ArrayList<String> getNonForgeClonedPaths(String[] clone) {
		ArrayList<String> paths = new ArrayList<>();
		for (String str : clone) {
			if (!str.contains("github.com") && !str.contains("sourceforge.net")) {
				final AbstractForge forge = CandoiaConfiguration.getForge(str);
				String repoName = forge.getDirName(str);
				String userName = forge.getUsrName(str);
				String repo = userName + "/" + repoName;
				repo = DefaultProperties.GH_GIT_PATH + "/" + repo;
				paths.add(repo);
			}
		}
		return paths;
	}

	private static final void printHelp(Options options, String message) {
		String header = "The most commonly used Boa options are:";
		String footer = "\nPlease report issues at http://www.github.com/boalang/";
		System.err.println(message);
		new HelpFormatter().printHelp("Boa", header, options, footer);
	}

	private void addOptions(Options options) {
		options.addOption("json", "metadata", true, ".json files for metadata");
		options.addOption("repo", "repository", true, "cloned repo path");
		options.addOption("output", "output dir", true, "directory where output is desired");
		options.addOption("clone", "clone from remote", true, "your username and url to create the dataset");
		options.addOption("cache", "delete caching", false, "enable if you want to delete the cloned code for user.");
		options.addOption("help", "help", true, "help");
	}

	private boolean handleCmdOptions(CommandLine cl, Options options, final String[] args) {
		if (cl.hasOption("json") && cl.hasOption("repo") && cl.hasOption("output")) {
			DefaultProperties.GH_JSON_PATH = cl.getOptionValue("json");
			DefaultProperties.GH_JSON_CACHE_PATH = cl.getOptionValue("output");
			DefaultProperties.GH_GIT_PATH = cl.getOptionValue("repo");
			return true;
		} else if (cl.hasOption("json") && cl.hasOption("output")) {
			DefaultProperties.GH_JSON_PATH = cl.getOptionValue("json");
			DefaultProperties.GH_JSON_CACHE_PATH = cl.getOptionValue("output");
			DefaultProperties.GH_GIT_PATH = cl.getOptionValue("output");
			return true;
		} else if (cl.hasOption("repo") && cl.hasOption("output")) {
			DefaultProperties.GH_JSON_PATH = cl.getOptionValue("repo");
			DefaultProperties.GH_JSON_CACHE_PATH = cl.getOptionValue("output");
			return true;
		} else if (cl.hasOption("clone") && cl.hasOption("output")) {
			try {
				String GH_JSON_PATH = new java.io.File(".").getCanonicalPath();
				DefaultProperties.GH_JSON_PATH = GH_JSON_PATH + "/__json";
				DefaultProperties.GH_JSON_CACHE_PATH = cl.getOptionValue("output");
				DefaultProperties.GH_GIT_PATH = DefaultProperties.GH_JSON_CACHE_PATH + "/__clonedByBoa";
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		} else if (cl.hasOption("help")) {
			printHelp(options, cl.getOptionValue("output"));
			return true;
		} else {
			System.err.println("User must specify the path of the repository. Please see --remote and --local options");
			return false;
		}

	}

	private boolean getProjectFromRemote(String[] url) {
		for (final String str : url) {
			final AbstractForge forge = CandoiaConfiguration.getForge(str);
			executor.execute(new Runnable() {
				@Override
				public void run() {
					String repoName = forge.getDirName(str);
					String userName = forge.getUsrName(str);
					String repo = userName + "/" + repoName;
					if (!new File(DefaultProperties.GH_JSON_PATH + "/" + repo).exists()){
						forge.getJSON(str, DefaultProperties.GH_JSON_PATH + "/" + repo);
					}
					if (!new File(DefaultProperties.GH_GIT_PATH + "/" + repo).exists()){
						forge.cloneRepo(str, DefaultProperties.GH_GIT_PATH + "/" + repo);
					}

				}
			});
		}
		executor.shutdown();
		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private void buildProject(String[] clone, String[] localRepos, String[] bugs) {
		final HashMap<String, byte[]> repos = new HashMap<>();
		File jsonFiles = new File(DefaultProperties.GH_JSON_PATH);
		ArrayList<String> listOfForges = CandoiaConfiguration.getSupportedForges();

		this.executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(maxThread);
		/*
		 * Building all remote repository as project
		 */

		if(jsonFiles.isDirectory()){
			for(int i =0; i< clone.length; i++){
				String path = convertCloneRepoPath(clone[i]);
				this.executor.execute(new ProjectbuildTask(repos, new File(path), listOfForges, bugs[i]));
			}
		}

		executor.shutdown();
		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}


		/*
		 * Building all local repository as project
		 */
		for(int i =0; i < localRepos.length; i++){
			String local = localRepos[i];
			Project pr = AbstractForge.buildLocalProject(local);
			Project.Builder pb = pr.toBuilder();
			String bugUrl = bugs[bugs.length-localRepos.length-i];
			Issues.IssueRepository.Builder ir = Issues.IssueRepository.newBuilder();
			ir.setUrl(bugUrl);
			ir.setKind(Issues.IssueRepository.IssueRepositoryKind.UNKNOWN);
			pb.addIssueRepositories(ir.build());
			pr = pb.build();
			synchronized (repos) {
				repos.put(pr.getId(), pr.toByteArray());
			}
		}

		File output = new File(DefaultProperties.GH_JSON_CACHE_PATH);
		output.mkdirs();
		FileIO.writeObjectToFile(repos, DefaultProperties.GH_JSON_CACHE_PATH + "/buf-map", false);

		try {
			SeqRepoImporter.importFromLocalSystem();
			SeqProjectCombiner.combine();
			MapFileGenerator.generateMap(DefaultProperties.GH_JSON_CACHE_PATH);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static AbstractForge getCorrectForgeHandler(File jsonFile, ArrayList<String> listOfForges) {
		try {
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(jsonFile));
			byte[] bytes = new byte[(int) jsonFile.length()];
			in.read(bytes);
			in.close();
			String jsonText = new String(bytes);
			ArrayList<Integer> occuranceOfForgeName = new ArrayList<>();
			for (String str : listOfForges) {
				occuranceOfForgeName.add(countOccuranceOf(jsonText, str));
			}
			int maxCommomNameCount = 0;
			int forgeNumber = -1;
			for (int i = 0; i < occuranceOfForgeName.size(); i++) {
				if (maxCommomNameCount < occuranceOfForgeName.get(i)) {
					maxCommomNameCount = occuranceOfForgeName.get(i);
					forgeNumber = i;
				}
			}
			return CandoiaConfiguration.getForge(listOfForges.get(forgeNumber));
		} catch (Exception e) {
			System.err.println("Error reading file " + jsonFile.getAbsolutePath());
			return null;
		}

	}

	private static int countOccuranceOf(String str, String findStr) {
		int lastIndex = 0;
		int count = 0;
		while (lastIndex != -1) {

			lastIndex = str.indexOf(findStr, lastIndex);

			if (lastIndex != -1) {
				count++;
				lastIndex += findStr.length();
			}
		}
		return count;
	}

	public static class ProjectbuildTask implements Runnable {
		final private HashMap<String, byte[]> repos;
		private File projectJson;
		private ArrayList<String> listOfForges;
		private String bugUrl;

		public ProjectbuildTask(HashMap<String, byte[]> repos, File projectJson, ArrayList<String> listOfForges, String bugUrl) {
			this.repos = repos;
			this.projectJson = projectJson;
			this.listOfForges = listOfForges;
			this.bugUrl = bugUrl;
		}

		public void run() {
			if (projectJson.isDirectory()) {
				File dir = new File(projectJson.getAbsolutePath() + "/repos");
				for (File file : dir.listFiles()) {
					if (file.getName().endsWith(".json")) {
						AbstractForge forgeHandler = getCorrectForgeHandler(file, listOfForges);
						if (forgeHandler != null) {
							Project project = forgeHandler.toBoaProject(file);
							Project.Builder pb = project.toBuilder();
							Issues.IssueRepository.Builder ir = Issues.IssueRepository.newBuilder();
							ir.setUrl(bugUrl);
							ir.setKind(Issues.IssueRepository.IssueRepositoryKind.UNKNOWN);
							pb.addIssueRepositories(ir.build());
							project = pb.build();
							synchronized (repos) {
								repos.put(project.getId(), project.toByteArray());
							}
						} else {
							System.err.println("Json files corresponds to unsupported forge kind");
						}
					}
				}
			}
		}
	}

	private String convertCloneRepoPath(String cloneUrl){
		String[] details = cloneUrl.split("/");
		return DefaultProperties.GH_JSON_PATH +"/" + details[details.length-2] + "/" + details[details.length-1];
	}
}
