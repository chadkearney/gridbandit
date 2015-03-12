package it.gridband.campaigner.select;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MaxElementRandomTiebreakDoubleArrayIndexSelectorTest {

	private MaxElementRandomTiebreakDoubleArrayIndexSelector maxElementRandomTiebreakDoubleArrayIndexSelector;
	private RandomDataGenerator randomDataGenerator;

	@Before
	public void setup() {
		randomDataGenerator = mock(RandomDataGenerator.class);
		maxElementRandomTiebreakDoubleArrayIndexSelector = new MaxElementRandomTiebreakDoubleArrayIndexSelector(randomDataGenerator);
	}

	@Test
	public void returnsNegativeOneWhenGivenNullArray() {
		assertEquals(-1, maxElementRandomTiebreakDoubleArrayIndexSelector.select(null));
	}

	@Test
	public void returnsNegativeOneForEmptyInput() {
		assertEquals(-1, maxElementRandomTiebreakDoubleArrayIndexSelector.select(new double[0]));
	}

	@Test
	public void returnsIndexOfUniqueMaximumWhenPossible() {
		double[] array = { 0d, 1d, -1d, 2d, -2d };

		when(randomDataGenerator.nextPermutation(5, 5)).thenReturn(new int[] { 1, 3, 0, 2, 4 });

		assertEquals(3, maxElementRandomTiebreakDoubleArrayIndexSelector.select(array));
	}

	@Test
	public void returnsLastIndexInPermutationInCaseOfTies() {
		double[] array = { 1d, 1d, 1d, 1d, 1d };

		when(randomDataGenerator.nextPermutation(5, 5)).thenReturn(new int[] { 2, 4, 0, 1, 3 });

		assertEquals(3, maxElementRandomTiebreakDoubleArrayIndexSelector.select(array));
	}

	@Test
	public void correctlyHandlesNegativeInfinity() {
		double[] array = { Double.NEGATIVE_INFINITY };

		when(randomDataGenerator.nextPermutation(1, 1)).thenReturn(new int[] { 0 });

		assertEquals(0, maxElementRandomTiebreakDoubleArrayIndexSelector.select(array));
	}

	@Test
	public void correctlyHandlesPositiveInfinity() {
		double[] array = { Double.POSITIVE_INFINITY };

		when(randomDataGenerator.nextPermutation(1, 1)).thenReturn(new int[] { 0 });

		assertEquals(0, maxElementRandomTiebreakDoubleArrayIndexSelector.select(array));
	}

}
