package it.gridband.campaigner.config;

import javax.validation.constraints.NotNull;

public class RedisConfiguration {

	@NotNull
	private String campaignSummaryRedisHost;

	@NotNull
	private Integer campaignSummaryRedisPort;

	public String getCampaignSummaryRedisHost() {
		return campaignSummaryRedisHost;
	}

	public void setCampaignSummaryRedisHost(String campaignSummaryRedisHost) {
		this.campaignSummaryRedisHost = campaignSummaryRedisHost;
	}

	public Integer getCampaignSummaryRedisPort() {
		return campaignSummaryRedisPort;
	}

	public void setCampaignSummaryRedisPort(Integer campaignSummaryRedisPort) {
		this.campaignSummaryRedisPort = campaignSummaryRedisPort;
	}

}
