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

}
