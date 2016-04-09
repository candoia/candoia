package boa.debugger.value;

public class NumVal implements Value {
	long num;

	public NumVal(long num) {
		this.num = num;
	}

	public long v() {
		return num;
	}

	public String tostring() {
		return "" + num;
	}

	@Override
	public boolean equals(Value v) {
		if (v instanceof NumVal) {
			return ((NumVal) v).v() == this.v();
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
			if (boa.debugger.Evaluator.DEBUG)
				System.out.println("isLessThanOrEqualTo" + (((NumVal) v).v() >= this.v()));
			return ((NumVal) v).v() >= this.v();
		}
		throw new IllegalArgumentException("Compare operator has to have same types.");
	}

	@Override
	public long size() {
		return 0;
	}

}
