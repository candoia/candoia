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
			return new NumVal(this.v() * ((NumVal)rhs).v());
		case "+":
			return new NumVal(this.v() + ((NumVal)rhs).v());
		case "-":
			return new NumVal(this.v() - ((NumVal)rhs).v());
		case "!":
			throw new UnsupportedOperationException();
		case "~":
			throw new UnsupportedOperationException();
		case "not":
			throw new UnsupportedOperationException();			
		case "/":
			return new NumVal(this.v() / ((NumVal)rhs).v());
		case "%":
			return new NumVal(this.v() % ((NumVal)rhs).v());
		case "&":
			return new NumVal(this.v() & ((NumVal)rhs).v());
		case "<<":
			return new NumVal(this.v() << ((NumVal)rhs).v());
		case ">>":
			return new NumVal(this.v() >> ((NumVal)rhs).v());
		default:
			throw new UnsupportedOperationException();
		}
	}

}
