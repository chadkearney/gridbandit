package it.gridband.campaigner.score;

import java.util.Map;
import java.util.Set;

public interface TemplateIdWeightCalculator {
	void addScore(String templateId, double score);
	Map<String, Double> generateWeights(Set<String> targetTemplateIds);
}
