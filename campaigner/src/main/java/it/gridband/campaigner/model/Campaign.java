package it.gridband.campaigner.model;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.HashSet;
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

	@Column(name = "last_potentially_score_altering_mutation_mse")
	private long lastPotentiallyScoreAlteringMutationMse;

	@NotNull
	@Length(max = 256)
	@Column(name = "scoring_formula")
	private String scoringFormula;

	@Column(name = "template_probabilities")
	private Map<String, Double> templateProbabilities;

	@Column(name = "template_probabilities_last_updated_mse")
	private Long templateProbabilitiesLastUpdatedMse;

	@Column(name = "template_probabilities_update_last_activity_mse")
	private Long templateProbabilitiesUpdateLastActivityMse;

	public Campaign() {
		activeTemplateIds = new HashSet<String>();
		templateProbabilities = new HashMap<String, Double>();
		lastPotentiallyScoreAlteringMutationMse = System.currentTimeMillis();
	}

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
	public long getLastPotentiallyScoreAlteringMutationMse() {
		return lastPotentiallyScoreAlteringMutationMse;
	}
	public void setLastPotentiallyScoreAlteringMutationMse(long lastPotentiallyScoreAlteringMutationMse) {
		this.lastPotentiallyScoreAlteringMutationMse = lastPotentiallyScoreAlteringMutationMse;
	}

	@JsonProperty
	public String getScoringFormula() {
		return scoringFormula;
	}
	public void setScoringFormula(String scoringFormula) {
		this.scoringFormula = scoringFormula;
	}

	@JsonProperty
	public Map<String, Double> getTemplateProbabilities() {
		return templateProbabilities;
	}
	public void setTemplateProbabilities(Map<String, Double> templateProbabilities) {
		this.templateProbabilities = templateProbabilities;
	}

	@JsonProperty
	public Long getTemplateProbabilitiesLastUpdatedMse() {
		return templateProbabilitiesLastUpdatedMse;
	}
	public void setTemplateProbabilitiesLastUpdatedMse(Long templateProbabilitiesLastUpdatedMse) {
		this.templateProbabilitiesLastUpdatedMse = templateProbabilitiesLastUpdatedMse;
	}

	@JsonIgnore
	public Long getTemplateProbabilitiesUpdateLastActivityMse() {
		return templateProbabilitiesUpdateLastActivityMse;
	}
	public void setTemplateProbabilitiesUpdateLastActivityMse(Long templateProbabilitiesUpdateLastActivityMse) {
		this.templateProbabilitiesUpdateLastActivityMse = templateProbabilitiesUpdateLastActivityMse;
	}
}
