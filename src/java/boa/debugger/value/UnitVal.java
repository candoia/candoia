package boa.debugger.value;

import boa.types.BoaType;

/**
 * @author nmtiwari
 *
 */
public class UnitVal extends BoaType implements Value {
	public static final UnitVal v = new UnitVal();

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
		return UnitVal.v;
	}

	@Override
	public int hashCode() {
		return this.get().hashCode();
	}
}
