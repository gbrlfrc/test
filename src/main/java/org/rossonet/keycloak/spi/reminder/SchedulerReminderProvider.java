package org.rossonet.keycloak.spi.reminder;

import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.keycloak.services.resource.RealmResourceProvider;

public class SchedulerReminderProvider implements RealmResourceProvider {

	private static final Logger LOG = Logger.getLogger(SchedulerReminderProvider.class);

	public SchedulerReminderProvider(KeycloakSession session) {
		SchedulerReminderProviderFactory.REMINDER_TASK.setSessionFromProvider(session);
		LOG.info("SchedulerReminderProvider create");
	}

	@Override
	public void close() {
		LOG.info("SchedulerReminderProvider closed");

	}

	@Override
	public Object getResource() {
		LOG.info("SchedulerReminderProvider getResource");
		return SchedulerReminderProviderFactory.REMINDER_TASK;
	}

}
