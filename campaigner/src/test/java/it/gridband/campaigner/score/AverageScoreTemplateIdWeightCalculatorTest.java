package it.gridband.campaigner.score;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AverageScoreTemplateIdWeightCalculatorTest {

	private static double TOLERANCE = 0.000001d;

	private AverageScoreTemplateIdWeightCalculator averageScoreTemplateIdWeightCalculator;

	@Before
	public void setUp() {
		averageScoreTemplateIdWeightCalculator = new AverageScoreTemplateIdWeightCalculator();
	}

	@Test
	public void returnsAnEmptyWeightMapWhenGivenNoScores() {
		Map<String, Double> weights = averageScoreTemplateIdWeightCalculator.generateWeights();

		assertTrue(weights.isEmpty());
	}

	@Test
	public void canHandleNullTemplateId() {
		averageScoreTemplateIdWeightCalculator.addScore(null, 1d);

		Map<String, Double> weights = averageScoreTemplateIdWeightCalculator.generateWeights();

		assertEquals(1, weights.size());
		assertEquals(1d, weights.get(null), TOLERANCE);
	}

	@Test
	public void correctlyHandlesMultipleKeys() {
		averageScoreTemplateIdWeightCalculator.addScore("key1", 1d);
		averageScoreTemplateIdWeightCalculator.addScore("key2", 2d);

		Map<String, Double> weights = averageScoreTemplateIdWeightCalculator.generateWeights();

		assertEquals(2, weights.size());
		assertEquals(1d, weights.get("key1"), TOLERANCE);
		assertEquals(2d, weights.get("key2"), TOLERANCE);
	}

	@Test
	public void correctlyComputesAveragesForMultipleKeys() {
		averageScoreTemplateIdWeightCalculator.addScore("key1", 1d);
		averageScoreTemplateIdWeightCalculator.addScore("key1", 2d);
		averageScoreTemplateIdWeightCalculator.addScore("key2", 0d);
		averageScoreTemplateIdWeightCalculator.addScore("key2", 2d);
		averageScoreTemplateIdWeightCalculator.addScore("key3", 0d);

		Map<String, Double> weights = averageScoreTemplateIdWeightCalculator.generateWeights();

		assertEquals(3, weights.size());
		assertEquals(1.5d, weights.get("key1"), TOLERANCE);
		assertEquals(1d, weights.get("key2"), TOLERANCE);
		assertEquals(0d, weights.get("key3"), TOLERANCE);
	}

	@Test
	public void correctlyHandlesNegativeScores() {
		averageScoreTemplateIdWeightCalculator.addScore("key1", -1d);
		averageScoreTemplateIdWeightCalculator.addScore("key1", 0d);
		averageScoreTemplateIdWeightCalculator.addScore("key2", -5d);

		Map<String, Double> weights = averageScoreTemplateIdWeightCalculator.generateWeights();

		assertEquals(2, weights.size());
		assertEquals(-0.5d, weights.get("key1"), TOLERANCE);
		assertEquals(-5d, weights.get("key2"), TOLERANCE);
	}

}
