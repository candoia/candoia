/**
 * 
 */
package boa.datagen.forges.github;

import java.io.Console;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

import boa.datagen.candoia.CandoiaUtilities;
import org.eclipse.jgit.api.errors.GitAPIException;

import boa.datagen.forges.AbstractForge;
import boa.datagen.util.FileIO;
import boa.types.Toplevel.Project;

/**
 * @author nmtiwari
 *
 */
public class ForgeGithub extends AbstractForge {

	/**
	 * 
	 */
	private MetadataCacher mc;

	public ForgeGithub() {
		// TODO Auto-generated constructor stub
	}

	private MetadataCacher initialize(String url, String jsonPath) {
		if (!url.contains("github.com")) {
			return null;
		} else {
			String userName = url.substring(0, url.indexOf('@'));
			String temp[] = url.split("/");
			String targetURL = "https://api.github.com/repos/" + temp[temp.length - 2] + "/" + temp[temp.length - 1];
			char[] pswrd = readPassword();
			if (pswrd == null) {
				String password = "candoiauser2016";
				userName = "candoiaISU";
				return new MetadataCacher(targetURL, userName, password);
			} else {
				String password = new String(pswrd);
				return new MetadataCacher(targetURL, userName, password);
			}
			// String password = "candoiauser2016";

		}
	}

	@Override
	public boolean getJSON(String url, String jsonPath) {
		MetadataCacher mc = initialize(url, jsonPath);
		if (mc != null) {
			if (downloadJSON(mc, jsonPath)) {
				String mcUrl = mc.getUrl();
				mc.setUrl(mcUrl + "/languages");
				downloadLangJSON(mc, jsonPath);
				mc.setUrl(mcUrl + "/issues?page=1");
				return downloadIssuesJSON(mc, jsonPath);
			}
		}
		return false;
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
		if (mc.authenticate()) {
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
		} else {
			System.err.println("Authentication failed!");
			return false;
		}
		return true;
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

	private boolean downloadIssuesJSON(MetadataCacher mc, String jsonPath) {
		String pageContent = "";
		int pageNumber = 0;
		File dir = new File(jsonPath + "/issues");
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
					String path = jsonPath + "/issues/";
					File f = new File(path);
					if (!f.exists()) {
						f.mkdirs();
					}
					path = jsonPath + "/issues/issue" + pageNumber + ".json";
					f = new File(path);
					FileWriter file = null;
					try {
						file = new FileWriter(path);
						file.write(pageContent);
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
					pageNumber++;
					String newURL = mc.getUrl().substring(0, mc.getUrl().length() - 1);
					newURL = newURL + (pageNumber + 1);
					mc.setUrl(newURL);
				}
				// break;
			}
		} else {
			System.out.println("Authentication failed!");
			return false;
		}
		return true;
	}

	public boolean cloneRepo(String URL, String repoPath) {
		URL = URL.substring(URL.indexOf('@') + 1, URL.length()) + ".git";
		try {
			GITRepositoryCloner.clone(URL, repoPath);
		} catch (IOException | GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public Project toBoaProject(File jsonFile) {
		if (jsonFile.getName().endsWith(".json")) {
			RepoMetadata repo = new RepoMetadata(jsonFile);
			if (repo.build() && repo.id != null && repo.name != null) {
				Project protobufRepo = repo.toBoaMetaDataProtobuf();
				return protobufRepo;
			}
		}
		return null;
	}
	// @Override
	// public boolean buildProject(String url) {
	// this.getJSON(url, DefaultProperties.GH_JSON_PATH);
	// this.cloneRepo(url, DefaultProperties.GH_GIT_PATH);
	// return false;
	// }

	@Override
	public String getDirName(String URL) {
		String repoName = URL.substring(URL.lastIndexOf('/') + 1, URL.length());
		return repoName;
	}

	@Override
	public String getUsrName(String URL) {
		String[] details = URL.split("/");
		return details[details.length - 2];
	}
}
