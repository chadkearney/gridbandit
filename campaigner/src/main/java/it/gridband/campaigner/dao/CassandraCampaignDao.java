package it.gridband.campaigner.dao;

import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.mapping.Mapper;
import com.google.common.base.Optional;
import it.gridband.campaigner.model.Campaign;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

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
	public void ensureExists(String campaignName) {
		Insert insert = QueryBuilder
				.insertInto("gridbandit", "campaigns")
				.value("name", campaignName)
				.value("scoring_formula", "1")
				.value("last_potentially_score_altering_mutation_mse", System.currentTimeMillis())
				.ifNotExists();
		session.execute(insert);
	}

	@Override
	public void setScoringFormula(String campaignName, String scoringFormula) {
		Optional<Campaign> campaignOptional = tryGetCampaign(campaignName);
		if (!campaignOptional.isPresent()) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}

		Campaign campaign = campaignOptional.get();
		campaign.setScoringFormula(scoringFormula);
		campaign.setLastPotentiallyScoreAlteringMutationMse(System.currentTimeMillis());
		campaignMapper.save(campaign);
	}

}
