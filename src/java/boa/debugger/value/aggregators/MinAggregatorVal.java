package boa.debugger.value.aggregators;

import java.io.IOException;
import java.util.HashMap;

import boa.aggregators.Aggregator;
import boa.aggregators.FinishedException;
import boa.aggregators.MinimumAggregator;
import boa.debugger.value.UnitVal;
import boa.debugger.value.Value;
import boa.io.EmitKey;

public class MinAggregatorVal extends AggregatorVal {
	protected long min;

	public MinAggregatorVal(long min) {
		this.min = min;
		this.aggregators = new HashMap<String, Aggregator>();
	}

	public MinAggregatorVal(long min, String name) {
		this.min = min;
		key = new EmitKey("min", 0);
		this.aggregators = new HashMap<String, Aggregator>();
	}

	public void finish() {
		for (Aggregator aggregator : this.aggregators.values()) {
			try {
				aggregator.finish();
			} catch (IOException e) {
				System.out.println("IOException has been thrown from MinAggregatorVal");
			} catch (InterruptedException e) {
				System.out.println("InterruptedException has been thrown from MinAggregatorVal");
			}
		}
	}

	@Override
	public Value aggregate(String weight, String key, String value, String name) {
		Aggregator aggregator = (Aggregator) aggregators.get(key);
		if (aggregator == null) {
			aggregator = new MinimumAggregator(this.min);
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
