package it.gridband.campaigner.dao;

import com.google.common.base.Optional;
import it.gridband.campaigner.model.Campaign;

import java.util.List;
import java.util.Map;

public interface CampaignDao {
	Optional<Campaign> tryGetCampaign(String campaignName);

	void ensureExistence(String campaignName);

	void updateLastPotentiallyScoreAlteringChangeMse(String campaignName, long lastPotentiallyScoreAlteringChangeMse);

	void setScoringFormula(String campaignName, String scoringFormula);

	void ensureTemplatePresence(String campaignName, String templateId);

	void ensureTemplateAbsence(String campaignName, String templateId);

	List<Campaign> getClaimableCampaignsWithOutOfDateTemplateWeights();

	boolean tryClaimCampaignForTemplateWeightUpdate(Campaign campaign);

	void updateTemplateWeights(String campaignName, Map<String, Double> templateIdToWeight, long changeUpperBoundMse);

	void updateHeartbeatToPresent(String campaignName);
}
