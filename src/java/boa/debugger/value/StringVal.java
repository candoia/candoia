package boa.debugger.value;

import boa.types.BoaType;

/**
 * @author nmtiwari
 *
 */
public class StringVal extends BoaType implements Value {
	private java.lang.String _val;

	public StringVal(String v) {
		_val = v;
	}

	public String v() {
		return _val;
	}

	public java.lang.String tostring() {
		return "" + _val;
	}

	@Override
	public boolean equals(Value v) {
		if (v instanceof StringVal) {
			return ((StringVal) v).v().equals(this.v());
		}
		return false;
	}

	@Override
	public boolean isLessThan(Value v) {
		if (v instanceof StringVal) {
			return ((StringVal) v).v().compareTo(this.v()) <= -1;
		}
		return false;
	}

	@Override
	public boolean isLessThanOrEqualTo(Value v) {
		if (v instanceof StringVal) {
			return (((StringVal) v).v().compareTo(this.v()) <= -1) || ((StringVal) v).v().equals(this.v());
		}
		return false;
	}

	@Override
	public long size() {
		// TODO Auto-generated method stub
		return _val.length();
	}

	@Override
	public String toString() {
		return _val;
	}

	@Override
	public Value compute(Value rhs, String op) {
		switch (op) {
		case "==":
			return new BoolVal(rhs.equals(rhs));
		case "!=":
			return new BoolVal(!rhs.equals(rhs));
		case "<":
			return new BoolVal(rhs.isLessThan(rhs));
		case "<=":
			return new BoolVal(rhs.isLessThanOrEqualTo(rhs));
		case ">":
			return new BoolVal(!rhs.isLessThanOrEqualTo(rhs));
		case ">=":
			return new BoolVal(rhs.isLessThan(rhs));
		case "&&":
			throw new UnsupportedOperationException();
		case "||":
			throw new UnsupportedOperationException();
		case "+":
			return new StringVal(this.v() + ((StringVal)rhs).v());
		default:
			throw new UnsupportedOperationException();
		}
	}
}
