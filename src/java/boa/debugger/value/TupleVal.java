package boa.debugger.value;

import boa.types.BoaType;

public abstract class TupleVal extends BoaType implements Value {
	public Value get(String search) {
		return UnitVal.v;
	}

	@Override
	public int hashCode() {
		return this.get().hashCode();
	}
}
