package it.gridband.campaigner.dao;

import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.Result;
import it.gridband.campaigner.model.Message;
import it.gridband.campaigner.model.WebhookEventType;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.put;

public class CassandraMessageDao implements MessageDao {

	private Session session;
	private Mapper<Message> messageMapper;

	public CassandraMessageDao(Session session, Mapper<Message> messageMapper) {
		this.session = session;
		this.messageMapper = messageMapper;
	}

	@Override
	public void recordEvent(String messageId, String campaignName, String templateId, long timestamp, WebhookEventType eventType) {
		String eventTimestampMseColonMetric = timestamp + ":" + eventType.getSendgridApiEventName();

		Statement statement = QueryBuilder
				.update("gridbandit", "messages")
				.with(put("event_timestamp_mse_colon_metric_to_value", eventTimestampMseColonMetric, 1.0d))
				.where(eq("message_id", messageId))
				.and(eq("campaign_name", campaignName))
				.and(eq("template_id", templateId));

		session.execute(statement);
	}

	@Override
	public Result<Message> getAllForCampaignNameAndTemplateId(String campaignName, String templateId) {
		Statement statement = QueryBuilder.select()
				.all()
				.from("gridbandit", "messages")
				.where(eq("campaign_name", campaignName))
				.and(eq("template_id", templateId));

		return messageMapper.map(session.execute(statement));
	}
}
