package boa.datagen.candoia;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.io.FileDeleteStrategy;
import boa.datagen.scm.AbstractConnector;
import boa.types.Code;
import boa.types.Toplevel;
import net.sf.json.JSONObject;
import org.json.*;

/**
 * Created by nmtiwari on 7/15/16.
 */
public class CandoiaUtilities {

	public static long getLatestRevisionId(Toplevel.Project proj) {
		Code.Revision latest = null;
		Date latest_date = new Date();
		for (Code.CodeRepository cr : proj.getCodeRepositoriesList()) {
			for (Code.Revision rev : cr.getRevisionsList()) {
				if (rev.hasCommitDate()) {
					Date this_commit = new Date(rev.getCommitDate());
					if (latest_date == null) {
						latest_date = new Date(rev.getCommitDate());
					} else if (latest_date.before(this_commit)) {
						latest_date = this_commit;
					}
				}
			}
		}
		return latest_date.getTime();
	}

	public static JSONObject buildJSONObject(String projName, String projURL, long revId) {
		JSONObject json = new JSONObject();
		json.put("name", projName);
		json.put("url", projURL);
		json.put("latest_seen_id", revId);
		return json;
	}

	public static boolean prevDataExists(String path) {
		int countForAllThreeFiles = 0;
		File directory = new File(path);
		if (directory.exists()) {
			for (String fileName : directory.list()) {
				if ((fileName.equals("data")) || fileName.equals("index") || fileName.equals("projects.seq")) {
					countForAllThreeFiles++;
				}
			}
			return (countForAllThreeFiles == 3);
		} else {
			return false;
		}
	}

	private static List<org.json.JSONObject> getExistingProjs(File cacheFile) throws IOException {
		ArrayList<org.json.JSONObject> projs = new ArrayList<org.json.JSONObject>(); 
		List<String> cached = Files.readAllLines(Paths.get(cacheFile.getAbsolutePath()));
		for(String line: cached){
			org.json.JSONObject json = new org.json.JSONObject(line);
			projs.add(json);
		}
		return projs;
	}

	private static boolean isMostAlreadyPresentAndLatest(AbstractConnector conn, String path){
		File cache = new File(path + "/" + CandoiaConfiguration.getCachefilename());
		return true;
	}
	
	private static ArrayList<String> filterToBeCloned(List<String> input, List<String> available){
		ArrayList<String> result = new ArrayList<String>();
		for(String str: input){
			String[] temp = str.split("/");
			String usrRepo = temp[temp.length-2] + "/" + temp[temp.length-1];
			if(!available.contains(usrRepo)){
				result.add(str);
			}
		}
		return result;
	}
	
	public static ArrayList<String> getToBeCloned(String path, List<String> proj) {
		ArrayList<String> available = new ArrayList<>();
		File directory = new File(path);
		if (directory.exists()) {
			for (File usr : directory.listFiles()) {
				if(usr.isDirectory()){
					for(File repo: usr.listFiles()){
						if(CandoiaConfiguration.isProperVCSDir(repo.getAbsolutePath())){
							AbstractConnector conn = CandoiaConfiguration.getVCS(repo.getAbsolutePath());
							if(isMostAlreadyPresentAndLatest(conn, path)){
								available.add(usr.getName()+ "/" + repo.getName());
							}
						}
					}
				}
			}
			return filterToBeCloned(proj, available);
		}else{
			return (ArrayList<String>) proj;
		}
	}

	public static void cleanOlderDataset(String path){
		File dir = new File(path);
		for(File sub: dir.listFiles()){
			if("data".equals(sub.getName()) || "projects".equals(sub.getName()) || "index".equals(sub.getName())){
				try{
					FileDeleteStrategy.FORCE.delete(sub);
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}
	}
}
