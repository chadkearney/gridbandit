package it.gridband.campaigner.score;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.gridband.campaigner.select.DoubleArrayIndexSelector;
import it.gridband.campaigner.select.DoubleArrayIndexSelectorFactory;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class PartialPoolingTemplateIdWeightCalculator implements TemplateIdWeightCalculator {

	private final int burnInIterations;
	private final int scoringIterations;

	private Map<String, SummaryStatistics> summaryStatsPerTemplateId;
	private SummaryStatistics overallSummaryStatistics;
	private RandomGenerator randomGenerator;

	private DoubleArrayIndexSelectorFactory doubleArrayIndexSelectorFactory;

	public PartialPoolingTemplateIdWeightCalculator(int burnInIterations, int scoringIterations, DoubleArrayIndexSelectorFactory doubleArrayIndexSelectorFactory) {
		this.burnInIterations = burnInIterations;
		this.scoringIterations = scoringIterations;

		this.summaryStatsPerTemplateId = Maps.newHashMap();
		this.overallSummaryStatistics = new SummaryStatistics();
		this.randomGenerator = new MersenneTwister();

		this.doubleArrayIndexSelectorFactory = doubleArrayIndexSelectorFactory;
	}

	@Override
	public void addScore(String templateId, double score) {
		overallSummaryStatistics.addValue(score);

		SummaryStatistics summaryStatistics = summaryStatsPerTemplateId.get(templateId);
		if (summaryStatistics == null) {
			summaryStatistics = new SummaryStatistics();
			summaryStatsPerTemplateId.put(templateId, summaryStatistics);
		}
		summaryStatistics.addValue(score);
	}

	@Override
	public Map<String, Double> generateWeights(Set<String> targetTemplateIds) {
		ArrayList<String> targetTemplateIdsWithObservations = Lists.newArrayList();
		ArrayList<String> targetTemplateIdsWithoutObservations = Lists.newArrayList();

		ArrayList<Double> groupMeans = Lists.newArrayList();
		ArrayList<Double> standardErrorOfGroupMeans = Lists.newArrayList();
		ArrayList<Long> groupSizes = Lists.newArrayList();

		for (String templateId : targetTemplateIds) {
			SummaryStatistics stats = summaryStatsPerTemplateId.get(templateId);
			if (stats == null || stats.getN() == 0) {
				targetTemplateIdsWithoutObservations.add(templateId);
			} else {
				targetTemplateIdsWithObservations.add(templateId);

				groupMeans.add(stats.getMean());
				standardErrorOfGroupMeans.add(stats.getStandardDeviation() / Math.sqrt(stats.getN()));
				groupSizes.add(stats.getN());
			}
		}

		PartialPoolingNoPredictorGibbsSampler sampler =
				new PartialPoolingNoPredictorGibbsSampler(
						overallSummaryStatistics.getMean(), overallSummaryStatistics.getStandardDeviation(),
						groupMeans, standardErrorOfGroupMeans, groupSizes);

		if (!sampler.passesPreconditions()) {
			return identityMap(targetTemplateIds);
		}

		sampler.prepareForWalk();
		burnInSampler(sampler);

		double[] numberOfTimesTemplateIdWasSelected = new double[targetTemplateIds.size()];
		double[] nextMeanEstimates = new double[targetTemplateIds.size()];
		DoubleArrayIndexSelector selector = doubleArrayIndexSelectorFactory.build();

		for (int i = 0; i < scoringIterations; i++) {
			sampler.step();
			copyNextEstimates(nextMeanEstimates, sampler, targetTemplateIdsWithObservations.size(), targetTemplateIdsWithoutObservations.size());
			numberOfTimesTemplateIdWasSelected[selector.select(nextMeanEstimates)] += 1;
		}

		return generateNewWeightMapFromSelectionCounts(targetTemplateIdsWithObservations, targetTemplateIdsWithoutObservations, numberOfTimesTemplateIdWasSelected);
	}

	private void burnInSampler(PartialPoolingNoPredictorGibbsSampler sampler) {
		for (int i = 0; i < burnInIterations; i++) {
			sampler.step();
		}
	}

	private void copyNextEstimates(double[] dest, PartialPoolingNoPredictorGibbsSampler sampler, int directlyEstimatedMeanCount, int simulatedMeanCount) {
		System.arraycopy(sampler.getCurrentEstimatedGroupMeans(), 0, dest, 0, directlyEstimatedMeanCount);

		if (simulatedMeanCount > 0) {
			NormalDistribution normalDistribution =
					new NormalDistribution(randomGenerator,
							sampler.getCurrentEstimatedGroupOverallMean(),
							sampler.getCurrentEstimatedBetweenGroupStdDev());

			System.arraycopy(normalDistribution.sample(simulatedMeanCount), 0, dest,
					directlyEstimatedMeanCount, simulatedMeanCount);
		}
	}

	private Map<String, Double> identityMap(Set<String> targetTemplateIds) {
		Map<String, Double> newWeights = Maps.newHashMap();
		for (String targetTemplateId : targetTemplateIds) {
			newWeights.put(targetTemplateId, 1d);
		}
		return newWeights;
	}

	private Map<String, Double> generateNewWeightMapFromSelectionCounts(ArrayList<String> targetTemplateIdsWithObservations, ArrayList<String> targetTemplateIdsWithoutObservations, double[] numberOfTimesTemplateIdWasSelected) {
		Map<String, Double> newWeights = Maps.newHashMap();

		for (int i = 0; i < targetTemplateIdsWithObservations.size(); i++) {
			newWeights.put(targetTemplateIdsWithObservations.get(i), numberOfTimesTemplateIdWasSelected[i]);
		}

		for (int i = 0; i < targetTemplateIdsWithoutObservations.size(); i++) {
			newWeights.put(targetTemplateIdsWithoutObservations.get(i),
					numberOfTimesTemplateIdWasSelected[i + targetTemplateIdsWithObservations.size()]);
		}

		return newWeights;

	}
}
