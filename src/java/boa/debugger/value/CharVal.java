package boa.debugger.value;

import boa.types.BoaType;

/**
 * @author nmtiwari
 *
 */
public class CharVal extends BoaType implements Value {
	char character;

	public CharVal(char character) {
		this.character = character;
	}
	
	public CharVal(String character) {
		this.character = character.charAt(0);
	}

	public char v() {
		return character;
	}

	public String tostring() {
		return "" + character;
	}

	@Override
	public boolean equals(Value v) {
		if (v instanceof CharVal) {
			return ((CharVal) v).v() == this.v();
		}
		return false;
	}

	@Override
	public boolean isLessThan(Value v) {
		if (v instanceof NumVal) {
			return ((NumVal) v).v() > this.v();
		}
		return false;
	}

	@Override
	public boolean isLessThanOrEqualTo(Value v) {
		if (v instanceof NumVal) {
			return ((NumVal) v).v() >= this.v();
		}
		return false;
	}

	@Override
	public long size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String toString() {
		return character + ""; 
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
		case "*":
			throw new UnsupportedOperationException();
		case "+":
			throw new UnsupportedOperationException();
		case "-":
			throw new UnsupportedOperationException();
		case "!":
			throw new UnsupportedOperationException();
		case "~":
			throw new UnsupportedOperationException();
		case "not":
			throw new UnsupportedOperationException();			
		case "/":
			throw new UnsupportedOperationException();
		case "%":
			throw new UnsupportedOperationException();
		case "&":
			throw new UnsupportedOperationException();
		case "<<":
			throw new UnsupportedOperationException();
		case ">>":
			throw new UnsupportedOperationException();	
		default:
			throw new UnsupportedOperationException();
		}
	}

}
