package it.gridband.campaigner.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.jackson.JsonSnakeCase;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@JsonSnakeCase
@JsonIgnoreProperties(ignoreUnknown = true)
public class WebhookEvent {
	@NotBlank
	private String sgMessageId;

	@NotNull
	private Long timestamp;

	@NotNull
	private WebhookEventType eventType;

	@NotBlank
	private String campaignName;

	@NotBlank
	private String templateId;

	@JsonProperty
	public String getSgMessageId() {
		return sgMessageId;
	}

	public void setSgMessageId(String sgMessageId) {
		this.sgMessageId = sgMessageId;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	@JsonProperty("event")
	public WebhookEventType getEventType() {
		return eventType;
	}

	public void setEventType(WebhookEventType eventType) {
		this.eventType = eventType;
	}

	@JsonProperty
	public String getCampaignName() {
		return campaignName;
	}

	public void setCampaignName(String campaignName) {
		this.campaignName = campaignName;
	}

	@JsonProperty
	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
}
