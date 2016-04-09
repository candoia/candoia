package boa.datagen.forges;

import java.io.File;
import java.util.ArrayList;

import boa.types.Code.CodeRepository;
import boa.types.Code.CodeRepository.RepositoryKind;
import boa.types.Toplevel.Project;
import boa.types.Toplevel.Project.ForgeKind;

public abstract class AbstractForge {

	public abstract boolean getJSON(String url, String jsonPath);

	public abstract boolean cloneRepo(String jsonPath, String repoPath);

	// public boolean cloneRepoFromURL(String URL, String repoPath);
	// public boolean cloneRepoFromJSON(String jsonPath, String repoPath);
	// public boolean buildProject(String url);
	public abstract Project toBoaProject(File jsonFile);

	public abstract String getDirName(String URL);

	public static Project buildLocalProject(String path) {
		if (path.contains("@")) {
			path = path.substring(path.lastIndexOf('@') + 1);
		}
		Project.Builder project = Project.newBuilder();
		project.setKind(ForgeKind.OTHER);
		project.setId(path);
		project.setName(path.substring(path.lastIndexOf('/')));
		project.setCreatedDate(-1);
		project.setProjectUrl(path);
		project.setHomepageUrl("no homepage");
		project.setDescription("no description");
		project.addAllProgrammingLanguages(new ArrayList<String>());
		CodeRepository.Builder cr = CodeRepository.newBuilder();
		cr.setUrl(path);
		cr.setKind(RepositoryKind.UNKNOWN);
		project.addCodeRepositories(cr.build());
		return project.build();
	}
}
