package boa.debugger.value;

import java.util.HashMap;

import boa.compiler.ast.Identifier;
import boa.compiler.ast.statements.Block;
import boa.compiler.ast.statements.VisitStatement;
import boa.debugger.Env;
import boa.debugger.Env.ExtendEnv;
import boa.debugger.Evaluator;
import boa.runtime.BoaAbstractVisitor;
import boa.types.Ast.ASTRoot;
import boa.types.Ast.Comment;
import boa.types.Ast.Declaration;
import boa.types.Ast.Expression;
import boa.types.Ast.Method;
import boa.types.Ast.Modifier;
import boa.types.Ast.Namespace;
import boa.types.Ast.Statement;
import boa.types.Ast.Type;
import boa.types.Ast.Variable;
import boa.types.Code.CodeRepository;
import boa.types.Code.Revision;
import boa.types.Diff.ChangedFile;
import boa.types.Shared.Person;
import boa.types.Toplevel.Project;

public class VisitorVal extends BoaAbstractVisitor implements Value {
	private Block _body;
	private Env<Value> _env;
	private Evaluator _eval;
	private boolean hasWildCardBefore;
	private boolean hasWildCardAfter;
	private HashMap<String, VisitStatement> _toBeExecutedBefore;
	private HashMap<String, VisitStatement> _toBeExecutedAfter;

	public VisitorVal(Block body, Env<Value> env, Evaluator eval) {
		this._body = body;
		this._toBeExecutedBefore = new HashMap<>();
		this._toBeExecutedAfter = new HashMap<>();
		this._env = env;
		this._eval = eval;
		VisitStatement visit = null;
		for (boa.compiler.ast.statements.Statement v : _body.getStatements()) {
			visit = (VisitStatement) v;
			if (visit.isBefore()) {
				if (visit.hasWildcard()) { // wildcard
					hasWildCardBefore = true;
					_toBeExecutedBefore.put("wildcard", visit);
				} else if (visit.hasComponent()) {
					_toBeExecutedBefore.put(visit.getComponent().getType().toString(), visit);
				} else {
					for (Identifier id : visit.getIdList()) {
						_toBeExecutedBefore.put(id.getToken(), visit);
					}
				}
			} else {
				if (visit.hasWildcard()) { // wildcard
					hasWildCardAfter = true;
					_toBeExecutedAfter.put("wildcard", visit);
				} else if (visit.hasComponent()) {
					_toBeExecutedAfter.put(visit.getComponent().getType().toString(), visit);
				} else {
					for (Identifier id : visit.getIdList()) {
						_toBeExecutedAfter.put(id.getToken(), visit);
					}
				}
			}
		}
		System.out.println("");
	}

	public boolean getWildCardBefore() {
		return this.hasWildCardBefore;
	}
	
	public boolean getWildCardAfter() {
		return this.hasWildCardAfter;
	}

	public Block getBody() {
		return this._body;
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

	@Override
	public long size() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Value compute(Value rhs, String op) {
		throw new UnsupportedOperationException();
	}

	public boolean isNodeBefore(String nodeName) {
		return this._toBeExecutedBefore.containsKey(nodeName);
	}

	@Override
	public Object get() {
		return this;
	}

	public void initialize(Object node) throws Exception {
		this._env = new ExtendEnv<Value>(this._env, boa.debugger.Evaluator.visitorVar, this);
		if (node instanceof Project) {
			visit((Project) node);
		} else if (node instanceof CodeRepository) {
			visit((CodeRepository) node);
		} else if (node instanceof ASTRoot) {
			visit((ASTRoot) node);
		} else if (node instanceof ChangedFile) {
			visit((ChangedFile) node);
		} else if (node instanceof Comment) {
			visit((Comment) node);
		} else if (node instanceof Declaration) {
			visit((Declaration) node);
		} else if (node instanceof Expression) {
			visit((Expression) node);
		} else if (node instanceof Method) {
			visit((Method) node);
		} else if (node instanceof Modifier) {
			visit((Modifier) node);
		} else if (node instanceof Namespace) {
			visit((Namespace) node);
		} else if (node instanceof Person) {
			visit((Person) node);
		} else if (node instanceof Revision) {
			visit((Revision) node);
		} else if (node instanceof Statement) {
			visit((Statement) node);
		} else if (node instanceof Type) {
			visit((Type) node);
		} else if (node instanceof Variable) {
			visit((Variable) node);
		}
	}

	@Override
	protected boolean preVisit(final Project node) throws Exception {
		Value result = null;
		if (_toBeExecutedBefore.containsKey("Project")) {
			VisitStatement stmt = _toBeExecutedBefore.get("Project");
			if (stmt.hasComponent()) {
				_env = new ExtendEnv<Value>(_env, stmt.getComponent().getIdentifier().toString(), new AnyVal(node));
			}
			result = this._eval.visit(stmt, _env);
		} else {
			if (hasWildCardBefore) {
				VisitStatement stmt = _toBeExecutedBefore.get("wildcard");
				result = this._eval.visit(stmt, _env);
			}
		}
		if (result instanceof ReturnVal) {
			return false;
		}
		return true;
	}

	@Override
	protected boolean preVisit(final CodeRepository node) throws Exception {
		Value result = null;
		if (_toBeExecutedBefore.containsKey("CodeRepository")) {
			VisitStatement stmt = _toBeExecutedBefore.get("CodeRepository");
			if (stmt.hasComponent()) {
				_env = new ExtendEnv<Value>(_env, stmt.getComponent().getIdentifier().toString(), new AnyVal(node));
			}
			result = this._eval.visit(stmt, _env);
		} else {
			if (hasWildCardBefore) {
				VisitStatement stmt = _toBeExecutedBefore.get("wildcard");
				result = this._eval.visit(stmt, _env);
			}
		}
		if (result instanceof ReturnVal) {
			return false;
		}
		return true;
	}

	@Override
	protected boolean preVisit(final Revision node) throws Exception {
		Value result = null;
		if (_toBeExecutedBefore.containsKey("Revision")) {
			VisitStatement stmt = _toBeExecutedBefore.get("Revision");
			if (stmt.hasComponent()) {
				_env = new ExtendEnv<Value>(_env, stmt.getComponent().getIdentifier().toString(), new AnyVal(node));
			}
			result = this._eval.visit(stmt, _env);
		} else {
			if (hasWildCardBefore) {
				VisitStatement stmt = _toBeExecutedBefore.get("wildcard");
				result = this._eval.visit(stmt, _env);
			}
		}
		if (result instanceof ReturnVal) {
			return false;
		}
		return true;
	}

	@Override
	protected boolean preVisit(final ChangedFile node) throws Exception {
		Value result = null;
		if (_toBeExecutedBefore.containsKey("ChangedFile")) {
			VisitStatement stmt = _toBeExecutedBefore.get("ChangedFile");
			if (stmt.hasComponent()) {
				_env = new ExtendEnv<Value>(_env, stmt.getComponent().getIdentifier().toString(), new AnyVal(node));
			}
			result = this._eval.visit(stmt, _env);
		} else {
			if (hasWildCardBefore) {
				VisitStatement stmt = _toBeExecutedBefore.get("wildcard");
				result = this._eval.visit(stmt, _env);
			}
		}
		if (result instanceof ReturnVal) {
			return false;
		}
		return true;
	}

	@Override
	protected boolean preVisit(final Expression node) throws Exception {
		Value result = null;
		if (_toBeExecutedBefore.containsKey("Expression")) {
			VisitStatement stmt = _toBeExecutedBefore.get("Expression");
			if (stmt.hasComponent()) {
				_env = new ExtendEnv<Value>(_env, stmt.getComponent().getIdentifier().toString(), new AnyVal(node));
			}
			result = this._eval.visit(stmt, _env);
		} else {
			if (hasWildCardBefore) {
				VisitStatement stmt = _toBeExecutedBefore.get("wildcard");
				result = this._eval.visit(stmt, _env);
			}
		}
		if (result instanceof ReturnVal) {
			return false;
		}
		return true;
	}

	@Override
	protected boolean preVisit(final ASTRoot node) throws Exception {
		Value result = null;
		if (_toBeExecutedBefore.containsKey("ASTRoot")) {
			VisitStatement stmt = _toBeExecutedBefore.get("ASTRoot");
			if (stmt.hasComponent()) {
				_env = new ExtendEnv<Value>(_env, stmt.getComponent().getIdentifier().toString(), new AnyVal(node));
			}
			result = this._eval.visit(stmt, _env);
		} else {
			if (hasWildCardBefore) {
				VisitStatement stmt = _toBeExecutedBefore.get("wildcard");
				result = this._eval.visit(stmt, _env);
			}
		}
		if (result instanceof ReturnVal) {
			return false;
		}
		return true;
	}

	@Override
	protected boolean preVisit(final Namespace node) throws Exception {
		Value result = null;
		if (_toBeExecutedBefore.containsKey("Namespace")) {
			VisitStatement stmt = _toBeExecutedBefore.get("Namespace");
			if (stmt.hasComponent()) {
				_env = new ExtendEnv<Value>(_env, stmt.getComponent().getIdentifier().toString(), new AnyVal(node));
			}
			result = this._eval.visit(stmt, _env);
		} else {
			if (hasWildCardBefore) {
				VisitStatement stmt = _toBeExecutedBefore.get("wildcard");
				result = this._eval.visit(stmt, _env);
			}
		}
		if (result instanceof ReturnVal) {
			return false;
		}
		return true;
	}

	@Override
	protected boolean preVisit(final Declaration node) throws Exception {
		Value result = null;
		if (_toBeExecutedBefore.containsKey("Declaration")) {
			VisitStatement stmt = _toBeExecutedBefore.get("Declaration");
			if (stmt.hasComponent()) {
				_env = new ExtendEnv<Value>(_env, stmt.getComponent().getIdentifier().toString(), new AnyVal(node));
			}
			result = this._eval.visit(stmt, _env);
		} else {
			if (hasWildCardBefore) {
				VisitStatement stmt = _toBeExecutedBefore.get("wildcard");
				result = this._eval.visit(stmt, _env);
			}
		}
		if (result instanceof ReturnVal) {
			return false;
		}
		return true;
	}

	@Override
	protected boolean preVisit(final Type node) throws Exception {
		Value result = null;
		if (_toBeExecutedBefore.containsKey("Type")) {
			VisitStatement stmt = _toBeExecutedBefore.get("Type");
			if (stmt.hasComponent()) {
				_env = new ExtendEnv<Value>(_env, stmt.getComponent().getIdentifier().toString(), new AnyVal(node));
			}
			result = this._eval.visit(stmt, _env);
		} else {
			if (hasWildCardBefore) {
				VisitStatement stmt = _toBeExecutedBefore.get("wildcard");
				result = this._eval.visit(stmt, _env);
			}
		}
		if (result instanceof ReturnVal) {
			return false;
		}
		return true;
	}

	@Override
	protected boolean preVisit(final Method node) throws Exception {
		Value result = null;
		if (_toBeExecutedBefore.containsKey("Method")) {
			VisitStatement stmt = _toBeExecutedBefore.get("Method");
			if (stmt.hasComponent()) {
				_env = new ExtendEnv<Value>(_env, stmt.getComponent().getIdentifier().toString(), new AnyVal(node));
			}
			result = this._eval.visit(stmt, _env);
		} else {
			if (hasWildCardBefore) {
				VisitStatement stmt = _toBeExecutedBefore.get("wildcard");
				result = this._eval.visit(stmt, _env);
			}
		}
		if (result instanceof ReturnVal) {
			return false;
		}
		return true;
	}

	@Override
	protected boolean preVisit(final Statement node) throws Exception {
		Value result = null;
		if (_toBeExecutedBefore.containsKey("Statement")) {
			VisitStatement stmt = _toBeExecutedBefore.get("Statement");
			if (stmt.hasComponent()) {
				_env = new ExtendEnv<Value>(_env, stmt.getComponent().getIdentifier().toString(), new AnyVal(node));
			}
			result = this._eval.visit(stmt, _env);
		} else {
			if (hasWildCardBefore) {
				VisitStatement stmt = _toBeExecutedBefore.get("wildcard");
				result = this._eval.visit(stmt, _env);
			}
		}
		if (result instanceof ReturnVal) {
			return false;
		}
		return true;
	}

	@Override
	protected boolean preVisit(final Variable node) throws Exception {
		Value result = null;
		if (_toBeExecutedBefore.containsKey("Variable")) {
			VisitStatement stmt = _toBeExecutedBefore.get("Variable");
			if (stmt.hasComponent()) {
				_env = new ExtendEnv<Value>(_env, stmt.getComponent().getIdentifier().toString(), new AnyVal(node));
			}
			result = this._eval.visit(stmt, _env);
		} else {
			if (hasWildCardBefore) {
				VisitStatement stmt = _toBeExecutedBefore.get("wildcard");
				result = this._eval.visit(stmt, _env);
			}
		}
		if (result instanceof ReturnVal) {
			return false;
		}
		return true;
	}

	@Override
	protected boolean preVisit(final Modifier node) throws Exception {
		Value result = null;
		if (_toBeExecutedBefore.containsKey("Modifier")) {
			VisitStatement stmt = _toBeExecutedBefore.get("Modifier");
			if (stmt.hasComponent()) {
				_env = new ExtendEnv<Value>(_env, stmt.getComponent().getIdentifier().toString(), new AnyVal(node));
			}
			result = this._eval.visit(stmt, _env);
		} else {
			if (hasWildCardBefore) {
				VisitStatement stmt = _toBeExecutedBefore.get("wildcard");
				result = this._eval.visit(stmt, _env);
			}
		}
		if (result instanceof ReturnVal) {
			return false;
		}
		return true;
	}

	@Override
	protected boolean preVisit(final Comment node) throws Exception {
		Value result = null;
		if (_toBeExecutedBefore.containsKey("Comment")) {
			VisitStatement stmt = _toBeExecutedBefore.get("Comment");
			if (stmt.hasComponent()) {
				_env = new ExtendEnv<Value>(_env, stmt.getComponent().getIdentifier().toString(), new AnyVal(node));
			}
			result = this._eval.visit(stmt, _env);
		} else {
			if (hasWildCardBefore) {
				VisitStatement stmt = _toBeExecutedBefore.get("wildcard");
				result = this._eval.visit(stmt, _env);
			}
		}
		if (result instanceof ReturnVal) {
			return false;
		}
		return true;
	}

	@Override
	protected boolean preVisit(final Person node) throws Exception {
		Value result = null;
		if (_toBeExecutedBefore.containsKey("Person")) {
			VisitStatement stmt = _toBeExecutedBefore.get("Person");
			if (stmt.hasComponent()) {
				_env = new ExtendEnv<Value>(_env, stmt.getComponent().getIdentifier().toString(), new AnyVal(node));
			}
			result = this._eval.visit(stmt, _env);
		} else {
			if (hasWildCardBefore) {
				VisitStatement stmt = _toBeExecutedBefore.get("wildcard");
				result = this._eval.visit(stmt, _env);
			}
		}
		if (result instanceof ReturnVal) {
			return false;
		}
		return true;
	}

	
	@Override
	protected void postVisit(final Project node) throws Exception {
		if (_toBeExecutedAfter.containsKey("Project")) {
			VisitStatement stmt = _toBeExecutedAfter.get("Project");
			if (stmt.hasComponent()) {
				_env = new ExtendEnv<Value>(_env, stmt.getComponent().getIdentifier().toString(), new AnyVal(node));
			}
			this._eval.visit(stmt, _env);
		} else {
			if (hasWildCardAfter) {
				VisitStatement stmt = _toBeExecutedAfter.get("wildcard");
				this._eval.visit(stmt, _env);
			}
		}
	}
	
	@Override
	protected void postVisit(final CodeRepository node) throws Exception {
		if (_toBeExecutedAfter.containsKey("CodeRepository")) {
			VisitStatement stmt = _toBeExecutedAfter.get("CodeRepository");
			if (stmt.hasComponent()) {
				_env = new ExtendEnv<Value>(_env, stmt.getComponent().getIdentifier().toString(), new AnyVal(node));
			}
			this._eval.visit(stmt, _env);
		} else {
			if (hasWildCardAfter) {
				VisitStatement stmt = _toBeExecutedAfter.get("wildcard");
				this._eval.visit(stmt, _env);
			}
		}
	}
	
	@Override
	protected void postVisit(final Revision node) throws Exception {
		if (_toBeExecutedAfter.containsKey("Revision")) {
			VisitStatement stmt = _toBeExecutedAfter.get("Revision");
			if (stmt.hasComponent()) {
				_env = new ExtendEnv<Value>(_env, stmt.getComponent().getIdentifier().toString(), new AnyVal(node));
			}
			this._eval.visit(stmt, _env);
		} else {
			if (hasWildCardAfter) {
				VisitStatement stmt = _toBeExecutedAfter.get("wildcard");
				this._eval.visit(stmt, _env);
			}
		}
	}
	
	@Override
	protected void postVisit(final ChangedFile node) throws Exception {
		if (_toBeExecutedAfter.containsKey("ChangedFile")) {
			VisitStatement stmt = _toBeExecutedAfter.get("ChangedFile");
			if (stmt.hasComponent()) {
				_env = new ExtendEnv<Value>(_env, stmt.getComponent().getIdentifier().toString(), new AnyVal(node));
			}
			this._eval.visit(stmt, _env);
		} else {
			if (hasWildCardAfter) {
				VisitStatement stmt = _toBeExecutedAfter.get("wildcard");
				this._eval.visit(stmt, _env);
			}
		}
	}
	
	@Override
	protected void postVisit(final ASTRoot node) throws Exception {
		if (_toBeExecutedAfter.containsKey("ASTRoot")) {
			VisitStatement stmt = _toBeExecutedAfter.get("ASTRoot");
			if (stmt.hasComponent()) {
				_env = new ExtendEnv<Value>(_env, stmt.getComponent().getIdentifier().toString(), new AnyVal(node));
			}
			this._eval.visit(stmt, _env);
		} else {
			if (hasWildCardAfter) {
				VisitStatement stmt = _toBeExecutedAfter.get("wildcard");
				this._eval.visit(stmt, _env);
			}
		}
	}
	
	@Override
	protected void postVisit(final Namespace node) throws Exception {
		if (_toBeExecutedAfter.containsKey("Namespace")) {
			VisitStatement stmt = _toBeExecutedAfter.get("Namespace");
			if (stmt.hasComponent()) {
				_env = new ExtendEnv<Value>(_env, stmt.getComponent().getIdentifier().toString(), new AnyVal(node));
			}
			this._eval.visit(stmt, _env);
		} else {
			if (hasWildCardAfter) {
				VisitStatement stmt = _toBeExecutedAfter.get("wildcard");
				this._eval.visit(stmt, _env);
			}
		}
	}
	
	@Override
	protected void postVisit(final Declaration node) throws Exception {
		if (_toBeExecutedAfter.containsKey("Declaration")) {
			VisitStatement stmt = _toBeExecutedAfter.get("Declaration");
			if (stmt.hasComponent()) {
				_env = new ExtendEnv<Value>(_env, stmt.getComponent().getIdentifier().toString(), new AnyVal(node));
			}
			this._eval.visit(stmt, _env);
		} else {
			if (hasWildCardAfter) {
				VisitStatement stmt = _toBeExecutedAfter.get("wildcard");
				this._eval.visit(stmt, _env);
			}
		}
	}
	
	@Override
	protected void postVisit(final Expression node) throws Exception {
		if (_toBeExecutedAfter.containsKey("Expression")) {
			VisitStatement stmt = _toBeExecutedAfter.get("Expression");
			if (stmt.hasComponent()) {
				_env = new ExtendEnv<Value>(_env, stmt.getComponent().getIdentifier().toString(), new AnyVal(node));
			}
			this._eval.visit(stmt, _env);
		} else {
			if (hasWildCardAfter) {
				VisitStatement stmt = _toBeExecutedAfter.get("wildcard");
				this._eval.visit(stmt, _env);
			}
		}
	}
	
	@Override
	protected void postVisit(final Method node) throws Exception {
		if (_toBeExecutedAfter.containsKey("Method")) {
			VisitStatement stmt = _toBeExecutedAfter.get("Method");
			if (stmt.hasComponent()) {
				_env = new ExtendEnv<Value>(_env, stmt.getComponent().getIdentifier().toString(), new AnyVal(node));
			}
			this._eval.visit(stmt, _env);
		} else {
			if (hasWildCardAfter) {
				VisitStatement stmt = _toBeExecutedAfter.get("wildcard");
				this._eval.visit(stmt, _env);
			}
		}
	}
	
	@Override
	protected void postVisit(final Modifier node) throws Exception {
		if (_toBeExecutedAfter.containsKey("Modifier")) {
			VisitStatement stmt = _toBeExecutedAfter.get("Modifier");
			if (stmt.hasComponent()) {
				_env = new ExtendEnv<Value>(_env, stmt.getComponent().getIdentifier().toString(), new AnyVal(node));
			}
			this._eval.visit(stmt, _env);
		} else {
			if (hasWildCardAfter) {
				VisitStatement stmt = _toBeExecutedAfter.get("wildcard");
				this._eval.visit(stmt, _env);
			}
		}
	}
	
	@Override
	protected void postVisit(final Statement node) throws Exception {
		if (_toBeExecutedAfter.containsKey("Statement")) {
			VisitStatement stmt = _toBeExecutedAfter.get("Statement");
			if (stmt.hasComponent()) {
				_env = new ExtendEnv<Value>(_env, stmt.getComponent().getIdentifier().toString(), new AnyVal(node));
			}
			this._eval.visit(stmt, _env);
		} else {
			if (hasWildCardAfter) {
				VisitStatement stmt = _toBeExecutedAfter.get("wildcard");
				this._eval.visit(stmt, _env);
			}
		}
	}
	
	@Override
	protected void postVisit(final Type node) throws Exception {
		if (_toBeExecutedAfter.containsKey("Type")) {
			VisitStatement stmt = _toBeExecutedAfter.get("Type");
			if (stmt.hasComponent()) {
				_env = new ExtendEnv<Value>(_env, stmt.getComponent().getIdentifier().toString(), new AnyVal(node));
			}
			this._eval.visit(stmt, _env);
		} else {
			if (hasWildCardAfter) {
				VisitStatement stmt = _toBeExecutedAfter.get("wildcard");
				this._eval.visit(stmt, _env);
			}
		}
	}
	
	@Override
	protected void postVisit(final Variable node) throws Exception {
		if (_toBeExecutedAfter.containsKey("Variable")) {
			VisitStatement stmt = _toBeExecutedAfter.get("Variable");
			if (stmt.hasComponent()) {
				_env = new ExtendEnv<Value>(_env, stmt.getComponent().getIdentifier().toString(), new AnyVal(node));
			}
			this._eval.visit(stmt, _env);
		} else {
			if (hasWildCardAfter) {
				VisitStatement stmt = _toBeExecutedAfter.get("wildcard");
				this._eval.visit(stmt, _env);
			}
		}
	}
	
	@Override
	protected void postVisit(final Person node) throws Exception {
		if (_toBeExecutedAfter.containsKey("Person")) {
			VisitStatement stmt = _toBeExecutedAfter.get("Person");
			if (stmt.hasComponent()) {
				_env = new ExtendEnv<Value>(_env, stmt.getComponent().getIdentifier().toString(), new AnyVal(node));
			}
			this._eval.visit(stmt, _env);
		} else {
			if (hasWildCardAfter) {
				VisitStatement stmt = _toBeExecutedAfter.get("wildcard");
				this._eval.visit(stmt, _env);
			}
		}
	}
	
	
	@Override
	protected void postVisit(final Comment node) throws Exception {
		if (_toBeExecutedAfter.containsKey("Comment")) {
			VisitStatement stmt = _toBeExecutedAfter.get("Comment");
			if (stmt.hasComponent()) {
				_env = new ExtendEnv<Value>(_env, stmt.getComponent().getIdentifier().toString(), new AnyVal(node));
			}
			this._eval.visit(stmt, _env);
		} else {
			if (hasWildCardAfter) {
				VisitStatement stmt = _toBeExecutedAfter.get("wildcard");
				this._eval.visit(stmt, _env);
			}
		}
	}
}
