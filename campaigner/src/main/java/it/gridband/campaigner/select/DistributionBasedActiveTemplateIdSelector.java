package it.gridband.campaigner.select;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import it.gridband.campaigner.model.Campaign;
import it.gridband.campaigner.probability.ArrayIndexDistribution;
import it.gridband.campaigner.probability.ArrayIndexDistributionFactory;
import it.gridband.campaigner.score.ExistingScoreExtractor;

public class DistributionBasedActiveTemplateIdSelector implements ActiveTemplateIdSelector {

	private ArrayIndexDistributionFactory arrayIndexDistributionFactory;
	private ExistingScoreExtractor existingScoreExtractor;

	public DistributionBasedActiveTemplateIdSelector(ArrayIndexDistributionFactory arrayIndexDistributionFactory, ExistingScoreExtractor existingScoreExtractor) {
		this.arrayIndexDistributionFactory = arrayIndexDistributionFactory;
		this.existingScoreExtractor = existingScoreExtractor;
	}

	@Override
	public Optional<String> select(Campaign campaign) {
		if (campaign.getActiveTemplateIds() == null || campaign.getActiveTemplateIds().isEmpty()) {
			return Optional.absent();
		}

		ImmutableList<String> activeTemplateIds = new ImmutableList.Builder<String>().addAll(campaign.getActiveTemplateIds()).build();
		ImmutableList<Double> activeTemplateIdScores = existingScoreExtractor.extractScoresForKeys(activeTemplateIds, Optional.fromNullable(campaign.getTemplateIdToProbability()));

		Optional<ArrayIndexDistribution> distribution = arrayIndexDistributionFactory.build(activeTemplateIdScores);

		if (distribution.isPresent()) {
			return Optional.of(activeTemplateIds.get(distribution.get().nextIndex()));
		} else {
			return Optional.absent();
		}
	}

}
