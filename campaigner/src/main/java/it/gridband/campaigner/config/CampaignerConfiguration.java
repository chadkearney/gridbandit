package it.gridband.campaigner.config;

import io.dropwizard.Configuration;

import javax.validation.Valid;

public class CampaignerConfiguration extends Configuration {

	@Valid
	private CassandraConfiguration cassandraConfiguration;

	public CassandraConfiguration getCassandraConfiguration() {
		return cassandraConfiguration;
	}

	public void setCassandraConfiguration(CassandraConfiguration cassandraConfiguration) {
		this.cassandraConfiguration = cassandraConfiguration;
	}

}
