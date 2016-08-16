package boa.debugger.value;

import boa.debugger.Evaluator;
import boa.types.Ast.*;
import boa.types.Code.CodeRepository;
import boa.types.Code.Revision;
import boa.types.Diff.ChangedFile;
import boa.types.Issues.Milestone;
import boa.types.Toplevel.Project;

/**
 * @author nmtiwari
 *
 */
public class AnyVal extends TupleVal implements Value {
	protected Object value;

	public AnyVal(Object any) {
		value = any;
	}

	public Object getObject() {
		return value;
	}

	public Value get(String search) {
		if (value instanceof Project) {
			Project proj = (Project) value;
			if (search.equals("programming_languages")) {
				ListVal<Value> programlangs = new ListVal<Value>();
				for (String s : proj.getProgrammingLanguagesList()) {
					programlangs.add(new StringVal(s));
				}
				return programlangs;
			} else if (search.equals("topics")) {
				ListVal<Value> topics = new ListVal<Value>();
				for (String s : proj.getTopicsList()) {
					topics.add(new StringVal(s));
				}
				return topics;
			} else if (search.equals("created_date")) {
				if (proj.hasCreatedDate()) {
					NumVal actualDate = new NumVal(proj.getCreatedDate());
					return actualDate;
				} else {
					NumVal actualDate = new NumVal(0000);
					return actualDate;
				}
			} else if (search.equals("licenses")) {
				ListVal<Value> licenses = new ListVal<Value>();
				for (String s : proj.getLicensesList()) {
					licenses.add(new StringVal(s));
				}
				return licenses;
			} else if (search.equals("operating_systems")) {
				ListVal<Value> os = new ListVal<Value>();
				for (String s : proj.getOperatingSystemsList()) {
					os.add(new StringVal(s));
				}
				return os;
			} else if (search.equals("databases")) {
				ListVal<Value> databases = new ListVal<Value>();
				for (String s : proj.getDatabasesList()) {
					databases.add(new StringVal(s));
				}
				return databases;
			} else if (search.equals("id")) {
				StringVal id = new StringVal(proj.getId());
				return id;
			} else if (search.equals("name")) {
				StringVal name = new StringVal(proj.getName());
				return name;
			} else if (search.equals("project_url")) {
				StringVal project_url = new StringVal(proj.getProjectUrl());
				return project_url;
			} else if (search.equals("code_repositories")) {
				ListVal<Value> rep = new ListVal<Value>();
				for (boa.types.Code.CodeRepository s : proj.getCodeRepositoriesList()) {
					rep.add(new AnyVal(s));
				}
				return rep;
			}
		} else if (value instanceof CodeRepository) {
			CodeRepository repository = (CodeRepository) value;
			if (search.equals("kind")) {
				if (repository.hasKind()) {
					int number = (repository.getKind()).getNumber();
					return new NumVal(number);
				}
				return new DynamicError("No kind field in this code repository");
			} else if (search.equals("url")) {
				if (repository.hasUrl()) {
					return new StringVal(repository.getUrl());
				}
				return new DynamicError("No Url field in this code repository");
			}

			else if (search.equals("revisions")) {
				ListVal<Value> revisions = new ListVal<Value>();
				for (Revision v : repository.getRevisionsList()) {
					revisions.add(new AnyVal(v));
				}

				return revisions;
			}
		}

		else if (value instanceof Revision) {
			Revision revision = (Revision) value;
			if (search.equals("commit_date")) {
				if (revision.hasCommitDate()) {
					return new NumVal(revision.getCommitDate());
				} else {
					return new DynamicError("No CommitDate was found for this revision");
				}
			}
			if (search.equals("id")) {
				if (revision.hasId()) {
					return new StringVal(revision.getId());
				} else {
					return new DynamicError("No CommitDate was found for this revision");
				}
			} else if (search.equals("files")) {
				ListVal<Value> list = new ListVal<Value>();
				for (ChangedFile c : revision.getFilesList()) {
					list.add(new AnyVal(c));
				}
				return list;
			} else if (search.equals("log")) {
				if (revision.hasLog()) {
					StringVal log = new StringVal(revision.getLog());
					return log;
				}
				return new DynamicError("No logs for this Object are found");
			} else if (search.equals("committer")) {
				if (revision.hasCommitter()) {
					return new AnyVal(revision.getCommitter());
				}
				return new DynamicError("No COmmiter for this revison found");
			} else if (search.equals("author")) {
				if (revision.hasAuthor()) {
					return new AnyVal(revision.getAuthor());
				}
				return new DynamicError("No Author for this revison found");
			}
		}

		else if (value instanceof Declaration) {
			Declaration decl = (Declaration) value;
			if (search.equals("kind")) {
				if (decl.hasKind()) {
					int number = (decl.getKind()).getNumber();
					return new NumVal(number);
				}
				return new DynamicError("No kind for this Declaration is found");
			} else if (search.equals("methods")) {
				ListVal<Value> list = new ListVal<Value>();
				for (boa.types.Ast.Method m : decl.getMethodsList()) {
					list.add(new AnyVal(m));
				}
				return list;
			} else if (search.equals("parents")) {
				ListVal<Value> list = new ListVal<Value>();
				for (boa.types.Ast.Type m : decl.getParentsList()) {
					list.add(new AnyVal(m));
				}
				return list;
			} else if (search.equals("comments")) {
				ListVal<Value> list = new ListVal<Value>();
				for (boa.types.Ast.Comment m : decl.getCommentsList()) {
					list.add(new AnyVal(m));
				}
				return list;
			}

			else if (search.equals("name")) {
				if (decl.hasName()) {
					StringVal name = new StringVal(decl.getName());
					return name;
				}
				return new DynamicError("NO name for this declaration found");
			}

			else if (search.equals("fields")) {
				ListVal<Value> list = new ListVal<Value>();
				for (boa.types.Ast.Variable m : decl.getFieldsList()) {
					list.add(new AnyVal(m));
				}
				return list;
			} else if (search.equals("modifiers")) {
				ListVal<Value> list = new ListVal<Value>();
				for (boa.types.Ast.Modifier m : decl.getModifiersList()) {
					list.add(new AnyVal(m));
				}
				return list;
			} else if (search.equals("generic_parameters")) {
				ListVal<Value> list = new ListVal<Value>();
				for (boa.types.Ast.Type m : decl.getGenericParametersList()) {
					list.add(new AnyVal(m));
				}
				return list;
			} else if (search.equals("nested_declarations")) {
				ListVal<Value> list = new ListVal<Value>();
				for (boa.types.Ast.Declaration m : decl.getNestedDeclarationsList()) {
					list.add(new AnyVal(m));
				}
				return list;
			}

		}

		else if (value instanceof ASTRoot) {
			ASTRoot ast = (ASTRoot) value;
			if (search.equals("imports")) {
				ListVal<Value> list = new ListVal<Value>();
				for (String v : ast.getImportsList()) {
					list.add(new StringVal(v));
				}
				return list;
			} else if (search.equals("namespaces")) {
				ListVal<Value> list = new ListVal<Value>();
				for (Namespace v : ast.getNamespacesList()) {
					list.add(new AnyVal(v));
				}
				return list;
			} else {
				return UnitVal.v;
			}
		}

		else if (value instanceof Namespace) {
			Namespace ast = (Namespace) value;
			if (search.equals("decalrations")) {
				ListVal<Value> list = new ListVal<Value>();
				for (Declaration v : ast.getDeclarationsList()) {
					list.add(new AnyVal(v));
				}
				return list;
			} else if (search.equals("modifiers")) {
				ListVal<Value> list = new ListVal<Value>();
				for (Modifier v : ast.getModifiersList()) {
					list.add(new AnyVal(v));
				}
				return list;
			} else if (search.equals("name")) {
				return new StringVal(ast.getName());
			} else {
				throw new UnsupportedOperationException();
			}
		}

		else if (value instanceof ChangedFile) {
			ChangedFile file = (ChangedFile) value;
			if (search.equals("name")) {
				if (file.hasName()) {
					return new StringVal(file.getName());
				} else {
					return new DynamicError("No name found for this changedfile");
				}

			} else if (search.equals("change")) {
				if (file.hasChange()) {
					int Number = (file.getChange()).getNumber();
					return new NumVal(Number);
				} else {

				}
			} else if (search.equals("kind")) {
				if (file.hasKind()) {
					return new NumVal((file.getKind()).getNumber());
				}
				return new DynamicError("No kind  for this changedfile");
			} else if (search.equals("key")) {
				if (file.hasKey()) {
					return new StringVal(file.getKey());
				} else {
					return new DynamicError("No key for this changed file found");
				}
			} else if (search.equals("comments")) {
				if (file.hasComments()) {
					return new AnyVal(file.getComments());
				} else {
					return new DynamicError("No comment for this changed file found");
				}
			} else if (search.equals("ast")) {
				if (file.hasAst()) {
					return new AnyVal(file.getAst());
				} else {
					return new DynamicError("No Ast for this changed file found");
				}
			} else if (search.equals("loc")) {
				if (file.hasLoc()) {
					return new NumVal(file.getLoc());
				} else {
					return new NumVal(0);
				}
			}

		}

		else if (value instanceof boa.types.Ast.Method) {
			boa.types.Ast.Method m = (boa.types.Ast.Method) value;
			if (search.equals("arguments")) {
				ListVal<Value> list = new ListVal<Value>();
				for (boa.types.Ast.Variable v : m.getArgumentsList()) {
					list.add(new AnyVal(v));
				}
				return list;
			}
			if (search.equals("comments")) {
				ListVal<Value> list = new ListVal<Value>();
				for (boa.types.Ast.Comment v : m.getCommentsList()) {
					list.add(new AnyVal(v));
				}
				return list;
			} else if (search.equals("name")) {
				if (m.hasName()) {
					return new StringVal(m.getName());
				}
				return new DynamicError("No name for this method found");
			} else if (search.equals("return_type")) {
				if (m.hasName()) {
					return new AnyVal(m.getReturnType());
				}
				return new DynamicError("No return type for this method found");
			} else if (search.equals("generic_parameters")) {
				ListVal<Value> list = new ListVal<Value>();
				for (boa.types.Ast.Type v : m.getGenericParametersList()) {
					list.add(new AnyVal(v));
				}
				return list;
			} else if (search.equals("modifiers")) {
				ListVal<Value> list = new ListVal<Value>();
				for (boa.types.Ast.Modifier v : m.getModifiersList()) {
					list.add(new AnyVal(v));
				}
				return list;
			}

			else if (search.equals("statements")) {
				ListVal<Value> list = new ListVal<Value>();
				for (boa.types.Ast.Statement v : m.getStatementsList()) {
					list.add(new AnyVal(v));
				}
				return list;
			}

			else if (search.equals("exception_types")) {
				ListVal<Value> list = new ListVal<Value>();
				for (boa.types.Ast.Type v : m.getExceptionTypesList()) {
					list.add(new AnyVal(v));
				}
				return list;
			}
		}

		else if (value instanceof boa.types.Ast.Variable) {
			boa.types.Ast.Variable m = (boa.types.Ast.Variable) value;
			if (search.equals("variable_type")) {
				if (m.hasVariableType()) {
					boa.types.Ast.Type list = m.getVariableType();
					return new AnyVal(list);
				}
				return new DynamicError("No variable type found for this variable");
			} else if (search.equals("modifiers")) {
				ListVal<Value> list = new ListVal<Value>();
				for (boa.types.Ast.Modifier v : m.getModifiersList()) {
					list.add(new AnyVal(v));
				}
				return list;
			} else if (search.equals("comments")) {
				ListVal<Value> list = new ListVal<Value>();
				for (boa.types.Ast.Comment v : m.getCommentsList()) {
					list.add(new AnyVal(v));
				}
				return list;
			} else if (search.equals("initializer")) {
				if (m.hasInitializer()) {
					return new AnyVal(m.getInitializer());
				}

				else
					return new DynamicError("There is no initializer for this variable");
			} else if (search.equals("name")) {
				return new StringVal(m.getName());
			}
		}

		else if (value instanceof boa.types.Ast.Type) {
			boa.types.Ast.Type m = (boa.types.Ast.Type) value;
			if (search.equals("name")) {
				if (m.hasName()) {
					return new StringVal(m.getName());
				}
				return new DynamicError("No name for this type found");
			} else if (search.equals("kind")) {
				if (m.hasKind()) {
					int number = (m.getKind()).getNumber();
					return new NumVal(number);
				}
				return new DynamicError("No kind field in this code repository");
			} else if (search.equals("id")) {
				if (m.hasId()) {
					return new StringVal(m.getId());
				} else {
					return new DynamicError("No CommitDate was found for this revision");
				}
			}
		}

		else if (value instanceof boa.types.Ast.Expression) {
			boa.types.Ast.Expression m = (boa.types.Ast.Expression) value;
			if (search.equals("expressions")) {
				ListVal<Value> list = new ListVal<Value>();
				for (boa.types.Ast.Expression v : m.getExpressionsList()) {
					list.add(new AnyVal(v));
				}
				return list;
			} else if (search.equals("kind")) {
				if (m.hasKind()) {
					return new NumVal((m.getKind()).getNumber());
				}
				return new DynamicError("There was no kind information for this expressions");
			} else if (search.equals("variable_declarations")) {
				ListVal<Value> list = new ListVal<Value>();
				for (boa.types.Ast.Variable v : m.getVariableDeclsList()) {
					list.add(new AnyVal(v));
				}
				return list;
			}

			else if (search.equals("generic")) {
				ListVal<Value> list = new ListVal<Value>();
				for (Type v : m.getGenericParametersList()) {
					list.add(new AnyVal(v));
				}
				return list;
			}

			else if (search.equals("method_args")) {
				ListVal<Value> list = new ListVal<Value>();
				for (Expression v : m.getMethodArgsList()) {
					list.add(new AnyVal(v));
				}
				return list;
			} else if (search.equals("variable")) {
				if (m.hasVariable()) {
					return new StringVal(m.getVariable());
				}
				return new DynamicError("No variable found for this Expression");
			}

			else if (search.equals("new_type")) {
				if (m.hasNewType()) {
					return new AnyVal(m.getNewType());
				}
				return new DynamicError("There was no new type information for this expressions");
			} else if (search.equals("method")) {
				if (m.hasMethod()) {
					return new StringVal(m.getMethod());
				}
				return new DynamicError("No Methd found for this Expression");
			} else if (search.equals("literal")) {
				if (m.hasLiteral()) {
					return new StringVal(m.getLiteral());
				}
				return new DynamicError("No literal found for this Expression");
			} else if (search.equals("annotation_declaration")) {
				if (m.hasAnonDeclaration()) {
					return new AnyVal(m.getAnonDeclaration());
				}
				return new DynamicError("There was no annotation_declaration information for this expressions");
			} else if (search.equals("annotation")) {
				if (m.hasAnnotation()) {
					return new AnyVal(m.getAnonDeclaration());
				}
				return new DynamicError("There was no annotation information for this expressions");
			} else if (search.equals("has_method")) {
				return new BoolVal(m.hasMethod());
			}

		}

		else if (value instanceof boa.types.Ast.Modifier) {
			boa.types.Ast.Modifier m = (boa.types.Ast.Modifier) value;
			if (search.equals("kind")) {
				if (m.hasKind()) {
					return new NumVal((m.getKind()).getNumber());
				}
				return new DynamicError("There is no field called kind in this modifier");
			} else if (search.equals("other")) {
				if (m.hasOther()) {
					return new StringVal(m.getOther());
				}
				return new DynamicError("There is no field called other in this modifier");
			} else if (search.equals("visibility")) {
				if (m.hasVisibility()) {
					return new StringVal(m.getOther());
				}
				return new DynamicError("There is no field for visibility field for MOdifier");
			} else if (search.equals("annotation_member")) {
				ListVal<Value> list = new ListVal<Value>();
				for (java.lang.String v : m.getAnnotationMembersList()) {
					list.add(new StringVal(v));
				}
				return list;
			} else if (search.equals("annotation_name")) {
			} else if (search.equals("annotation_value")) {
				ListVal<Value> list = new ListVal<Value>();
				for (boa.types.Ast.Expression v : m.getAnnotationValuesList()) {
					list.add(new AnyVal(v));
				}
				return list;
			}
		}

		else if (value instanceof boa.types.Shared.Person) {
			boa.types.Shared.Person p = (boa.types.Shared.Person) value;
			if (search.equals("username")) {
				if (p.hasUsername()) {
					return new StringVal(p.getUsername());
				}
				return new DynamicError("No username found for this commiter");

			}
			if (search.equals("realname")) {
				if (p.hasRealName()) {
					return new StringVal(p.getRealName());
				}
				return new DynamicError("No hasRealName found for this commiter");

			}
			if (search.equals("email")) {
				if (p.hasEmail()) {
					return new StringVal(p.getEmail());
				}
				return new DynamicError("No email found for this commiter");

			}
		} else if (value instanceof boa.types.Ast.Statement) {
			boa.types.Ast.Statement p = (boa.types.Ast.Statement) value;
			if (search.equals("kind")) {
				if (p.hasKind()) {
					return new NumVal((p.getKind()).getNumber());
				}
				return new DynamicError("No kind filed found for this statement");
			} else if (search.equals("statements")) {
				ListVal<Value> list = new ListVal<Value>();
				for (boa.types.Ast.Statement v : p.getStatementsList()) {
					list.add(new AnyVal(v));
				}
				return list;
			} else if (search.equals("expression")) {
				if (p.hasExpression()) {
					return new AnyVal(p.getExpression());
				}
				return new DynamicError("There is not field for expression in this statement");
			} else if (search.equals("condition")) {
				if (p.hasCondition()) {
					return new AnyVal(p.getExpression());
				}
				return new DynamicError("No field called condition found in this statement");
			} else if (search.equals("variable_declaration")) {
				if (p.hasVariableDeclaration()) {
					return new AnyVal(p.getVariableDeclaration());
				}
				return new DynamicError("No field called variable_declaration found in this statement");
			} else if (search.equals("type_declaration")) {
				if (p.hasTypeDeclaration()) {
					return new AnyVal(p.getTypeDeclaration());
				}
				return new DynamicError("No field called type_declaration found in this statement");
			} else if (search.equals("comments")) {
				ListVal<Value> list = new ListVal<Value>();
				for (boa.types.Ast.Comment m : p.getCommentsList()) {
					list.add(new AnyVal(m));
				}
				return list;
			} else if (search.equals("initialization")) {
				ListVal<Value> list = new ListVal<Value>();
				for (boa.types.Ast.Expression m : p.getInitializationsList()) {
					list.add(new AnyVal(m));
				}
				return list;
			}
		}

		else if (value instanceof boa.types.Issues.IssueRepository) {
			boa.types.Issues.IssueRepository p = (boa.types.Issues.IssueRepository) value;
			if (search.equals("url")) {
				if (p.hasUrl())
					return new StringVal(p.getUrl());
				else
					return new DynamicError("No url found for this issue");
			} else if (search.equals("issues")) {
				ListVal<Value> list = new ListVal<Value>();
				for (boa.types.Issues.Issue m : p.getIssuesList()) {
					list.add(new AnyVal(m));
				}
				return list;
			}

		}

		else if (value instanceof boa.types.Issues.IssueAttachment) {
			boa.types.Issues.IssueAttachment p = (boa.types.Issues.IssueAttachment) value;
			if (search.equals("url")) {
				if (p.hasUrl())
					return new StringVal(p.getUrl());
				else
					return new DynamicError("No url found for this issue");
			} else if (search.equals("type")) {
				if (p.hasType())
					return new StringVal(p.getType());
				else
					return new DynamicError("No type found for this issueAttachment");
			}

			else if (search.equals("description")) {
				if (p.hasDescription())
					return new StringVal(p.getDescription());
				else
					return new DynamicError("No description found for this issueAttachment");
			} else if (search.equals("filename")) {
				if (p.hasFilename())
					return new StringVal(p.getFilename());
				else
					return new DynamicError("No file name found for this issueAttachment");
			}

			else if (search.equals("content")) {
				if (p.hasContent())
					return new StringVal(p.getContent().toString());
				else
					return new DynamicError("No file name found for this issueAttachment");
			} else if (search.equals("date")) {
				if (p.hasDate())
					return new NumVal(p.getDate());
				else
					return new DynamicError("No date  found for this issueAttachment");
			}

		}

		else if (value instanceof boa.types.Issues.Issue) {
			boa.types.Issues.Issue p = (boa.types.Issues.Issue) value;
			if (search.equals("id")) {
				if (p.hasId())
					return new NumVal(p.getId());
				else
					return new DynamicError("No id found for this issue");
			} else if (search.equals("number")) {
				if (p.hasNumber())
					return new NumVal(p.getNumber());
				else
					return new DynamicError("No number found for this issue");
			} else if (search.equals("kind")) {
				int number = (p.getKind()).getNumber();
				return new NumVal(number);
			} else if (search.equals("state")) {
				int number = (p.getState()).getNumber();
				return new NumVal(number);
			} else if (search.equals("comments")) {
				ListVal<Value> list = new ListVal<Value>();
				for (boa.types.Issues.IssueComment m : p.getCommentsList()) {
					list.add(new AnyVal(m));
				}
				return list;
			} else if (search.equals("attachments")) {
				ListVal<Value> list = new ListVal<Value>();
				for (boa.types.Issues.IssueAttachment m : p.getAttachmentsList()) {
					list.add(new AnyVal(m));
				}
				return list;
			} else if (search.equals("title")) {
				if (p.hasTitle())
					return new StringVal(p.getTitle());
				else
					return new DynamicError("No title found for this issue");
			}

			else if (search.equals("body")) {
				if (p.hasBody())
					return new StringVal(p.getBody());
				else
					return new DynamicError("No body found for this issue");
			} else if (search.equals("assignee")) {
				if (p.hasAssignee())
					return new AnyVal(p.getAssignee());
				else
					return new DynamicError("No assignee found for this issue");
			} else if (search.equals("created_by")) {
				if (p.hasCreatedBy())
					return new AnyVal(p.getCreatedBy());
				else
					return new DynamicError("No created by found for this issue");
			} else if (search.equals("closed_by")) {
				if (p.hasClosedBy())
					return new AnyVal(p.getClosedBy());
				else
					return new DynamicError("No closed by found for this issue");

			} else if (search.equals("closed_at")) {
				try {
					return new NumVal(p.getCreatedAt());
				} catch (NullPointerException ex) {
					return new DynamicError("No created at found for this issue");
				}
			}

			else if (search.equals("updated_at")) {
				if (p.hasUpdatedAt())
					return new NumVal(p.getUpdatedAt());
				else
					return new DynamicError("No updated  at found for this issue");
			}

			else if (search.equals("created_at")) {
				try {
					return new NumVal(p.getCreatedAt());
				} catch (NullPointerException ex) {
					return new DynamicError("No created at found for this issue");
				}
			} else if (search.equals("pull_request")) {
				if (p.hasPullRequest())
					return new AnyVal(p.getPullRequest());
				else
					return new DynamicError("No pull request  found for this issue");
			} else if (search.equals("milestone")) {
				if (p.hasMilestone())
					return new AnyVal(p.getMilestone());
				else
					return new DynamicError("No milestone  found for this issue");
			}
		}

		else if (value instanceof boa.types.Issues.IssueComment) {
			boa.types.Issues.IssueComment p = (boa.types.Issues.IssueComment) value;
			if (search.equals("id")) {
				if (p.hasId())
					return new NumVal(p.getId());
				else
					return new DynamicError("No id found for this issue");
			} else if (search.equals("body")) {
				if (p.hasBody())
					return new StringVal(p.getBody());
				else
					return new DynamicError("No body found for this issue");
			} else if (search.equals("user")) {
				if (p.hasUser())
					return new AnyVal(p.getUser());
				else
					return new DynamicError("No assignee found for this issue");
			}

			else if (search.equals("updated_at")) {
				if (p.hasUpdatedAt())
					return new NumVal(p.getUpdatedAt());
				else
					return new DynamicError("No updated  at found for this issue");
			}

			else if (search.equals("created_at")) {
				if (p.hasCreatedAt())
					return new NumVal(p.getCreatedAt());
				else
					return new DynamicError("No created at found for this issue");
			} else if (search.equals("attachments")) {
				ListVal<Value> list = new ListVal<Value>();
				for (String m : p.getAttachmentsList()) {
					list.add(new StringVal(m));
				}
				return list;
			}
		}

		else if (value instanceof boa.types.Issues.PullRequest) {
			boa.types.Issues.PullRequest p = (boa.types.Issues.PullRequest) value;
			if (search.equals("id")) {
				if (p.hasId())
					return new NumVal(p.getId());
				else
					return new DynamicError("No id found for this issue");
			} else if (search.equals("merge_commit_sha")) {
				if (p.hasMergeCommitSha())
					return new StringVal(p.getMergeCommitSha());
				else
					return new DynamicError("No body found for this issue");
			} else if (search.equals("merged_by")) {
				if (p.hasMergedBy())
					return new AnyVal(p.getMergedBy());
				else
					return new DynamicError("No assignee found for this issue");
			}

			else if (search.equals("merged_at")) {
				if (p.hasMergedAt())
					return new NumVal(p.getMergedAt());
				else
					return new DynamicError("No updated  at found for this issue");
			} else if (search.equals("review_comments")) {
				if (p.hasReviewComments())
					return new NumVal(p.getReviewComments());
				else
					return new DynamicError("No updated  at found for this issue");
			} else if (search.equals("commits")) {
				if (p.hasCommits())
					return new NumVal(p.getCommits());
				else
					return new DynamicError("No updated  at found for this issue");
			} else if (search.equals("merged")) {
				if (p.hasMerged())
					return new BoolVal(p.getMerged());
				else
					return new DynamicError("No updated  at found for this issue");
			} else if (search.equals("mergeable")) {
				if (p.hasMergeable())
					return new BoolVal(p.getMergeable());
				else
					return new DynamicError("No updated  at found for this issue");
			} else if (search.equals("addition")) {
				if (p.hasAdditions())
					return new NumVal(p.getAdditions());
				else
					return new DynamicError("No updated  at found for this issue");
			} else if (search.equals("deletion")) {
				if (p.hasDeletions())
					return new NumVal(p.getDeletions());
				else
					return new DynamicError("No updated  at found for this issue");
			} else if (search.equals("changed_files")) {
				if (p.hasChangedFiles())
					return new NumVal(p.getChangedFiles());
				else
					return new DynamicError("No updated  at found for this issue");
			} else if (search.equals("mergeable_state")) {
				if (p.hasMergeableState())
					return new StringVal(p.getMergeableState());
				else
					return new DynamicError("No updated  at found for this issue");
			}
		}

		else if (value instanceof Milestone) {
			Milestone p = (Milestone) value;
			if (search.equals("id")) {
				if (p.hasId())
					return new NumVal(p.getId());
				else
					return new DynamicError("No id found for this issue");
			} else if (search.equals("number")) {
				if (p.hasNumber())
					return new NumVal(p.getNumber());
				else
					return new DynamicError("No number found for this issue");
			} else if (search.equals("state")) {
				return new StringVal(p.getState());
			} else if (search.equals("title")) {
				if (p.hasTitle())
					return new StringVal(p.getTitle());
				else
					return new DynamicError("No title found for this issue");
			}

			else if (search.equals("description")) {
				if (p.hasDescription())
					return new StringVal(p.getDescription());
				else
					return new DynamicError("No body found for this issue");
			} else if (search.equals("creator")) {
				if (p.hasCreator())
					return new AnyVal(p.getCreator());
				else
					return new DynamicError("No created by found for this issue");
			}

			else if (search.equals("due_on")) {
				if (p.hasDueOn())
					return new NumVal(p.getDueOn());
				else
					return new DynamicError("No closed at found for this issue");
			}

			else if (search.equals("updated_at")) {
				if (p.hasUpdatedAt())
					return new NumVal(p.getUpdatedAt());
				else
					return new DynamicError("No updated  at found for this issue");
			}

			else if (search.equals("created_at")) {
				if (p.hasCreatedAt())
					return new NumVal(p.getCreatedAt());
				else
					return new DynamicError("No created at found for this issue");
			} else if (search.equals("open_issues")) {
				if (p.hasOpenIssues())
					return new NumVal(p.getOpenIssues());
				else
					return new DynamicError("No number found for this issue");
			} else if (search.equals("closed_issues")) {
				if (p.hasClosedIssues())
					return new NumVal(p.getClosedIssues());
				else
					return new DynamicError("No number found for this issue");
			}

		}
		
		throw new UnsupportedOperationException("value: " + search.length() + " and " + value);
	}

	@Override
	public boolean equals(Value v) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isLessThan(Value v) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isLessThanOrEqualTo(Value v) {
		throw new UnsupportedOperationException();
	}

	public String toString() {
		return this.value.toString();
	}

	@Override
	public long size() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Value compute(Value rhs, String op) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object get() {
		return this.value;
	}

	@Override
	public int hashCode() {
		return this.get().hashCode();
	}
}
