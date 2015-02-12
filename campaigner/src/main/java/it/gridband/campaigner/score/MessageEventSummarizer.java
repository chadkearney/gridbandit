package it.gridband.campaigner.score;

import it.gridband.campaigner.model.Message;

import java.util.Map;

public interface MessageEventSummarizer {
	Map<String, Double> summarize(Message message);
}
