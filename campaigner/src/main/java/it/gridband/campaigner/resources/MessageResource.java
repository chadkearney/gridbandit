package it.gridband.campaigner.resources;

import com.codahale.metrics.annotation.Timed;
import it.gridband.campaigner.model.Message;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/campaigns/{campaignName}/candidates/{candidateName}/messages")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MessageResource {

	@PUT
	@Path("/{messageId}/metrics/{metricName}")
	@Timed
	// Example: curl -X PUT -H 'Content-Type: application/json' -d '1' http://localhost:8080/campaigns/xmas-campaign/candidates/colorful/messages/123/metrics/opened
	public Message set(@PathParam("campaignName") String campaignName, @PathParam("candidateName") String candidateName,
	                   @PathParam("messageId") String messageId, @PathParam("metricName") String metricName,
	                   @Valid Double metricValue)
	{
		return new Message(messageId);
	}

}
