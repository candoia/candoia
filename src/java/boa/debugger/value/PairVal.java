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
		return "( " + _fst.toString() + "," + _snd.toString() + ")" ;
	}

}
