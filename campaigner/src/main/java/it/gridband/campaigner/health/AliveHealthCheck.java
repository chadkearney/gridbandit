package it.gridband.campaigner.health;

import com.codahale.metrics.health.HealthCheck;

public class AliveHealthCheck extends HealthCheck {

	@Override
	protected Result check() throws Exception {
		return Result.healthy();
	}

}
