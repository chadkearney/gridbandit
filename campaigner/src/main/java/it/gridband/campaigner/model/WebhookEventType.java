package it.gridband.campaigner.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum WebhookEventType {
	DELIVERED("delivered"),
	OPEN("open"),
	CLICK("click"),
	SPAMREPORT("spamreport"),
	UNSUBSCRIBE("unsubscribe");

	private String sendgridApiEventName;

	WebhookEventType(String sendgridApiEventName) {
		this.sendgridApiEventName = sendgridApiEventName;
	}

	@JsonCreator
	public static WebhookEventType buildFromSendgridApiEventName(String sendgridApiEventName) {
		return WebhookEventType.valueOf(sendgridApiEventName.toUpperCase());
	}

	@JsonValue
	public String getSendgridApiEventName() {
		return sendgridApiEventName;
	}
}
