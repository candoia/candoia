package boa.debugger.value.aggregators;

import java.util.HashMap;

import boa.aggregators.Aggregator;
import boa.debugger.value.Value;
import boa.io.EmitKey;

/**
 * @author nmtiwari
 *
 */
public abstract class AggregatorVal extends Aggregator implements Value {
	protected EmitKey key;
	protected HashMap<String, Aggregator> aggregators;
	
	public abstract void finish();


	@Override
	public boolean equals(Value v) {
		return this == v;
	}

	@Override
	public boolean isLessThan(Value v) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isLessThanOrEqualTo(Value v) {
		throw new UnsupportedOperationException();
	}

	@Override
	public long size() {
		return 0;
	}

	@Override
	public String toString() {
		throw new UnsupportedOperationException();
	}


	@Override
	public Value compute(Value rhs, String op) {
		throw new UnsupportedOperationException();
	}

	public abstract Value aggregate(String weight, String key, String value, String name);

}
