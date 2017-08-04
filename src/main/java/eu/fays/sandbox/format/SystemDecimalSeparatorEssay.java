package eu.fays.sandbox.format;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DecimalFormatSymbols;
import java.util.concurrent.TimeUnit;

public class SystemDecimalSeparatorEssay {

	public static void main(String[] args) {
		System.out.println(System.getProperty("os.name"));
		char decimalSeparator = detectSystemDecimalSeparator();
		System.out.println(decimalSeparator);
	}

	public static char detectSystemDecimalSeparator() {
		char result = new DecimalFormatSymbols().getDecimalSeparator();
		if ("Mac OS X".equals(System.getProperty("os.name"))) {
			// @formatter:off
			final ProcessBuilder processBuilder = new ProcessBuilder(
				"/usr/bin/osascript",
				"-e",
				"use framework \"Foundation\"",
				"-e",
				"return (localizedStringFromNumber_numberStyle_(numberWithFloat_(0.5) of NSNumber of current application, NSNumberFormatterDecimalStyle of current application) of NSNumberFormatter of current application) as string"
			);
			// @formatter:on
			try {
				final Process process = processBuilder.start();
				if (process.waitFor(3, TimeUnit.SECONDS)) {
					if (process.exitValue() == 0) {
						try (final InputStream in = process.getInputStream(); final ByteArrayOutputStream os = new ByteArrayOutputStream()) {
							int c = in.read();
							while (c != -1) {
								os.write(c);
								c = in.read();
							}
							result = os.toString().charAt(1);
						}
					}
				}
			} catch (final Exception e) {
				// Do Nothing
			}
		}
		return result;
	}
}
