package it.gridband.campaigner.score;

public class BasicPostfixFormulaFactory implements PostfixFormulaFactory {
	@Override
	public PostfixFormula build(String formula) {
		return new PostfixFormula(formula);
	}
}
