package it.gridband.campaigner.score;

import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;
import com.google.common.collect.TreeMultiset;

import java.util.Map;

public class AverageScoreTemplateIdWeightCalculator implements TemplateIdWeightCalculator {

	private Map<String, Multiset<Double>> scoreCountsPerTemplateId;

	public AverageScoreTemplateIdWeightCalculator() {
		this.scoreCountsPerTemplateId = Maps.newHashMap();
	}

	@Override
	public void addScore(String templateId, double score) {
		getScoreCountMultisetForTemplateId(templateId).add(score);
	}

	@Override
	public Map<String, Double> generateWeights() {
		Map<String, Double> weights = Maps.newHashMap();

		for (Map.Entry<String, Multiset<Double>> templateIdToScoresAndCounts : scoreCountsPerTemplateId.entrySet()) {
			String templateId = templateIdToScoresAndCounts.getKey();

			double scoreTotal = 0d;
			int numberOfScores = 0;
			for (Multiset.Entry<Double> scoreAndCount : templateIdToScoresAndCounts.getValue().entrySet()) {
				scoreTotal += scoreAndCount.getCount() * scoreAndCount.getElement();
				numberOfScores += scoreAndCount.getCount();
			}

			weights.put(templateId, scoreTotal / numberOfScores);
		}
		return weights;
	}

	private Multiset<Double> getScoreCountMultisetForTemplateId(String templateId) {
		Multiset<Double> scoreCounts = scoreCountsPerTemplateId.get(templateId);
		if (scoreCounts == null) {
			scoreCounts = TreeMultiset.create();
			scoreCountsPerTemplateId.put(templateId, scoreCounts);
		}
		return scoreCounts;
	}
}
