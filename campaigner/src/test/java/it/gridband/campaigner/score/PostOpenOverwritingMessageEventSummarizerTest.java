package it.gridband.campaigner.score;

import com.google.common.collect.Maps;
import it.gridband.campaigner.model.Message;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PostOpenOverwritingMessageEventSummarizerTest {

	private static double TOLERANCE = 0.000001d;

	private PostOpenOverwritingMessageEventSummarizer summarizer = new PostOpenOverwritingMessageEventSummarizer();

	private Message message = mock(Message.class);

	@Test
	public void canHandleNullMessageEventMap() {
		when(message.getEventTimestampMseColonMetricToValue()).thenReturn(null);

		Map<String, Double> summary = summarizer.summarize(message);

		assertEquals(0, summary.size());
	}

	@Test
	public void canHandleEmptyMessageEventMap() {
		when(message.getEventTimestampMseColonMetricToValue()).thenReturn(Maps.<String, Double>newHashMap());

		Map<String, Double> summary = summarizer.summarize(message);

		assertEquals(0, summary.size());
	}

	@Test
	public void correctlySummarizesBasicEventMap() {
		Map<String, Double> eventMap = Maps.newHashMap();
		eventMap.put("0001:open", 3d);
		eventMap.put("0002:click", 2d);
		eventMap.put("0003:unsubscribe", 1d);

		when(message.getEventTimestampMseColonMetricToValue()).thenReturn(eventMap);

		Map<String, Double> summary = summarizer.summarize(message);

		assertEquals(3, summary.size());
		assertEquals(3d, summary.get("open"), TOLERANCE);
		assertEquals(2d, summary.get("click"), TOLERANCE);
		assertEquals(1d, summary.get("unsubscribe"), TOLERANCE);
	}

	@Test
	public void correctlyDiscardsPreOpenEvents() {
		Map<String, Double> eventMap = Maps.newHashMap();
		eventMap.put("0001:click", 3d);
		eventMap.put("0002:open", 2d);
		eventMap.put("0003:unsubscribe", 1d);

		when(message.getEventTimestampMseColonMetricToValue()).thenReturn(eventMap);

		Map<String, Double> summary = summarizer.summarize(message);

		assertEquals(2, summary.size());
		assertEquals(2d, summary.get("open"), TOLERANCE);
		assertEquals(1d, summary.get("unsubscribe"), TOLERANCE);
	}

	@Test
	public void correctlyOverwritesEarlierEventsWithLaterOnes() {
		Map<String, Double> eventMap = Maps.newHashMap();
		eventMap.put("0001:open", 3d);
		eventMap.put("0002:click", 2d);
		eventMap.put("0003:unsubscribe", 1d);
		eventMap.put("0004:unsubscribe", 0d);
		eventMap.put("0005:click", 1d);
		eventMap.put("0006:open", 5d);

		when(message.getEventTimestampMseColonMetricToValue()).thenReturn(eventMap);

		Map<String, Double> summary = summarizer.summarize(message);

		assertEquals(3, summary.size());
		assertEquals(5d, summary.get("open"), TOLERANCE);
		assertEquals(1d, summary.get("click"), TOLERANCE);
		assertEquals(0d, summary.get("unsubscribe"), TOLERANCE);
	}

	@Test
	public void correctlyDiscardsPreOpenEventsAndOverwritesEarlierEventsWithLaterOnes() {
		Map<String, Double> eventMap = Maps.newHashMap();
		eventMap.put("0001:click", 2d);
		eventMap.put("0002:open", 5d);
		eventMap.put("0003:unsubscribe", 1d);
		eventMap.put("0004:unsubscribe", 7d);


		when(message.getEventTimestampMseColonMetricToValue()).thenReturn(eventMap);

		Map<String, Double> summary = summarizer.summarize(message);

		assertEquals(2, summary.size());
		assertEquals(5d, summary.get("open"), TOLERANCE);
		assertEquals(7d, summary.get("unsubscribe"), TOLERANCE);
	}
}
