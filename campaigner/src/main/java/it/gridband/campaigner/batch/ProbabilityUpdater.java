package it.gridband.campaigner.batch;

import com.google.common.collect.Maps;
import it.gridband.campaigner.dao.CampaignDao;
import it.gridband.campaigner.dao.MessageDao;
import it.gridband.campaigner.model.Campaign;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class ProbabilityUpdater implements Runnable {

	private MessageDao messageDao;
	private CampaignDao campaignDao;

	final static Logger logger = LoggerFactory.getLogger(ProbabilityUpdater.class);

	public ProbabilityUpdater(MessageDao messageDao, CampaignDao campaignDao) {
		this.messageDao = messageDao;
		this.campaignDao = campaignDao;
	}

	// TODO: Absorb all exceptions, so that the executor won't die.
	@Override
	public void run() {
		logger.info("ProbabilityUpdater waking up.");

		List<Campaign> updatableCampaigns = campaignDao.getClaimableCampaignsWithOutOfDateTemplateProbabilities();

		for (Campaign campaign : updatableCampaigns) {
			if (campaignDao.tryClaimCampaignForTemplateProbabilityUpdate(campaign)) {
				logger.info("ProbabilityUpdater claimed campaign " + campaign.getName() + "; updating.");

				long upperBoundChangeTimestamps = System.currentTimeMillis();
				Map<String, Double> newProbabilities = Maps.newHashMap();
				for (String activeTemplateId : campaign.getActiveTemplateIds()) {
					newProbabilities.put(activeTemplateId, 1d / campaign.getActiveTemplateIds().size());
				}

				campaignDao.updateTemplateProbabilities(campaign.getName(), newProbabilities, upperBoundChangeTimestamps);
			}
		}

		logger.info("ProbabilityUpdater complete.");
	}
}
