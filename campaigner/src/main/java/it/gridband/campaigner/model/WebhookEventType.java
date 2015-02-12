package it.gridband.campaigner.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum WebhookEventType {
	DELIVERED("delivered"),
	OPEN("open"),
	CLICK("click"),
	SPAMREPORT("spamreport"),
	UNSUBSCRIBE("unsubscribe");

	private String sendgridApiKey;

	WebhookEventType(String sendgridApiKey) {
		this.sendgridApiKey = sendgridApiKey;
	}

	@JsonCreator
	public static WebhookEventType buildFromSendgridApiKey(String sendgridApiKey) {
		return WebhookEventType.valueOf(sendgridApiKey.toUpperCase());
	}

	@JsonValue
	public String getSendgridApiKey() {
		return sendgridApiKey;
	}
}
