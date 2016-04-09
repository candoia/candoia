package boa.datagen.forges.sf;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

import org.apache.commons.io.FileDeleteStrategy;
import org.eclipse.jgit.api.errors.GitAPIException;

import boa.datagen.forges.AbstractForge;
import boa.datagen.forges.sf.RepoMetadata;
import boa.datagen.util.FileIO;
import boa.types.Toplevel.Project;

public class ForgeSF extends AbstractForge{

	@Override
	public boolean getJSON(String url, String jsonPath) {
		MetadataCacher mc = initialize(url, jsonPath);
		if (mc != null) {
			if (downloadJSON(mc, jsonPath)) {
//				String mcUrl = mc.getUrl();
//				mc.setUrl(mcUrl + "/languages");
//				return downloadLangJSON(mc, jsonPath);
//				mc.setUrl(mcUrl + "/issues?page=1");
//				return downloadIssuesJSON(mc, jsonPath);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean cloneRepo(String URL, String repoPath) {
		String details[] = URL.split("/");
		URL = "http://git.code.sf.net/p/" + details[details.length-1] + "/code";
		try {
			if(!GITRepositoryCloner.clone(URL, repoPath)){
				File f = new File(repoPath);
				if (!f.delete())
					FileDeleteStrategy.FORCE.delete(f);
				URL = "svn://svn.code.sf.net/p/" + details[details.length-1] + "/svn";
				SVNRepositoryCloner.clone(URL, repoPath);
			}
		} catch (IOException | GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
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

	private MetadataCacher initialize(String url, String jsonPath) {
		if (!url.contains("sourceforge.net")) {
			return null;
		} else {
			String userName = url.substring(0, url.indexOf('@'));
			String temp[] = url.split("/");
			String targetURL = "http://sourceforge.net/rest/p/" + temp[temp.length - 1];
			System.out.println("ForgeSF has received target URL:" + targetURL);
			// String password = readPassword();
//			String password = "candoiauser2016";
			String password = "candoiauser";
			return new MetadataCacher(targetURL, userName, password);
		}
	}
	
	private boolean downloadLangJSON(MetadataCacher mc, String jsonPath) {
		String pageContent = "";
		int pageNumber = 0;
		File dir = new File(jsonPath + "/languages");
		if (!dir.exists())
			dir.mkdirs();
		File[] files = dir.listFiles();

		Arrays.sort(files, new Comparator<File>() {
			@Override
			public int compare(File f1, File f2) {
				int n1 = getNumber(f1.getName()), n2 = getNumber(f2.getName());
				return n1 - n2;
			}

			private int getNumber(String name) {
				String s = name.substring(5, name.length());
				return Integer.valueOf(s);
			}
		});
		pageNumber = files.length;
		if (pageNumber > 0)
			pageContent = FileIO.readFileContents(files[pageNumber - 1]);
		if (mc.authenticate()) {
			while (true) {
				mc.getResponseJson();
				pageContent = mc.getContent();
				if (pageContent.equals("[]"))
					break;
				if (!pageContent.isEmpty()) {
					String path = jsonPath + "/languages/";
					File f = new File(path);
					if (!f.exists()) {
						f.mkdirs();
					}
					path = jsonPath + "/languages/lang" + pageNumber + ".json";
					f = new File(path);
					FileWriter file = null;
					try {
						file = new FileWriter(path);
						file.write(pageContent);
						pageNumber++;
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						return false;
					} finally {
						try {
							file.flush();
							file.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				break;
			}
		} else {
			System.out.println("Authentication failed!");
			return false;
		}
		return true;
	}
	
	private boolean downloadJSON(MetadataCacher mc, String jsonPath) {
		String pageContent = "";
		int pageNumber = 0;
		File dir = new File(jsonPath + "/repos");
		if (!dir.exists())
			dir.mkdirs();
		File[] files = dir.listFiles();
		Arrays.sort(files, new Comparator<File>() {
			@Override
			public int compare(File f1, File f2) {
				int n1 = getNumber(f1.getName()), n2 = getNumber(f2.getName());
				return n1 - n2;
			}

			private int getNumber(String name) {
				String s = name.substring(5, name.length());
				return Integer.valueOf(s);
			}
		});
		pageNumber = files.length;
		if (pageNumber > 0)
			pageContent = FileIO.readFileContents(files[pageNumber - 1]);
//		if (mc.authenticate()) {
			while (true) {
				mc.getResponseJson();
				pageContent = mc.getContent();
				if (pageContent.equals("[]"))
					break;
				if (!pageContent.isEmpty()) {
					String path = jsonPath + "/repos/";
					File f = new File(path);
					if (!f.exists()) {
						f.mkdirs();
					}
					path = jsonPath + "/repos/repo" + pageNumber + ".json";
					f = new File(path);
					FileWriter file = null;
					try {
						file = new FileWriter(path);
						file.write(pageContent);
						pageNumber++;
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						return false;
					} finally {
						try {
							file.flush();
							file.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				break;
			}
//		} else {
//			System.err.println("Authentication failed!");
//			return false;
//		}
		return true;
	}

	@Override
	public String getDirName(String URL) {
		String[] details = URL.split("/");
		return details[details.length-1];
	}
}
