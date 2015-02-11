package it.gridband.campaigner.probability;

import com.google.common.base.Optional;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class UniformArrayIndexDistribution implements ArrayIndexDistribution {

	private int indexCount;

	private Optional<Random> random;

	public UniformArrayIndexDistribution(int indexCount) {
		this(indexCount, Optional.<Random>absent());
	}

	public UniformArrayIndexDistribution(int indexCount, Optional<Random> random) {
		if (indexCount <= 0) {
			throw new IllegalArgumentException("UniformArrayIndexDistribution: positive indexCount required.");
		}
		this.random = random;
		this.indexCount = indexCount;
	}

	@Override
	public int nextIndex() {
		return random().nextInt(indexCount);
	}

	private Random random() {
		return random.isPresent() ? random.get() : ThreadLocalRandom.current();
	}
}
