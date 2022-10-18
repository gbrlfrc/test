package org.rossonet.keycloak.spi;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.rossonet.keycloak.spi.dictionary.DictionaryCheck;

public class DictionaryCheckTest {

	@Test
	public void checkWordInDictionary() throws MalformedURLException, IOException {
		final Set<String> dictionary = DictionaryPasswordPolicyProvider.loadDictionary(null);
		boolean foundInDictionary = DictionaryCheck.check(dictionary, "sgerro");
		assertFalse(foundInDictionary);
		foundInDictionary = DictionaryCheck.check(dictionary, "cazzata");
		assertTrue(foundInDictionary);
		foundInDictionary = DictionaryCheck.check(dictionary, "porcocane");
		assertFalse(foundInDictionary);
		foundInDictionary = DictionaryCheck.check(dictionary, "porcocervo");
		assertTrue(foundInDictionary);
	}

}
