package it.gridband.campaigner.resources;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Optional;
import it.gridband.campaigner.dao.CampaignDao;
import it.gridband.campaigner.model.Campaign;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/campaigns")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CampaignResource {

	private CampaignDao campaignDao;

	public CampaignResource(CampaignDao campaignDao) {
		this.campaignDao = campaignDao;
	}

	@GET
	@Timed
	@Path("{campaignName}")
	// Example: curl http://localhost:8080/campaigns/xmas-campaign
	public Optional<Campaign> get(@PathParam("campaignName") String campaignName) {
		return campaignDao.tryGetCampaign(campaignName);
	}

	@PUT
	@Timed
	@Path("{campaignName}")
	// Example: curl -X PUT http://localhost:8080/campaigns/xmas-campaign
	public void ensureExists(@PathParam("campaignName") String campaignName) {
		campaignDao.ensureExists(campaignName);
	}

	@PUT
	@Timed
	@Path("{campaignName}/scoring-formula")
	// Example: curl -X PUT -H 'Content-Type: application/json' -d '"opens"' http://localhost:8080/campaigns/xmas-campaign/scoring-formula
	public void setScoringFormula(@PathParam("campaignName") String campaignName, String scoringFormula) {
		campaignDao.setScoringFormula(campaignName, scoringFormula);
	}

}
