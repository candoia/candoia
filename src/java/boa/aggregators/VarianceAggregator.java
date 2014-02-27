package boa.aggregators;

import java.io.IOException;
import java.util.TreeMap;
import java.util.SortedMap;

import org.apache.commons.math.stat.descriptive.SummaryStatistics;

import boa.io.EmitKey;

/**
 * A Boa aggregator to calculate the variance of the values in a dataset.
 * 
 * @author rdyer
 */
@AggregatorSpec(name = "variance", type = "int")
public class VarianceAggregator extends Aggregator {
	private SortedMap<Long, Long> map;

	/** {@inheritDoc} */
	@Override
	public void start(final EmitKey key) {
		super.start(key);

		map = new TreeMap<Long, Long>();
	}

	/** {@inheritDoc} */
	@Override
	public void aggregate(final String data, final String metadata) throws IOException, InterruptedException {
		for (final String s : data.split(";")) {
			final int idx = s.indexOf(":");
			if (idx > 0) {
				final long count = Long.valueOf(s.substring(idx + 1));
				for (int i = 0; i < count; i++)
					aggregate(Long.valueOf(s.substring(0, idx)), metadata);
			} else
				aggregate(Long.valueOf(s), metadata);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void aggregate(final long data, final String metadata) {
		if (map.containsKey(data))
			map.put(data, map.get(data) + 1L);
		else
			map.put(data, 1L);
	}

	/** {@inheritDoc} */
	@Override
	public void aggregate(final double data, final String metadata) {
		this.aggregate(Double.valueOf(data).longValue(), metadata);
	}

	/** {@inheritDoc} */
	@Override
	public void finish() throws IOException, InterruptedException {
		if (this.isCombining()) {
			String s = "";
			for (final Long key : map.keySet())
				s += key + ":" + map.get(key) + ";";
			this.collect(s, null);
			return;
		}

		final SummaryStatistics summaryStatistics = new SummaryStatistics();

		for (final Long key : map.keySet()) {
			final long count = map.get(key);
			for (long i = 0; i < count; i++)
				summaryStatistics.addValue(key);
		}

		this.collect(summaryStatistics.getVariance());
	}
}