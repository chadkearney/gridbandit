package it.gridband.campaigner.resources;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import it.gridband.campaigner.model.Campaign;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/campaigns")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CampaignResource {

	@GET
	@Timed
	@Path("{campaignName}")
	// Example: curl http://localhost:8080/campaigns/xmas-campaign
	public Optional<Campaign> get(@PathParam("campaignName") String campaignName) {
		return Optional.of(new Campaign("xmas-campaign", "opens", Sets.newHashSet("colorful", "monochrome")));
	}

	@PUT
	@Timed
	// Example: curl -X PUT -H 'Content-Type: application/json' -d '{ "name": "xmas-campaign", "scoringFormula": "opens" }' http://localhost:8080/campaigns
	public Campaign set(@Valid Campaign campaign) {
		return campaign;
	}

}
