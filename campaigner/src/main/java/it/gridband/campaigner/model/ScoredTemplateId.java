package it.gridband.campaigner.model;

public class ScoredTemplateId {
	private String templateId;
	private Double score;

	public ScoredTemplateId(String templateId, Double score) {
		this.templateId = templateId;
		this.score = score;
	}

	public String getTemplateId() {
		return templateId;
	}

	public Double getScore() {
		return score;
	}
}
