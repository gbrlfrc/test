package org.rossonet.keycloak.spi;

import org.jboss.logging.Logger;
import org.keycloak.Config.Scope;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.policy.PasswordPolicyProvider;
import org.keycloak.policy.PasswordPolicyProviderFactory;

public class DictionaryPasswordPolicyProviderFactory implements PasswordPolicyProviderFactory {

	public static final String ID = "passwordUrlDictionary";
	private static final Logger LOG = Logger.getLogger(DictionaryPasswordPolicyProviderFactory.class);

	private Scope config;

	@Override
	public void close() {
		// nothing to do
	}

	@Override
	public PasswordPolicyProvider create(final KeycloakSession session) {
		return new DictionaryPasswordPolicyProvider(session.getContext(), this);
	}

	public Scope getConfig() {
		return config;
	}

	@Override
	public String getConfigType() {
		return PasswordPolicyProvider.STRING_CONFIG_TYPE;
	}

	@Override
	public String getDefaultConfigValue() {
		return "https://github.com/rossonet/keycloak-password-check-spi/raw/main/src/main/resources/parole_italiane.txt";
	}

	@Override
	public String getDisplayName() {
		return "Password Blacklist Dictionary from URL";
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public void init(final Scope config) {
		this.config = config;
		LOG.info("init of DictionaryPasswordPolicyProviderFactory done");
	}

	@Override
	public boolean isMultiplSupported() {
		return false;
	}

	@Override
	public void postInit(final KeycloakSessionFactory factory) {
		// nothing to do

	}

}
