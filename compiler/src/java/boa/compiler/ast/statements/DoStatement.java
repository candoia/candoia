package boa.compiler.ast.statements;

import boa.compiler.ast.Node;
import boa.compiler.ast.expressions.Expression;
import boa.compiler.visitors.AbstractVisitor;
import boa.compiler.visitors.AbstractVisitorNoArg;
import boa.parser.Token;

/**
 * 
 * @author rdyer
 */
public class DoStatement extends Statement {
	protected Expression condition;
	protected Block body;

	public Expression getCondition() {
		return condition;
	}

	public Block getBody() {
		return body;
	}

	public DoStatement(final Expression condition, final Statement s) {
		this(condition, Node.ensureBlock(s));
	}

	public DoStatement(final Expression condition, final Block body) {
		if (condition != null)
			condition.setParent(this);
		if (body != null)
			body.setParent(this);
		this.condition = condition;
		this.body = body;
	}

	/** {@inheritDoc} */
	@Override
	public <A> void accept(final AbstractVisitor<A> v, A arg) {
		v.visit(this, arg);
	}

	/** {@inheritDoc} */
	@Override
	public void accept(final AbstractVisitorNoArg v) {
		v.visit(this);
	}

	public DoStatement clone() {
		final DoStatement s = new DoStatement(condition.clone(), body.clone());
		copyFieldsTo(s);
		return s;
	}

	public DoStatement setPositions(final Token first, final Token last) {
		return (DoStatement)setPositions(first.beginLine, first.beginColumn, last.endLine, last.endColumn);
	}
}