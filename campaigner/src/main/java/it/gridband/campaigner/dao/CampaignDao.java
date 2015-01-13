package it.gridband.campaigner.dao;

import com.google.common.base.Optional;
import it.gridband.campaigner.model.Campaign;

public interface CampaignDao {
	Optional<Campaign> tryGetCampaign(String campaignName);
	void ensureExists(String campaignName);
	void setScoringFormula(String campaignName, String scoringFormula);
}
