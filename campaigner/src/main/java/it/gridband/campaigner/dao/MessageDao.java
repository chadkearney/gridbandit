package it.gridband.campaigner.dao;

import it.gridband.campaigner.model.WebhookEventType;

public interface MessageDao {
	void recordEvent(String messageId, String campaignName, String templateId, long timestamp, WebhookEventType eventType);
}
