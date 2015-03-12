package it.gridband.campaigner.score;

import it.gridband.campaigner.select.DoubleArrayIndexSelectorFactory;

public class PartialPoolingTemplateWeightCalculatorFactory implements TemplateWeightCalculatorFactory {

	private int burnInIterations;
	private int scoringIterations;
	private DoubleArrayIndexSelectorFactory doubleArrayIndexSelectorFactory;

	public PartialPoolingTemplateWeightCalculatorFactory(int burnInIterations, int scoringIterations, DoubleArrayIndexSelectorFactory doubleArrayIndexSelectorFactory) {
		this.burnInIterations = burnInIterations;
		this.scoringIterations = scoringIterations;
		this.doubleArrayIndexSelectorFactory = doubleArrayIndexSelectorFactory;
	}

	@Override
	public TemplateIdWeightCalculator build() {
		return new PartialPoolingTemplateIdWeightCalculator(burnInIterations, scoringIterations, doubleArrayIndexSelectorFactory);
	}
}
