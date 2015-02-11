package it.gridband.campaigner.probability;

import com.google.common.base.Optional;
import org.junit.Test;

import java.util.Random;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UniformArrayIndexDistributionTest {

	private final Random random = mock(Random.class);

	@Test(expected = IllegalArgumentException.class)
	public void raisesWhenGivenNegativeIndexCount() {
		new UniformArrayIndexDistribution(-1, Optional.of(random));
	}

	@Test(expected = IllegalArgumentException.class)
	public void raisesWhenGivenZeroIndexCount() {
		new UniformArrayIndexDistribution(0, Optional.of(random));
	}

	@Test
	public void generatesExpectedValuesForDistributionWithSingleElement() {
		UniformArrayIndexDistribution distribution = new UniformArrayIndexDistribution(1, Optional.of(random));

		when(random.nextInt(1)).thenReturn(0, 0, 0);

		assertEquals(0, distribution.nextIndex());
		assertEquals(0, distribution.nextIndex());
		assertEquals(0, distribution.nextIndex());
	}

	@Test
	public void generatesExpectedValuesForMultiElementDistribution() {
		UniformArrayIndexDistribution distribution = new UniformArrayIndexDistribution(3, Optional.of(random));

		when(random.nextInt(3)).thenReturn(0, 1, 2, 1, 0);

		assertEquals(0, distribution.nextIndex());
		assertEquals(1, distribution.nextIndex());
		assertEquals(2, distribution.nextIndex());
		assertEquals(1, distribution.nextIndex());
		assertEquals(0, distribution.nextIndex());
	}
}
