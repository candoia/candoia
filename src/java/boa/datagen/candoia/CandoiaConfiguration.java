package boa.datagen.candoia;

import boa.datagen.bugForge.BugForge;
import boa.datagen.bugForge.gitIssue.GithubIssues;
import boa.datagen.bugForge.sfIssues.SFTickets;
import boa.datagen.forges.AbstractForge;
import boa.datagen.forges.github.ForgeGithub;
import boa.datagen.forges.sf.ForgeSF;
import boa.datagen.scm.AbstractConnector;
import boa.datagen.scm.GitConnector;

import java.util.ArrayList;
import java.util.HashMap;

public class CandoiaConfiguration {
	public static final boolean DEBUG = false;
	private final static HashMap<String, AbstractForge> forges;
	private final static HashMap<String, BugForge> bugforges;
	private final static ArrayList<AbstractConnector> vcs;
	private final static String githubAccessToken = "3a46b401267a5efa9dab8b9371174f23a08d5181";
	private final static String cacheFileName = ".candoiaCache.txt";
	
	static {
		bugforges = new HashMap<String, BugForge>();
		bugforges.put("github.com", new GithubIssues());
		bugforges.put("sourceforge.net", new SFTickets());
	}
	
	static {
		forges = new HashMap<String, AbstractForge>();
		forges.put("github.com", new ForgeGithub());
		forges.put("sourceforge.net", new ForgeSF());
	}

	static {
		vcs = new ArrayList<AbstractConnector>();
		vcs.add(new GitConnector());
	}
	
	public static String getGithubAccessToken(){
		return githubAccessToken;
	}

	public static AbstractForge getForge(String url) {
		for (String str : forges.keySet()) {
			if (url.contains(str)) {
				return forges.get(str);
			}
		}
		return null;
	}

	public static BugForge getBugForge(String url) {
		for (String str : bugforges.keySet()) {
			if (url.contains(str)) {
				return bugforges.get(str);
			}
		}
		return null;
	}
	
	public static ArrayList<AbstractConnector> getVCS() {
		return vcs;
	}
	
	public static ArrayList<String> getSupportedForges(){
		ArrayList<String>listOfForge = new ArrayList<>();
		for(Object obj: forges.keySet()){
			listOfForge.add(obj.toString());
		}
		return listOfForge;
	}
	
	public static AbstractConnector getVCS(String path) {
		ArrayList<AbstractConnector> supportedVCS = CandoiaConfiguration.getVCS();
		for (AbstractConnector conn : supportedVCS) {
			AbstractConnector copy= conn.getNewInstance();
			if (copy.initialize(path)) {
				return copy;
			}
		}
		System.err.println(
				"Given version control system is not supported by Candoia. Please consider extending Candoia platform.");
		return null;
	}

	public static boolean isProperVCSDir(String path){
		ArrayList<AbstractConnector> supportedVCS = CandoiaConfiguration.getVCS();
		for (AbstractConnector conn : supportedVCS) {
			AbstractConnector copy= conn.getNewInstance();
			if (copy.initialize(path)) {
				return true;
			}
		}
		System.err.println(
				"Given version control system is not supported by Candoia. Please consider extending Candoia platform.");
		return false;
	}
	
	public static String getCachefilename() {
		return cacheFileName;
	}

}
