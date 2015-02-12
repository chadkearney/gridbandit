package it.gridband.campaigner.score;

import java.util.Map;

public interface TemplateIdWeightCalculator {
	void addScore(String templateId, double score);
	Map<String, Double> generateWeights();
}
