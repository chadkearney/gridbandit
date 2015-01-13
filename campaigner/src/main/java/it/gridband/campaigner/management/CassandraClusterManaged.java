package it.gridband.campaigner.management;

import com.datastax.driver.core.Cluster;
import io.dropwizard.lifecycle.Managed;

public class CassandraClusterManaged implements Managed {

	private Cluster cluster;

	public CassandraClusterManaged(Cluster cluster) {
		this.cluster = cluster;
	}

	public void start() throws Exception {
		// nothing to do.
	}

	public void stop() throws Exception {
		cluster.close();
	}
}