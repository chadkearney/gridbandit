package it.gridband.campaigner.score;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.Map;

public class ExistingScoreExtractor {

	public ImmutableList<Double> extractScoresForKeys(ImmutableList<String> keys, final Optional<Map<String, Double>> optionalKeysAndScores) {
		return ImmutableList.copyOf(
				Lists.transform(keys, new Function<String, Double>() {
					@Override
					public Double apply(String input) {
						if (!optionalKeysAndScores.isPresent()) return 0d;
						Double score = optionalKeysAndScores.get().get(input);
						return (score != null) ? score : 0d;
					}
				}));
	}

}
