package boa.debugger.value;

import boa.types.BoaType;

/**
 * @author nmtiwari
 *
 */
public class DoubleVal extends BoaType implements Value {
	double num;

	public DoubleVal(double result) {
		this.num = result;
	}

	public double v() {
		return num;
	}

	public String tostring() {
		return "" + num;
	}

	@Override
	public boolean equals(Value v) {
		if (v instanceof DoubleVal) {
			return ((NumVal) v).v() == this.v();
		}
		return false;
	}

	@Override
	public boolean isLessThan(Value v) {
		if (v instanceof DoubleVal) {
			return ((DoubleVal) v).v() > this.v();
		}
		return false;
	}

	@Override
	public boolean isLessThanOrEqualTo(Value v) {
		if (v instanceof DoubleVal) {
			return ((DoubleVal) v).v() >= this.v();
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
		return num + "";
	}

}
