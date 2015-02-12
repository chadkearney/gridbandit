package it.gridband.campaigner.dao;

import com.datastax.driver.mapping.Result;
import it.gridband.campaigner.model.Message;
import it.gridband.campaigner.model.WebhookEventType;

public interface MessageDao {
	void recordEvent(String messageId, String campaignName, String templateId, long timestamp, WebhookEventType eventType);
	Result<Message> getAllForCampaignNameAndTemplateId(String campaignName, String templateId);
}
