package boa.debugger.value;

import boa.types.BoaType;

/**
 * @author nmtiwari
 *
 */
public class BoolVal extends BoaType implements Value {
	private boolean _val;

	public BoolVal(boolean v) {
		_val = v;
	}

	public boolean v() {
		return _val;
	}

	@Override
	public boolean equals(Value v) {
		if (v instanceof BoolVal) {
			return ((BoolVal) v).v() == this.v();
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
		return this._val + "";
	}

}
