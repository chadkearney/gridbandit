package it.gridband.campaigner.select;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import it.gridband.campaigner.model.Campaign;
import it.gridband.campaigner.probability.ArrayIndexDistribution;
import it.gridband.campaigner.probability.ArrayIndexDistributionFactory;
import it.gridband.campaigner.score.ExistingScoreExtractor;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DistributionBasedActiveTemplateIdSelectorTest {

	private final ArrayIndexDistributionFactory arrayIndexDistributionFactory = mock(ArrayIndexDistributionFactory.class);
	private final ExistingScoreExtractor existingScoreExtractor = mock(ExistingScoreExtractor.class);

	private final ArrayIndexDistribution arrayIndexDistribution = mock(ArrayIndexDistribution.class);
	private final Campaign campaign = mock(Campaign.class);

	@Test
	public void returnsAbsentWhenActiveTemplateIdSetIsNull() {
		DistributionBasedActiveTemplateIdSelector distributionBasedActiveTemplateIdSelector = new DistributionBasedActiveTemplateIdSelector(arrayIndexDistributionFactory, existingScoreExtractor);

		when(campaign.getActiveTemplateIds()).thenReturn(null);

		Optional<String> templateId = distributionBasedActiveTemplateIdSelector.select(campaign);

		assertFalse(templateId.isPresent());
	}

	@Test
	public void returnsAbsentWhenActiveTemplateIdSetIsEmpty() {
		DistributionBasedActiveTemplateIdSelector distributionBasedActiveTemplateIdSelector = new DistributionBasedActiveTemplateIdSelector(arrayIndexDistributionFactory, existingScoreExtractor);

		when(campaign.getActiveTemplateIds()).thenReturn(ImmutableSet.<String>of());

		Optional<String> templateId = distributionBasedActiveTemplateIdSelector.select(campaign);

		assertFalse(templateId.isPresent());
	}

	@Test
	public void returnsAbsentWhenDistributionCannotBeBuilt() {
		DistributionBasedActiveTemplateIdSelector distributionBasedActiveTemplateIdSelector = new DistributionBasedActiveTemplateIdSelector(arrayIndexDistributionFactory, existingScoreExtractor);
		Set<String> activeTemplateIds = ImmutableSet.of("template1");

		when(campaign.getActiveTemplateIds()).thenReturn(activeTemplateIds);
		when(existingScoreExtractor.extractScoresForKeys(Matchers.<ImmutableList<String>>any(), Matchers.<Optional<Map<String, Double>>>any())).thenReturn(ImmutableList.<Double>of());
		when(arrayIndexDistributionFactory.build(Matchers.<ImmutableList<Double>>any())).thenReturn(Optional.<ArrayIndexDistribution>absent());

		Optional<String> templateId = distributionBasedActiveTemplateIdSelector.select(campaign);

		assertFalse(templateId.isPresent());
	}

	@Test
	public void returnsExpectedTemplateIdWhenDistributionCanBeBuilt() {
		DistributionBasedActiveTemplateIdSelector distributionBasedActiveTemplateIdSelector = new DistributionBasedActiveTemplateIdSelector(arrayIndexDistributionFactory, existingScoreExtractor);
		Set<String> activeTemplateIds = ImmutableSet.of("template1", "template2");

		when(campaign.getActiveTemplateIds()).thenReturn(activeTemplateIds);
		when(existingScoreExtractor.extractScoresForKeys(Matchers.<ImmutableList<String>>any(), Matchers.<Optional<Map<String, Double>>>any())).thenReturn(ImmutableList.<Double>of());
		when(arrayIndexDistributionFactory.build(Matchers.<ImmutableList<Double>>any())).thenReturn(Optional.of(arrayIndexDistribution));
		when(arrayIndexDistribution.nextIndex()).thenReturn(1);

		Optional<String> templateId = distributionBasedActiveTemplateIdSelector.select(campaign);

		assertTrue(templateId.isPresent());
		assertEquals("template2", templateId.get());
	}

}
