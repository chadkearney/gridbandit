package it.gridband.campaigner.resources;

import com.codahale.metrics.annotation.Timed;
import it.gridband.campaigner.dao.CampaignDao;
import it.gridband.campaigner.dao.MessageDao;
import it.gridband.campaigner.model.WebhookEvent;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/webhook")
@Consumes(MediaType.APPLICATION_JSON)
public class WebhookResource {

	private MessageDao messageDao;
	private CampaignDao campaignDao;

	public WebhookResource(MessageDao messageDao, CampaignDao campaignDao) {
		this.messageDao = messageDao;
		this.campaignDao = campaignDao;
	}

	@POST
	@Timed
	// Example: curl -X POST -H 'Content-Type: application/json' -d '[ { "sg_message_id": "1234", "email": "john.doe@example.com", "timestamp": 1337966815, "event": "click", "campaign_name": "xmas-campaign", "template_id": "colorfulTemplate" } ]' http://localhost:8080/webhook
	public void handleWebhookEvents(List<WebhookEvent> webhookEvents)
	{
		for (WebhookEvent event : webhookEvents) {
			messageDao.recordEvent(event.getSgMessageId(), event.getCampaignName(), event.getTemplateId(),
					event.getTimestamp(), event.getEventType());
			campaignDao.updateLastPotentiallyScoreAlteringChangeMse(event.getCampaignName(), System.currentTimeMillis());
		}
	}

}
