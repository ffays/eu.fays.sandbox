package eu.fays.sandbox.format;

import static eu.fays.sandbox.process.ExecuteCommandTrait.executeCommand;

import java.text.DecimalFormatSymbols;

public class PlatformSeparatorEssay {
	public static void main(String[] args) {
		System.out.println("Default Decimal Separator: " + new DecimalFormatSymbols().getDecimalSeparator());
		System.out.println("Platform Decimal Separator: " + getPlatformDecimalSeparator());
		System.out.println("Platform List Separator: " + getPlatformListSeparator());
		System.out.println("Excel Formula Separator: " + getExcelFormulaSeparator());
	}

	private static char PLATFORM_DECIMAL_SEPARATOR = '\u0000';
	private static char PLATFORM_LIST_SEPARATOR = '\u0000';

	@SuppressWarnings("nls")
	public static char getPlatformDecimalSeparator() {
		if (PLATFORM_DECIMAL_SEPARATOR == '\u0000') {
			PLATFORM_DECIMAL_SEPARATOR = new DecimalFormatSymbols().getDecimalSeparator();
			if (System.getProperty("os.name").startsWith("Windows")) {
				// reg query "HKCU\Control Panel\International" /v sDecimal
				final String stdout = executeCommand("reg", "query", "\"HKCU\\Control Panel\\International\"", "/v", "sDecimal").trim();
				PLATFORM_DECIMAL_SEPARATOR = stdout.charAt(stdout.length() - 1);
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
				PLATFORM_DECIMAL_SEPARATOR = stdout.charAt(1);
			}
		}
		return PLATFORM_DECIMAL_SEPARATOR;
	}

	@SuppressWarnings("nls")
	public static char getPlatformListSeparator() {
		if (PLATFORM_LIST_SEPARATOR == '\u0000') {
			PLATFORM_LIST_SEPARATOR = ',';
			if (System.getProperty("os.name").startsWith("Windows")) {
				// reg query "HKCU\Control Panel\International" /v sList
				final String stdout = executeCommand("reg", "query", "\"HKCU\\Control Panel\\International\"", "/v", "sList").trim();
				PLATFORM_LIST_SEPARATOR = stdout.charAt(stdout.length() - 1);
			} else if ("Mac OS X".equals(System.getProperty("os.name"))) {
				final char decimalSeparator = getPlatformDecimalSeparator();
				if (decimalSeparator == PLATFORM_LIST_SEPARATOR) {
					PLATFORM_LIST_SEPARATOR = ';';
				}
			}
		}
		return PLATFORM_LIST_SEPARATOR;
	}

	@SuppressWarnings("nls")
	public static char getExcelFormulaSeparator() {
		char result = ',';
		if (System.getProperty("os.name").startsWith("Windows")) {
			result = getPlatformListSeparator();
		} else if ("Mac OS X".equals(System.getProperty("os.name"))) {
			// http://www.macfreek.nl/memory/Decimal_Seperator_in_Mac_OS_X
			final char defaultDecimalSeparator = new DecimalFormatSymbols().getDecimalSeparator();
			final char platformDecimalSeparator = getPlatformDecimalSeparator();
			if (defaultDecimalSeparator == result || platformDecimalSeparator == result) {
				result = ';';
			}
		}
		return result;
	}
}
