package boa.datagen.bugForge;

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
import b4j.core.session.BugzillaHttpSession;
import boa.types.Issues.Issue.IssueKind;
import boa.types.Issues.Issue.State;
import boa.types.Issues.IssueAttachment;
import boa.types.Issues.IssueComment;
import boa.types.Issues.IssueComment.Builder;
import boa.types.Issues.IssueRepository;
import boa.types.Shared.Person;

//import org.apache.commons.configuration.*;

public class BugzillaReports implements BugForge{

	public final List<boa.types.Issues.Issue> importBugs(String url,
			String product) throws Exception {
		List<boa.types.Issues.Issue> issues = new ArrayList<>();
		// Create the session
		BugzillaHttpSession session = new BugzillaHttpSession();
		session.setBaseUrl(new URL(url)); // https://landfill.bugzilla.org/bugzilla-tip/
		session.setBugzillaBugClass(DefaultIssue.class);

		// Open the session
		if (session.open()) {
			// Search abug
			DefaultSearchData searchData = new DefaultSearchData();
			searchData.add("product", product);

			// Perform the search
			Iterable<Issue> it = session.searchBugs(searchData, null);
			for (Issue issue : it) {
				boa.types.Issues.Issue.Builder issueBuilder = boa.types.Issues.Issue
						.newBuilder();
				issues.add(storeProperties(issueBuilder, issue));
			}
			// Close the session
			session.close();
		}
		return issues;
	}

	private static boa.types.Issues.Issue storeProperties(
			boa.types.Issues.Issue.Builder issueBuilder, Issue issue) {
		int id = Integer.valueOf(issue.getId());
		issueBuilder.setId(id);
		issueBuilder.setNumber(id);
		IssueKind kind = null;
		State state = null;
		if (issue.getStatus().isClosed()) {
			if (issue.getResolution().getName().equalsIgnoreCase("Fixed")) {
				kind = IssueKind.BUG;
				state = State.RESOLVED;
			} else if (issue.getResolution().getName()
					.equalsIgnoreCase("INVALID")) {
				kind = IssueKind.INVALID;
				state = State.RESOLVED;
			} else if (issue.getResolution().getName()
					.equalsIgnoreCase("WONTFIX")) {
				kind = IssueKind.WONTFIX;
				state = State.RESOLVED;
			} else if (issue.getResolution().getName()
					.equalsIgnoreCase("DUPLICATE")) {
				kind = IssueKind.DUPLICATE;
				state = State.RESOLVED;
			} else if (issue.getResolution().getName()
					.equalsIgnoreCase("WORKSFORME")) {
				kind = IssueKind.WORKSFORME;
				state = State.RESOLVED;
			} else if (issue.getResolution().getName()
					.equalsIgnoreCase("INCOMPLETE")) {
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
			personBuilder.setEmail("");
			String realName = "";
			if (issue.getAssignee().getRealName() != null)
				realName = issue.getAssignee().getRealName();
			personBuilder.setRealName(realName);
			
			String name = "";
			if (issue.getAssignee().getName() != null)
				name = issue.getAssignee().getName();
			personBuilder.setUsername(name);
			
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
		// pullrequest
		// none for bugzilla
		// milestone
		// commments
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
			
			//personBuilder.setRealName(comment.getAuthor().getRealName());
			//personBuilder.setUsername(comment.getAuthor().getName());

			issueCommentsBuilder.setUser(personBuilder.build());

			if (comment.getCreationTimestamp() != null)
				issueCommentsBuilder.setCreatedAt(comment
						.getCreationTimestamp().getTime());
			if (comment.getUpdateTimestamp() != null)
				issueCommentsBuilder.setUpdatedAt(comment.getUpdateTimestamp()
						.getTime());

			issueCommentsBuilder.setBody(comment.getTheText());

			issueBuilder.addComments(issueCommentsBuilder.build());
		}

		Iterator attachmentIter = issue.getAttachments().iterator();
		while (attachmentIter.hasNext()) {
			DefaultAttachment attachment = (DefaultAttachment) attachmentIter
					.next();
			boa.types.Issues.IssueAttachment.Builder attachmentBuilder = IssueAttachment
					.newBuilder();

			attachmentBuilder.setId(Integer.valueOf(attachment.getId()));
			attachmentBuilder.setType(attachment.getType());
			attachmentBuilder.setDescription(attachment.getDescription());
			attachmentBuilder.setFilename(attachment.getFilename());

			if (attachment.getContent() != null)
				attachmentBuilder.setContent(ByteString.copyFrom(attachment
						.getContent()));
			if (attachment.getDate() != null)
				attachmentBuilder.setDate(attachment.getDate().getTime());
			if (attachment.getUri() != null)
				attachmentBuilder.setUrl(attachment.getUri().toString());

			issueBuilder.addAttachments(attachmentBuilder.build());
		}
		//system.out.println(issueBuilder.build());
		return issueBuilder.build();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BugzillaReports b4jimporter = new BugzillaReports();
		String url = "https://bz.apache.org/bugzilla/";
		String product = "Tomcat 8";
		try {
			b4jimporter.importBugs(url, product);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void getIssuesWithBuilder(boa.types.Toplevel.Project.Builder project,String url,String product) {
		//system.out.println("product in bugzilla:"+product);
		String temp=product;
		final IssueRepository.Builder issueRepoBuilder = IssueRepository.newBuilder();
		if(product.equalsIgnoreCase("Tomcat")){
			for(int i=1;i<10;i++){
				//system.out.println("adding issues for tomcat"+i);
				product=temp+" "+i;
				//system.out.println("product in bugzilla:"+product);
				List<boa.types.Issues.Issue> issues = null ;
				BugzillaReports b4jimporter = new BugzillaReports();
			
				issueRepoBuilder.setUrl(url);
				try {
					issues=b4jimporter.importBugs(url, product);
				} catch (Exception e) {
					e.printStackTrace();
				}
				//system.out.println("Total issues from bugzilla : " + issues.size());
				for (boa.types.Issues.Issue issue : issues) {
					boa.types.Issues.Issue.Builder issueBuilder = boa.types.Issues.Issue
							.newBuilder();
					issueRepoBuilder.addIssues(issue);
				}
				
			}
			project.addIssueRepositories(issueRepoBuilder);
			for(IssueRepository b:project.getIssueRepositoriesList()){
				//system.out.println("nmumber of issues:"+b.getIssuesCount());
			}
		}
		
	}

	@Override
	public void buildIssue(boa.types.Toplevel.Project.Builder pr, String details) {
		// TODO Auto-generated method stub
	}


}
