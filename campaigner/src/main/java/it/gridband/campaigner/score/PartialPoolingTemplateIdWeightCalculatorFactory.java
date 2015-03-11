package it.gridband.campaigner.score;

import it.gridband.campaigner.select.DoubleArrayIndexSelectorFactory;

public class PartialPoolingTemplateIdWeightCalculatorFactory implements TemplateIdWeightCalculatorFactory {

	private int burnInIterations;
	private int scoringIterations;
	private DoubleArrayIndexSelectorFactory doubleArrayIndexSelectorFactory;

	public PartialPoolingTemplateIdWeightCalculatorFactory(int burnInIterations, int scoringIterations, DoubleArrayIndexSelectorFactory doubleArrayIndexSelectorFactory) {
		this.burnInIterations = burnInIterations;
		this.scoringIterations = scoringIterations;
		this.doubleArrayIndexSelectorFactory = doubleArrayIndexSelectorFactory;
	}

	@Override
	public TemplateIdWeightCalculator build() {
		return new PartialPoolingTemplateIdWeightCalculator(burnInIterations, scoringIterations, doubleArrayIndexSelectorFactory);
	}
}
