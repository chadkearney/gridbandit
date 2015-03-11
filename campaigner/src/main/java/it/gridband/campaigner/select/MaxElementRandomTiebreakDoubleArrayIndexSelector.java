package it.gridband.campaigner.select;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.commons.math3.random.RandomGenerator;

public class MaxElementRandomTiebreakDoubleArrayIndexSelector implements DoubleArrayIndexSelector {

	private RandomGenerator randomGenerator;

	public MaxElementRandomTiebreakDoubleArrayIndexSelector(RandomGenerator randomGenerator) {
		this.randomGenerator = randomGenerator;
	}

	@Override
	public int select(double[] array) {
		double maxValue = Double.NEGATIVE_INFINITY;
		int maxIndex = 0;
		for (int i : new RandomDataGenerator(randomGenerator).nextPermutation(array.length, array.length)) {
			if (array[i] >= maxValue) {
				maxValue = array[i];
				maxIndex = i;
			}
		}

		return maxIndex;

	}
}
