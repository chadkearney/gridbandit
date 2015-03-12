package it.gridband.campaigner.select;

import org.apache.commons.math3.random.RandomDataGenerator;

public class MaxElementRandomTiebreakDoubleArrayIndexSelector implements DoubleArrayIndexSelector {

	private RandomDataGenerator randomDataGenerator;

	public MaxElementRandomTiebreakDoubleArrayIndexSelector(RandomDataGenerator randomDataGenerator) {
		this.randomDataGenerator = randomDataGenerator;
	}

	@Override
	public int select(double[] array) {
		int maxIndex = -1;
		double maxValue = Double.NEGATIVE_INFINITY;
		if (array != null && array.length > 0) {
			for (int i : randomDataGenerator.nextPermutation(array.length, array.length)) {
				if (array[i] >= maxValue) {
					maxValue = array[i];
					maxIndex = i;
				}
			}
		}

		return maxIndex;

	}
}
