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

public class DictionaryCheck {

	public static final String DEFAULT_BLACKLIST_FILE = "parole_italiane.txt";

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
		return blacklist.contains(wordToCheck);
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
