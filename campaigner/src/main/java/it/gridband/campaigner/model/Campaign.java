package it.gridband.campaigner.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

public class Campaign {

	@NotBlank
	@Length(max = 100)
	private String name;

	@NotNull
	@Length(max = 256)
	private String scoringFormula;

	private Set<String> candidateNames;

	public Campaign() {
		candidateNames = new HashSet<String>();
	}

	public Campaign(String name, String scoringFormula, Set<String> candidateNames) {
		this.name = name;
		this.scoringFormula = scoringFormula;
		this.candidateNames = candidateNames;
	}

	@JsonProperty
	public String getName() {
		return name;
	}

	@JsonProperty
	public String getScoringFormula() {
		return scoringFormula;
	}

	@JsonProperty
	public Set<String> getCandidateNames() {
		return candidateNames;
	}
}
