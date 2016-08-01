package boa.datagen.forges.sf;

import boa.datagen.forges.AbstractForge;
import boa.datagen.util.FileIO;
import boa.types.Toplevel.Project;
import org.apache.commons.io.FileDeleteStrategy;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.tmatesoft.svn.core.SVNException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

public class SVNForge extends AbstractForge {

	@Override
	public boolean getJSON(String url, String jsonPath) {
		return false;
	}

	@Override
	public boolean cloneRepo(String URL, String repoPath) {
		URL = URL.substring(URL.indexOf('@') + 1);
		try {
			File f = new File(repoPath);
			if (!f.delete())
				FileDeleteStrategy.FORCE.delete(f);
			SVNRepositoryCloner.clone(createURL(URL), repoPath);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public Project toBoaProject(File jsonFile) {
		if (jsonFile.getName().endsWith(".json")) {
			RepoMetadata repo = new RepoMetadata(jsonFile);
			Project protobufRepo = repo.toBoaMetaDataProtobuf();
			return protobufRepo;
		}
		return null;
	}

	@Override
	public String getDirName(String URL) {
		String[] details = URL.split("/");
		return details[details.length - 1];
	}

	@Override
	public String getUsrName(String URL) {
		String[] details = URL.split("/");
		return details[details.length - 2];
	}

	private String createURL(String raw) {
		String processed = "svn.code.sf.net::";
		String[] details = raw.split("/");
		int length = details.length;
		processed = processed + details[length - 3] + "/" + details[length - 2] + "/" + details[length - 1];
		return processed;
	}
}
