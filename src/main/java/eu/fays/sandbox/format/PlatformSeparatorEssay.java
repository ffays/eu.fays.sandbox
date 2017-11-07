package eu.fays.sandbox.format;

import static eu.fays.sandbox.process.ExecuteCommandTrait.executeCommand;
import static java.util.Collections.unmodifiableMap;
import static java.util.stream.Collectors.toMap;

import java.text.DecimalFormatSymbols;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

public class PlatformSeparatorEssay {
	public static void main(String[] args) {
		System.out.println("Locale: " + Locale.getDefault());
		System.out.println("Java Decimal Separator: " + new DecimalFormatSymbols().getDecimalSeparator());
		System.out.println("Default Decimal Separator: " + DEFAULT_DECIMAL_SEPARATOR_MAP.get(Locale.getDefault().toString()));
		System.out.println("Platform Decimal Separator: " + getPlatformDecimalSeparator());
		System.out.println("Platform List Separator: " + getPlatformListSeparator());
		System.out.println("Excel Formula Separator: " + getExcelFormulaSeparator());
	}

	@SuppressWarnings("nls")
	public static char getPlatformDecimalSeparator() {
		if (PLATFORM_DECIMAL_SEPARATOR == '\u0000') {
			PLATFORM_DECIMAL_SEPARATOR = new DecimalFormatSymbols().getDecimalSeparator();
			if (isWindows()) {
				// reg query "HKCU\Control Panel\International" /v sDecimal
				final String stdout = executeCommand("reg", "query", "\"HKCU\\Control Panel\\International\"", "/v", "sDecimal").trim();
				PLATFORM_DECIMAL_SEPARATOR = stdout.charAt(stdout.length() - 1);
			} else if (isMacOSX()) {
				final String stdout = executeCommand("/usr/bin/osascript", "-e", "return 1/2 as string");
				PLATFORM_DECIMAL_SEPARATOR = stdout.charAt(1);
			}
		}
		return PLATFORM_DECIMAL_SEPARATOR;
	}

	@SuppressWarnings("nls")
	public static char getPlatformListSeparator() {
		if (PLATFORM_LIST_SEPARATOR == '\u0000') {
			PLATFORM_LIST_SEPARATOR = ',';
			if (isWindows()) {
				// reg query "HKCU\Control Panel\International" /v sList
				final String stdout = executeCommand("reg", "query", "\"HKCU\\Control Panel\\International\"", "/v", "sList").trim();
				PLATFORM_LIST_SEPARATOR = stdout.charAt(stdout.length() - 1);
			} else if (isMacOSX()) {
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
		if (isWindows()) {
			result = getPlatformListSeparator();
		} else if (isMacOSX()) {
			// http://www.macfreek.nl/memory/Decimal_Seperator_in_Mac_OS_X
			final String localeName = Locale.getDefault().toString();
			if (DEFAULT_DECIMAL_SEPARATOR_MAP.containsKey(localeName)) {
				final char defaultDecimalSeparator = DEFAULT_DECIMAL_SEPARATOR_MAP.get(localeName);
				final char platformDecimalSeparator = getPlatformDecimalSeparator();
				if (defaultDecimalSeparator == result || platformDecimalSeparator == result) {
					result = ';';
				}
			}
		}
		return result;
	}

	private static boolean isWindows() {
		return System.getProperty("os.name").startsWith("Windows");
	}

	private static boolean isMacOSX() {
		return "Mac OS X".equals(System.getProperty("os.name"));
	}

	private static char PLATFORM_DECIMAL_SEPARATOR = '\u0000';
	private static char PLATFORM_LIST_SEPARATOR = '\u0000';

	// @formatter:off
	public static final Map<String,Character> DEFAULT_DECIMAL_SEPARATOR_MAP = unmodifiableMap(
		Stream.of(
				new SimpleImmutableEntry<>("ar", '.'),
				new SimpleImmutableEntry<>("ar_AE", '.'),
				new SimpleImmutableEntry<>("ar_BH", '.'),
				new SimpleImmutableEntry<>("ar_DZ", '.'),
				new SimpleImmutableEntry<>("ar_EG", '.'),
				new SimpleImmutableEntry<>("ar_IQ", '.'),
				new SimpleImmutableEntry<>("ar_JO", '.'),
				new SimpleImmutableEntry<>("ar_KW", '.'),
				new SimpleImmutableEntry<>("ar_LB", '.'),
				new SimpleImmutableEntry<>("ar_LY", '.'),
				new SimpleImmutableEntry<>("ar_MA", '.'),
				new SimpleImmutableEntry<>("ar_OM", '.'),
				new SimpleImmutableEntry<>("ar_QA", '.'),
				new SimpleImmutableEntry<>("ar_SA", '.'),
				new SimpleImmutableEntry<>("ar_SD", '.'),
				new SimpleImmutableEntry<>("ar_SY", '.'),
				new SimpleImmutableEntry<>("ar_TN", '.'),
				new SimpleImmutableEntry<>("ar_YE", '.'),
				new SimpleImmutableEntry<>("be", ','),
				new SimpleImmutableEntry<>("be_BY", ','),
				new SimpleImmutableEntry<>("bg", ','),
				new SimpleImmutableEntry<>("bg_BG", ','),
				new SimpleImmutableEntry<>("ca", ','),
				new SimpleImmutableEntry<>("ca_ES", ','),
				new SimpleImmutableEntry<>("cs", ','),
				new SimpleImmutableEntry<>("cs_CZ", ','),
				new SimpleImmutableEntry<>("da", ','),
				new SimpleImmutableEntry<>("da_DK", ','),
				new SimpleImmutableEntry<>("de", ','),
				new SimpleImmutableEntry<>("de_AT", ','),
				new SimpleImmutableEntry<>("de_CH", '.'),
				new SimpleImmutableEntry<>("de_DE", ','),
				new SimpleImmutableEntry<>("de_LU", ','),
				new SimpleImmutableEntry<>("el", ','),
				new SimpleImmutableEntry<>("el_CY", ','),
				new SimpleImmutableEntry<>("el_GR", ','),
				new SimpleImmutableEntry<>("en", '.'),
				new SimpleImmutableEntry<>("en_AU", '.'),
				new SimpleImmutableEntry<>("en_CA", '.'),
				new SimpleImmutableEntry<>("en_GB", '.'),
				new SimpleImmutableEntry<>("en_IE", '.'),
				new SimpleImmutableEntry<>("en_IN", '.'),
				new SimpleImmutableEntry<>("en_MT", '.'),
				new SimpleImmutableEntry<>("en_NZ", '.'),
				new SimpleImmutableEntry<>("en_PH", '.'),
				new SimpleImmutableEntry<>("en_SG", '.'),
				new SimpleImmutableEntry<>("en_US", '.'),
				new SimpleImmutableEntry<>("en_ZA", ','),
				new SimpleImmutableEntry<>("es", ','),
				new SimpleImmutableEntry<>("es_AR", ','),
				new SimpleImmutableEntry<>("es_BO", ','),
				new SimpleImmutableEntry<>("es_CL", ','),
				new SimpleImmutableEntry<>("es_CO", ','),
				new SimpleImmutableEntry<>("es_CR", ','),
				new SimpleImmutableEntry<>("es_CU", '.'),
				new SimpleImmutableEntry<>("es_DO", '.'),
				new SimpleImmutableEntry<>("es_EC", ','),
				new SimpleImmutableEntry<>("es_ES", ','),
				new SimpleImmutableEntry<>("es_GT", '.'),
				new SimpleImmutableEntry<>("es_HN", '.'),
				new SimpleImmutableEntry<>("es_MX", '.'),
				new SimpleImmutableEntry<>("es_NI", '.'),
				new SimpleImmutableEntry<>("es_PA", '.'),
				new SimpleImmutableEntry<>("es_PE", '.'),
				new SimpleImmutableEntry<>("es_PR", '.'),
				new SimpleImmutableEntry<>("es_PY", ','),
				new SimpleImmutableEntry<>("es_SV", '.'),
				new SimpleImmutableEntry<>("es_US", '.'),
				new SimpleImmutableEntry<>("es_UY", ','),
				new SimpleImmutableEntry<>("es_VE", ','),
				new SimpleImmutableEntry<>("et", ','),
				new SimpleImmutableEntry<>("et_EE", ','),
				new SimpleImmutableEntry<>("fi", ','),
				new SimpleImmutableEntry<>("fi_FI", ','),
				new SimpleImmutableEntry<>("fr", ','),
				new SimpleImmutableEntry<>("fr_BE", ','),
				new SimpleImmutableEntry<>("fr_CA", ','),
				new SimpleImmutableEntry<>("fr_CH", ','),
				new SimpleImmutableEntry<>("fr_FR", ','),
				new SimpleImmutableEntry<>("fr_LU", ','),
				new SimpleImmutableEntry<>("ga", '.'),
				new SimpleImmutableEntry<>("ga_IE", '.'),
				new SimpleImmutableEntry<>("hi", '.'),
				new SimpleImmutableEntry<>("hi_IN", '.'),
				new SimpleImmutableEntry<>("hr", ','),
				new SimpleImmutableEntry<>("hr_HR", ','),
				new SimpleImmutableEntry<>("hu", ','),
				new SimpleImmutableEntry<>("hu_HU", ','),
				new SimpleImmutableEntry<>("is", ','),
				new SimpleImmutableEntry<>("is_IS", ','),
				new SimpleImmutableEntry<>("it", ','),
				new SimpleImmutableEntry<>("it_CH", '.'),
				new SimpleImmutableEntry<>("it_IT", ','),
				new SimpleImmutableEntry<>("ja", '.'),
				new SimpleImmutableEntry<>("ja_JP", '.'),
				new SimpleImmutableEntry<>("ko", '.'),
				new SimpleImmutableEntry<>("ko_KR", '.'),
				new SimpleImmutableEntry<>("lt", ','),
				new SimpleImmutableEntry<>("lt_LT", ','),
				new SimpleImmutableEntry<>("lv", ','),
				new SimpleImmutableEntry<>("lv_LV", ','),
				new SimpleImmutableEntry<>("mk", ','),
				new SimpleImmutableEntry<>("mk_MK", ','),
				new SimpleImmutableEntry<>("ms", '.'),
				new SimpleImmutableEntry<>("ms_MY", '.'),
				new SimpleImmutableEntry<>("mt", '.'),
				new SimpleImmutableEntry<>("mt_MT", '.'),
				new SimpleImmutableEntry<>("nl", ','),
				new SimpleImmutableEntry<>("nl_BE", ','),
				new SimpleImmutableEntry<>("nl_NL", ','),
				new SimpleImmutableEntry<>("no", ','),
				new SimpleImmutableEntry<>("pl", ','),
				new SimpleImmutableEntry<>("pl_PL", ','),
				new SimpleImmutableEntry<>("pt", ','),
				new SimpleImmutableEntry<>("pt_BR", ','),
				new SimpleImmutableEntry<>("pt_PT", ','),
				new SimpleImmutableEntry<>("ro", ','),
				new SimpleImmutableEntry<>("ro_RO", ','),
				new SimpleImmutableEntry<>("ru", ','),
				new SimpleImmutableEntry<>("ru_RU", ','),
				new SimpleImmutableEntry<>("sk", ','),
				new SimpleImmutableEntry<>("sk_SK", ','),
				new SimpleImmutableEntry<>("sl", ','),
				new SimpleImmutableEntry<>("sl_SI", ','),
				new SimpleImmutableEntry<>("sq", ','),
				new SimpleImmutableEntry<>("sq_AL", ','),
				new SimpleImmutableEntry<>("sr", ','),
				new SimpleImmutableEntry<>("sv", ','),
				new SimpleImmutableEntry<>("sv_SE", ','),
				new SimpleImmutableEntry<>("th", '.'),
				new SimpleImmutableEntry<>("th_TH", '.'),
				new SimpleImmutableEntry<>("tr", ','),
				new SimpleImmutableEntry<>("tr_TR", ','),
				new SimpleImmutableEntry<>("uk", ','),
				new SimpleImmutableEntry<>("uk_UA", ','),
				new SimpleImmutableEntry<>("vi", ','),
				new SimpleImmutableEntry<>("vi_VN", ','),
				new SimpleImmutableEntry<>("zh", '.'),
				new SimpleImmutableEntry<>("zh_CN", '.'),
				new SimpleImmutableEntry<>("zh_HK", '.'),
				new SimpleImmutableEntry<>("zh_SG", '.'),
				new SimpleImmutableEntry<>("zh_TW", '.')				
		).collect(toMap(Entry::getKey, Entry::getValue))
	);
	// @formatter:on

}
