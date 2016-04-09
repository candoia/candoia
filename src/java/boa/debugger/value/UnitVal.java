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

}
