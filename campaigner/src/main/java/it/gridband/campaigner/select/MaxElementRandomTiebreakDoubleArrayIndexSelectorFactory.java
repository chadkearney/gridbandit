package it.gridband.campaigner.select;

import org.apache.commons.math3.random.MersenneTwister;

public class MaxElementRandomTiebreakDoubleArrayIndexSelectorFactory implements DoubleArrayIndexSelectorFactory {

	@Override
	public DoubleArrayIndexSelector build() {
		return new MaxElementRandomTiebreakDoubleArrayIndexSelector(new MersenneTwister());
	}

}
