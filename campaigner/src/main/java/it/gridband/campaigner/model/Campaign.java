package it.gridband.campaigner.model;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Set;

@Table(keyspace = "gridbandit", name = "campaigns")
public class Campaign {

	@NotBlank
	@Length(max = 100)
	@PartitionKey
	@Column(name = "name")
	private String name;

	@Column(name = "active_template_ids")
	private Set<String> activeTemplateIds;

	@Column(name = "template_id_to_probability")
	private Map<String, Double> templateIdToProbability;

	@NotNull
	@Length(max = 1024)
	@Column(name = "scoring_formula")
	private String scoringFormula;

	@Column(name = "last_potentially_score_altering_change_mse")
	private Long lastPotentiallyScoreAlteringChangeMse;

	@Column(name = "template_probabilities_include_changes_up_to_mse")
	private Long templateProbabilitiesIncludeChangesUpToMse;

	@Column(name = "template_probabilities_update_heartbeat_mse")
	private Long templateProbabilitiesUpdateHeartbeatMse;


	@JsonProperty
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty
	public Set<String> getActiveTemplateIds() {
		return activeTemplateIds;
	}
	public void setActiveTemplateIds(Set<String> activeTemplateIds) {
		this.activeTemplateIds = activeTemplateIds;
	}

	@JsonIgnore
	public Long getLastPotentiallyScoreAlteringChangeMse() {
		return lastPotentiallyScoreAlteringChangeMse;
	}
	public void setLastPotentiallyScoreAlteringChangeMse(Long lastPotentiallyScoreAlteringChangeMse) {
		this.lastPotentiallyScoreAlteringChangeMse = lastPotentiallyScoreAlteringChangeMse;
	}

	@JsonProperty
	public String getScoringFormula() {
		return scoringFormula;
	}
	public void setScoringFormula(String scoringFormula) {
		this.scoringFormula = scoringFormula;
	}

	@JsonProperty
	public Map<String, Double> getTemplateIdToProbability() {
		return templateIdToProbability;
	}
	public void setTemplateIdToProbability(Map<String, Double> templateIdToProbability) {
		this.templateIdToProbability = templateIdToProbability;
	}

	@JsonProperty
	public Long getTemplateProbabilitiesIncludeChangesUpToMse() {
		return templateProbabilitiesIncludeChangesUpToMse;
	}
	public void setTemplateProbabilitiesIncludeChangesUpToMse(Long templateProbabilitiesIncludeChangesUpToMse) {
		this.templateProbabilitiesIncludeChangesUpToMse = templateProbabilitiesIncludeChangesUpToMse;
	}

	@JsonIgnore
	public Long getTemplateProbabilitiesUpdateHeartbeatMse() {
		return templateProbabilitiesUpdateHeartbeatMse;
	}
	public void setTemplateProbabilitiesUpdateHeartbeatMse(Long templateProbabilitiesUpdateHeartbeatMse) {
		this.templateProbabilitiesUpdateHeartbeatMse = templateProbabilitiesUpdateHeartbeatMse;
	}
}
