package it.gridband.campaigner.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

public class Message {

	@NotBlank
	@Length(max = 100)
	private String id;

	public Message() {
		// Intentionally blank.
	}

	public Message(String id) {
		this.id = id;
	}

	@JsonProperty
	public String getId() {
		return id;
	}
}
