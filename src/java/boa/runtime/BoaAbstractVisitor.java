package boa.runtime;

import boa.functions.BoaAstIntrinsics;
import boa.types.Ast.*;
import boa.types.Code.CodeRepository;
import boa.types.Code.Revision;
import boa.types.Diff.ChangedFile;
import boa.types.Issues.*;
import boa.types.Shared.Person;
import boa.types.Toplevel.Project;

import java.util.List;

/**
 * Boa abstract AST visitor.
 *
 * The <code>visit()</code> methods first call <code>preVisit()</code> for the node.
 * If <code>preVisit()</code> returns <code>true</code>, then each of that node's children are visited and then <code>postVisit()</code> is called.
 *
 * By default, all <code>preVisit()</code> methods call {@link #defaultPreVisit()} and return <code>true</code>.
 * By default, all <code>postVisit()</code> methods call {@link #defaultPostVisit()}.
 *
 * @author rdyer
 */
public abstract class BoaAbstractVisitor {
	/**
	 * Initializes any visitor-specific data before starting a visit.
	 *
	 * @return itself, to allow method chaining
	 */
	public BoaAbstractVisitor initialize() {
		return this;
	}

	/**
	 * Provides a default action for pre-visiting nodes.
	 * Any <code>preVisit()</code> method that is not overridden calls this method.
	 *
	 * @return always returns true
	 */
	protected boolean defaultPreVisit() throws Exception {
		return true;
	}

	protected boolean preVisit(final Project node) throws Exception {
		return defaultPreVisit();
	}
	protected boolean preVisit(final PullRequest node) throws Exception {
		return defaultPreVisit();
	}
	protected boolean preVisit(final CodeRepository node) throws Exception {
		return defaultPreVisit();
	}
	protected boolean preVisit(final IssueRepository node) throws Exception {
		return defaultPreVisit();
	}
	protected boolean preVisit(final IssueAttachment node) throws Exception {
		return defaultPreVisit();
	}

	protected boolean preVisit(final Issue node) throws Exception {
		return defaultPreVisit();
	}
	protected boolean preVisit(final IssueComment node) throws Exception {
		return defaultPreVisit();
	}
	protected boolean preVisit(final Revision node) throws Exception {
		return defaultPreVisit();
	}
	protected boolean preVisit(final ChangedFile node) throws Exception {
		return defaultPreVisit();
	}
	protected boolean preVisit(final ASTRoot node) throws Exception {
		return defaultPreVisit();
	}
	protected boolean preVisit(final Namespace node) throws Exception {
		return defaultPreVisit();
	}
	protected boolean preVisit(final Declaration node) throws Exception {
		return defaultPreVisit();
	}
	protected boolean preVisit(final Type node) throws Exception {
		return defaultPreVisit();
	}
	protected boolean preVisit(final Method node) throws Exception {
		return defaultPreVisit();
	}
	protected boolean preVisit(final Variable node) throws Exception {
		return defaultPreVisit();
	}
	protected boolean preVisit(final Statement node) throws Exception {
		return defaultPreVisit();
	}
	protected boolean preVisit(final Expression node) throws Exception {
		return defaultPreVisit();
	}
	protected boolean preVisit(final Modifier node) throws Exception {
		return defaultPreVisit();
	}
	protected boolean preVisit(final Comment node) throws Exception {
		return defaultPreVisit();
	}
	protected boolean preVisit(final Person node) throws Exception {
		return defaultPreVisit();
	}
	protected boolean preVisit(final Milestone node) throws Exception {
		return defaultPreVisit();
	}

	/**
	 * Provides a default action for post-visiting nodes.
	 * Any <code>postVisit()</code> method that is not overridden calls this method.
	 */
	protected void defaultPostVisit() throws Exception { }

	protected void postVisit(final Project node) throws Exception {
		defaultPostVisit();
	}
	protected void postVisit(final CodeRepository node) throws Exception {
		defaultPostVisit();
	}
	protected void postVisit(final IssueRepository node) throws Exception {
		defaultPostVisit();
	}
	protected void postVisit(final IssueAttachment node) throws Exception {
		defaultPostVisit();
	}
	protected void postVisit(final Issue node) throws Exception {
		defaultPostVisit();
	}
	protected void postVisit(final IssueComment node) throws Exception {
		defaultPostVisit();
	}
	protected void postVisit(final Milestone node) throws Exception {
		defaultPostVisit();
	}
	protected void postVisit(final Revision node) throws Exception {
		defaultPostVisit();
	}
	protected void postVisit(final ChangedFile node) throws Exception {
		defaultPostVisit();
	}
	protected void postVisit(final ASTRoot node) throws Exception {
		defaultPostVisit();
	}
	protected void postVisit(final Namespace node) throws Exception {
		defaultPostVisit();
	}
	protected void postVisit(final Declaration node) throws Exception {
		defaultPostVisit();
	}
	protected void postVisit(final Type node) throws Exception {
		defaultPostVisit();
	}
	protected void postVisit(final Method node) throws Exception {
		defaultPostVisit();
	}
	protected void postVisit(final Variable node) throws Exception {
		defaultPostVisit();
	}
	protected void postVisit(final Statement node) throws Exception {
		defaultPostVisit();
	}
	protected void postVisit(final Expression node) throws Exception {
		defaultPostVisit();
	}
	protected void postVisit(final Modifier node) throws Exception {
		defaultPostVisit();
	}
	protected void postVisit(final Comment node) throws Exception {
		defaultPostVisit();
	}
	protected void postVisit(final PullRequest node) throws Exception {
		defaultPostVisit();
	}
	protected void postVisit(final Person node) throws Exception {
		defaultPostVisit();
	}

	public final void visit(final Project node) throws Exception {
		if (preVisit(node)) {
			final List<CodeRepository> reposList = node.getCodeRepositoriesList();
			final int reposSize = reposList.size();
			for (int i = 0; i < reposSize; i++)
				visit(reposList.get(i));

			final List<IssueRepository> issueList = node.getIssueRepositoriesList();
			final int issueSize = issueList.size();
			for (int i = 0; i < issueSize; i++)
				visit(issueList.get(i));

			final List<Person> devsList = node.getDevelopersList();
			final int devsSize = devsList.size();
			for (int i = 0; i < devsSize; i++)
				visit(devsList.get(i));

			final List<Person> maintsList = node.getMaintainersList();
			final int maintsSize = maintsList.size();
			for (int i = 0; i < maintsSize; i++)
				visit(maintsList.get(i));

			postVisit(node);
		}
	}
	public final void visit(final CodeRepository node) throws Exception {
		if (preVisit(node)) {
			final List<Revision> revisionsList = node.getRevisionsList();
			final int revisionsSize = revisionsList.size();
			for (int i = 0; i < revisionsSize; i++)
				visit(revisionsList.get(i));

			postVisit(node);
		}
	}


	public final void visit(final IssueRepository node) throws Exception {
		if (preVisit(node)) {
			final List<Issue> issueList = node.getIssuesList();
			final int issueSize = issueList.size();
			for (int i = 0; i < issueSize; i++)
				visit(issueList.get(i));
			postVisit(node);
		}
	}



	public final void visit(final Revision node) throws Exception {
		if (preVisit(node)) {
			final List<ChangedFile> filesList = node.getFilesList();
			final int filesSize = filesList.size();
			for (int i = 0; i < filesSize; i++)
				visit(filesList.get(i));

			if (node.hasAuthor())
				visit(node.getAuthor());

			if (node.hasCommitter())
				visit(node.getCommitter());

			postVisit(node);
		}
	}

	public final void visit(final Issue node) throws Exception {
		if (preVisit(node)) {
			final List<IssueAttachment> filesList = node.getAttachmentsList();
			final int filesSize = filesList.size();
			for (int i = 0; i < filesSize; i++)
				visit(filesList.get(i));

			final List<IssueComment> commentList = node.getCommentsList();
			final int commentsSize = commentList.size();
			for (int i = 0; i < commentsSize; i++)
				visit(commentList.get(i));
			if (node.hasAssignee())
				visit(node.getAssignee());

			if (node.hasCreatedBy())
				visit(node.getCreatedBy());

			if (node.hasClosedBy())
				visit(node.getClosedBy());

			postVisit(node);
		}
	}

	public final void visit(final IssueAttachment node) throws Exception {
		if (preVisit(node)) {

			postVisit(node);
		}
	}


	public final void visit(final ChangedFile node) throws Exception {
		if (preVisit(node)) {
			visit(BoaAstIntrinsics.getast(node));
			postVisit(node);
		}
	}
	public final void visit(final ASTRoot node) throws Exception {
		if (preVisit(node)) {
			final List<Namespace> namespacesList = node.getNamespacesList();
			final int namespacesSize = namespacesList.size();
			for (int i = 0; i < namespacesSize; i++)
				visit(namespacesList.get(i));

			postVisit(node);
		}
	}
	public final void visit(final Namespace node) throws Exception {
		if (preVisit(node)) {
			final List<Declaration> declarationsList = node.getDeclarationsList();
			final int declarationsSize = declarationsList.size();
			for (int i = 0; i < declarationsSize; i++)
				visit(declarationsList.get(i));

			final List<Modifier> modifiersList = node.getModifiersList();
			final int modifiersSize = modifiersList.size();
			for (int i = 0; i < modifiersSize; i++)
				visit(modifiersList.get(i));

			postVisit(node);
		}
	}
	public final void visit(final Declaration node) throws Exception {
		if (preVisit(node)) {
			final List<Modifier> modifiersList = node.getModifiersList();
			final int modifiersSize = modifiersList.size();
			for (int i = 0; i < modifiersSize; i++)
				visit(modifiersList.get(i));

			final List<Type> genericParamsList = node.getGenericParametersList();
			final int genericParamsSize = genericParamsList.size();
			for (int i = 0; i < genericParamsSize; i++)
				visit(genericParamsList.get(i));

			final List<Type> parentsList = node.getParentsList();
			final int parentsSize = parentsList.size();
			for (int i = 0; i < parentsSize; i++)
				visit(parentsList.get(i));

			final List<Method> methodsList = node.getMethodsList();
			final int methodsSize = methodsList.size();
			for (int i = 0; i < methodsSize; i++)
				visit(methodsList.get(i));

			final List<Variable> fieldsList = node.getFieldsList();
			final int fieldsSize = fieldsList.size();
			for (int i = 0; i < fieldsSize; i++)
				visit(fieldsList.get(i));

			final List<Declaration> nestedList = node.getNestedDeclarationsList();
			final int nestedSize = nestedList.size();
			for (int i = 0; i < nestedSize; i++)
				visit(nestedList.get(i));

			postVisit(node);
		}
	}
	public final void visit(final Type node) throws Exception {
		//system.out.println("Abstract visitor Type");
		if (preVisit(node)) {
			postVisit(node);
		}
	}
	public final void visit(final Method node) throws Exception {
		if (preVisit(node)) {
			visit(node.getReturnType());

			final List<Modifier> modifiersList = node.getModifiersList();
			final int modifiersSize = modifiersList.size();
			for (int i = 0; i < modifiersSize; i++)
				visit(modifiersList.get(i));

			final List<Type> genericParametersList = node.getGenericParametersList();
			final int genericParametersSize = genericParametersList.size();
			for (int i = 0; i < genericParametersSize; i++)
				visit(genericParametersList.get(i));

			final List<Variable> argumentsList = node.getArgumentsList();
			final int argumentsSize = argumentsList.size();
			for (int i = 0; i < argumentsSize; i++)
				visit(argumentsList.get(i));

			final List<Type> exceptionTypesList = node.getExceptionTypesList();
			final int exceptionTypesSize = exceptionTypesList.size();
			for (int i = 0; i < exceptionTypesSize; i++)
				visit(exceptionTypesList.get(i));

			final List<Statement> statementsList = node.getStatementsList();
			final int statementsSize = statementsList.size();
			for (int i = 0; i < statementsSize; i++)
				visit(statementsList.get(i));

			postVisit(node);
		}
	}
	public final void visit(final Variable node) throws Exception {
		if (preVisit(node)) {
			visit(node.getVariableType());

			final List<Modifier> modifiersList = node.getModifiersList();
			final int modifiersSize = modifiersList.size();
			for (int i = 0; i < modifiersSize; i++)
				visit(modifiersList.get(i));

			if (node.hasInitializer())
				visit(node.getInitializer());

			postVisit(node);
		}
	}
	public final void visit(final Statement node) throws Exception {
		if (preVisit(node)) {
			final List<Statement> statementsList = node.getStatementsList();
			final int statementsSize = statementsList.size();
			for (int i = 0; i < statementsSize; i++)
				visit(statementsList.get(i));

			final List<Expression> initsList = node.getInitializationsList();
			final int initsSize = initsList.size();
			for (int i = 0; i < initsSize; i++)
				visit(initsList.get(i));

			if (node.hasCondition())
				visit(node.getCondition());

			final List<Expression> updatesList = node.getUpdatesList();
			final int updatesSize = updatesList.size();
			for (int i = 0; i < updatesSize; i++)
				visit(updatesList.get(i));

			if (node.hasVariableDeclaration())
				visit(node.getVariableDeclaration());

			if (node.hasTypeDeclaration())
				visit(node.getTypeDeclaration());

			if (node.hasExpression())
				visit(node.getExpression());

			postVisit(node);
		}
	}
	public final void visit(final Expression node) throws Exception {
		if (preVisit(node)) {
			final List<Expression> expressionsList = node.getExpressionsList();
			final int expressionsSize = expressionsList.size();
			for (int i = 0; i < expressionsSize; i++)
				visit(expressionsList.get(i));

			final List<Variable> varDeclsList = node.getVariableDeclsList();
			final int varDeclsSize = varDeclsList.size();
			for (int i = 0; i < varDeclsSize; i++)
				visit(varDeclsList.get(i));

			if (node.hasNewType())
				visit(node.getNewType());

			final List<Type> genericParametersList = node.getGenericParametersList();
			final int genericParametersSize = genericParametersList.size();
			for (int i = 0; i < genericParametersSize; i++)
				visit(genericParametersList.get(i));

			final List<Expression> methodArgsList = node.getMethodArgsList();
			final int methodArgsSize = methodArgsList.size();
			for (int i = 0; i < methodArgsSize; i++)
				visit(methodArgsList.get(i));

			if (node.hasAnonDeclaration())
				visit(node.getAnonDeclaration());

			postVisit(node);
		}
	}
	public final void visit(final Modifier node) throws Exception {
		if (preVisit(node)) {
			final List<Expression> annotationValuesList = node.getAnnotationValuesList();
			final int annotationValuesSize = annotationValuesList.size();
			for (int i = 0; i < annotationValuesSize; i++)
				visit(annotationValuesList.get(i));

			postVisit(node);
		}
	}
	public final void visit(final Comment node) throws Exception {
		if (preVisit(node)) {
			postVisit(node);
		}
	}
	public final void visit(final IssueComment node) throws Exception {
		if (preVisit(node)) {
			if(node.hasUser())
				visit(node.getUser());
			postVisit(node);
		}
	}

	public final void visit(final Milestone node) throws Exception {
		if (preVisit(node)) {
			if(node.hasCreator())
				visit(node.getCreator());
			postVisit(node);
		}
	}
	public final void visit(final PullRequest node) throws Exception {
		if (preVisit(node)) {
			if(node.hasMergedBy())
				visit(node.getMergedBy());
			postVisit(node);
		}
	}

	public final void visit(final Person node) throws Exception {
		if (preVisit(node)) {
			postVisit(node);
		}
	}
}