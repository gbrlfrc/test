package org.rossonet.keycloak.spi.dictionary;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import com.google.common.primitives.Chars;

public class DictionaryCheck {

	public static final String DEFAULT_BLACKLIST_FILE = "parole_italiane.txt";

	public static final char[] SPECIAL_CHARACTERS = new char[] { '\\', '"', '|', '!', '”', '£', '$', '%', '&', '/', '(',
			')', '=', '?', '’', '^', '[', ']', '*', '@', '#', '°', '_', '-', ':', '.', ';', ',', '<', '>', '€' };

	/*
	 * Il controllo automatico deve verificare la presenza di un termine del
	 * dizionario di riferimento all’interno della password: 1) se il termine del
	 * dizionario è presente la password è validata soltanto se nella parte restante
	 * della stessa, diversa dal termine incluso nel dizionario, sono rispettati
	 * contemporaneamente i seguenti controlli: a) lunghezza pari ad almeno 5
	 * caratteri, posizionati liberamente prima o dopo il termine del dizionario; b)
	 * nessun carattere uguale consecutivo; c) almeno 2 caratteri speciali diversi;
	 * d) almeno 1 carattere numerico; e) almeno 1 carattere alfabetico con lettera
	 * maiuscola; f) almeno 1 carattere alfabetico con lettera minuscola;
	 *
	 */
	public static boolean check(final Set<String> blacklist, final String wordToCheck) {
		boolean found = false;
		for (final String w : blacklist) {
			if (w.length() < 5) {
				// non controlla il match
			} else {
				if (wordToCheck.toLowerCase().contains(w.toLowerCase())) {
					if (w.length() + 5 > wordToCheck.length()) {
						found = true;
					}
					for (int i = 0; i < wordToCheck.length() - 1; i++) {
						if (wordToCheck.charAt(i) == wordToCheck.charAt(i + 1)) {
							found = true;
							break;
						}
					}
					int specialCounter = 0;
					int uppercase = 0;
					int number = 0;
					int lowercase = 0;
					for (int i = 0; i < wordToCheck.length(); i++) {
						if (Chars.contains(SPECIAL_CHARACTERS, wordToCheck.charAt(i))) {
							specialCounter++;
						}
						if (Character.isUpperCase(wordToCheck.charAt(i))) {
							uppercase++;
						}
						if (Character.isLowerCase(wordToCheck.charAt(i))) {
							lowercase++;
						}
						if (Character.isDigit(wordToCheck.charAt(i))) {
							number++;
						}
					}
					if (specialCounter < 2) {
						found = true;
					}
					if (uppercase < 1) {
						found = true;
					}
					if (lowercase < 1) {
						found = true;
					}
					if (number < 1) {
						found = true;
					}
				}
			}
			if (found) {
				break;
			}
		}

		return found;
	}

	public static Set<String> loadDictionary(final String dictionaryUrl) throws IOException, MalformedURLException {
		final Set<String> vocabolary = new HashSet<>();
		final InputStream is = (dictionaryUrl == null || dictionaryUrl.isEmpty())
				? DictionaryCheck.class.getClassLoader().getResourceAsStream(DEFAULT_BLACKLIST_FILE)
				: new ByteArrayInputStream(readStringFromURL(dictionaryUrl).getBytes());
		final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		while (reader.ready()) {
			vocabolary.add(reader.readLine());
		}
		return vocabolary;
	}

	public static String readStringFromURL(final String requestURL) throws IOException {
		try (Scanner scanner = new Scanner(new URL(requestURL).openStream(), StandardCharsets.UTF_8.toString())) {
			scanner.useDelimiter("\\A");
			return scanner.hasNext() ? scanner.next() : "";
		}
	}
}
