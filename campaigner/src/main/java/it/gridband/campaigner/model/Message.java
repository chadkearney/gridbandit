package it.gridband.campaigner.model;

import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import java.util.Map;

@Table(keyspace = "gridbandit", name = "messages")
public class Message {

	@NotBlank
	@Length(max = 100)
	@ClusteringColumn
	@Column(name = "message_id")
	private String messageId;


	@NotBlank
	@Length(max = 100)
	@PartitionKey(0)
	@Column(name = "campaign_name")
	private String campaignName;

	@NotBlank
	@Length(max = 100)
	@PartitionKey(1)
	@Column(name = "template_id")
	private String templateId;

	@Column(name = "event_timestamp_mse_colon_metric_to_value")
	private Map<String, Double> eventTimestampMseColonMetricToValue;


	public String getCampaignName() {
		return campaignName;
	}
	public void setCampaignName(String campaignName) {
		this.campaignName = campaignName;
	}

	public String getTemplateId() {
		return templateId;
	}
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public Map<String, Double> getEventTimestampMseColonMetricToValue() {
		return eventTimestampMseColonMetricToValue;
	}
	public void setEventTimestampMseColonMetricToValue(Map<String, Double> eventTimestampMseColonMetricToValue) {
		this.eventTimestampMseColonMetricToValue = eventTimestampMseColonMetricToValue;
	}
}
