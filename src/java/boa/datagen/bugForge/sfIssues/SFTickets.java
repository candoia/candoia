package boa.datagen.bugForge.sfIssues;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Guice;
import com.google.inject.Injector;

import boa.datagen.bugForge.BugForge;
import boa.types.Issues.Issue;
import boa.types.Issues.IssueRepository;
import boa.types.Issues.IssueRepository.IssueRepositoryKind;
import boa.types.Toplevel.Project.Builder;
import br.ufpe.cin.groundhog.search.SearchModule;

public class SFTickets implements BugForge{
	static SearchSVN searchSVN;
	private static List<Issue> issues;

	public final List<boa.types.Issues.Issue> importTickets(String pname) {
		if (searchSVN == null)
			setup();

		List<Issue> issues = searchSVN.storeTickets(pname);
		return issues;
	}

	public static List<Issue> getSVNTickets(String args) {
		if (args == null) {
			return new ArrayList<Issue>();
		}
		SFTickets svnTickets = new SFTickets();
		return svnTickets.importTickets(args);
	}

	public static void main(String[] args) {
		if (args.length < 1) {
		}
		SFTickets svnTickets = new SFTickets();
		issues = getSVNTickets(args[0]);
		for (Issue i : issues) {
			 System.out.println(i);
		}
	}

	private static void setup() {
		Injector injector = Guice.createInjector(new SearchModule());
		searchSVN = injector.getInstance(SearchSVN.class);
	}

	public static void getIssuesWithBuilder(boa.types.Toplevel.Project.Builder project, String details) {
		issues = getSVNTickets(details);
		final IssueRepository.Builder issueRepoBuilder = IssueRepository.newBuilder();
		issueRepoBuilder.setUrl(details);
		for (Issue issue : issues) {
			issueRepoBuilder.addIssues(issue);
		}
		issueRepoBuilder.setKind(IssueRepositoryKind.SVNTICKETS);
		project.addIssueRepositories(issueRepoBuilder);
	}

	@Override
	public void buildIssue(Builder pr, String details) {
		if(details.endsWith("/")){
			details = details.substring(0, details.length()-1);
		}
		String detail[] = details.split("/");
		String projName = detail[detail.length-2];
		getIssuesWithBuilder(pr, projName);
	}

}
