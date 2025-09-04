package eu.fays.sandbox.datetime;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class RFC7231DateTime {

	/** Cf. <a href="https://www.rfc-editor.org/rfc/rfc7231#section-7.1.1.1">RFC7231 - Hypertext Transfer Protocol (HTTP/1.1): Semantics and Content &rArr; 7.1.1.1 Date/Time Formats</a>  */
	public static final DateTimeFormatter RFC7231_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("EEE, dd LLL yyyy HH:mm:ss z", Locale.ENGLISH).withZone(ZoneId.of("GMT"));

	public static void main(String[] args) {
		final ZonedDateTime now = ZonedDateTime.now();
		// https://stackoverflow.com/questions/7707555/getting-date-in-http-format-in-java
		// Used for Expires HTTP Header
		final String timestamp = RFC7231_DATE_TIME_FORMATTER.format(now);
		System.out.println(timestamp);
	}
}
