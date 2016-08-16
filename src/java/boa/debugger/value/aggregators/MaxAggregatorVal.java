package boa.debugger.value.aggregators;

import java.io.IOException;
import java.util.HashMap;

import boa.aggregators.Aggregator;
import boa.aggregators.FinishedException;
import boa.aggregators.MaximumAggregator;
import boa.debugger.value.UnitVal;
import boa.debugger.value.Value;
import boa.io.EmitKey;

public class MaxAggregatorVal extends AggregatorVal {
	protected long max;

	public MaxAggregatorVal(long max) {
		this.max = max;
		this.aggregators = new HashMap<String, Aggregator>();
	}

	public MaxAggregatorVal(long max, String name) {
		this.max = max;
		key = new EmitKey("max", 0);
		this.aggregators = new HashMap<String, Aggregator>();
	}

	public void finish() {
		for (Aggregator aggregator : this.aggregators.values()) {
			try {
				aggregator.finish();
			} catch (IOException e) {
				System.out.println("IOException has been thrown from MaxAggregatorVal");
			} catch (InterruptedException e) {
				System.out.println("InterruptedException has been thrown from MaxAggregatorVal");
			}
		}
	}

	@Override
	public Value aggregate(String weight, String key, String value, String name) {
		Aggregator aggregator = (Aggregator) aggregators.get(key);
		if (aggregator == null) {
			aggregator = new MaximumAggregator(this.max);
			aggregators.put(key, aggregator);
			aggregator.start(new EmitKey(key, name, 0));
		}
		try {
			aggregator.aggregate(value, weight);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (FinishedException e) {
			e.printStackTrace();
		} // metadata is null
		return UnitVal.v;
	}

	@Override
	public Object get() {
		return this;
	}

	@Override
	public void aggregate(String data, String metadata) throws IOException, InterruptedException, FinishedException {
		
	}

}
