package it.gridband.campaigner.score;

import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Maps;
import it.gridband.campaigner.model.Message;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

public class PostOpenOverwritingMessageEventSummarizer implements MessageEventSummarizer {

	@Override
	public Map<String, Double> summarize(Message message) {
		boolean openHasOccurred = false;

		Map<String, Double> eventToValue = Maps.newHashMap();
		for (Map.Entry<String, Double> eventTimestampMseColonMetricToValue : toSortedMap(message.getEventTimestampMseColonMetricToValue()).entrySet()) {

			double value = eventTimestampMseColonMetricToValue.getValue();
			String metric = StringUtils.split(eventTimestampMseColonMetricToValue.getKey(), ':')[1];

			openHasOccurred = openHasOccurred || (metric.equals("open") && value > 0);

			if (openHasOccurred) {
				eventToValue.put(metric, value);
			}
		}

		return eventToValue;
	}

	private ImmutableSortedMap<String, Double> toSortedMap(Map<String, Double> input) {
		return (input == null)
				? ImmutableSortedMap.<String, Double>of()
				:  ImmutableSortedMap.<String, Double>naturalOrder().putAll(input).build();
	}
}
