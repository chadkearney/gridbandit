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
	public List<Campaign> getClaimableCampaignsWithOutOfDateTemplateProbabilities() {
		Select statement = QueryBuilder.select()
				.all()
				.from("gridbandit", "campaigns");
		Result<Campaign> campaigns = campaignMapper.map(session.execute(statement));

		List<Campaign> campaignsWithOutOfDateTemplateProbabilities = new ArrayList<>();
		for (Campaign campaign : campaigns) {
			if (heartbeatHasNotBeenUpdatedInLastGivenMillis(campaign, 60 * 1000) &&
					templateProbabilitiesAreMissingMoreRecentChanges(campaign))
			{
				campaignsWithOutOfDateTemplateProbabilities.add(campaign);
			}
		}

		return campaignsWithOutOfDateTemplateProbabilities;
	}

	@Override
	public boolean tryClaimCampaignForTemplateProbabilityUpdate(Campaign campaign) {
		Statement statement = QueryBuilder
				.update("gridbandit", "campaigns")
				.with(set("template_probabilities_update_heartbeat_mse", System.currentTimeMillis()))
				.where(eq("name", campaign.getName()))
				.onlyIf(eq("template_probabilities_update_heartbeat_mse", campaign.getTemplateProbabilitiesUpdateHeartbeatMse()));
		ResultSet resultSet = session.execute(statement);

		return resultSet.one().getBool("[applied]");
	}

	@Override
	public void updateTemplateProbabilities(String campaignName, Map<String, Double> templateIdToProbability, long changeUpperBoundMse) {
		Statement statement = QueryBuilder
				.update("gridbandit", "campaigns")
				.with(set("template_id_to_probability", templateIdToProbability))
				.and(set("template_probabilities_include_changes_up_to_mse", changeUpperBoundMse))
				.where(eq("name", campaignName));
		session.execute(statement);
	}

	@Override
	public void updateHeartbeatToPresent(String campaignName) {
		Statement statement = QueryBuilder
				.update("gridbandit", "campaigns")
				.with(set("template_probabilities_update_heartbeat_mse", System.currentTimeMillis()))
				.where(eq("name", campaignName));
		session.execute(statement);
	}

	private boolean heartbeatHasNotBeenUpdatedInLastGivenMillis(Campaign campaign, long givenMillis) {
		return campaign.getTemplateProbabilitiesUpdateHeartbeatMse() == null ||
				System.currentTimeMillis() - campaign.getTemplateProbabilitiesUpdateHeartbeatMse() > givenMillis;
	}

	private boolean templateProbabilitiesAreMissingMoreRecentChanges(Campaign campaign) {
		if (campaign.getLastPotentiallyScoreAlteringChangeMse() == null) {
			return false;
		}

		if (campaign.getTemplateProbabilitiesIncludeChangesUpToMse() == null) {
			return true;
		}

		return campaign.getTemplateProbabilitiesIncludeChangesUpToMse() < campaign.getLastPotentiallyScoreAlteringChangeMse();
	}

}
