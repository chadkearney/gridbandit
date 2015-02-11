package it.gridband.campaigner.probability;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class WeightedArrayIndexDistribution implements ArrayIndexDistribution {

	private ImmutableMap<Integer, Double> cumulativeWeightsByPossibleIndex;
	private double totalWeight;

	private Optional<Random> random;

	public WeightedArrayIndexDistribution(ImmutableList<Double> weights) {
		this(weights, Optional.<Random>absent());
	}

	public WeightedArrayIndexDistribution(ImmutableList<Double> weights, Optional<Random> random) {
		this.random = random;
		this.totalWeight = 0d;

		ImmutableMap.Builder<Integer, Double> builder = ImmutableMap.builder();
		for (int i = 0; i < weights.size(); i++) {
			if (weights.get(i) < 0d) {
				throw new IllegalArgumentException("Weights provided to WeightedArrayIndexDistribution must be non-negative");
			} else if (weights.get(i) > 0) {
				totalWeight += weights.get(i);
				builder.put(i, totalWeight);
			}
		}
		this.cumulativeWeightsByPossibleIndex = builder.build();

		if (totalWeight <= 0d) {
			throw new IllegalStateException("WeightedArrayIndexDistribution: weight total must be positive.");
		}
	}

	@Override
	public int nextIndex() {
		return inverseCdf(random().nextDouble());
	}

	private int inverseCdf(double probability) {
		double targetValue = probability * totalWeight;

		int matchingIndex = 0;
		for (Map.Entry<Integer, Double> cumulativeWeightByPossibleIndex : cumulativeWeightsByPossibleIndex.entrySet()) {
			matchingIndex = cumulativeWeightByPossibleIndex.getKey();
			if (targetValue <= cumulativeWeightByPossibleIndex.getValue()) {
				break;
			}
		}
		return matchingIndex;
	}

	private Random random() {
		return random.isPresent() ? random.get() : ThreadLocalRandom.current();
	}
}
