package it.gridband.campaigner.config;

import javax.validation.constraints.NotNull;

public class CassandraConfiguration {

	@NotNull
	private String address;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

}
