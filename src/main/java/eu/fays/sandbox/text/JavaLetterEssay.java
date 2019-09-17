package eu.fays.sandbox.text;

import java.text.MessageFormat;

public class JavaLetterEssay {

	static final char[] letters = { 'a', '~', '-', '\u2011', '\u2012', '\u2013', '\u2014', '\u2212', '\u254D', '\uFE58', '\uFFDA' /* HALFWIDTH HANGUL LETTER EU */ };

	public static void main(String[] args) {
		int i = 0;
		for (char l : letters) {
			String x = Integer.toHexString(l).toUpperCase();
			System.out.println(MessageFormat.format("{0,number,00} \\u{1} {2} {3} ", i, x.length() == 2 ? "00" + x : x, l, Character.isJavaIdentifierPart(l)));

			i++;
		}

	}
}
