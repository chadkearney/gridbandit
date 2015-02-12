package it.gridband.campaigner.score;


import org.junit.Assert;
import org.junit.Test;

import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Map;

public class PostfixFormulaTest {

	private final double delta = 0.0001d;

	@Test
	public void worksCorrectlyForTypicalInput() {
		PostfixFormula postfixFormula = new PostfixFormula("0 _exp_ 1 _ln_ + 4 * 2 / 1 - click + open *");

		Map<String, Double> args = new HashMap<>();
		args.put("click", 5d);
		args.put("open", 3d);

		double result = postfixFormula.evaluate(args);

		Assert.assertEquals(18d, result, delta);
	}

	@Test
	public void worksCorrectlyWithNoArguments() {
		PostfixFormula postfixFormula = new PostfixFormula("0 _exp_ 1 _ln_ + 4 * 2 / 1 -");

		double result = postfixFormula.evaluate(new HashMap<String, Double>());

		Assert.assertEquals(1d, result, delta);
	}

	@Test
	public void handlesConstantValue() {
		PostfixFormula postfixFormula = new PostfixFormula("2");
		double result = postfixFormula.evaluate(new HashMap<String, Double>());

		Assert.assertEquals(2, result, delta);
	}

	@Test
	public void handlesRepeatedVariableValues() {
		PostfixFormula postfixFormula = new PostfixFormula("open open * open -");

		Map<String, Double> args = new HashMap<>();
		args.put("open", 5d);

		double result = postfixFormula.evaluate(args);

		Assert.assertEquals(20, result, delta);
	}

	@Test
	public void correctlyDefaultsMissingArguments() {
		PostfixFormula postfixFormula = new PostfixFormula("open _exp_");

		double result = postfixFormula.evaluate(new HashMap<String, Double>());

		Assert.assertEquals(1, result, delta);
	}

	@Test(expected = EmptyStackException.class)
	public void raisesWhenGivenNoFormula() {
		PostfixFormula postfixFormula = new PostfixFormula("");
		postfixFormula.evaluate(new HashMap<String, Double>());
	}

	@Test(expected = EmptyStackException.class)
	public void raisesWhenGivenAnOperatorWithNoInputs() {
		PostfixFormula postfixFormula = new PostfixFormula("_ln_");
		postfixFormula.evaluate(new HashMap<String, Double>());
	}

	@Test(expected = EmptyStackException.class)
	public void raisesWhenThereAreTooFewArguments() {
		PostfixFormula postfixFormula = new PostfixFormula("1 2 + +");
		postfixFormula.evaluate(new HashMap<String, Double>());
	}

	@Test(expected = IllegalStateException.class)
	public void raisesWhenThereAreTooManyArguments() {
		PostfixFormula postfixFormula = new PostfixFormula("1 2 + 3");
		postfixFormula.evaluate(new HashMap<String, Double>());
	}

}
