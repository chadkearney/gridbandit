package it.gridband.campaigner.select;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;

public class MaxElementRandomTiebreakDoubleArrayIndexSelectorFactory implements DoubleArrayIndexSelectorFactory {

	@Override
	public DoubleArrayIndexSelector build() {
		return new MaxElementRandomTiebreakDoubleArrayIndexSelector(new RandomDataGenerator(new MersenneTwister()));
	}

}
