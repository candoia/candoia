package boa.debugger.value;

import boa.types.BoaType;

public class PairVal extends BoaType implements Value {
	protected Value _fst;
	protected Value _snd;

	public PairVal(Value fst, Value snd) {
		_fst = fst;
		_snd = snd;
	}

	public Value fst() {
		return _fst;
	}

	public Value snd() {
		return _snd;
	}

	@Override
	public boolean equals(Value v) {
		if (v instanceof PairVal) {
			PairVal pair = (PairVal) v;
			return (pair.fst().equals(this.fst())) & (pair.snd().equals(this.snd()));
		}
		return false;
	}

	@Override
	public boolean isLessThan(Value v) {
		if (v instanceof PairVal) {
			PairVal pair = (PairVal) v;
			return (pair.fst().isLessThan((this.fst()))) & (pair.snd().isLessThan((this.snd())));
		}
		return false;
	}

	@Override
	public boolean isLessThanOrEqualTo(Value v) {
		if (v instanceof PairVal) {
			PairVal pair = (PairVal) v;
			return (pair.fst().isLessThanOrEqualTo((this.fst()))) & (pair.snd().isLessThanOrEqualTo((this.snd())));
		}
		return false;
	}

	@Override
	public long size() {
		return 0;
	}

	@Override
	public String toString() {
		return "( " + _fst.toString() + ")";
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
			throw new UnsupportedOperationException();
		case "+":
			throw new UnsupportedOperationException();
		case "-":
			throw new UnsupportedOperationException();
		case "!":
			throw new UnsupportedOperationException();
		case "~":
			throw new UnsupportedOperationException();
		case "not":
			throw new UnsupportedOperationException();
		case "/":
			throw new UnsupportedOperationException();
		case "%":
			throw new UnsupportedOperationException();
		case "&":
			throw new UnsupportedOperationException();
		case "<<":
			throw new UnsupportedOperationException();
		case ">>":
			throw new UnsupportedOperationException();
		default:
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public Object get() {
		return this;
	}

	@Override
	public int hashCode(){
		int fstHash = this.fst().hashCode();
		if(this.snd() == null){
			return fstHash;
		}
		int sndHash = this.snd().hashCode();
		return  31 * fstHash + sndHash;
	}

}
