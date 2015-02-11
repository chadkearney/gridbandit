package it.gridband.campaigner.probability;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

public class WeightedArrayIndexDistributionFactoryWithUniformFallback implements ArrayIndexDistributionFactory {
	@Override
	public Optional<ArrayIndexDistribution> build(ImmutableList<Double> weights) {
		if (weights.isEmpty()) {
			return Optional.absent();
		} else if (sum(weights) > 0) {
			return Optional.<ArrayIndexDistribution>of(new WeightedArrayIndexDistribution(weights));
		} else {
			return Optional.<ArrayIndexDistribution>of(new UniformArrayIndexDistribution(weights.size()));
		}
	}

	private double sum(ImmutableList<Double> weights) {
		double total = 0d;
		for (double weight : weights) {
			total += weight;
		}
		return total;
	}
}
