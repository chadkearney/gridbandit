package it.gridband.campaigner.dao;

import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import it.gridband.campaigner.model.WebhookEventType;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.put;

public class CassandraMessageDao implements MessageDao {

	private Session session;

	public CassandraMessageDao(Session session) {
		this.session = session;
	}

	@Override
	public void recordEvent(String messageId, String campaignName, String templateId, long timestamp, WebhookEventType eventType) {
		String eventTimestampMseColonMetric = timestamp + ":" + eventType.getSendgridApiKey();

		Statement statement = QueryBuilder
				.update("gridbandit", "messages")
				.with(put("event_timestamp_mse_colon_metric_to_value", eventTimestampMseColonMetric, 1.0d))
				.where(eq("message_id", messageId))
				.and(eq("campaign_name", campaignName))
				.and(eq("template_id", templateId));

		session.execute(statement);
	}
}
