package it.gridband.campaigner.config;

import io.dropwizard.Configuration;

import javax.validation.Valid;

public class CampaignerConfiguration extends Configuration {

	@Valid
	private RedisConfiguration redisConfiguration;

	public RedisConfiguration getRedisConfiguration() {
		return redisConfiguration;
	}

	public void setRedisConfiguration(RedisConfiguration redisConfiguration) {
		this.redisConfiguration = redisConfiguration;
	}

}
