package boa.datagen.bugForge;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.protobuf.ByteString;

import b4j.core.DefaultAttachment;
import b4j.core.DefaultComment;
import b4j.core.DefaultIssue;
import b4j.core.DefaultSearchData;
import b4j.core.Issue;
import b4j.core.session.AbstractHttpSession;
import b4j.core.session.JiraRpcSession;
import boa.types.Issues.Issue.IssueKind;
import boa.types.Issues.Issue.State;
import boa.types.Issues.IssueAttachment;
import boa.types.Issues.IssueComment;
import boa.types.Issues.IssueComment.Builder;
import boa.types.Issues.IssueRepository;
import boa.types.Shared.Person;

public class JiraIssues implements BugForge {
	static int ids = 0;

	public final List<boa.types.Issues.Issue> importBugs(String url, String project) {
		List<boa.types.Issues.Issue> issues = new ArrayList<>();
		AbstractHttpSession session = new JiraRpcSession();
		try {
			((JiraRpcSession) session).setBaseUrl(new URL(url));
			session.setBugzillaBugClass(DefaultIssue.class);

			if (session.open()) {
				DefaultSearchData searchData = new DefaultSearchData();
				searchData.add("jql", "project = " + project);
				Iterator i = session.searchBugs(searchData, null).iterator();

				while (i.hasNext()) {
					Issue issue = (Issue) i.next();
					boa.types.Issues.Issue.Builder issueBuilder = boa.types.Issues.Issue.newBuilder();
					try {
						issues.add(storeProperties(issueBuilder, issue));
					} catch (Exception e) {
						System.out.println("Exception found in issue : " + issue.getId());
					}
				}

				// system.out.println("Total issues : " + issues.size());
				session.close();
			}

			return issues;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			session.close();
			return new ArrayList<>();
		}
	}

	private static boa.types.Issues.Issue storeProperties(boa.types.Issues.Issue.Builder issueBuilder, Issue issue) {
		int id = ids++; // Integer.valueOf(issue.getId());
		issueBuilder.setId(id);
		issueBuilder.setNumber(id);
		IssueKind kind = null;
		State state = null;
		if (issue.getStatus().isClosed()) {
			if (issue.getResolution().getName().equalsIgnoreCase("Fixed")) {
				kind = IssueKind.BUG;
				state = State.RESOLVED;
			} else if (issue.getResolution().getName().equalsIgnoreCase("INVALID")) {
				kind = IssueKind.INVALID;
				state = State.RESOLVED;
			} else if (issue.getResolution().getName().equalsIgnoreCase("WONTFIX")) {
				kind = IssueKind.WONTFIX;
				state = State.RESOLVED;
			} else if (issue.getResolution().getName().equalsIgnoreCase("DUPLICATE")) {
				kind = IssueKind.DUPLICATE;
				state = State.RESOLVED;
			} else if (issue.getResolution().getName().equalsIgnoreCase("WORKSFORME")) {
				kind = IssueKind.WORKSFORME;
				state = State.RESOLVED;
			} else if (issue.getResolution().getName().equalsIgnoreCase("INCOMPLETE")) {
				kind = IssueKind.INCOMPLETE;
				state = State.RESOLVED;
			} else {
				kind = IssueKind.BUG;
				state = State.RESOLVED;
			}
		} else if (issue.getStatus().isOpen()) {
			if (issue.getStatus().getName().equalsIgnoreCase("UNCONFIRMED")) {
				kind = IssueKind.BUG;
				state = State.UNCONFIRMED;
			} else if (issue.getStatus().getName().equalsIgnoreCase("NEW")) {
				kind = IssueKind.BUG;
				state = State.NEW;
			} else if (issue.getStatus().getName().equalsIgnoreCase("REOPENED")) {
				kind = IssueKind.BUG;
				state = State.REOPENED;
			} else if (issue.getStatus().getName().equalsIgnoreCase("ASSIGNED")) {
				kind = IssueKind.BUG;
				state = State.ASSIGNED;
			} else {
				kind = IssueKind.BUG;
				state = State.NEW;
			}
		} else {
			kind = IssueKind.BUG;
			state = State.UNCONFIRMED;
		}
		issueBuilder.setKind(kind);
		issueBuilder.setTitle(issue.getSummary());
		issueBuilder.setBody(issue.getDescription());

		issueBuilder.setState(state);

		Person.Builder personBuilder;

		// setAssignee
		if (issue.getAssignee() != null) {
			personBuilder = Person.newBuilder();
			try {
				personBuilder.setEmail("");
				String realName = "";
				if (issue.getAssignee().getRealName() != null)
					realName = issue.getAssignee().getRealName();
				personBuilder.setRealName(realName);

				String name = "";
				if (issue.getAssignee().getName() != null)
					name = issue.getAssignee().getName();
				personBuilder.setUsername(name);
			} catch (Exception e) {
				personBuilder.setEmail("");
				personBuilder.setRealName("");
				personBuilder.setUsername("");
			}
			issueBuilder.setAssignee(personBuilder.build());
		}

		// setClosedBy
		// considering issue reporter in bugzilla to be the
		// person who will close the bug
		if (issue.getReporter() != null) {
			personBuilder = Person.newBuilder();
			personBuilder.setEmail("");
			String realName = "";
			if (issue.getReporter().getRealName() != null)
				realName = issue.getReporter().getRealName();
			personBuilder.setRealName(realName);

			String name = "";
			if (issue.getReporter().getName() != null)
				name = issue.getReporter().getName();
			personBuilder.setUsername(name);

			issueBuilder.setClosedBy(personBuilder.build());
		}

		// setCreatedAt
		if (issue.getCreationTimestamp() != null)
			issueBuilder.setCreatedAt(issue.getCreationTimestamp().getTime());
		// setUpdatedAt
		if (issue.getUpdateTimestamp() != null)
			issueBuilder.setUpdatedAt(issue.getUpdateTimestamp().getTime());
		// setClosedAt same as updatedAt
		if (issue.getUpdateTimestamp() != null)
			issueBuilder.setClosedAt(issue.getUpdateTimestamp().getTime());
		Iterator commentsIter = issue.getComments().iterator();
		while (commentsIter.hasNext()) {
			DefaultComment comment = (DefaultComment) commentsIter.next();
			Builder issueCommentsBuilder = IssueComment.newBuilder();

			issueCommentsBuilder.setId(Integer.valueOf(comment.getId()));
			personBuilder = Person.newBuilder();
			personBuilder.setEmail("");

			String realName = "";
			if (comment.getAuthor().getRealName() != null)
				realName = comment.getAuthor().getRealName();
			personBuilder.setRealName(realName);

			String name = "";
			if (comment.getAuthor().getName() != null)
				name = comment.getAuthor().getName();
			personBuilder.setUsername(name);

			// personBuilder.setRealName(comment.getAuthor().getRealName());
			// personBuilder.setUsername(comment.getAuthor().getName());

			issueCommentsBuilder.setUser(personBuilder.build());

			if (comment.getCreationTimestamp() != null)
				issueCommentsBuilder.setCreatedAt(comment.getCreationTimestamp().getTime());
			if (comment.getUpdateTimestamp() != null)
				issueCommentsBuilder.setUpdatedAt(comment.getUpdateTimestamp().getTime());

			issueCommentsBuilder.setBody(comment.getTheText());

			issueBuilder.addComments(issueCommentsBuilder.build());
		}

		Iterator attachmentIter = issue.getAttachments().iterator();
		while (attachmentIter.hasNext()) {
			DefaultAttachment attachment = (DefaultAttachment) attachmentIter.next();
			boa.types.Issues.IssueAttachment.Builder attachmentBuilder = IssueAttachment.newBuilder();

			attachmentBuilder.setId(Integer.valueOf(attachment.getId()));
			attachmentBuilder.setType(attachment.getType());
			attachmentBuilder.setDescription(attachment.getDescription());
			attachmentBuilder.setFilename(attachment.getFilename());

			if (attachment.getContent() != null)
				attachmentBuilder.setContent(ByteString.copyFrom(attachment.getContent()));
			if (attachment.getDate() != null)
				attachmentBuilder.setDate(attachment.getDate().getTime());
			if (attachment.getUri() != null)
				attachmentBuilder.setUrl(attachment.getUri().toString());

			issueBuilder.addAttachments(attachmentBuilder.build());
		}
		// system.out.println(issueBuilder.build());
		return issueBuilder.build();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String url = args[0];
		String project = args[1];
		JiraIssues jira = new JiraIssues();
		jira.importBugs(url, project);

		// boa.types.Toplevel.Project.Builder project = null;
		// getIssuesWithBuilder(project,"https://issues.apache.org/jira/","HADOOP");
	}

	public static void getIssuesWithBuilder(boa.types.Toplevel.Project.Builder project, String url, String product) {
		List<boa.types.Issues.Issue> issues = new ArrayList<>();
		JiraIssues jira = new JiraIssues();
		final IssueRepository.Builder issueRepoBuilder = IssueRepository.newBuilder();
		issueRepoBuilder.setUrl(url);
		issueRepoBuilder.setKind(IssueRepository.IssueRepositoryKind.JIRA);
		try {
			issues = jira.importBugs(url, product);
		} catch (Exception e) {
//			e.printStackTrace();
			System.out.println("Could not find issues on: "+ url);
		}
		for (boa.types.Issues.Issue issue : issues) {
			boa.types.Issues.Issue.Builder issueBuilder = boa.types.Issues.Issue.newBuilder();
			issueRepoBuilder.addIssues(issue);
		}
		project.addIssueRepositories(issueRepoBuilder);
	}

	@Override
	public void buildIssue(boa.types.Toplevel.Project.Builder pr, String details) {
		getIssuesWithBuilder(pr, details.substring(details.indexOf('@')+1), details.substring(0, details.indexOf('@')));
		return;
	}

}
