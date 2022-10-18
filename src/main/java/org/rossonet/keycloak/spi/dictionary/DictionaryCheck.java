package org.rossonet.keycloak.spi.dictionary;

import java.util.Set;

public class DictionaryCheck {

	/**
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

}
