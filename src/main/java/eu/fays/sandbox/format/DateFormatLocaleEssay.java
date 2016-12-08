package eu.fays.sandbox.format;

import java.text.DateFormat;
import java.util.Arrays;

/**
 * Lists all available locales for date format
 */
public class DateFormatLocaleEssay {

	static public void main(String[] args) {
		Arrays.stream(DateFormat.getAvailableLocales()).map(l -> l.toString()).sorted().forEach(s -> System.out.println(s));
	}

}
