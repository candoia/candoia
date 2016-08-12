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

package boa.datagen.scm;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

/**
 * @author rdyer
 * @author josephb
 */
public class GitConnector extends AbstractConnector {
	private static final boolean debug = boa.datagen.util.Properties.getBoolean("debug",
			boa.datagen.DefaultProperties.DEBUG);

	private String path;

	private Repository repository;
	private Git git;
	private RevWalk revwalk;

	private String lastCommitId = null;

	public GitConnector() {

	}

	public GitConnector(final String path) {
		try {
			this.path = path;
			this.repository = new FileRepositoryBuilder().setGitDir(new File(path + "/.git")).setMustExist(true).build();
			this.git = new Git(this.repository);
			this.revwalk = new RevWalk(this.repository);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public boolean initialize(final String path){
		try {
			this.path = path;
			this.repository = new FileRepositoryBuilder().setGitDir(new File(path + "/.git")).setMustExist(true).build();
			this.git = new Git(this.repository);
			this.revwalk = new RevWalk(this.repository);
		} catch (final IOException e) {
			//e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean clear(){
//			this.path = null;
//			this.repository = null;
//			this.git = null;
//			this.revwalk = null;
			this.revwalk.reset();
		return true;
	}
	
	@Override
	public void close() {
		repository.close();
	}

	@Override
	public String getLastCommitId() {
		if (lastCommitId == null) {
			revwalk.reset();
			try {
				revwalk.markStart(revwalk.parseCommit(repository.resolve(Constants.HEAD)));
				revwalk.sort(RevSort.COMMIT_TIME_DESC);
				lastCommitId = revwalk.next().getId().toString();
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
		return lastCommitId;
	}

	@Override
	public void setLastSeenCommitId(final String id) {
	}

	@Override
	protected void setRevisions() {
		try {
			revwalk.reset();
			revwalk.markStart(revwalk.parseCommit(repository.resolve(Constants.HEAD)));
			revwalk.sort(RevSort.TOPO, true);
			revwalk.sort(RevSort.COMMIT_TIME_DESC, true);
			revwalk.sort(RevSort.REVERSE, true);
			revisions.clear();
			revisionMap = new HashMap<String, Integer>();

			for (final RevCommit rc : revwalk) {
				final GitCommit gc = new GitCommit(repository, this);
				gc.setId(rc.getName());
				gc.setAuthor(rc.getAuthorIdent().getName());
				gc.setCommitter(rc.getCommitterIdent().getName());
				gc.setDate(new Date(((long) rc.getCommitTime()) * 1000));
				gc.setMessage(rc.getFullMessage());
				gc.getChangeFiles(this.revisionMap, rc);
				revisionMap.put(gc.id, revisions.size());
				revisions.add(gc);
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void getTags(final List<String> names, final List<String> commits) {
		try {
			for (final Ref ref : git.tagList().call()) {
				names.add(ref.getName());
				commits.add(ref.getObjectId().getName());
			}
		} catch (final GitAPIException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void getBranches(final List<String> names, final List<String> commits) {
		try {
			for (final Ref ref : git.branchList().call()) {
				names.add(ref.getName());
				commits.add(ref.getObjectId().getName());
			}
		} catch (final GitAPIException e) {
			e.printStackTrace();
		}
	}

	@Override
	public AbstractConnector getNewInstance() {
		return new GitConnector();
	}
}
