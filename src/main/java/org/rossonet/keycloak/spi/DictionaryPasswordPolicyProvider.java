package org.rossonet.keycloak.spi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jboss.logging.Logger;
import org.keycloak.authentication.forms.RegistrationPage;
import org.keycloak.models.KeycloakContext;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.policy.PasswordPolicyProvider;
import org.keycloak.policy.PolicyError;
import org.keycloak.services.messages.Messages;
import org.rossonet.keycloak.spi.dictionary.DictionaryCheck;

public class DictionaryPasswordPolicyProvider implements PasswordPolicyProvider {

	public static final String DEFAULT_BLACKLIST_FILE = "parole_italiane.txt";

	public static final String ERROR_MESSAGE = "invalidPasswordDictionaryMessage";

	private static final Logger LOG = Logger.getLogger(DictionaryPasswordPolicyProvider.class);

	static Set<String> loadDictionary(final String dictionaryUrl) throws IOException, MalformedURLException {
		final Set<String> vocabolary = new HashSet<>();
		final InputStream is = (dictionaryUrl == null || dictionaryUrl.isEmpty())
				? DictionaryPasswordPolicyProvider.class.getClassLoader().getResourceAsStream(DEFAULT_BLACKLIST_FILE)
				: new URL(dictionaryUrl).openStream();
		final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		while (reader.ready()) {
			vocabolary.add(reader.readLine());
		}
		return vocabolary;
	}

	private final KeycloakContext context;

	private final DictionaryPasswordPolicyProviderFactory factory;

	public DictionaryPasswordPolicyProvider(final KeycloakContext context,
			final DictionaryPasswordPolicyProviderFactory factory) {
		this.context = context;
		this.factory = factory;
		LOG.info("DictionaryPasswordPolicyProvider created");
	}

	@Override
	public void close() {
		// nothing to do

	}

	public DictionaryPasswordPolicyProviderFactory getFactory() {
		return factory;
	}

	@Override
	public Object parseConfig(final String dictionaryUrl) {
		try {
			LOG.info("parse diction config from url: " + dictionaryUrl);
			final Set<String> vocabolary = loadDictionary(dictionaryUrl);
			LOG.info("found " + vocabolary.size() + " words in " + dictionaryUrl);
			return vocabolary;
		} catch (final IOException e) {
			LOG.error("parsing dictionary", e);
			return null;
		}
	}

	@Override
	public PolicyError validate(final RealmModel realm, final UserModel user, final String password) {
		return validate(user.getUsername(), password);
	}

	@Override
	public PolicyError validate(final String user, final String password) {
		final Object policyConfig = context.getRealm().getPasswordPolicy()
				.getPolicyConfig(DictionaryPasswordPolicyProviderFactory.ID);
		if (policyConfig == null) {
			return null;
		}

		if (!(policyConfig instanceof Set<?>)) {
			return null;
		}

		@SuppressWarnings("unchecked")
		final Set<String> blacklist = (Set<String>) policyConfig;

		final List<FormMessage> errors = new ArrayList<>();
		if (DictionaryCheck.check(blacklist, password)) {
			errors.add(new FormMessage(RegistrationPage.FIELD_PASSWORD, Messages.INVALID_PASSWORD));
		}
		if (errors.size() > 0) {
			return new PolicyError(ERROR_MESSAGE);
		} else {
			return null;
		}
	}

}
