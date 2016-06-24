package boa.debugger.value;

import boa.types.BoaType;

/**
 * @author nmtiwari
 *
 */
public class ReturnVal extends BoaType implements Value {
	private Value val;
	
	public ReturnVal(Value val) {
		this.val = val;
	}

	public Value getVal() {
		return val;
	}

	public void setVal(Value val) {
		this.val = val;
	}

	public String tostring() {
		return "";
	}

	@Override
	public boolean equals(Value v) {
		if (v instanceof UnitVal) {
			return true;
		}
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

	@Override
	public String toString() {
		return "UnitType";
	}

	@Override
	public Value compute(Value rhs, String op) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object get() {
		return this.val;
	}

	@Override
	public int hashCode(){
		return this.get().hashCode();
	}
}
