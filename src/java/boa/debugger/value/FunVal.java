package boa.debugger.value;

import java.util.ArrayList;

import boa.compiler.ast.expressions.Expression;
import boa.compiler.ast.statements.Block;
import boa.debugger.Env;
import boa.debugger.Evaluator;

public class FunVal implements Value {

	private ArrayList<Expression> _formals;
	private Block _body;
	private Value returnType;

	public FunVal(ArrayList<Expression> formals, Block body, Value returnType) {
		_formals = formals;
		_body = body;
		this.setReturnType(returnType);
	}

	public FunVal(ArrayList<Expression> formals, Block body) {
		_formals = formals;
		_body = body;
		setReturnType(UnitVal.v);
	}
	
	public FunVal(Block body) {
		_body = body;
	}

	public ArrayList<Expression> formals() {
		return _formals;
	}

	public Block body() {
		return _body;
	}

	public String tostring() {
		String result = "(lambda ( ";
		return result + ")";
	}

	@Override
	public boolean equals(Value v) {
		if (v == this)
			return true;
		return false;
	}

	@Override
	public boolean isLessThan(Value v) {
		return false;
	}

	@Override
	public boolean isLessThanOrEqualTo(Value v) {
		return false;
	}

	@Override
	public long size() {
		return 0;
	}

	public Value getReturnType() {
		return returnType;
	}

	public void setReturnType(Value returnType) {
		this.returnType = returnType;
	}

	@Override
	public Value compute(Value rhs, String op) {
		throw new UnsupportedOperationException();
		// return null;
	}

	@Override
	public Object get() {
		return this;
	}

}
