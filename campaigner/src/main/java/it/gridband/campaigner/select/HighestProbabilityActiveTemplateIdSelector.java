package it.gridband.campaigner.select;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import it.gridband.campaigner.model.Campaign;
import it.gridband.campaigner.model.ScoredTemplateId;

import java.util.*;

public class HighestProbabilityActiveTemplateIdSelector implements ActiveTemplateIdSelector {
	@Override
	public Optional<String> select(Campaign campaign) {
		ArrayList<ScoredTemplateId> scoredActiveTemplateIds = scoreActiveTemplateIds(campaign);

		if (scoredActiveTemplateIds.isEmpty()) {
			return Optional.absent();
		} else if (totalOfScores(scoredActiveTemplateIds) <= 0d) {
			ScoredTemplateId arbitraryScoredTemplateId = scoredActiveTemplateIds.get(new Random().nextInt(scoredActiveTemplateIds.size()));
			return Optional.of(arbitraryScoredTemplateId.getTemplateId());
		}

		ScoredTemplateId highestScoringTemplateId = Collections.max(scoredActiveTemplateIds, new Comparator<ScoredTemplateId>() {
			@Override
			public int compare(ScoredTemplateId sti1, ScoredTemplateId sti2) {
				return Double.compare(sti1.getScore(), sti2.getScore());
			}
		});

		return Optional.of(highestScoringTemplateId.getTemplateId());
	}

	private double totalOfScores(ArrayList<ScoredTemplateId> scoredActiveTemplateIds) {
		double total = 0d;
		for (ScoredTemplateId scoredActiveTemplateId : scoredActiveTemplateIds) {
			total += scoredActiveTemplateId.getScore();
		}
		return total;
	}

	private ArrayList<ScoredTemplateId> scoreActiveTemplateIds(Campaign campaign) {
		final Map<String, Double> templateIdToProbability = campaign.getTemplateIdToProbability();

		Collection<ScoredTemplateId> scoredActiveTemplateIds =
				Collections2.transform(campaign.getActiveTemplateIds(), new Function<String, ScoredTemplateId>() {
					@Override
					public ScoredTemplateId apply(String activeTemplateId) {
						Double templateScore = templateIdToProbability.get(activeTemplateId);
						return new ScoredTemplateId(activeTemplateId, (templateScore == null) ? 0d : templateScore);
					}
				});

		return Lists.newArrayList(scoredActiveTemplateIds);
	}
}
