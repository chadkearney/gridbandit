package it.gridband.campaigner.dao;

import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.mapping.Mapper;
import com.google.common.base.Optional;
import it.gridband.campaigner.model.Campaign;

import static com.datastax.driver.core.querybuilder.QueryBuilder.*;

public class CassandraCampaignDao implements CampaignDao {

	private Mapper<Campaign> campaignMapper;
	private Session session;

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

}
