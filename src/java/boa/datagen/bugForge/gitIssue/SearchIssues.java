package boa.datagen.bugForge.gitIssue;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.inject.Guice;
import com.google.inject.Inject;

import boa.datagen.DefaultProperties;
import boa.datagen.bugForge.gitIssue.UrlBuilder.GithubAPI;
import boa.types.Issues.IssueRepository;
import br.ufpe.cin.groundhog.Issue;
import br.ufpe.cin.groundhog.IssueLabel;
import br.ufpe.cin.groundhog.Project;
import br.ufpe.cin.groundhog.http.HttpModule;
import br.ufpe.cin.groundhog.http.Requests;

public class SearchIssues {
	private final Gson gson;
	private final Requests requests;
	private final UrlBuilder builder;

	@Inject
	public SearchIssues(Requests requests) {
		this.requests = requests;
		this.gson = new Gson();
		this.builder = Guice.createInjector(new HttpModule()).getInstance(UrlBuilder.class);
	}

	public List<Issue> getAllProjectIssues(Project project) {

		int pageNumber = 1;
		boolean check = true;
		List<Issue> issues = new ArrayList<Issue>();

		while (true) {
			String searchUrl = builder.uses(GithubAPI.ROOT).withParam("repos")
					.withSimpleParam("/", project.getOwner().getLogin()).withSimpleParam("", project.getName())
					.withParam("/issues").withParam("?state=all&").withParam("page=" + pageNumber).build();
			// String
			// searchUrl="https://api.github.com/repos/"+project.getOwner().getLogin()
			// + project.getName()+"/issues?page="+pageNumber;

			String jsonString = requests.get(searchUrl);
			List<IssueLabel> lables = new ArrayList<IssueLabel>();
			if (!jsonString.equals("[]") && !jsonString.contains(":API rate limit exceeded for")) {
				if (pageNumber % 10 == 0)
					System.out.println("page:" + pageNumber);
				try {
					JsonArray jsonArray = gson.fromJson(jsonString, JsonArray.class);

					for (JsonElement element : jsonArray) {
						Issue issue = gson.fromJson(element, Issue.class);
						issue.setProject(project);
						for (JsonElement lab : element.getAsJsonObject().get("labels").getAsJsonArray()) {
							IssueLabel label = gson.fromJson(lab, IssueLabel.class);
							lables.add(label);
						}

						issue.setLabels(lables);
						issues.add(issue);
						lables.clear();
					}
				} catch (java.lang.ClassCastException e) {
					JsonElement element = gson.fromJson(jsonString, JsonElement.class);
					Issue issue = gson.fromJson(element, Issue.class);
					issue.setProject(project);
					try {
						for (JsonElement lab : element.getAsJsonObject().get("labels").getAsJsonArray()) {
							IssueLabel label = gson.fromJson(lab, IssueLabel.class);
							lables.add(label);
						}

					} catch (java.lang.NullPointerException ex) {

					}
					issue.setLabels(lables);
					issues.add(issue);
					lables.clear();
				}
				pageNumber++;
			} else {
				break;
			}
		}
		return issues;
	}

	public long buildIssuesFromRemote(Project project, IssueRepository.Builder issueRepoBuilder) {
		int pageNumber = 1;
		long numIssue = 0;
		String projName = project.getName().substring(project.getName().lastIndexOf('/') + 1);
		String issuepath = DefaultProperties.GH_TICKETS_PATH + "/" + projName + "/issues";
		File f = new File(issuepath);
		if (!f.exists()) {
			f.mkdirs();
		}
		while (true) {
			String searchUrl = builder.uses(GithubAPI.ROOT).withParam("repos")
					.withSimpleParam("/", project.getOwner().getLogin()).withSimpleParam("", project.getName())
					.withParam("/issues").withParam("?state=all&").withParam("page=" + pageNumber).build();
			// String
			// searchUrl="https://api.github.com/repos/"+project.getOwner().getLogin()
			// + project.getName()+"/issues?page="+pageNumber;

			String jsonString = requests.get(searchUrl);
			List<IssueLabel> lables = new ArrayList<IssueLabel>();
			writeToFile(jsonString, issuepath + "/issue" + pageNumber + ".json");
			if (!jsonString.equals("[]") && !jsonString.contains(":API rate limit exceeded for")) {
				try {
					JsonArray jsonArray = gson.fromJson(jsonString, JsonArray.class);

					for (JsonElement element : jsonArray) {
						Issue issue = gson.fromJson(element, Issue.class);
						issue.setProject(project);
						for (JsonElement lab : element.getAsJsonObject().get("labels").getAsJsonArray()) {
							IssueLabel label = gson.fromJson(lab, IssueLabel.class);
							lables.add(label);
						}

						issue.setLabels(lables);
						boa.types.Issues.Issue.Builder issueBuilder = boa.types.Issues.Issue.newBuilder();
						issueRepoBuilder.addIssues(GithubIssues.storeProperties(issueBuilder, issue));
						// issues.add(issue);
						numIssue++;
						lables.clear();
					}
				} catch (java.lang.ClassCastException e) {
					JsonElement element = gson.fromJson(jsonString, JsonElement.class);
					Issue issue = gson.fromJson(element, Issue.class);
					issue.setProject(project);
					try {
						for (JsonElement lab : element.getAsJsonObject().get("labels").getAsJsonArray()) {
							IssueLabel label = gson.fromJson(lab, IssueLabel.class);
							lables.add(label);
						}

					} catch (java.lang.NullPointerException ex) {

					}
					issue.setLabels(lables);
					boa.types.Issues.Issue.Builder issueBuilder = boa.types.Issues.Issue.newBuilder();
					issueRepoBuilder.addIssues(GithubIssues.storeProperties(issueBuilder, issue));
					// issues.add(issue);
					numIssue++;
					lables.clear();
				}
				pageNumber++;
			} else {
				break;
			}
		}
		return numIssue;
	}

	private static void writeToFile(String content, String path) {
		FileWriter file = null;
		try {
			file = new FileWriter(path);
			file.write(content);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
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

	public long buildIssuesFromJSON(Project project, IssueRepository.Builder issueRepoBuilder, String path) {
		String projName = project.getName().substring(project.getName().lastIndexOf('/') + 1);
		String issueJson = DefaultProperties.GH_TICKETS_PATH + "/" + projName + DefaultProperties.BUG_DIR_NAME;
		File dir = new File(issueJson);
		long numIssue = 0;
		if (dir.isDirectory()) {
			for (File file : dir.listFiles()) {
				if (file.getName().endsWith(".json")) {
					try {
						BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
						byte[] bytes = new byte[(int) file.length()];
						in.read(bytes);
						in.close();
						String jsonTxt = new String(bytes);
						numIssue = numIssue + buildIssuesFromString(project, jsonTxt, issueRepoBuilder);
					} catch (Exception e) {
						System.err.println("Error reading file " + file.getAbsolutePath());
					}
				}
			}
		}
		return numIssue;
	}

	private long buildIssuesFromString(Project project, String jsonString, IssueRepository.Builder issueRepoBuilder) {
		List<IssueLabel> lables = new ArrayList<IssueLabel>();
		long numIssue = 0;
		if (!jsonString.equals("[]") && !jsonString.contains(":API rate limit exceeded for")) {
			try {
				JsonArray jsonArray = gson.fromJson(jsonString, JsonArray.class);

				for (JsonElement element : jsonArray) {
					Issue issue = gson.fromJson(element, Issue.class);
					issue.setProject(project);
					for (JsonElement lab : element.getAsJsonObject().get("labels").getAsJsonArray()) {
						IssueLabel label = gson.fromJson(lab, IssueLabel.class);
						lables.add(label);
					}

					issue.setLabels(lables);
					boa.types.Issues.Issue.Builder issueBuilder = boa.types.Issues.Issue.newBuilder();
					issueRepoBuilder.addIssues(GithubIssues.storeProperties(issueBuilder, issue));
					// issues.add(issue);
					numIssue++;
					lables.clear();
				}
			} catch (java.lang.ClassCastException e) {
				JsonElement element = gson.fromJson(jsonString, JsonElement.class);
				Issue issue = gson.fromJson(element, Issue.class);
				issue.setProject(project);
				try {
					for (JsonElement lab : element.getAsJsonObject().get("labels").getAsJsonArray()) {
						IssueLabel label = gson.fromJson(lab, IssueLabel.class);
						lables.add(label);
					}

				} catch (java.lang.NullPointerException ex) {

				}
				issue.setLabels(lables);
				boa.types.Issues.Issue.Builder issueBuilder = boa.types.Issues.Issue.newBuilder();
				issueRepoBuilder.addIssues(GithubIssues.storeProperties(issueBuilder, issue));
				// issues.add(issue);
				numIssue++;
				lables.clear();
			}
		} else {
			return 0;
		}
		return numIssue;
	}

}
