package boa.datagen.forges.sf;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import boa.datagen.util.FileIO;
import boa.types.Code.CodeRepository;
import boa.types.Code.CodeRepository.RepositoryKind;
import boa.types.Issues.IssueRepository;
import boa.types.Issues.IssueRepository.IssueRepositoryKind;
import boa.types.Shared.Person;
import boa.types.Toplevel.Project;
import boa.types.Toplevel.Project.ForgeKind;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

public class RepoMetadata {
	private static final String ID = "id";
	private static final String NAME = "name";
	private static final String SHORT_DESCRIPTION = "shortdesc";
	private static final String HOME_PAGE = "homepage";
	private static final String SUMMARY_PAGE = "summary-page";
	private static final String CREATED_TIMESTAMP = "created_timestamp";
	private static final String DESCRIPTION = "description";
	private static final String OS = "os";
	private static final String PROGRAMMING_LANGUAGES = "programming-languages";
	private static final String DATABASES = "databases";
	private static final String LICENSES = "licenses";
	private static final String TOPICS = "topics";
	private static final String AUDIENCES = "audiences";
	private static final String ENVIRONMENTS = "environments";
	private static final String DONATION = "donation";
	private static final String MAINTAINERS = "maintainers";
	private static final String DEVELOPERS = "developers";
	private static final String TRACKERS = "trackers";
	private static final String SVN_REPO = "SVNRepository";
	private static final String GIT_REPO = "GitRepository";

	private static final String GIT_ID = "id";
	private static final String GIT_NAME = "full_name";
	private static final String GIT_SHORT_DESCRIPTION = "name";
	private static final String GIT_HOME_PAGE = "homepage";
	private static final String GIT_SUMMARY_PAGE = "html_url";
	// private static final String GIT_CREATED_TIMESTAMP = null;
	private static final String GIT_CREATE = "created_at"; // "created_at":
															// "2007-10-29T14:37:16Z",
	private static final String GIT_UPDATE = "updated_at"; // "updated_at":
															// "2015-06-15T11:40:32Z",
	private static final String GIT_PUSH = "pushed_at"; // "pushed_at":
														// "2014-02-03T19:33:59Z",
	private static final String GIT_DESCRIPTION = "description";
	private static final String GIT_OS = "os";
	private static final String GIT_PROGRAMMING_LANGUAGES = "language";
	private static final String GIT_DATABASES = null;
	private static final String GIT_LICENSES = null;
	private static final String GIT_TOPICS = null;
	private static final String GIT_AUDIENCES = null;
	private static final String GIT_ENVIRONMENTS = null;
	private static final String GIT_DONATION = null;
	private static final String GIT_MAINTAINERS = null;
	private static final String GIT_DEVELOPERS = null;
	private static final String GIT_TRACKERS = null;
	private static final String GIT_SVN_REPO = "svn_url";
	private static final String GIT_GIT_REPO = "clone_url";
	private static final String GIT_ISSUE_REPO = "issues_url";

	private static final String GIT_FORK = "fork";
	/*
	 * other git fields "size": 7954, "stargazers_count": 1856,
	 * "watchers_count": 1856, "language": "Ruby", "has_issues": true,
	 * "has_downloads": true, "has_wiki": true, "has_pages": false,
	 * "forks_count": 448, "mirror_url": null, "open_issues_count": 2, "forks":
	 * 448, "open_issues": 2, "watchers": 1856, "default_branch": "master",
	 * "network_count": 448, "subscribers_count": 60
	 */

	private File metadataFile;

	public String id;
	public String name;
	private String shortDescription;
	private String homepage;
	private String summaryPage;
	private long created_timestamp = -1;
	private String description;
	private String os;
	private String[] programmingLanguages;
	private String databases;
	private String licenses;
	private String topics;
	private String audiences;
	private String environments;
	private String donation;
	private String maintainers;
	private String developers;
	private String trackers;
	private String svnRepository;
	private String gitRepository;
	private String issueRepository;

	private String fork;

	public RepoMetadata(File file) {
		this.metadataFile = file;
	}

	public RepoMetadata() {
	}

	// this method is for github projects and has not been modified as per the
	// source forge. Change this before using it.
	public boolean build() {
		String jsonTxt = "";
		try {
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(metadataFile));
			byte[] bytes = new byte[(int) metadataFile.length()];
			in.read(bytes);
			in.close();
			jsonTxt = new String(bytes);
		} catch (Exception e) {
			System.err.println("Error reading file " + metadataFile.getAbsolutePath());
			return false;
		}
		if (jsonTxt.isEmpty()) {
			System.err.println("File is empty " + metadataFile.getAbsolutePath());
			return false;
		}
		// System.out.println(jsonTxt);

		JSONObject json = null;
		try {
			json = (JSONObject) JSONSerializer.toJSON(jsonTxt);
		} catch (JSONException e) {
		}
		if (json == null) {
			System.err.println("Error parsing file " + metadataFile.getAbsolutePath());
			return false;
		}
		JSONObject jsonProject = json;
		if (jsonProject.has(GIT_ID))
			this.id = jsonProject.getString(GIT_ID);
		if (jsonProject.has(GIT_NAME))
			this.name = jsonProject.getString(GIT_NAME);
		if (jsonProject.has(GIT_SHORT_DESCRIPTION))
			this.shortDescription = jsonProject.getString(GIT_SHORT_DESCRIPTION);
		if (jsonProject.has(GIT_HOME_PAGE)) {
			this.homepage = jsonProject.getString(GIT_HOME_PAGE);
		}
		if (jsonProject.has(GIT_SUMMARY_PAGE)) {
			this.summaryPage = jsonProject.getString(GIT_SUMMARY_PAGE);
		}
		if (jsonProject.has(GIT_CREATE)) {
			String time = jsonProject.getString(GIT_CREATE);
			this.created_timestamp = getTimeStamp(time); // project.setCreatedDate(timestamp
															// * 1000000);
		}
		if (jsonProject.has(GIT_DESCRIPTION))
			this.description = jsonProject.getString(GIT_DESCRIPTION);
		/*
		 * if (jsonProject.has("os")) { JSONArray jsonOSes =
		 * jsonProject.getJSONArray("os"); if (jsonOSes != null &&
		 * jsonOSes.isArray()) { for (int i = 0; i < jsonOSes.size(); i++)
		 * project.addOperatingSystems(jsonOSes.getString(i)); } }
		 */
		if (jsonProject.has(GIT_PROGRAMMING_LANGUAGES)) {
			buildProgrammingLanguages(metadataFile, id);
			if (this.programmingLanguages == null || this.programmingLanguages.length == 0)
				this.programmingLanguages = new String[] { jsonProject.getString(GIT_PROGRAMMING_LANGUAGES) };
		}
		if (jsonProject.has(GIT_GIT_REPO)) {
			this.gitRepository = jsonProject.getString(GIT_GIT_REPO);
		}
		if (jsonProject.has(GIT_ISSUE_REPO)) {
			this.issueRepository = jsonProject.getString(GIT_ISSUE_REPO);
		}
		return true;
	}

	private long getTimeStamp(String time) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		try {
			Date date = df.parse(time);
			return date.getTime() * 1000000;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return -1;
	}

	private void buildProgrammingLanguages(File metadataFile, String id) {
		File file = new File(metadataFile.getParentFile().getParentFile().getAbsolutePath() + "/languages/");
		for (File lang : file.listFiles()) {
			String content = FileIO.readFileContents(lang);
			ArrayList<String> languages = getLanguages(content);
			if (languages.isEmpty())
				return;
			this.programmingLanguages = new String[languages.size()];
			for (int i = 0; i < this.programmingLanguages.length; i++)
				this.programmingLanguages[i] = languages.get(i);
		}
	}

	private static ArrayList<String> getLanguages(String content) {
		ArrayList<String> languages = new ArrayList<String>();
		int status = 0, s = 0;
		String name = null;
		for (int i = 0; i < content.length(); i++) {
			if (status == 0 && content.charAt(i) == '\"') {
				status = 1;
				s = i + 1;
			} else if (status == 1 && content.charAt(i) == '\"') {
				status = 2;
				name = content.substring(s, i);
			} else if (status == 2 && content.charAt(i) == ':') {
				status = 3;
				s = i + 1;
			} else if (status == 3 && !Character.isDigit(content.charAt(i))) {
				status = 0;
				languages.add(name);
			}
		}
		return languages;
	}

	public JSONObject toBoaMetaDataJson() {
		JSONObject jsonRepo = new JSONObject();
		jsonRepo.put(ID, id);
		jsonRepo.put(NAME, name);
		jsonRepo.put(CREATED_TIMESTAMP, created_timestamp);
		jsonRepo.put(SUMMARY_PAGE, summaryPage);
		jsonRepo.put(HOME_PAGE, homepage);
		jsonRepo.put(DESCRIPTION, description);
		if (programmingLanguages != null) {
			JSONArray langs = new JSONArray();
			for (String lang : programmingLanguages)
				langs.add(lang);
			jsonRepo.put(PROGRAMMING_LANGUAGES, langs);
		}
		if (gitRepository != null) {
			JSONObject jsonGit = new JSONObject();
			jsonGit.put("location", gitRepository);
			jsonRepo.put(GIT_REPO, jsonGit);
		}

		JSONObject jo = new JSONObject();
		jo.put("Project", jsonRepo);
		return jo;
	}

	private static void getAndFillPersonDetails(String url, ArrayList<Person> requestToBeFilled) {
		String reposnseFromUrl = null;
		net.sf.json.JSONArray jsonData = null;
		MetadataCacher mc = new MetadataCacher(url, "nmtiwari", "swanit*49912");
		if (mc.authenticate()) {
			try {
				mc.getResponse();
				reposnseFromUrl = mc.getContent();
				jsonData = (net.sf.json.JSONArray) JSONSerializer.toJSON(reposnseFromUrl);
				for (Object o : jsonData) {
					JSONObject obj = (JSONObject) o;
					Person.Builder p = Person.newBuilder();
					if (obj.has("login")) {
						p.setUsername(obj.getString("login"));
						p.setRealName(obj.getString("login"));
					}
					if (obj.has("html_url")) {
						p.setEmail(obj.getString("html_url"));
					}
					requestToBeFilled.add(p.build());
				}

			} catch (Exception e) {

			}
		}

	}

	private static void buildOwners(String owner, ArrayList<Person> followers, ArrayList<Person> following,
			ArrayList<String> organizations) {
		net.sf.json.JSONObject json = null;
		try {
			json = (net.sf.json.JSONObject) JSONSerializer.toJSON(owner);
			String followersUrl = null;
			String followingUrl = null;
			String OrganizationUrl = null;
			if (json.has("followers_url")) {
				followersUrl = json.getString("followers_url");
				getAndFillPersonDetails(followersUrl, followers);
			}
			if (json.has("following_url")) {
				followingUrl = json.getString("following_url");
				followingUrl = followingUrl.substring(0, followingUrl.length() - 13);
				getAndFillPersonDetails(followingUrl, following);
			}
			if (json.has("organizations_url")) {
				OrganizationUrl = json.getString("organizations_url");
				getAndFillOrgDetails(OrganizationUrl, organizations);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	private static void getAndFillOrgDetails(String url, ArrayList<String> requestToBeFilled) {
		String reposnseFromUrl = null;
		net.sf.json.JSONArray jsonData = null;
		try {
			reposnseFromUrl = HttpURLConnect.getHttpData("get", url);
			jsonData = (net.sf.json.JSONArray) JSONSerializer.toJSON(reposnseFromUrl);
			for (Object o : jsonData) {
				JSONObject obj = (JSONObject) o;
				if (obj.has("login"))
					requestToBeFilled.add(obj.getString("login"));
			}
		} catch (Exception e) {
			System.out.println("Error 403 occured while readnig organizations detail");
			requestToBeFilled = new ArrayList<String>();
		}
	}

	public Project toBoaMetaDataProtobuf() {
		String jsonTxt = "";
		try {
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(metadataFile));
			byte[] bytes = new byte[(int) metadataFile.length()];
			in.read(bytes);
			in.close();
			jsonTxt = new String(bytes);
		} catch (Exception e) {
			System.err.println("Error reading file " + metadataFile.getAbsolutePath());
			return null;
		}
		if (jsonTxt.isEmpty()) {
			System.err.println("File is empty " + metadataFile.getAbsolutePath());
			return null;
		}
		// System.out.println(jsonTxt);

		JSONObject json = null;
		try {
			json = (JSONObject) JSONSerializer.toJSON(jsonTxt);
		} catch (JSONException e) {
		}
		if (json == null) {
			System.err.println("Error parsing file " + metadataFile.getAbsolutePath());
			return null;
		}
		JSONObject jsonProject = json;
		Project.Builder project = Project.newBuilder();
		project.setKind(ForgeKind.SOURCEFORGE);
		if (jsonProject.has("name")) {
			String[] brokenUrl = jsonProject.get("url").toString().split("/");
//			project.setName(jsonProject.getString("name"));
			project.setName("projects/" + brokenUrl[brokenUrl.length-1]);
		}
		if (jsonProject.has("url")) {
			String name = jsonProject.getString("url");
			project.setProjectUrl(name);
		}
		if (jsonProject.has("_id"))
			project.setId(jsonProject.getString("_id"));
		if (jsonProject.has("external_homepage"))
			project.setHomepageUrl(jsonProject.getString("external_homepage"));
		if (jsonProject.has("summary"))
			project.setDescription(jsonProject.getString("summary"));
		if (jsonProject.has("creation_date")) {
			String timestamp = jsonProject.getString("creation_date");
			DateFormat format = new SimpleDateFormat("YYYY-MM-DD");
			Date date = null;
			try {
				date = format.parse(timestamp);
			} catch (Exception e) {
				System.out.println(e);
			}
			project.setCreatedDate(date.getTime() * 1000000);
		} else {
			project.setCreatedDate(-1);
		}
		if (jsonProject.has("owner")) {
			Person.Builder person = Person.newBuilder();
			JSONObject jsonPerson = (JSONObject) JSONSerializer.toJSON(jsonProject.getString("owner"));
			if (jsonPerson.has("login")) {
				String ownerName = jsonPerson.getString("login");
				person.setRealName(ownerName);
				person.setUsername(ownerName);
			} else {
				person.setRealName(jsonPerson.getString("unknown"));
				person.setUsername(jsonPerson.getString("unknown"));
			}
			if (jsonPerson.has("html_url")) {
				person.setEmail(jsonPerson.getString("html_url"));
			}
			ArrayList<Person> followers = new ArrayList<Person>();
			ArrayList<Person> following = new ArrayList<Person>();
			ArrayList<String> organizations = new ArrayList<String>();
			RepoMetadata.buildOwners(jsonProject.getString("owner"), followers, following, organizations);
			person.addAllFollowers(followers);
			person.addAllFollowings(following);
			person.addAllOrganizations(organizations);
			project.addDevelopers(person.build());
		}
		if (jsonProject.has("short_description"))
			project.setDescription(jsonProject.getString("short_description"));
		if (jsonProject.has("os")) {
			JSONArray jsonOSes = jsonProject.getJSONArray("os");
			if (jsonOSes != null && jsonOSes.isArray())
				for (int i = 0; i < jsonOSes.size(); i++)
					project.addOperatingSystems(jsonOSes.getString(i));
		}
		if (jsonProject.has("language")) {
			// ArrayList<String> langs = new ArrayList<String>();
			// JSONArray languages =
			// jsonProject.getJSONArray("programming-languages");
			// if (languages.isArray())
			// for (int i = 0; i < languages.size(); i++)
			// langs.add(languages.getString(i).trim().toLowerCase());
			// if (!langs.isEmpty())
			// project.addAllProgrammingLanguages(langs);
			String lang = jsonProject.getString("language");
			project.addProgrammingLanguages(lang);

		}
		if (jsonProject.has("databases")) {
			JSONArray jsonDBs = jsonProject.getJSONArray("databases");
			if (jsonDBs.isArray())
				for (int i = 0; i < jsonDBs.size(); i++)
					project.addDatabases(jsonDBs.getString(i).trim());
		}
		if (jsonProject.has("licenses")) {
			ArrayList<String> strLicenses = new ArrayList<String>();
			JSONArray licenses = jsonProject.getJSONArray("licenses");
			if (licenses.isArray())
				for (int i = 0; i < licenses.size(); i++) {
					JSONObject license = licenses.getJSONObject(i);
					if (license.has("name"))
						strLicenses.add(license.getString("name"));
				}
			if (!strLicenses.isEmpty())
				project.addAllLicenses(strLicenses);
		}
		if (jsonProject.has("topics")) {
			ArrayList<String> strTopics = new ArrayList<String>();
			JSONArray topics = jsonProject.getJSONArray("topics");
			if (topics.isArray())
				for (int i = 0; i < topics.size(); i++)
					strTopics.add(topics.getString(i).trim().toLowerCase());
			if (!strTopics.isEmpty())
				project.addAllTopics(strTopics);
		}
		if (jsonProject.has("audiences")) {
			JSONArray jsonAudiences = jsonProject.getJSONArray("audiences");
			if (jsonAudiences.isArray())
				for (int i = 0; i < jsonAudiences.size(); i++)
					project.addAudiences(jsonAudiences.getString(i).trim());
		}
		if (jsonProject.has("environments")) {
			JSONArray jsonEnvs = jsonProject.getJSONArray("environments");
			if (jsonEnvs.isArray())
				for (int i = 0; i < jsonEnvs.size(); i++)
					project.addInterfaces(jsonEnvs.getString(i).trim());
		}
		if (jsonProject.has("topics")) {
			JSONArray jsonTopics = jsonProject.getJSONArray("topics");
			if (jsonTopics.isArray())
				for (int i = 0; i < jsonTopics.size(); i++)
					project.addTopics(jsonTopics.getString(i).trim());
		}
		if (jsonProject.has("donation")) {
			JSONObject jsonDonation = jsonProject.getJSONObject("donation");
			String status = jsonDonation.getString("status");
			if (status.equals("Not Accepting"))
				project.setDonations(false);
			else if (status.equals("Accepting"))
				project.setDonations(true);
		}
		if (jsonProject.has("maintainers")) {
			ArrayList<Person> persons = new ArrayList<Person>();
			JSONArray maintainers = jsonProject.getJSONArray("maintainers");
			if (maintainers.isArray())
				for (int i = 0; i < maintainers.size(); i++) {
					JSONObject maintainer = maintainers.getJSONObject(i);
					if (maintainer.has("name")) {
						Person.Builder personMain = Person.newBuilder();
						personMain.setRealName(maintainer.getString("name"));
						personMain.setUsername(maintainer.getString("name"));
						personMain.setEmail(maintainer.getString("homepage"));
						persons.add(personMain.build());
					}
				}
			if (!persons.isEmpty())
				project.addAllMaintainers(persons);
		}
		if (jsonProject.has("developers")) {
			ArrayList<Person> persons = new ArrayList<Person>();
			JSONArray developers = jsonProject.getJSONArray("developers");
			if (developers.isArray())
				for (int i = 0; i < developers.size(); i++) {
					JSONObject developer = developers.getJSONObject(i);
					if (developer.has("name")) {
						Person.Builder person = Person.newBuilder();
						person.setRealName(developer.getString("name"));
						person.setUsername(developer.getString("name"));
						if (developer.has("homepage"))
							person.setEmail(developer.getString("homepage"));
						else
							person.setEmail("not found");
						persons.add(person.build());
					}
				}
			if (!persons.isEmpty())
				project.addAllDevelopers(persons);
		}
		if (jsonProject.has("SVNRepository")) {
			JSONObject rep = jsonProject.getJSONObject("SVNRepository");
			if (rep.has("location")) {
				CodeRepository.Builder cr = CodeRepository.newBuilder();
				cr.setUrl(rep.getString("location"));
				cr.setKind(RepositoryKind.SVN);
				project.addCodeRepositories(cr.build());
			}
		}
		if (jsonProject.has("CVSRepository")) {
			JSONObject rep = jsonProject.getJSONObject("CVSRepository");
			if (rep.has("browse")) {
				CodeRepository.Builder cr = CodeRepository.newBuilder();
				cr.setUrl(rep.getString("browse"));
				cr.setKind(RepositoryKind.CVS);
				project.addCodeRepositories(cr.build());
			}
		}
		// FIXME verify key name
		if (jsonProject.has("GitRepository")) {
			JSONObject rep = jsonProject.getJSONObject("GitRepository");
			if (rep.has("location")) {
				CodeRepository.Builder cr = CodeRepository.newBuilder();
				cr.setUrl(rep.getString("location"));
				cr.setKind(RepositoryKind.GIT);
				project.addCodeRepositories(cr.build());
			}
		}
		// FIXME verify key name
		if (jsonProject.has("BzrRepository")) {
			JSONObject rep = jsonProject.getJSONObject("BzrRepository");
			if (rep.has("location")) {
				CodeRepository.Builder cr = CodeRepository.newBuilder();
				cr.setUrl(rep.getString("location"));
				cr.setKind(RepositoryKind.BZR);
				project.addCodeRepositories(cr.build());
			}
		}
		// FIXME verify key name
		if (jsonProject.has("HgRepository")) {
			JSONObject rep = jsonProject.getJSONObject("HgRepository");
			if (rep.has("location")) {
				CodeRepository.Builder cr = CodeRepository.newBuilder();
				cr.setUrl(rep.getString("location"));
				cr.setKind(RepositoryKind.HG);
				project.addCodeRepositories(cr.build());
			}
		}
		if (project.getCodeRepositoriesCount() <= 0) {
			CodeRepository.Builder cr = CodeRepository.newBuilder();
			cr.setUrl("Unknown: Information not available in JSON Files");
			cr.setKind(RepositoryKind.UNKNOWN);
			project.addCodeRepositories(cr.build());
		}
		if(jsonProject.has("tools")){
			JSONArray tools = jsonProject.getJSONArray("tools");
			for(Object obj : tools){
				JSONObject bug = (JSONObject)obj;
				if(bug.has("name") && "tickets".equals(bug.get("name"))){
					IssueRepository.Builder ir = IssueRepository.newBuilder();
					issueRepository = "https://sourceforge.net" + bug.get("url");
					ir.setUrl(issueRepository);
					ir.setKind(IssueRepositoryKind.SVNTICKETS);
					project.addIssueRepositories(ir.build());
					break;
				}
			}
		}
		return project.build();
	}

	public Project toBoaMetaDataProtobufWithoutJSON() {
		Project.Builder project = Project.newBuilder();
		project.setKind(ForgeKind.GITHUB);
		project.setId("local");
		project.setName("local");
		project.setCreatedDate(created_timestamp);
		project.setProjectUrl("no summary");
		project.setHomepageUrl("no homepage");
		project.setDescription("no description");
		if (programmingLanguages != null) {
			ArrayList<String> langs = new ArrayList<String>();
			for (String lang : programmingLanguages)
				langs.add(lang);
			if (!langs.isEmpty())
				project.addAllProgrammingLanguages(langs);
		}
		if (gitRepository != null) {
			CodeRepository.Builder cr = CodeRepository.newBuilder();
			cr.setUrl(gitRepository);
			cr.setKind(RepositoryKind.GIT);
			project.addCodeRepositories(cr.build());
		}
		if (issueRepository != null) {
			IssueRepository.Builder ir = IssueRepository.newBuilder();
			ir.setUrl(issueRepository);
			ir.setKind(IssueRepositoryKind.SVNTICKETS);
			project.addIssueRepositories(ir.build());
		}
		Project prj = project.build();
		return prj;
	}
}
