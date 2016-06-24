package boa.debugger.value;

public class NumVal implements Value {
	long num;

	public NumVal(long num) {
		this.num = num;
	}

	public NumVal(double d) {
		// TODO Auto-generated constructor stub
	}

	public long v() {
		return num;
	}

	public String toString() {
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
			return ((NumVal) v).v() > this.v();
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
			return new BoolVal(this.equals(rhs));
		case "!=":
			return new BoolVal(!this.equals(rhs));
		case "<":
			return new BoolVal(this.isLessThan(rhs));
		case "<=":
			return new BoolVal(this.isLessThanOrEqualTo(rhs));
		case ">":
			return new BoolVal(rhs.isLessThanOrEqualTo(this));
		case ">=":
			return new BoolVal(rhs.isLessThan(this));
		case "&&":
			throw new UnsupportedOperationException();
		case "||":
			throw new UnsupportedOperationException();
		case "*": {
			if (rhs instanceof NumVal)
				return new NumVal(this.v() * ((NumVal) rhs).v());
			else if (rhs instanceof DoubleVal)
				return new NumVal(this.v() * ((DoubleVal) rhs).v());
		}
		case "+": {
			if (rhs instanceof NumVal)
				return new NumVal(this.v() + ((NumVal) rhs).v());
			else if (rhs instanceof DoubleVal)
				return new NumVal(this.v() + ((DoubleVal) rhs).v());
		}

		case "-": {
			if (rhs instanceof NumVal)
				return new NumVal(this.v() - ((NumVal) rhs).v());
			else if (rhs instanceof DoubleVal)
				return new NumVal(this.v() - ((DoubleVal) rhs).v());
		}
		case "!":
			throw new UnsupportedOperationException();
		case "~":
			throw new UnsupportedOperationException();
		case "not":
			throw new UnsupportedOperationException();
		case "/": {
			if (rhs instanceof NumVal)
				return new NumVal(this.v() / ((NumVal) rhs).v());
			else if (rhs instanceof DoubleVal)
				return new NumVal(this.v() / ((DoubleVal) rhs).v());
		}
		case "%":
			return new NumVal(this.v() % ((NumVal) rhs).v());
		case "&":
			return new NumVal(this.v() & ((NumVal) rhs).v());
		case "<<":
			return new NumVal(this.v() << ((NumVal) rhs).v());
		case ">>":
			return new NumVal(this.v() >> ((NumVal) rhs).v());
		case "++": {
			return new NumVal(this.v() + 1);
		}
		case "--": {
			return new NumVal(this.v() - 1);
		}
		default:
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public Object get() {
		return this.num;
	}

	@Override
	public int hashCode(){
		return this.get().hashCode();
	}
}
