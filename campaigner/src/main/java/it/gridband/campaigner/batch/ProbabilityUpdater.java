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

public class ProbabilityUpdater implements Runnable {

	private MessageDao messageDao;
	private CampaignDao campaignDao;
	private PostfixFormulaFactory postfixFormulaFactory;
	private TemplateIdWeightCalculatorFactory templateIdWeightCalculatorFactory;
	private MessageEventSummarizer messageEventSummarizer;

	final static Logger logger = LoggerFactory.getLogger(ProbabilityUpdater.class);

	public ProbabilityUpdater(MessageDao messageDao, CampaignDao campaignDao, PostfixFormulaFactory postfixFormulaFactory, TemplateIdWeightCalculatorFactory templateIdWeightCalculatorFactory, MessageEventSummarizer messageEventSummarizer) {
		this.messageDao = messageDao;
		this.campaignDao = campaignDao;
		this.postfixFormulaFactory = postfixFormulaFactory;
		this.templateIdWeightCalculatorFactory = templateIdWeightCalculatorFactory;
		this.messageEventSummarizer = messageEventSummarizer;
	}

	@Override
	public void run() {
		logger.info("ProbabilityUpdater waking up.");

		List<Campaign> updatableCampaigns = campaignDao.getClaimableCampaignsWithOutOfDateTemplateProbabilities();

		for (Campaign campaign : updatableCampaigns) {
			if (campaignDao.tryClaimCampaignForTemplateProbabilityUpdate(campaign)) {
				logger.info("ProbabilityUpdater claimed campaign " + campaign.getName() + "; updating.");

				long upperBoundChangeTimestamps = System.currentTimeMillis();

				Optional<Map<String, Double>> newProbabilities = generateNewProbabilitiesForCampaign(campaign);
				if (!newProbabilities.isPresent()) {
					logger.error("ProbabilityUpdater failed to generate new probabilities for campaign " + campaign.getName() + "; task will be retried.");
					continue;
				}

				campaignDao.updateTemplateProbabilities(campaign.getName(), newProbabilities.get(), upperBoundChangeTimestamps);
			}
		}

		logger.info("ProbabilityUpdater complete.");
	}

	private Optional<Map<String, Double>> generateNewProbabilitiesForCampaign(Campaign campaign) {
		Set<String> activeTemplateIds = campaign.getActiveTemplateIds();
		if (activeTemplateIds == null || activeTemplateIds.isEmpty()) {
			return Optional.absent();
		}

		TemplateIdWeightCalculator templateIdWeightCalculator = templateIdWeightCalculatorFactory.build();
		PostfixFormula formula = postfixFormulaFactory.build(campaign.getScoringFormula());

		for (String activeTemplateId : activeTemplateIds) {
			for (Message message : messageDao.getAllForCampaignNameAndTemplateId(campaign.getName(), activeTemplateId)) {
				Map<String, Double> eventSummary = messageEventSummarizer.summarize(message);
				double score = formula.evaluate(eventSummary);
				templateIdWeightCalculator.addScore(activeTemplateId, score);
				campaignDao.updateHeartbeatToPresent(campaign.getName());
			}
		}

		return Optional.of(templateIdWeightCalculator.generateWeights());
	}
}
