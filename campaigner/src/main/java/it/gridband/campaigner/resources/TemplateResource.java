package it.gridband.campaigner.resources;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Optional;
import it.gridband.campaigner.dao.CampaignDao;
import it.gridband.campaigner.model.Campaign;
import it.gridband.campaigner.select.ActiveTemplateIdSelector;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

@Path("/campaigns/{campaignName}/templates")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TemplateResource {

	private CampaignDao campaignDao;
	private ActiveTemplateIdSelector activeTemplateIdSelector;

	public TemplateResource(CampaignDao campaignDao, ActiveTemplateIdSelector activeTemplateIdSelector) {
		this.campaignDao = campaignDao;
		this.activeTemplateIdSelector = activeTemplateIdSelector;
	}

	@PUT
	@Timed
	@Path("{templateId}")
	// Example: curl -X PUT http://localhost:8080/campaigns/xmas-campaign/templates/colorfulTemplate
	public void ensureTemplatePresence(@PathParam("campaignName") String campaignName, @PathParam("templateId") String templateId) {
		campaignDao.ensureTemplatePresence(campaignName, templateId);
	}

	@DELETE
	@Timed
	@Path("{templateId}")
	// Example: curl -X DELETE http://localhost:8080/campaigns/xmas-campaign/templates/colorfulTemplate
	public void ensureTemplateAbsence(@PathParam("campaignName") String campaignName, @PathParam("templateId") String templateId) {
		campaignDao.ensureTemplateAbsence(campaignName, templateId);
	}

	@GET
	@Timed
	@Path("{templateId}")
	// Example: curl -X GET http://localhost:8080/campaigns/xmas-campaign/templates/colorfulTemplate
	public Optional<String> getTemplate(@PathParam("campaignName") String campaignName, @PathParam("templateId") String templateId)
	{
		Campaign campaign = getCampaignOrThrow404(campaignName);

		return campaign.getActiveTemplateIds().contains(templateId)
				? Optional.of(templateId) : Optional.<String>absent();
	}

	@GET
	@Timed
	// Example: curl -X GET http://localhost:8080/campaigns/xmas-campaign/templates
	public Response getNextTemplate(@PathParam("campaignName") String campaignName)
	{
		Campaign campaign = getCampaignOrThrow404(campaignName);

		Optional<String> optionalTemplateId = activeTemplateIdSelector.select(campaign);

		if (!optionalTemplateId.isPresent()) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}

		return Response.status(Response.Status.SEE_OTHER)
				.location(buildUriForTemplateId(campaignName, optionalTemplateId.get()))
				.entity(optionalTemplateId.get()).build();
	}

	private Campaign getCampaignOrThrow404(String campaignName) {
		Optional<Campaign> optionalCampaign = campaignDao.tryGetCampaign(campaignName);
		if (!optionalCampaign.isPresent()) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		} else {
			return optionalCampaign.get();
		}
	}

	private URI buildUriForTemplateId(String campaignName, String templateId) {
		return UriBuilder.fromResource(TemplateResource.class).path(TemplateResource.class, "getTemplate")
				.build(campaignName, templateId);
	}
}
