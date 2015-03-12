package it.gridband.campaigner.batch;

import com.google.common.base.Optional;
import it.gridband.campaigner.dao.CampaignDao;
import it.gridband.campaigner.dao.MessageDao;
import it.gridband.campaigner.model.Campaign;
import it.gridband.campaigner.model.Message;
import it.gridband.campaigner.score.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class TemplateWeightUpdater implements Runnable {

	private MessageDao messageDao;
	private CampaignDao campaignDao;
	private PostfixFormulaFactory postfixFormulaFactory;
	private TemplateWeightCalculatorFactory templateWeightCalculatorFactory;
	private MessageEventSummarizer messageEventSummarizer;

	private final static Logger logger = LoggerFactory.getLogger(TemplateWeightUpdater.class);

	public TemplateWeightUpdater(MessageDao messageDao, CampaignDao campaignDao, PostfixFormulaFactory postfixFormulaFactory, TemplateWeightCalculatorFactory templateWeightCalculatorFactory, MessageEventSummarizer messageEventSummarizer) {
		this.messageDao = messageDao;
		this.campaignDao = campaignDao;
		this.postfixFormulaFactory = postfixFormulaFactory;
		this.templateWeightCalculatorFactory = templateWeightCalculatorFactory;
		this.messageEventSummarizer = messageEventSummarizer;
	}

	@Override
	public void run() {
		logger.info("TemplateWeightUpdater waking up.");

		List<Campaign> updatableCampaigns = campaignDao.getClaimableCampaignsWithOutOfDateTemplateWeights();

		for (Campaign campaign : updatableCampaigns) {
			if (campaignDao.tryClaimCampaignForTemplateWeightUpdate(campaign)) {
				logger.info("TemplateWeightUpdater claimed campaign " + campaign.getName() + "; updating.");

				long upperBoundChangeTimestamps = System.currentTimeMillis();

				Optional<Map<String, Double>> newWeights = generateNewWeightsForCampaign(campaign);
				if (!newWeights.isPresent()) {
					logger.error("TemplateWeightUpdater failed to generate new weights for campaign " + campaign.getName() + "; task will be retried.");
					continue;
				}

				campaignDao.updateTemplateWeights(campaign.getName(), newWeights.get(), upperBoundChangeTimestamps);
			}
		}

		logger.info("TemplateWeightUpdater complete.");
	}

	private Optional<Map<String, Double>> generateNewWeightsForCampaign(Campaign campaign) {
		Set<String> activeTemplateIds = campaign.getActiveTemplateIds();
		if (activeTemplateIds == null || activeTemplateIds.isEmpty()) {
			return Optional.absent();
		}

		TemplateIdWeightCalculator templateIdWeightCalculator = templateWeightCalculatorFactory.build();
		PostfixFormula formula = postfixFormulaFactory.build(campaign.getScoringFormula());

		for (String activeTemplateId : activeTemplateIds) {
			for (Message message : messageDao.getAllForCampaignNameAndTemplateId(campaign.getName(), activeTemplateId)) {
				Map<String, Double> eventSummary = messageEventSummarizer.summarize(message);
				double score = formula.evaluate(eventSummary);
				templateIdWeightCalculator.addScore(activeTemplateId, score);
				campaignDao.updateHeartbeatToPresent(campaign.getName());
			}
		}

		return Optional.of(templateIdWeightCalculator.generateWeights(activeTemplateIds));
	}
}
