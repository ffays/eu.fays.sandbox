package eu.fays.sandbox.format;

import static eu.fays.sandbox.process.ExecuteCommandTrait.executeCommand;

import java.text.DecimalFormatSymbols;

public class SystemDecimalSeparatorEssay {

	public static void main(String[] args) {
		System.out.println(detectSystemDecimalSeparator());
	}

	@SuppressWarnings("nls")
	public static char detectSystemDecimalSeparator() {
		char result = new DecimalFormatSymbols().getDecimalSeparator();
		if (System.getProperty("os.name").startsWith("Windows")) {
			final String stdout = executeCommand("reg", "query", "\"HKCU\\Control Panel\\International\"", "/v", "sDecimal").trim();
			result = stdout.charAt(stdout.length() - 1);
		} else if ("Mac OS X".equals(System.getProperty("os.name"))) {
			// @formatter:off
			final String stdout = executeCommand(
				"/usr/bin/osascript",
				"-e",
				"use framework \"Foundation\"",
				"-e",
				"return (localizedStringFromNumber_numberStyle_(numberWithFloat_(0.5) of NSNumber of current application, NSNumberFormatterDecimalStyle of current application) of NSNumberFormatter of current application) as string"
			);
			// @formatter:on
			result = stdout.charAt(1);
		}
		return result;
	}
}
