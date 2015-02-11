package it.gridband.campaigner.probability;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.util.Random;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WeightedArrayIndexDistributionTest {

	private static final double DELTA = 0.0000000001d;

	private final Random random = mock(Random.class);

	@Test(expected = IllegalArgumentException.class)
	public void raisesWhenGivenANegativeWeight() {
		new WeightedArrayIndexDistribution(ImmutableList.of(1d, -4d, 2d), Optional.of(random));
	}

	@Test(expected = IllegalStateException.class)
	public void raisesWhenGivenNoPositiveWeights() {
		new WeightedArrayIndexDistribution(ImmutableList.of(0d, 0d, 0d), Optional.of(random));
	}

	@Test
	public void generatesExpectedValuesForDistributionWithSingleElement() {
		WeightedArrayIndexDistribution distribution = new WeightedArrayIndexDistribution(ImmutableList.of(1d), Optional.of(random));

		when(random.nextDouble()).thenReturn(0d - DELTA, 0d, 0.5d, 1d, 1d + DELTA); // Values outside [0, 1] simulate rounding error.

		assertEquals(0, distribution.nextIndex());
		assertEquals(0, distribution.nextIndex());
		assertEquals(0, distribution.nextIndex());
		assertEquals(0, distribution.nextIndex());
		assertEquals(0, distribution.nextIndex());
	}

	@Test
	public void generatesExpectedValuesForMultiElementDistribution() {
		WeightedArrayIndexDistribution distribution =
				new WeightedArrayIndexDistribution(ImmutableList.of(1d, 1d, 2d), Optional.of(random));

		when(random.nextDouble()).thenReturn(0d - DELTA, 0d, 0.2d, 0.4d, 0.5d, 0.6d, 0.8d, 1d, 1d + DELTA); // Values outside [0, 1] simulate rounding error.

		assertEquals(0, distribution.nextIndex());
		assertEquals(0, distribution.nextIndex());
		assertEquals(0, distribution.nextIndex());
		assertEquals(1, distribution.nextIndex());
		assertEquals(1, distribution.nextIndex());
		assertEquals(2, distribution.nextIndex());
		assertEquals(2, distribution.nextIndex());
		assertEquals(2, distribution.nextIndex());
		assertEquals(2, distribution.nextIndex());
	}

	@Test
	public void generatesExpectedValuesForInputThatIncludesAnInitialZeroWeight() {
		WeightedArrayIndexDistribution distribution =
				new WeightedArrayIndexDistribution(ImmutableList.of(0d, 1d, 1d), Optional.of(random));

		when(random.nextDouble()).thenReturn(0d - DELTA, 0.3d, 0.5d, 0.6d, 1d, 1d + DELTA); // Values outside [0, 1] simulate rounding error.

		assertEquals(1, distribution.nextIndex());
		assertEquals(1, distribution.nextIndex());
		assertEquals(1, distribution.nextIndex());
		assertEquals(2, distribution.nextIndex());
		assertEquals(2, distribution.nextIndex());
		assertEquals(2, distribution.nextIndex());
	}

	@Test
	public void generatesExpectedValuesForInputThatIncludesAFinalZeroWeight() {
		WeightedArrayIndexDistribution distribution =
				new WeightedArrayIndexDistribution(ImmutableList.of(1d, 1d, 0d), Optional.of(random));

		when(random.nextDouble()).thenReturn(0d - DELTA, 0.3d, 0.5d, 0.6d, 1d, 1d + DELTA); // Values outside [0, 1] simulate rounding error.

		assertEquals(0, distribution.nextIndex());
		assertEquals(0, distribution.nextIndex());
		assertEquals(0, distribution.nextIndex());
		assertEquals(1, distribution.nextIndex());
		assertEquals(1, distribution.nextIndex());
		assertEquals(1, distribution.nextIndex());
	}

	@Test
	public void generatesExpectedValuesForInputThatIncludesAnInternalZeroWeight() {
		WeightedArrayIndexDistribution distribution =
				new WeightedArrayIndexDistribution(ImmutableList.of(1d, 0d, 1d), Optional.of(random));

		when(random.nextDouble()).thenReturn(0d - DELTA, 0.3d, 0.5d, 0.6d, 1d, 1d + DELTA); // Values outside [0, 1] simulate rounding error.

		assertEquals(0, distribution.nextIndex());
		assertEquals(0, distribution.nextIndex());
		assertEquals(0, distribution.nextIndex());
		assertEquals(2, distribution.nextIndex());
		assertEquals(2, distribution.nextIndex());
		assertEquals(2, distribution.nextIndex());
	}

}
