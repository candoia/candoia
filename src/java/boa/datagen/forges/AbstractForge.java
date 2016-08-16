package boa.datagen.forges;

import java.io.Console;
import java.io.File;
import java.util.ArrayList;

import boa.types.Code.CodeRepository;
import boa.types.Code.CodeRepository.RepositoryKind;
import boa.types.Toplevel.Project;
import boa.types.Toplevel.Project.ForgeKind;

public abstract class AbstractForge {

    private static char[] pwd;
    private static String usrName;
    private static class Lock{
    }

	public abstract boolean getJSON(String url, String jsonPath);

	public abstract boolean cloneRepo(String jsonPath, String repoPath);

	public abstract Project toBoaProject(File jsonFile);

	public abstract String getDirName(String URL);
	
	public abstract String getUsrName(String URL);

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

    public static char[] readPassword(){
        synchronized (Lock.class){
            if(pwd != null){
                return pwd;
            }else{
                Console cnsl = null;
                char[] password = null;
                try {
                    cnsl = System.console();
                    if (cnsl != null) {
                        password = cnsl.readPassword("Password: ");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                pwd = password;
                return password;
            }
        }
    }
}
