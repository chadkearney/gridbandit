package it.gridband.campaigner.score;

public class AverageScoreTemplateIdWeightCalculatorFactory implements TemplateIdWeightCalculatorFactory {
	@Override
	public TemplateIdWeightCalculator build() {
		return new AverageScoreTemplateIdWeightCalculator();
	}
}
