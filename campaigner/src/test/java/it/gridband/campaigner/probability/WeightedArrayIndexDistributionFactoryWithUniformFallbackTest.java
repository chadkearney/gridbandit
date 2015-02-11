package it.gridband.campaigner.probability;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

public class WeightedArrayIndexDistributionFactoryWithUniformFallbackTest {

	private final WeightedArrayIndexDistributionFactoryWithUniformFallback factory = new WeightedArrayIndexDistributionFactoryWithUniformFallback();

	@Test
	public void returnsAbsentWhenGivenEmptyWeights() {
		Optional<ArrayIndexDistribution> distributionOptional = factory.build(ImmutableList.<Double>of());

		assertFalse(distributionOptional.isPresent());
	}

	@Test
	public void returnsUniformWhenGivenSingleZeroWeight() {
		Optional<ArrayIndexDistribution> distributionOptional = factory.build(ImmutableList.of(0d));

		assertTrue(distributionOptional.isPresent());
		assertThat(distributionOptional.get(), instanceOf(UniformArrayIndexDistribution.class));
	}

	@Test
	public void returnsUniformWhenGivenManyZeroWeights() {
		Optional<ArrayIndexDistribution> distributionOptional = factory.build(ImmutableList.of(0d, 0d, 0d));

		assertTrue(distributionOptional.isPresent());
		assertThat(distributionOptional.get(), instanceOf(UniformArrayIndexDistribution.class));
	}

	@Test
	public void returnsWeightedWhenGivenOneNonZeroWeight() {
		Optional<ArrayIndexDistribution> distributionOptional = factory.build(ImmutableList.of(1d));

		assertTrue(distributionOptional.isPresent());
		assertThat(distributionOptional.get(), instanceOf(WeightedArrayIndexDistribution.class));
	}

	@Test
	public void returnsWeightedWhenGivenManyNonZeroWeights() {
		Optional<ArrayIndexDistribution> distributionOptional = factory.build(ImmutableList.of(1d, 2d, 3d));

		assertTrue(distributionOptional.isPresent());
		assertThat(distributionOptional.get(), instanceOf(WeightedArrayIndexDistribution.class));
	}

	@Test
	public void returnsWeightedWhenGivenAtLeastOneNonZeroWeight() {
		Optional<ArrayIndexDistribution> distributionOptional = factory.build(ImmutableList.of(0d, 2d, 0d));

		assertTrue(distributionOptional.isPresent());
		assertThat(distributionOptional.get(), instanceOf(WeightedArrayIndexDistribution.class));
	}

}
