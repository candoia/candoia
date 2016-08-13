package boa.datagen.forges.sf;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import boa.datagen.forges.AbstractForge;
import boa.datagen.util.FileIO;
import boa.types.Toplevel.Project;

public class ForgeSF extends AbstractForge {

	@Override
	public boolean getJSON(String url, String jsonPath) {
		MetadataCacher mc = initialize(url, jsonPath);
		if (mc != null) {
			if (downloadJSON(mc, jsonPath)) {
				 String mcUrl = mc.getUrl();
				 mc.setUrl(mcUrl + "/bugs?page=1");
				 return downloadIssuesJSON(mc, jsonPath);
			}
		}
		return false;
	}

  /*
   * There are two possibilities for source forge urls. Either it can be
   * sourceforge.com/project or it can be of format
   * svn.code.sf.net or git.sf.code.net
   */
	@Override
	public boolean cloneRepo(String URL, String repoPath) {
		if(URL.contains("git")){
			return GITRepositoryCloner.clone(URL, repoPath);
		}else if(URL.contains("svn")) try {
			return SVNRepositoryCloner.clone(this.createURL(URL), repoPath);
		} catch (IOException e) {
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
		if (!url.contains("sourceforge.net") && !url.contains("code.sf.net/")) {
			return null;
		} else {
			String userName = url.substring(0, url.indexOf('@'));
			String temp[] = url.split("/");
			// Because source forge naming convention is to follow code.sf.net/proj/repo
			// we use the second last item
			String targetURL = "http://sourceforge.net/rest/p/" + temp[temp.length - 2];
			System.out.println("ForgeSF has received target URL:" + targetURL);
			// String password = readPassword();
			// String password = "candoiauser2016";
			String password = "candoiauser";
			return new MetadataCacher(targetURL, userName, password);
		}
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
						e1.printStackTrace();
						return false;
					} finally {
						try {
							file.flush();
							file.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					pageNumber++;
					String newURL = mc.getUrl().substring(0, mc.getUrl().length() - 1);
					newURL = newURL + (pageNumber + 1);
					mc.setUrl(newURL);
				}
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
		// if (mc.authenticate()) {
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
		return true;
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
