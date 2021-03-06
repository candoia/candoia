package boa.datagen.bugForge.sfIssues;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.inject.Guice;
import com.google.inject.Inject;

import boa.datagen.bugForge.sfIssues.UrlBuilder.SVNAPI;
import boa.types.Issues.Issue;
import boa.types.Issues.Issue.Builder;
import boa.types.Issues.Issue.IssueKind;
import boa.types.Issues.Issue.State;
import boa.types.Shared.Person;
import br.ufpe.cin.groundhog.http.HttpModule;
import br.ufpe.cin.groundhog.http.Requests;

public class SearchSVN {
	private final Gson gson;
	private final Requests requests;
	private final UrlBuilder builder;

	@Inject
	public SearchSVN(Requests requests) {
		this.requests = requests;
		this.gson = new Gson();
		this.builder = Guice.createInjector(new HttpModule()).getInstance(UrlBuilder.class);
	}

	private void setTicketBuilder(List<Issue> issues, String searchUrl, boolean isBug) {
		try {

			String jsonString = requests.get(searchUrl);
			JsonObject ticketObject = new JsonParser().parse(jsonString).getAsJsonObject();

			JsonArray jsonArray = ticketObject.get("tickets").getAsJsonArray();

			for (JsonElement element : jsonArray) {
				String ticket_num = element.getAsJsonObject().get("ticket_num").getAsString();
				UrlBuilder builder = Guice.createInjector(new HttpModule()).getInstance(UrlBuilder.class);
				String bugUrl = builder.withParam(searchUrl).withSimpleParam("/", ticket_num).sbuild();
				String bugString = requests.get(bugUrl);
				JsonObject jObj = new JsonParser().parse(bugString).getAsJsonObject();

				JsonElement ticket = jObj.get("ticket").getAsJsonObject();
				Builder issueBuilder = Issue.newBuilder();
				int id = ticket.getAsJsonObject().get("ticket_num").getAsInt();
				int number = id;
				issueBuilder.setId(id);
				issueBuilder.setNumber(number);
				// issueBuilder.setKind(IssueKind.BUG);
				issueBuilder.setTitle(ticket.getAsJsonObject().get("summary").getAsString());
				issueBuilder.setBody(ticket.getAsJsonObject().get("description").getAsString());

				String status = ticket.getAsJsonObject().get("status").getAsString();
				String assigned_to_id = null;
				if (!ticket.getAsJsonObject().get("assigned_to_id").isJsonNull())
					assigned_to_id = ticket.getAsJsonObject().get("assigned_to_id").getAsString();

				if (status.equalsIgnoreCase("open")) {
					if (isBug)
						issueBuilder.setKind(IssueKind.BUG);
					else
						issueBuilder.setKind(IssueKind.ENHANCEMENT);
					if (assigned_to_id == null)
						issueBuilder.setState(State.UNCONFIRMED);
					else
						issueBuilder.setState(State.ASSIGNED);
				} else if (status.equalsIgnoreCase("open-accepted")) {
					issueBuilder.setKind(IssueKind.BUG);
					if (assigned_to_id == null)
						issueBuilder.setState(State.VERIFIED);
					else
						issueBuilder.setState(State.ASSIGNED);
				} else if (status.equalsIgnoreCase("closed")) {
					if (isBug)
						issueBuilder.setKind(IssueKind.UNKNOWN);
					else
						issueBuilder.setKind(IssueKind.ENHANCEMENT);
					issueBuilder.setState(State.RESOLVED);
				} else if (status.equalsIgnoreCase("closed-accepted")) {
					issueBuilder.setKind(IssueKind.BUG);
					issueBuilder.setState(State.RESOLVED);
				} else if (status.equalsIgnoreCase("closed-duplicate")) {
					issueBuilder.setKind(IssueKind.DUPLICATE);
					issueBuilder.setState(State.RESOLVED);
				} else if (status.equalsIgnoreCase("closed-fixed")) {
					issueBuilder.setKind(IssueKind.BUG);
					issueBuilder.setState(State.RESOLVED);
				} else if (status.equalsIgnoreCase("closed-invalid")) {
					issueBuilder.setKind(IssueKind.INVALID);
					issueBuilder.setState(State.RESOLVED);
				} else if (status.equalsIgnoreCase("closed-out-of-date")) {
					issueBuilder.setKind(IssueKind.INVALID);
					issueBuilder.setState(State.RESOLVED);
				} else if (status.equalsIgnoreCase("closed-postponed")) {
					issueBuilder.setKind(IssueKind.WONTFIX);
					issueBuilder.setState(State.RESOLVED);
				} else if (status.equalsIgnoreCase("closed-wont-fix")) {
					issueBuilder.setKind(IssueKind.WONTFIX);
					issueBuilder.setState(State.RESOLVED);
				} else if (status.equalsIgnoreCase("closed-works-for-me")) {
					issueBuilder.setKind(IssueKind.WORKSFORME);
					issueBuilder.setState(State.RESOLVED);
				} else {
					issueBuilder.setKind(IssueKind.UNKNOWN);
					issueBuilder.setState(State.UNCONFIRMED);
				}

				Person.Builder personBuilder;
				String assigned_to = null;
				if (!ticket.getAsJsonObject().get("assigned_to").isJsonNull())
					assigned_to = ticket.getAsJsonObject().get("assigned_to").getAsString();
				if (assigned_to != null) {
					personBuilder = Person.newBuilder();
					UrlBuilder personUrlBuilder = Guice.createInjector(new HttpModule()).getInstance(UrlBuilder.class);
					String personUrl = personUrlBuilder.uses(SVNAPI.USER).withSimpleParam("/", assigned_to)
							.withParam("/profile").sbuild();

					String personString = requests.get(personUrl);
					JsonObject personObject = null;
					try {
						personObject = new JsonParser().parse(personString).getAsJsonObject();
					} catch (com.google.gson.JsonSyntaxException ex) {

					}

					if (personObject != null) {
						personBuilder.setRealName(personObject.getAsJsonObject().get("name").getAsString());
						String username = personObject.getAsJsonObject()

								.get("username").getAsString();
						personBuilder.setUsername(username);
						String email = username + "@sf.net";
						personBuilder.setEmail(email);

						issueBuilder.setAssignee(personBuilder.build());

					} else {
						personBuilder.setRealName("anonymous");
						String username = "anonymous";
						personBuilder.setUsername(username);
						String email = username + "@sf.net";
						personBuilder.setEmail(email);

						issueBuilder.setAssignee(personBuilder.build());

					}

					if (status.contains("closed")) {
						// closed by the assigned user
						issueBuilder.setClosedBy(personBuilder.build());
					}
				}

				String datePattern = "yyyy-MM-dd";// HH:mm:ss.SSSXXX";
				SimpleDateFormat dateFormater = new SimpleDateFormat(datePattern);
				try {
					String dateString = ticket.getAsJsonObject().get("created_date").getAsString();
					String subs[] = dateString.split(" ");
					if (dateString != null) {
						Date date = dateFormater.parse(subs[0]);
						issueBuilder.setCreatedAt(date.getTime());
					}
					dateString = ticket.getAsJsonObject().get("mod_date").getAsString();
					subs = dateString.split(" ");
					if (dateString != null) {
						Date date = dateFormater.parse(subs[0]);
						issueBuilder.setUpdatedAt(date.getTime());
						issueBuilder.setClosedAt(date.getTime());
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				String milestone = null;
				if (!ticket.getAsJsonObject().get("custom_fields").isJsonNull()
						&& ticket.getAsJsonObject().get("custom_fields").getAsJsonObject().has("_milestone"))
					try {
						JsonObject obj = ticket.getAsJsonObject().get("custom_fields").getAsJsonObject();
						if (obj.has("_milestone")) {
							JsonElement o = obj.get("_milestone");
							milestone = o.toString();
						}

						else
							milestone = "mile stone not found";

						if (milestone != null) {
							boa.types.Issues.Milestone.Builder milestoneBuilder = boa.types.Issues.Milestone
									.newBuilder();
							milestoneBuilder.setId(id);
							milestoneBuilder.setNumber(number);
							milestoneBuilder.setTitle(milestone);
							issueBuilder.setMilestone(milestoneBuilder.build());
						}

					} catch (NullPointerException ex) {

					}
				issues.add(issueBuilder.build());
			}

		} catch (com.google.gson.JsonSyntaxException e) {

		}
	}

	public List<Issue> storeTickets(String project) {
		List<Issue> issues = new ArrayList<>();
		String searchUrl = builder.uses(SVNAPI.ROOT).withSimpleParam("/", project).withParam("/bugs").sbuild();
		setTicketBuilder(issues, searchUrl, true);
		UrlBuilder frBuilder = Guice.createInjector(new HttpModule()).getInstance(UrlBuilder.class);
		String frUrl = frBuilder.uses(SVNAPI.ROOT).withSimpleParam("/", project).withParam("/feature-requests")
				.sbuild();
		setTicketBuilder(issues, frUrl, false);
		UrlBuilder srBuilder = Guice.createInjector(new HttpModule()).getInstance(UrlBuilder.class);
		String srUrl = srBuilder.uses(SVNAPI.ROOT).withSimpleParam("/", project).withParam("/support-requests")
				.sbuild();
		setTicketBuilder(issues, srUrl, false);
		return issues;
	}

}
