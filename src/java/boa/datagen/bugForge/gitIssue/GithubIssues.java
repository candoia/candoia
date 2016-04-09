package boa.datagen.bugForge.gitIssue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.google.inject.Guice;
import com.google.inject.Injector;

import boa.types.Issues.Issue.IssueKind;
import boa.types.Issues.Issue.State;
import boa.types.Issues.IssueRepository.IssueRepositoryKind;
import boa.datagen.DefaultProperties;
import boa.datagen.bugForge.BugForge;
import boa.types.Issues.IssueRepository;
import boa.types.Shared.Person;
import boa.types.Toplevel.Project.Builder;
import br.ufpe.cin.groundhog.Issue;
import br.ufpe.cin.groundhog.Milestone;
import br.ufpe.cin.groundhog.Project;
import br.ufpe.cin.groundhog.PullRequest;
import br.ufpe.cin.groundhog.User;
import br.ufpe.cin.groundhog.search.SearchModule;

public class GithubIssues implements BugForge {
	static SearchIssues searchGitHub;

	public static void main(String[] args) {
		if (args.length < 2) {
			return;
		}
		String uname = args[0];
		String pname = args[1];

		User user = new User(uname);
		Project pr = new Project(user, pname);

		if (searchGitHub == null)
			setup();

		List<Issue> issues = searchGitHub.getAllProjectIssues(pr);
		for (Issue issue : issues) {
		}
	}

	public static void getIssues(boa.types.Toplevel.Project project, String uname, String pname, String url, int i) {
		User user = new User(uname);
		Project pr = new Project(user, pname);
		if (searchGitHub == null)
			setup();

		List<Issue> issues = searchGitHub.getAllProjectIssues(pr);
		// system.out.println("Total issues : " + issues.size());
		for (Issue issue : issues) {
			storeIssues(project, i, url, issues);
			return;
		}
	}

	public static void getIssuesWithBuilder(boa.types.Toplevel.Project.Builder project, String uname, String pname,
			String url, int i) {
		User user = new User(uname);
		Project pr = new Project(user, pname);
		if (searchGitHub == null) {
			setup();
		}
		final IssueRepository.Builder issueRepoBuilder = IssueRepository.newBuilder();
		issueRepoBuilder.setUrl(url);
		issueRepoBuilder.setKind(IssueRepositoryKind.GITISSUE);
		String projName = project.getName().substring(project.getName().lastIndexOf('/') + 1);
		String issuepath = DefaultProperties.GH_TICKETS_PATH + "/" + projName + "/issues";
		if (new File(issuepath).isDirectory()) {
			long count = searchGitHub.buildIssuesFromJSON(pr, issueRepoBuilder, issuepath);
			System.out.println("Total Issues for " + projName +":" + count);
		} else {
			long count = searchGitHub.buildIssuesFromRemote(pr, issueRepoBuilder);
			System.out.println("Total Issues for " + projName +":" + count);
		}
		project.addIssueRepositories(issueRepoBuilder.build());
		return;
	}

	public static void setup() {
		Injector injector = Guice.createInjector(new SearchModule());
		searchGitHub = injector.getInstance(SearchIssues.class);
	}

	private static boa.types.Toplevel.Project storeIssues(final boa.types.Toplevel.Project project, final int i,
			String url, List<Issue> issues) {
		final boa.types.Toplevel.Project.Builder projBuilder = boa.types.Toplevel.Project.newBuilder(project);
		final IssueRepository repo = project.getIssueRepositories(i);
		final IssueRepository.Builder issueRepoBuilder = IssueRepository.newBuilder(repo);

		issueRepoBuilder.setUrl(url);

		for (Issue issue : issues) {
			boa.types.Issues.Issue.Builder issueBuilder = boa.types.Issues.Issue.newBuilder();
			issueRepoBuilder.addIssues(storeProperties(issueBuilder, issue));
		}

		return projBuilder.setIssueRepositories(i, issueRepoBuilder.build()).build();
	}

	public static boa.types.Toplevel.Project.Builder storeIssuesWithBuilder(
			final boa.types.Toplevel.Project.Builder projBuilder, final int i, String url, List<Issue> issues) {
		final IssueRepository.Builder issueRepoBuilder = IssueRepository.newBuilder();
		issueRepoBuilder.setUrl(url);
		for (Issue issue : issues) {
			boa.types.Issues.Issue.Builder issueBuilder = boa.types.Issues.Issue.newBuilder();
			issueRepoBuilder.addIssues(storeProperties(issueBuilder, issue));
		}
		return projBuilder.addIssueRepositories(issueRepoBuilder.build());
	}

	public static boa.types.Issues.Issue storeProperties(boa.types.Issues.Issue.Builder issueBuilder, Issue issue) {
		issueBuilder.setId(issue.getId());
		issueBuilder.setNumber(issue.getNumber());
		issueBuilder.setKind(IssueKind.BUG);

		issueBuilder.setTitle(issue.getTitle());
		if (issue.getBody() != null) {
			issueBuilder.setBody(issue.getBody());
		}

		if (issue.getState().equalsIgnoreCase("open"))
			issueBuilder.setState(State.NEW);
		else if (issue.getState().equalsIgnoreCase("closed"))
			issueBuilder.setState(State.RESOLVED);

		Person.Builder personBuilder;

		if (issue.getAssignee() != null) {
			personBuilder = Person.newBuilder();
			if (issue.getAssignee().getEmail() == null)
				personBuilder.setEmail("unknown email");
			else
				personBuilder.setEmail(issue.getAssignee().getEmail());

			if (issue.getAssignee().getName() == null)
				personBuilder.setRealName("anonymous");
			else
				personBuilder.setRealName(issue.getAssignee().getName());

			if (issue.getAssignee().getLogin() == null)
				personBuilder.setUsername("anonymous");
			else
				personBuilder.setUsername(issue.getAssignee().getLogin());

			issueBuilder.setAssignee(personBuilder.build());
		}

		if (issue.getClosedBy() != null) {
			personBuilder = Person.newBuilder();

			if (issue.getClosedBy().getEmail() == null)
				personBuilder.setEmail("unknown email");
			else
				personBuilder.setEmail(issue.getClosedBy().getEmail());

			if (issue.getClosedBy().getName() == null)
				personBuilder.setRealName("anonymous");
			else
				personBuilder.setRealName(issue.getClosedBy().getName());

			if (issue.getClosedBy().getLogin() == null)
				personBuilder.setUsername("anonymous");
			else
				personBuilder.setUsername(issue.getClosedBy().getLogin());
			issueBuilder.setClosedBy(personBuilder.build());
		}

		if (issue.getCreatedAt() != null)
			issueBuilder.setCreatedAt(issue.getCreatedAt().getTime());
		if (issue.getUpdatedAt() != null)
			issueBuilder.setUpdatedAt(issue.getUpdatedAt().getTime());
		if (issue.getClosedAt() != null)
			issueBuilder.setClosedAt(issue.getClosedAt().getTime());

		if (issue.getPullRequest() != null) {
			PullRequest pull = issue.getPullRequest();
			boa.types.Issues.PullRequest.Builder pullRequestBuilider = boa.types.Issues.PullRequest.newBuilder();

			pullRequestBuilider.setId(pull.getId());
			if (pull.getMergedAt() != null)
				pullRequestBuilider.setMergedAt(pull.getMergedAt().getTime());
			pullRequestBuilider.setMergeCommitSha(pull.getMergeCommitSha());
			pullRequestBuilider.setReviewComments(pull.getReviewCommentsCount());
			pullRequestBuilider.setCommits(pull.getCommitsCount());
			pullRequestBuilider.setMerged(pull.isMerged());
			pullRequestBuilider.setMergeable(pull.isMergeable());
			pullRequestBuilider.setMergeableState(pull.getMergeableState());

			if (pull.getMergedBy() != null) {
				personBuilder = Person.newBuilder();
				personBuilder.setEmail(pull.getMergedBy().getEmail());
				personBuilder.setRealName(pull.getMergedBy().getName());
				personBuilder.setUsername(pull.getMergedBy().getLogin());

				pullRequestBuilider.setMergedBy(personBuilder.build());
			}

			pullRequestBuilider.setAdditions(pull.getAdditionsCount());
			pullRequestBuilider.setDeletions(pull.getDeletionsCount());
			pullRequestBuilider.setChangedFiles(pull.getChangedFilesCount());

			issueBuilder.setPullRequest(pullRequestBuilider.build());
		}

		if (issue.getMilestone() != null) {
			Milestone milestone = issue.getMilestone();
			boa.types.Issues.Milestone.Builder milestoneBuilder = boa.types.Issues.Milestone.newBuilder();

			milestoneBuilder.setId(milestone.getId());
			milestoneBuilder.setNumber(milestone.getNumber());
			milestoneBuilder.setState(milestone.getState());
			milestoneBuilder.setTitle(milestone.getTitle());
			if (milestone.getDescription() != null)
				milestoneBuilder.setDescription(milestone.getDescription());

			if (milestone.getCreator() != null) {
				personBuilder = Person.newBuilder();
				if (milestone.getCreator().getEmail() != null)
					personBuilder.setEmail(milestone.getCreator().getEmail());
				else
					personBuilder.setEmail("unknown");
				if (milestone.getCreator().getName() != null)
					personBuilder.setRealName(milestone.getCreator().getName());
				else
					personBuilder.setRealName("unknown");
				if (milestone.getCreator().getLogin() != null)
					personBuilder.setUsername(milestone.getCreator().getLogin());
				else
					personBuilder.setUsername("unknonw");

				milestoneBuilder.setCreator(personBuilder.build());
			}

			milestoneBuilder.setOpenIssues(milestone.getOpenIssuesCount());
			milestoneBuilder.setClosedIssues(milestone.getClosedIssuesCount());

			if (milestone.getCreatedAt() != null)
				milestoneBuilder.setCreatedAt(milestone.getCreatedAt().getTime());
			if (milestone.getUpdatedAt() != null)
				milestoneBuilder.setUpdatedAt(milestone.getUpdatedAt().getTime());
			if (milestone.getDueOn() != null)
				milestoneBuilder.setCreatedAt(milestone.getDueOn().getTime());

			issueBuilder.setMilestone(milestoneBuilder.build());
		}

		return issueBuilder.build();
	}

	@Override
	public void buildIssue(Builder pr, String url) {
		String projName = url.substring(url.lastIndexOf("/"), url.length());
		String details[] = url.split("/");
		String ownerName = details[details.length - 2];
		this.getIssuesWithBuilder(pr, ownerName, projName, url, 0);
	}
}
