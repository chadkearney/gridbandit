package it.gridband.campaigner;

import it.gridband.campaigner.config.CampaignerConfiguration;
import it.gridband.campaigner.health.AliveHealthCheck;
import it.gridband.campaigner.resources.CampaignResource;
import it.gridband.campaigner.resources.CandidateResource;
import it.gridband.campaigner.resources.MessageResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class CampaignerApplication extends Application<CampaignerConfiguration> {

	public static void main(String[] args) throws Exception {
		new CampaignerApplication().run(args);
	}

	@Override
	public String getName() {
		return "campaigner";
	}

	@Override
	public void initialize(Bootstrap<CampaignerConfiguration> bootstrap) {
		// nothing to do yet
	}

	@Override
	public void run(CampaignerConfiguration configuration, Environment environment) {
		final AliveHealthCheck aliveHealthCheck = new AliveHealthCheck();
		environment.healthChecks().register("alive", aliveHealthCheck);

		final CampaignResource campaignResource = new CampaignResource();
		environment.jersey().register(campaignResource);

		final CandidateResource candidateResource = new CandidateResource();
		environment.jersey().register(candidateResource);

		final MessageResource messageResource = new MessageResource();
		environment.jersey().register(messageResource);

	}

}
