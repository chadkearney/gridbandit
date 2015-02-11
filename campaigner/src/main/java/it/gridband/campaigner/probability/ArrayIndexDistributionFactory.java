package it.gridband.campaigner.probability;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

public interface ArrayIndexDistributionFactory {
	Optional<ArrayIndexDistribution> build(ImmutableList<Double> weights);
}
