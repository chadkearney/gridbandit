package it.gridband.campaigner.score;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ExistingScoreExtractorTest {

	private static double TOLERANCE = 0.000001d;

	private ExistingScoreExtractor existingScoreExtractor = new ExistingScoreExtractor();

	@Test
	public void returnsEmptyListWhenGivenEmptyListAndAbsentScores() {
		ImmutableList<Double> scores = existingScoreExtractor.extractScoresForKeys(ImmutableList.<String>of(), Optional.<Map<String, Double>>absent());

		assertTrue(scores.isEmpty());
	}

	@Test
	public void returnsEmptyListWhenGivenEmptyListAndEmptyScores() {
		ImmutableMap<String, Double> keysAndScores = ImmutableMap.of();
		ImmutableList<Double> scores = existingScoreExtractor.extractScoresForKeys(ImmutableList.<String>of(), Optional.<Map<String, Double>>of(keysAndScores));

		assertTrue(scores.isEmpty());
	}

	@Test
	public void returnsEmptyListWhenGivenEmptyListAndSomeScores() {
		ImmutableMap<String, Double> keysAndScores = ImmutableMap.of("key1", 0d, "key2", 1d);
		ImmutableList<Double> scores = existingScoreExtractor.extractScoresForKeys(ImmutableList.<String>of(), Optional.<Map<String, Double>>of(keysAndScores));

		assertTrue(scores.isEmpty());
	}

	@Test
	public void returnsListOfExpectedScoresWhenGivenSomeKeysAndAbsentScores() {
		ImmutableList<String> keys = ImmutableList.of("key1", "key2", "key3");
		ImmutableList<Double> scores = existingScoreExtractor.extractScoresForKeys(keys, Optional.<Map<String, Double>>absent());

		assertEquals(3, scores.size());
		assertEquals(0d, scores.get(0), TOLERANCE);
		assertEquals(0d, scores.get(1), TOLERANCE);
		assertEquals(0d, scores.get(2), TOLERANCE);
	}

	@Test
	public void returnsListOfExpectedScoresWhenGivenSomeKeysAndEmptyScores() {
		ImmutableList<String> keys = ImmutableList.of("key1", "key2", "key3");
		ImmutableMap<String, Double> keysAndScores = ImmutableMap.of();
		ImmutableList<Double> scores = existingScoreExtractor.extractScoresForKeys(keys, Optional.<Map<String, Double>>of(keysAndScores));

		assertEquals(3, scores.size());
		assertEquals(0d, scores.get(0), TOLERANCE);
		assertEquals(0d, scores.get(1), TOLERANCE);
		assertEquals(0d, scores.get(2), TOLERANCE);
	}

	@Test
	public void returnsListOfExpectedScoresWhenGivenSomeKeysAndIncompleteScores() {
		ImmutableList<String> keys = ImmutableList.of("key1", "key2", "key3");
		ImmutableMap<String, Double> keysAndScores = ImmutableMap.of("key1", 1d, "key3", 3d);
		ImmutableList<Double> scores = existingScoreExtractor.extractScoresForKeys(keys, Optional.<Map<String, Double>>of(keysAndScores));

		assertEquals(3, scores.size());
		assertEquals(1d, scores.get(0), TOLERANCE);
		assertEquals(0d, scores.get(1), TOLERANCE);
		assertEquals(3d, scores.get(2), TOLERANCE);
	}

	@Test
	public void returnsListOfExpectedScoresWhenGivenSomeKeysAndCompleteScores() {
		ImmutableList<String> keys = ImmutableList.of("key1", "key2", "key3");
		ImmutableMap<String, Double> keysAndScores = ImmutableMap.of("key1", 1d, "key2", 2d, "key3", 0d);
		ImmutableList<Double> scores = existingScoreExtractor.extractScoresForKeys(keys, Optional.<Map<String, Double>>of(keysAndScores));

		assertEquals(3, scores.size());
		assertEquals(1d, scores.get(0), TOLERANCE);
		assertEquals(2d, scores.get(1), TOLERANCE);
		assertEquals(0d, scores.get(2), TOLERANCE);
	}

}
