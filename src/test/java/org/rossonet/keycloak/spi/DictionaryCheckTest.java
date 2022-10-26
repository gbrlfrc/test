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
	public void checkComplexRules() throws MalformedURLException, IOException {
		final Set<String> dictionary = DictionaryCheck.loadDictionary(null);
		System.out.println("found " + dictionary.size() + " words in null");
		boolean foundInDictionary = DictionaryCheck.check(dictionary, "parola");
		assertTrue(foundInDictionary);
		foundInDictionary = DictionaryCheck.check(dictionary, "!Lparola.2A");
		assertFalse(foundInDictionary);
		foundInDictionary = DictionaryCheck.check(dictionary, "!lparola.2a");
		assertTrue(foundInDictionary);
		foundInDictionary = DictionaryCheck.check(dictionary, "!Lparola.jA");
		assertTrue(foundInDictionary);
		foundInDictionary = DictionaryCheck.check(dictionary, "!parola.2A");
		assertTrue(foundInDictionary);
		foundInDictionary = DictionaryCheck.check(dictionary, "!LparolaR2A");
		assertTrue(foundInDictionary);
		foundInDictionary = DictionaryCheck.check(dictionary, "!LPAROLA.2A");
		assertTrue(foundInDictionary);
	}

	@Test
	public void checkWordInDictionary() throws MalformedURLException, IOException {
		final Set<String> dictionary = DictionaryCheck.loadDictionary(null);
		System.out.println("found " + dictionary.size() + " words in null");
		boolean foundInDictionary = DictionaryCheck.check(dictionary, "sg1erro");
		assertFalse(foundInDictionary);
		foundInDictionary = DictionaryCheck.check(dictionary, "cazzata");
		assertTrue(foundInDictionary);
		foundInDictionary = DictionaryCheck.check(dictionary, "porCocAne");
		assertTrue(foundInDictionary);
		foundInDictionary = DictionaryCheck.check(dictionary, "porcocervo");
		assertTrue(foundInDictionary);
	}

	@Test
	public void checkWordInDictionaryWithUrl() throws MalformedURLException, IOException {
		final Set<String> dictionary = DictionaryCheck.loadDictionary(
				"https://github.com/rossonet/keycloak-password-check-spi/raw/main/src/main/resources/parole_italiane.txt");
		System.out.println("found " + dictionary.size()
				+ " words in https://github.com/rossonet/keycloak-password-check-spi/raw/main/src/main/resources/parole_italiane.txt");
		boolean foundInDictionary = DictionaryCheck.check(dictionary, "sge5rro");
		assertFalse(foundInDictionary);
		foundInDictionary = DictionaryCheck.check(dictionary, "cazzata");
		assertTrue(foundInDictionary);
		foundInDictionary = DictionaryCheck.check(dictionary, "por.coc.ane");
		assertFalse(foundInDictionary);
		foundInDictionary = DictionaryCheck.check(dictionary, "porcocervo");
		assertTrue(foundInDictionary);
	}

}
