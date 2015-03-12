package it.gridband.campaigner.dao;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.Result;
import com.google.common.base.Optional;
import it.gridband.campaigner.model.Campaign;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.datastax.driver.core.querybuilder.QueryBuilder.*;

public class CassandraCampaignDao implements CampaignDao {

	private Session session;
	private Mapper<Campaign> campaignMapper;

	public CassandraCampaignDao(Session session, Mapper<Campaign> campaignMapper) {
		this.session = session;
		this.campaignMapper = campaignMapper;
	}

	@Override
	public Optional<Campaign> tryGetCampaign(String campaignName) {
		return Optional.fromNullable(campaignMapper.get(campaignName));
	}

	@Override
	public void ensureExistence(String campaignName) {
		Statement statement = QueryBuilder
				.insertInto("gridbandit", "campaigns")
				.value("name", campaignName);
		session.execute(statement);
	}

	@Override
	public void updateLastPotentiallyScoreAlteringChangeMse(String campaignName, long lastPotentiallyScoreAlteringChangeMse) {
		Statement statement = QueryBuilder
				.update("gridbandit", "campaigns")
				.with(set("last_potentially_score_altering_change_mse", lastPotentiallyScoreAlteringChangeMse))
				.where(eq("name", campaignName));
		session.execute(statement);
	}

	@Override
	public void setScoringFormula(String campaignName, String scoringFormula) {
		Statement statement = QueryBuilder
				.update("gridbandit", "campaigns")
				.with(set("scoring_formula", scoringFormula))
				.and(set("last_potentially_score_altering_change_mse", System.currentTimeMillis()))
				.where(eq("name", campaignName));
		session.execute(statement);
	}

	@Override
	public void ensureTemplatePresence(String campaignName, String templateId) {
		Statement statement = QueryBuilder
				.update("gridbandit", "campaigns")
				.with(add("active_template_ids", templateId))
				.and(set("last_potentially_score_altering_change_mse", System.currentTimeMillis()))
				.where(eq("name", campaignName));
		session.execute(statement);
	}

	@Override
	public void ensureTemplateAbsence(String campaignName, String templateId) {
		Statement statement = QueryBuilder
				.update("gridbandit", "campaigns")
				.with(remove("active_template_ids", templateId))
				.and(set("last_potentially_score_altering_change_mse", System.currentTimeMillis()))
				.where(eq("name", campaignName));
		session.execute(statement);
	}

	@Override
	public List<Campaign> getClaimableCampaignsWithOutOfDateTemplateWeights() {
		Select statement = QueryBuilder.select()
				.all()
				.from("gridbandit", "campaigns");
		Result<Campaign> campaigns = campaignMapper.map(session.execute(statement));

		List<Campaign> campaignsWithOutOfDateTemplateWeights = new ArrayList<>();
		for (Campaign campaign : campaigns) {
			if (heartbeatHasNotBeenUpdatedInLastGivenMillis(campaign, 60 * 1000) &&
					templateWeightsAreMissingMoreRecentChanges(campaign))
			{
				campaignsWithOutOfDateTemplateWeights.add(campaign);
			}
		}

		return campaignsWithOutOfDateTemplateWeights;
	}

	@Override
	public boolean tryClaimCampaignForTemplateWeightUpdate(Campaign campaign) {
		Statement statement = QueryBuilder
				.update("gridbandit", "campaigns")
				.with(set("template_weights_update_heartbeat_mse", System.currentTimeMillis()))
				.where(eq("name", campaign.getName()))
				.onlyIf(eq("template_weights_update_heartbeat_mse", campaign.getTemplateWeightsUpdateHeartbeatMse()));
		ResultSet resultSet = session.execute(statement);

		return resultSet.one().getBool("[applied]");
	}

	@Override
	public void updateTemplateWeights(String campaignName, Map<String, Double> templateIdToWeight, long changeUpperBoundMse) {
		Statement statement = QueryBuilder
				.update("gridbandit", "campaigns")
				.with(set("template_id_to_weight", templateIdToWeight))
				.and(set("template_weights_include_changes_up_to_mse", changeUpperBoundMse))
				.where(eq("name", campaignName));
		session.execute(statement);
	}

	@Override
	public void updateHeartbeatToPresent(String campaignName) {
		Statement statement = QueryBuilder
				.update("gridbandit", "campaigns")
				.with(set("template_weights_update_heartbeat_mse", System.currentTimeMillis()))
				.where(eq("name", campaignName));
		session.execute(statement);
	}

	private boolean heartbeatHasNotBeenUpdatedInLastGivenMillis(Campaign campaign, long givenMillis) {
		return campaign.getTemplateWeightsUpdateHeartbeatMse() == null ||
				System.currentTimeMillis() - campaign.getTemplateWeightsUpdateHeartbeatMse() > givenMillis;
	}

	private boolean templateWeightsAreMissingMoreRecentChanges(Campaign campaign) {
		if (campaign.getLastPotentiallyScoreAlteringChangeMse() == null) {
			return false;
		}

		if (campaign.getTemplateWeightsIncludeChangesUpToMse() == null) {
			return true;
		}

		return campaign.getTemplateWeightsIncludeChangesUpToMse() < campaign.getLastPotentiallyScoreAlteringChangeMse();
	}

}
