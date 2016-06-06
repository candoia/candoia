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
	private boolean hasWildCard;
	// 0 before, 1 after
	private HashMap<String, Integer> _toBeVisit;
	private HashMap<String, VisitStatement> _toBeExecuted;

	public VisitorVal(Block body, Env<Value> env, Evaluator eval) {
		this._body = body;
		this._toBeVisit = new HashMap<>();
		this._toBeExecuted = new HashMap<>();
		this.hasWildCard = false;
		this._env = env;
		this._eval = eval;
		VisitStatement visit = null;
		for (boa.compiler.ast.statements.Statement v : _body.getStatements()) {
			visit = (VisitStatement) v;
			if (visit.hasWildcard()) { // wildcard
				hasWildCard = true;
				_toBeExecuted.put("wildcard", visit);
			} else if (visit.isBefore()) { // before
				if (visit.hasComponent()) {
					_toBeVisit.put(visit.getComponent().getType().toString(), 0);
					_toBeExecuted.put(visit.getComponent().getType().toString(), visit);
				} else {
					for (Identifier id : visit.getIdList()) {
						_toBeVisit.put(id.getToken(), 0);
						_toBeExecuted.put(id.getToken(), visit);
					}
				}
			} else { // after
				if (visit.hasComponent()) {
					_toBeVisit.put(visit.getComponent().getType().toString(), 1);
					_toBeExecuted.put(visit.getComponent().getType().toString(), visit);
				} else {
					for (Identifier id : visit.getIdList()) {
						_toBeVisit.put(id.getToken(), 1);
						_toBeExecuted.put(id.getToken(), visit);
					}
				}
			}
		}
	}

	public boolean getWildCard() {
		return this.hasWildCard;
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
		return this._toBeVisit.get(nodeName) == 0 ? true : false;
	}

	@Override
	public Object get() {
		return this;
	}

	public void initialize(Object node) throws Exception {
		this._env = new ExtendEnv<Value>(this._env, boa.debugger.Evaluator.visitorVar, this);
		if (node instanceof Project) {
			visit((Project) node);
		}
	}

	@Override
	protected boolean preVisit(final Project node) throws Exception {
		Value result = null;
		if (_toBeVisit.containsKey("Project") && _toBeVisit.get("Project") == 0) {
			VisitStatement stmt = _toBeExecuted.get("Project");
			if (stmt.hasComponent()) {
				_env = new ExtendEnv<Value>(_env, stmt.getComponent().getIdentifier().toString(), new AnyVal(node));
			}
			result = this._eval.visit(stmt, _env);
		} else {
			if (hasWildCard) {
				VisitStatement stmt = _toBeExecuted.get("wildcard");
				result = this._eval.visit(stmt, _env);
			}
		}
		if(result instanceof ReturnVal){
			return false;
		}
		return true;
	}

	@Override
	protected boolean preVisit(final CodeRepository node) throws Exception {
		Value result = null;
		if (_toBeVisit.containsKey("CodeRepository") && _toBeVisit.get("CodeRepository") == 0) {
			VisitStatement stmt = _toBeExecuted.get("CodeRepository");
			if (stmt.hasComponent()) {
				_env = new ExtendEnv<Value>(_env, stmt.getComponent().getIdentifier().toString(), new AnyVal(node));
			}
			result = this._eval.visit(stmt, _env);
		} else {
			if (hasWildCard) {
				VisitStatement stmt = _toBeExecuted.get("wildcard");
				result = this._eval.visit(stmt, _env);
			}
		}
		if(result instanceof ReturnVal){
			return false;
		}
		return true;
	}
	
	@Override
	protected boolean preVisit(final Revision node) throws Exception {
		Value result = null;
		if (_toBeVisit.containsKey("Revision") && _toBeVisit.get("Revision") == 0) {
			VisitStatement stmt = _toBeExecuted.get("Revision");
			if (stmt.hasComponent()) {
				_env = new ExtendEnv<Value>(_env, stmt.getComponent().getIdentifier().toString(), new AnyVal(node));
			}
			result = this._eval.visit(stmt, _env);
		} else {
			if (hasWildCard) {
				VisitStatement stmt = _toBeExecuted.get("wildcard");
				result = this._eval.visit(stmt, _env);
			}
		}
		if(result instanceof ReturnVal){
			return false;
		}
		return true;
	}
	
	@Override
	protected boolean preVisit(final ChangedFile node) throws Exception {
		Value result = null;
		if (_toBeVisit.containsKey("ChangedFile") && _toBeVisit.get("ChangedFile") == 0) {
			VisitStatement stmt = _toBeExecuted.get("ChangedFile");
			if (stmt.hasComponent()) {
				_env = new ExtendEnv<Value>(_env, stmt.getComponent().getIdentifier().toString(), new AnyVal(node));
			}
			result = this._eval.visit(stmt, _env);
		} else {
			if (hasWildCard) {
				VisitStatement stmt = _toBeExecuted.get("wildcard");
				result = this._eval.visit(stmt, _env);
			}
		}
		if(result instanceof ReturnVal){
			return false;
		}
		return true;
	}
	
	@Override
	protected boolean preVisit(final Expression node) throws Exception {
		Value result = null;
		if (_toBeVisit.containsKey("Expression") && _toBeVisit.get("Expression") == 0) {
			VisitStatement stmt = _toBeExecuted.get("Expression");
			if (stmt.hasComponent()) {
				_env = new ExtendEnv<Value>(_env, stmt.getComponent().getIdentifier().toString(), new AnyVal(node));
			}
			result = this._eval.visit(stmt, _env);
		} else {
			if (hasWildCard) {
				VisitStatement stmt = _toBeExecuted.get("wildcard");
				result = this._eval.visit(stmt, _env);
			}
		}
		if(result instanceof ReturnVal){
			return false;
		}
		return true;
	}
	
	@Override
	protected boolean preVisit(final ASTRoot node) throws Exception {
		Value result = null;
		if (_toBeVisit.containsKey("ASTRoot") && _toBeVisit.get("ASTRoot") == 0) {
			VisitStatement stmt = _toBeExecuted.get("ASTRoot");
			if (stmt.hasComponent()) {
				_env = new ExtendEnv<Value>(_env, stmt.getComponent().getIdentifier().toString(), new AnyVal(node));
			}
			result = this._eval.visit(stmt, _env);
		} else {
			if (hasWildCard) {
				VisitStatement stmt = _toBeExecuted.get("wildcard");
				result = this._eval.visit(stmt, _env);
			}
		}
		if(result instanceof ReturnVal){
			return false;
		}
		return true;
	}
	
	
	@Override
	protected boolean preVisit(final Namespace node) throws Exception {
		Value result = null;
		if (_toBeVisit.containsKey("Namespace") && _toBeVisit.get("Namespace") == 0) {
			VisitStatement stmt = _toBeExecuted.get("Namespace");
			if (stmt.hasComponent()) {
				_env = new ExtendEnv<Value>(_env, stmt.getComponent().getIdentifier().toString(), new AnyVal(node));
			}
			result = this._eval.visit(stmt, _env);
		} else {
			if (hasWildCard) {
				VisitStatement stmt = _toBeExecuted.get("wildcard");
				result = this._eval.visit(stmt, _env);
			}
		}
		if(result instanceof ReturnVal){
			return false;
		}
		return true;
	}
	
	@Override
	protected boolean preVisit(final Declaration node) throws Exception {
		Value result = null;
		if (_toBeVisit.containsKey("Declaration") && _toBeVisit.get("Declaration") == 0) {
			VisitStatement stmt = _toBeExecuted.get("Declaration");
			if (stmt.hasComponent()) {
				_env = new ExtendEnv<Value>(_env, stmt.getComponent().getIdentifier().toString(), new AnyVal(node));
			}
			result = this._eval.visit(stmt, _env);
		} else {
			if (hasWildCard) {
				VisitStatement stmt = _toBeExecuted.get("wildcard");
				result = this._eval.visit(stmt, _env);
			}
		}
		if(result instanceof ReturnVal){
			return false;
		}
		return true;
	}
	
	@Override
	protected boolean preVisit(final Type node) throws Exception {
		Value result = null;
		if (_toBeVisit.containsKey("Type") && _toBeVisit.get("Type") == 0) {
			VisitStatement stmt = _toBeExecuted.get("Type");
			if (stmt.hasComponent()) {
				_env = new ExtendEnv<Value>(_env, stmt.getComponent().getIdentifier().toString(), new AnyVal(node));
			}
			result = this._eval.visit(stmt, _env);
		} else {
			if (hasWildCard) {
				VisitStatement stmt = _toBeExecuted.get("wildcard");
				result = this._eval.visit(stmt, _env);
			}
		}
		if(result instanceof ReturnVal){
			return false;
		}
		return true;
	}
	
	@Override
	protected boolean preVisit(final Method node) throws Exception {
		Value result = null;
		if (_toBeVisit.containsKey("Method") && _toBeVisit.get("Method") == 0) {
			VisitStatement stmt = _toBeExecuted.get("Method");
			if (stmt.hasComponent()) {
				_env = new ExtendEnv<Value>(_env, stmt.getComponent().getIdentifier().toString(), new AnyVal(node));
			}
			result = this._eval.visit(stmt, _env);
		} else {
			if (hasWildCard) {
				VisitStatement stmt = _toBeExecuted.get("wildcard");
				result = this._eval.visit(stmt, _env);
			}
		}
		if(result instanceof ReturnVal){
			return false;
		}
		return true;
	}
	
	@Override
	protected boolean preVisit(final Statement node) throws Exception {
		Value result = null;
		if (_toBeVisit.containsKey("ASTRoot") && _toBeVisit.get("ASTRoot") == 0) {
			VisitStatement stmt = _toBeExecuted.get("ASTRoot");
			if (stmt.hasComponent()) {
				_env = new ExtendEnv<Value>(_env, stmt.getComponent().getIdentifier().toString(), new AnyVal(node));
			}
			result = this._eval.visit(stmt, _env);
		} else {
			if (hasWildCard) {
				VisitStatement stmt = _toBeExecuted.get("wildcard");
				result = this._eval.visit(stmt, _env);
			}
		}
		if(result instanceof ReturnVal){
			return false;
		}
		return true;
	}
	
	@Override
	protected boolean preVisit(final Variable node) throws Exception {
		Value result = null;
		if (_toBeVisit.containsKey("Variable") && _toBeVisit.get("Variable") == 0) {
			VisitStatement stmt = _toBeExecuted.get("Variable");
			if (stmt.hasComponent()) {
				_env = new ExtendEnv<Value>(_env, stmt.getComponent().getIdentifier().toString(), new AnyVal(node));
			}
			result = this._eval.visit(stmt, _env);
		} else {
			if (hasWildCard) {
				VisitStatement stmt = _toBeExecuted.get("wildcard");
				result = this._eval.visit(stmt, _env);
			}
		}
		if(result instanceof ReturnVal){
			return false;
		}
		return true;
	}
	
	@Override
	protected boolean preVisit(final Modifier node) throws Exception {
		Value result = null;
		if (_toBeVisit.containsKey("Modifier") && _toBeVisit.get("Modifier") == 0) {
			VisitStatement stmt = _toBeExecuted.get("Modifier");
			if (stmt.hasComponent()) {
				_env = new ExtendEnv<Value>(_env, stmt.getComponent().getIdentifier().toString(), new AnyVal(node));
			}
			result = this._eval.visit(stmt, _env);
		} else {
			if (hasWildCard) {
				VisitStatement stmt = _toBeExecuted.get("wildcard");
				result = this._eval.visit(stmt, _env);
			}
		}
		if(result instanceof ReturnVal){
			return false;
		}
		return true;
	}
	
	@Override
	protected boolean preVisit(final Comment node) throws Exception {
		Value result = null;
		if (_toBeVisit.containsKey("Comment") && _toBeVisit.get("Comment") == 0) {
			VisitStatement stmt = _toBeExecuted.get("Comment");
			if (stmt.hasComponent()) {
				_env = new ExtendEnv<Value>(_env, stmt.getComponent().getIdentifier().toString(), new AnyVal(node));
			}
			result = this._eval.visit(stmt, _env);
		} else {
			if (hasWildCard) {
				VisitStatement stmt = _toBeExecuted.get("wildcard");
				result = this._eval.visit(stmt, _env);
			}
		}
		if(result instanceof ReturnVal){
			return false;
		}
		return true;
	}
	
	@Override
	protected boolean preVisit(final Person node) throws Exception {
		Value result = null;
		if (_toBeVisit.containsKey("Person") && _toBeVisit.get("Person") == 0) {
			VisitStatement stmt = _toBeExecuted.get("Person");
			if (stmt.hasComponent()) {
				_env = new ExtendEnv<Value>(_env, stmt.getComponent().getIdentifier().toString(), new AnyVal(node));
			}
			result = this._eval.visit(stmt, _env);
		} else {
			if (hasWildCard) {
				VisitStatement stmt = _toBeExecuted.get("wildcard");
				result = this._eval.visit(stmt, _env);
			}
		}
		if(result instanceof ReturnVal){
			return false;
		}
		return true;
	}
	
	
}
