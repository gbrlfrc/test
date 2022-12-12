package org.rossonet.keycloak.spi.reminder;

import org.jboss.logging.Logger;
import org.keycloak.Config.Scope;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resource.RealmResourceProviderFactory;

public class SchedulerReminderProviderFactory implements RealmResourceProviderFactory {

	public static final String ID = "scheduler-reminder";
	private static final Logger LOG = Logger.getLogger(SchedulerReminderProviderFactory.class);
	public static final ReminderTimerTask REMINDER_TASK = new ReminderTimerTask();

	@Override
	public void close() {
		LOG.info("SchedulerReminderProviderFactory close");
	}

	@Override
	public RealmResourceProvider create(KeycloakSession session) {
		LOG.info("SchedulerReminderProviderFactory create");
		return new SchedulerReminderProvider(session);
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public void init(Scope config) {
		REMINDER_TASK.setConfig(config);
		LOG.info("SchedulerReminderProviderFactory init");
	}

	@Override
	public void postInit(KeycloakSessionFactory factory) {
		REMINDER_TASK.setKeycloakSessionFactory(factory);
		LOG.info("SchedulerReminderProviderFactory postInit");
	}

}
