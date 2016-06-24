package boa.debugger.value;

import java.util.ArrayList;

import boa.compiler.ast.expressions.Expression;
import boa.compiler.ast.statements.Block;
import boa.compiler.ast.types.FunctionType;

public class FunVal implements Value {

	private ArrayList<Expression> _formals;
	private Block _body;
	private Value returnType;
	private FunctionType type;

	public FunVal(ArrayList<Expression> formals, Block body, Value returnType, FunctionType type) {
		_formals = formals;
		_body = body;
		this.setReturnType(returnType);
		this.type = type;
	}

	public FunVal(Block body, FunctionType type) {
		_body = body;
		this.type = type;
	}

	public FunVal(ArrayList<Expression> formals, Block body, FunctionType type) {
		_formals = formals;
		_body = body;
		setReturnType(UnitVal.v);
		this.type = type;
	}

	public ArrayList<Expression> formals() {
		return _formals;
	}

	public FunctionType type() {
		return type;
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

	@Override
	public int hashCode() {
		int result = 31;
		int c = _body.hashCode();
		result = 37 * result + c;
		c = returnType.hashCode();
		result = 37 * result + c;
		c = type.hashCode();
		result = 37 * result + c;
		return result;
	}
}
