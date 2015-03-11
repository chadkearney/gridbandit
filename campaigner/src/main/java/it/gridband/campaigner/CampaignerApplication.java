package it.gridband.campaigner;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.MappingManager;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import it.gridband.campaigner.batch.ProbabilityUpdater;
import it.gridband.campaigner.config.CampaignerConfiguration;
import it.gridband.campaigner.config.CassandraConfiguration;
import it.gridband.campaigner.dao.CampaignDao;
import it.gridband.campaigner.dao.CassandraCampaignDao;
import it.gridband.campaigner.dao.CassandraMessageDao;
import it.gridband.campaigner.dao.MessageDao;
import it.gridband.campaigner.health.AliveHealthCheck;
import it.gridband.campaigner.management.CassandraClusterManaged;
import it.gridband.campaigner.management.CassandraSessionManaged;
import it.gridband.campaigner.model.Campaign;
import it.gridband.campaigner.model.Message;
import it.gridband.campaigner.probability.ArrayIndexDistributionFactory;
import it.gridband.campaigner.probability.WeightedArrayIndexDistributionFactoryWithUniformFallback;
import it.gridband.campaigner.resources.CampaignResource;
import it.gridband.campaigner.resources.TemplateResource;
import it.gridband.campaigner.resources.WebhookResource;
import it.gridband.campaigner.score.BasicPostfixFormulaFactory;
import it.gridband.campaigner.score.ExistingScoreExtractor;
import it.gridband.campaigner.score.PartialPoolingTemplateIdWeightCalculatorFactory;
import it.gridband.campaigner.score.PostOpenOverwritingMessageEventSummarizer;
import it.gridband.campaigner.select.ActiveTemplateIdSelector;
import it.gridband.campaigner.select.DistributionBasedActiveTemplateIdSelector;
import it.gridband.campaigner.select.MaxElementRandomTiebreakDoubleArrayIndexSelectorFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

		Cluster cluster = createManagedCassandraCluster(configuration.getCassandraConfiguration(), environment);
		Session session = createManagedCassandraSession(cluster, environment);

		MappingManager mappingManager = new MappingManager(session);
		final CampaignDao campaignDao = new CassandraCampaignDao(session, mappingManager.mapper(Campaign.class));
		final MessageDao messageDao = new CassandraMessageDao(session, mappingManager.mapper(Message.class));

		startProbabilityUpdater(environment, messageDao, campaignDao);

		ArrayIndexDistributionFactory factory = new WeightedArrayIndexDistributionFactoryWithUniformFallback();
		ActiveTemplateIdSelector activeTemplateIdSelector = new DistributionBasedActiveTemplateIdSelector(factory, new ExistingScoreExtractor());

		final CampaignResource campaignResource = new CampaignResource(campaignDao);
		environment.jersey().register(campaignResource);

		final TemplateResource templateResource = new TemplateResource(campaignDao, activeTemplateIdSelector);
		environment.jersey().register(templateResource);

		final WebhookResource messageResource = new WebhookResource(messageDao, campaignDao);
		environment.jersey().register(messageResource);

	}

	private Cluster createManagedCassandraCluster(CassandraConfiguration configuration, Environment environment)
	{
		Cluster cluster = Cluster.builder().addContactPoint(configuration.getAddress()).build();

		CassandraClusterManaged cassandraClusterManaged = new CassandraClusterManaged(cluster);
		environment.lifecycle().manage(cassandraClusterManaged);

		return cluster;
	}

	private Session createManagedCassandraSession(Cluster cluster, Environment environment)
	{
		Session session = cluster.connect();

		CassandraSessionManaged cassandraSessionManaged = new CassandraSessionManaged(session);
		environment.lifecycle().manage(cassandraSessionManaged);

		return session;
	}

	private void startProbabilityUpdater(Environment environment, MessageDao messageDao, CampaignDao campaignDao) {
		String probabilityUpdaterThreadNameFormat = "probability-updater-%d";
		ScheduledExecutorService probabilityUpdaterExecutorService =
				environment.lifecycle().scheduledExecutorService(probabilityUpdaterThreadNameFormat).build();
		Runnable probabilityUpdater = new ProbabilityUpdater(
				messageDao, campaignDao, new BasicPostfixFormulaFactory(),
				new PartialPoolingTemplateIdWeightCalculatorFactory(5000, 10000,
						new MaxElementRandomTiebreakDoubleArrayIndexSelectorFactory()
				),
				new PostOpenOverwritingMessageEventSummarizer());
		probabilityUpdaterExecutorService.scheduleWithFixedDelay(probabilityUpdater, 0, 15, TimeUnit.SECONDS);
	}
}
