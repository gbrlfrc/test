package org.rossonet.keycloak.spi.reminder;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;

import org.jboss.logging.Logger;
import org.keycloak.Config.Scope;
import org.keycloak.credential.CredentialModel;
import org.keycloak.email.DefaultEmailSenderProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

public class ReminderTimerTask {

	private static final String PASSWORD = "password";

	private static final Logger LOG = Logger.getLogger(ReminderTimerTask.class);

	private static final long CHECK_FIRST_INTERVAL = 120000L;

	private static final Timer timer = new Timer("ReminderTimerTask");

	private static final long MS_IN_DAY = 24 * 60 * 60 * 1000;

	private final TimerTask task = new TimerTask() {

		@Override
		public void run() {
			try {
				checkPasswordExpiration();
			} catch (final Throwable aa) {
				LOG.error("during reminder password expiration task", aa);
			}
		}

	};

	private KeycloakSessionFactory factory;

	@SuppressWarnings("unused")
	private Scope config;
	@SuppressWarnings("unused")
	private KeycloakSession currentSession;

	public ReminderTimerTask() {
		timer.schedule(task, CHECK_FIRST_INTERVAL, MS_IN_DAY);
		LOG.info("ReminderTimerTask started");
	}

	private long calcolateDaysBeforeExpire(Long createdDate, int expireInDays) {
		return createdDate + (expireInDays * MS_IN_DAY) - System.currentTimeMillis();
	}

	private int checkPasswordExpiration() {
		final KeycloakSession session = factory.create();
		int total = 0;
		for (final RealmModel realm : session.realmLocalStorage().getRealms()) {
			final int expireInDays = realm.getPasswordPolicy().getDaysToExpirePassword();
			if (expireInDays > 1) {
				LOG.info("working realm " + realm.getName());
				for (final UserModel user : session.userLocalStorage().getUsers(realm, false)) {
					LOG.info("working user " + user.getUsername());
					total = total + workSingleUser(session, realm, user, expireInDays);
				}
			} else {
				LOG.info("the realm " + realm.getName() + " has a DaysToExpirePasswordPolicy < 1 [" + expireInDays
						+ "]");
			}
		}
		LOG.info("checkPasswordExpiration call completed");
		return total;
	}

	@GET
	@Produces("text/plain; charset=utf-8")
	public String getStatus() {
		return "the plugin works";
	}

	private void sendMailToUser(KeycloakSession session, RealmModel realm, UserModel user, long passwordValidForDays)
			throws Exception {
		LOG.info("send mail to user " + user.getUsername() + " at email " + user.getEmail()
				+ " because the password expires in " + passwordValidForDays + " days");
		final DefaultEmailSenderProvider senderProvider = new DefaultEmailSenderProvider(session);
		senderProvider.send(realm.getSmtpConfig(), user, "your password expire in " + passwordValidForDays + " days",
				"Your password will expire in " + passwordValidForDays + " days\n\nthanks", null);
		LOG.info("sent mail to user " + user.getUsername() + " at email " + user.getEmail()
				+ " because the password expires in " + passwordValidForDays + " days");
	}

	public void setConfig(Scope config) {
		this.config = config;
		LOG.info("setConfig call completed " + config);
	}

	public void setKeycloakSessionFactory(KeycloakSessionFactory factory) {
		this.factory = factory;
		LOG.info("setKeycloakSessionFactory call completed " + factory);
	}

	public void setSessionFromProvider(KeycloakSession session) {
		this.currentSession = session;
		LOG.info("setSessionFromProvider call completed " + session);
	}

	private int workSingleUser(KeycloakSession session, RealmModel realm, UserModel user, int expireInDays) {
		int sent = 0;
		final List<CredentialModel> credentials = session.userCredentialManager().getStoredCredentials(realm, user);
		for (final CredentialModel credential : credentials) {
			if (credential.getType().equals(PASSWORD)) {
				final long validForMs = calcolateDaysBeforeExpire(credential.getCreatedDate(), expireInDays);
				final long passwordValidForDays = validForMs / MS_IN_DAY;
				LOG.info("the credential -> " + credential.getType() + "was created " + credential.getCreatedDate()
						+ " and is valid for other " + passwordValidForDays + " days");
				if (passwordValidForDays == 1 || passwordValidForDays == 2 || passwordValidForDays == 3
						|| passwordValidForDays == 7 || passwordValidForDays == 14 || passwordValidForDays == 30) {
					if (user.getEmail() != null && !user.getEmail().isEmpty()) {
						try {
							sendMailToUser(session, realm, user, passwordValidForDays);
						} catch (final Throwable e) {
							LOG.error("exception sending mail to user " + user.getUsername()
									+ ", the password expires in " + passwordValidForDays + " days", e);
						}
						sent++;
					} else {
						LOG.warn("for user " + user.getUsername() + " we haven't the email and the password expires in "
								+ passwordValidForDays + " days");
					}
				}
			}
		}
		return sent;
	}

}
