package boa.debugger.value.aggregators;

import boa.aggregators.Aggregator;
import boa.aggregators.FinishedException;
import boa.aggregators.IntSumAggregator;
import boa.debugger.value.UnitVal;
import boa.debugger.value.Value;
import boa.io.EmitKey;

import java.io.IOException;
import java.util.HashMap;

public class IntSumAggregatorVal extends AggregatorVal {

	public IntSumAggregatorVal() {
		this.aggregators = new HashMap<>();
	}

	public void finish() {
		for (Aggregator aggregator : this.aggregators.values()) {
			try {
				aggregator.finish();
			} catch (IOException e) {
				System.out.println("IOException has been thrown from IntSumAggregatorVal");
			} catch (InterruptedException e) {
				System.out.println("InterruptedException has been thrown from IntSumAggregatorVal");
			}
		}
	}

	@Override
	public Value aggregate(String weight, String key, String value, String name) {
		Aggregator aggregator = (Aggregator) aggregators.get(key);
		if (aggregator == null) {
			aggregator = new IntSumAggregator();
			aggregators.put(key, aggregator);
			aggregator.start(new EmitKey(key, name, 0));
		}
		try {
			aggregator.aggregate(value, null);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (FinishedException e) {
			e.printStackTrace();
		}
		return UnitVal.v;
	}

	@Override
	public void aggregate(String data, String metadata) throws IOException, InterruptedException, FinishedException {

	}

	@Override
	public Object get() {
		return this.aggregators;
	}
}
