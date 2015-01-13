package it.gridband.campaigner.management;

import com.datastax.driver.core.Session;
import io.dropwizard.lifecycle.Managed;

public class CassandraSessionManaged implements Managed {

	private Session session;

	public CassandraSessionManaged(Session session) {
		this.session = session;
	}

	public void start() throws Exception {
		// nothing to do.
	}

	public void stop() throws Exception {
		session.close();
	}
}