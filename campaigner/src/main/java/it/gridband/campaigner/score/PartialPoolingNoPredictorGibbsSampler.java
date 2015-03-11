package it.gridband.campaigner.score;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Longs;
import edu.stanford.nlp.math.ArrayMath;
import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;

import java.util.ArrayList;

// A fairly faithful Java translation of the Gibbs sampler approach to "Rubin's 8 schools" problem, as given/discussed
// in Gelman et al.'s Bayesian Data Analysis.
public class PartialPoolingNoPredictorGibbsSampler {

	private RandomGenerator randomGenerator;

	private final double overallMean;
	private final double overallStandardDeviation;
	private final double[] y;
	private final double[] sigma;
	private final long[] groupSizes;

	private double alpha;
	private double mu;
	private double tau;
	private double[] gamma;

	public PartialPoolingNoPredictorGibbsSampler(double overallMean, double overallStandardDeviation, ArrayList<Double> groupMeans, ArrayList<Double> standardErrorOfGroupMeans, ArrayList<Long> groupSizes) {
		this.overallMean = overallMean;
		this.overallStandardDeviation = overallStandardDeviation;

		this.y = Doubles.toArray(groupMeans);
		this.sigma = Doubles.toArray(standardErrorOfGroupMeans);
		this.groupSizes = Longs.toArray(groupSizes);

		this.randomGenerator = new MersenneTwister();
	}

	public double[] getCurrentEstimatedGroupMeans() {
		return ArrayMath.add(ArrayMath.multiply(gamma, alpha), mu);
	}

	public double getCurrentEstimatedGroupOverallMean() {
		return mu;
	}

	public double getCurrentEstimatedBetweenGroupStdDev() {
		return Math.abs(alpha) * tau;
	}

	public boolean passesPreconditions() {
		if (y.length <= 1) {
			return false;
		}

		for (long groupSize : groupSizes) {
			if (groupSize <= 0) {
				return false;
			}
		}

		boolean atLeastOneMeanEstimateHasNonZeroVariance = false;
		for (double observedStandardErrorsOfObservedMean : sigma) {
			if (observedStandardErrorsOfObservedMean > 0) {
				atLeastOneMeanEstimateHasNonZeroVariance = true;
			}
		}

		return atLeastOneMeanEstimateHasNonZeroVariance;
	}

	public void prepareForWalk() {
		// Use a pooled estimate of underlying score variance for groups with zero variance.
		for (int i = 0; i < sigma.length; i++) {
			if (sigma[i] <= 0) {
				sigma[i] = overallStandardDeviation / Math.sqrt(groupSizes[i]);
			}
		}

		alpha = 1;
		gamma = new double[y.length];
		mu = new NormalDistribution(randomGenerator, overallMean, overallStandardDeviation).sample();
		tau = randomGenerator.nextDouble() * overallStandardDeviation;
	}

	public void step() {
		for (int i = 0; i < gamma.length; i++) {
			gamma[i] = updateGammaI(i);
		}
		alpha = updateAlpha();
		mu = updateMu();
		tau = updateTau();
	}

	private double updateGammaI(int i) {
		double V_gamma = 1 / (Math.pow(tau, -2) + (Math.pow(alpha, 2) / Math.pow(sigma[i], 2)));
		double gamma_hat = V_gamma * alpha * (y[i] - mu) / Math.pow(sigma[i], 2);

		return new NormalDistribution(randomGenerator, gamma_hat, Math.sqrt(V_gamma)).sample();
	}

	private double updateAlpha() {
		double V_alpha = 1 / ArrayMath.sum(
				ArrayMath.pairwiseMultiply(ArrayMath.pow(gamma, 2), ArrayMath.pow(sigma, -2)));
		double alpha_hat = V_alpha * ArrayMath.sum(
				ArrayMath.pairwiseMultiply(
						ArrayMath.pairwiseMultiply(ArrayMath.add(y, -mu), gamma),
						ArrayMath.pow(sigma, -2)));

		return new NormalDistribution(randomGenerator, alpha_hat, Math.sqrt(V_alpha)).sample();
	}

	private double updateMu() {
		double V_mu = 1 / ArrayMath.sum(ArrayMath.pow(sigma, -2));
		double mu_hat = V_mu * ArrayMath.sum(
				ArrayMath.pairwiseMultiply(
						ArrayMath.pairwiseAdd(y, ArrayMath.multiply(gamma, -alpha)), ArrayMath.pow(sigma, -2)));

		return new NormalDistribution(randomGenerator, mu_hat, Math.sqrt(V_mu)).sample();
	}

	private double updateTau() {
		return Math.sqrt(ArrayMath.sum(ArrayMath.pow(gamma, 2)) /
				new ChiSquaredDistribution(randomGenerator, gamma.length - 1).sample());
	}
}
