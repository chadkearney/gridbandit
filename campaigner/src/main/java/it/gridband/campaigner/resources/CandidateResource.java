package it.gridband.campaigner.resources;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import it.gridband.campaigner.model.Candidate;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/campaigns/{campaignName}/candidates")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CandidateResource {

	@PUT
	@Timed
	// Example: curl -X PUT -H 'Content-Type: application/json' -d '{ "name": "colorful", "templateName": "colorfulTemplate" }' http://localhost:8080/campaigns/xmas-campaign/candidates
	public Candidate set(@PathParam("campaignName") String campaignName, @Valid Candidate candidate) {
		return candidate;
	}

	@GET
	@Timed
	// Example: curl http://localhost:8080/campaigns/xmas-campaign/candidates
	public List<Candidate> get(@PathParam("campaignName") String campaignName, @QueryParam("count") Optional<Integer> optionalCount) {
		final int count = optionalCount.or(1);
		return Lists.newArrayList(new Candidate("colorful", "colorfulTemplate"));
	}

	@GET
	@Timed
	@Path("{candidateName}")
	// Example: curl http://localhost:8080/campaigns/xmas-campaign/candidates/colorful
	public Optional<Candidate> getNamed(@PathParam("campaignName") String campaignName, @PathParam("candidateName") String candidateName) {
		return Optional.of(new Candidate(candidateName, candidateName + "Template"));
	}

}
