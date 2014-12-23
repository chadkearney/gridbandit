package it.gridband.campaigner.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

public class Candidate {

	@NotBlank
	@Length(max = 100)
	private String name;

	@NotBlank
	@Length(max = 100)
	private String templateName;

	public Candidate() {
		// Intentionally blank.
	}

	public Candidate(String name, String templateName) {
		this.name = name;
		this.templateName = templateName;
	}

	@JsonProperty
	public String getName() {
		return name;
	}

	@JsonProperty
	public String getTemplateName() {
		return templateName;
	}

}
