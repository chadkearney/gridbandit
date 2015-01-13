package it.gridband.campaigner.resources;

import com.codahale.metrics.annotation.Timed;
import it.gridband.campaigner.dao.CampaignDao;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/campaigns/{campaignName}/templates")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TemplateResource {

	private CampaignDao campaignDao;

	public TemplateResource(CampaignDao campaignDao) {
		this.campaignDao = campaignDao;
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

}
