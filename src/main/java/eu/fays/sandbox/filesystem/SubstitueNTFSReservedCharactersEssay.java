package eu.fays.sandbox.filesystem;

import static java.lang.String.join;
import static java.util.Collections.unmodifiableMap;
import static java.util.regex.Pattern.compile;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Utility class to substitute NTFS reserved characters.
 */
@SuppressWarnings("nls")
public class SubstitueNTFSReservedCharactersEssay {

	public static void main(String[] args) {
		final String x = "\"*/:<>?\\|";
		final String y = substitueNTFSReservedCharacters(x);
		final String z = unsubstitueNTFSReservedCharacters(y);

		SUBSTITUTION_CHARACTERS_MAP.entrySet().stream().forEach(e -> LOGGER.info(MessageFormat.format("''{0}'' -> ''{1}''", e.getKey(), e.getValue())));
		LOGGER.info(new Boolean(x.equals(y) == false).toString());
		LOGGER.info(new Boolean(y.equals(z) == false).toString());
		LOGGER.info(new Boolean(x.equals(z) == true).toString());

	}

	/**
	 * Substitue the NTFS reserved characters of the given input string as follow:
	 * <ul>
	 * <li>'"' to '&#168;' - DIAERESIS
	 * <li>'*' to '&#164;' - CURRENCY SIGN
	 * <li>'/' to '&#248;' - LATIN SMALL LETTER O WITH STROKE
	 * <li>':' to '&#247;' - DIVISION SIGN
	 * <li>'&lt;' to '&#171;' - LEFT-POINTING DOUBLE ANGLE QUOTATION MARK
	 * <li>'&gt;' to '&#187;' - RIGHT-POINTING DOUBLE ANGLE QUOTATION MARK
	 * <li>'?' to '&#191;' - INVERTED QUESTION MARK
	 * <li>'\' to '&#255;' - LATIN SMALL LETTER Y WITH DIAERESIS
	 * <li>'|' to '&#166;' - BROKEN BAR
	 * </ul>
	 * @param input the input string
	 * @return the input string having the NTFS reserved characters substitued
	 */
	public static String substitueNTFSReservedCharacters(final String input) {
		String result = input;

		for (Entry<Character, Character> e : SUBSTITUTION_CHARACTERS_MAP.entrySet()) {
			result = result.replace(e.getKey(), e.getValue());
		}
		return result;
	}

	/**
	 * Perform the opposite operation of {@link #substitueNTFSReservedCharacters(String)}
	 * @param input the input string
	 * @return the input string having the NTFS reserved characters un-substitued
	 */
	public static String unsubstitueNTFSReservedCharacters(final String input) {
		String result = input;

		for (Entry<Character, Character> e : SUBSTITUTION_CHARACTERS_MAP.entrySet()) {
			result = result.replace(e.getValue(), e.getKey());
		}
		return result;
	}

	/**
	 * Build the substitution character map
	 * @return the map
	 */
	private static Map<Character, Character> buildSubstitutionCharactersMap() {
		LinkedHashMap<Character, Character> result = new LinkedHashMap<>();
		result.put('"', (char) 0xa8 /* '¨' - DIAERESIS */);
		result.put('*', (char) 0xa4 /* '¤' - CURRENCY SIGN */);
		result.put('/', (char) 0xf8 /* 'ø' - LATIN SMALL LETTER O WITH STROKE */);
		result.put(':', (char) 0xf7 /* '÷' - DIVISION SIGN */);
		result.put('<', (char) 0xab /* '«' - LEFT-POINTING DOUBLE ANGLE QUOTATION MARK */);
		result.put('>', (char) 0xbb /* '»' - RIGHT-POINTING DOUBLE ANGLE QUOTATION MARK */);
		result.put('?', (char) 0xbf /* '¿' - INVERTED QUESTION MARK */);
		result.put('\\', (char) 0xff /* 'ÿ' - LATIN SMALL LETTER Y WITH DIAERESIS */);
		result.put('|', (char) 0xa6 /* '¦' - BROKEN BAR */);

		return unmodifiableMap(result);
	}

	/** The substitution character map */
	public static final Map<Character, Character> SUBSTITUTION_CHARACTERS_MAP = buildSubstitutionCharactersMap();

	@SuppressWarnings("unused")
	/** The substitution character map */
	private static final Map<Character, Character> UNSUBSTITUTION_CHARACTERS_MAP = unmodifiableMap(
			SUBSTITUTION_CHARACTERS_MAP.entrySet().stream().collect(toMap(Entry::getValue, Entry::getKey, (k, v) -> null, LinkedHashMap::new)));

	/** The pattern to check if a string contains NTFS reserved characters */
	public static final Pattern SUBSTITUTION_CHARACTERS_PATTERN = compile("[" + join("", SUBSTITUTION_CHARACTERS_MAP.keySet().stream().map(e -> e.toString()).collect(toList())) + "]");

	/** The pattern to check if a string contains NTFS reserved characters that have been substitued */
	public static final Pattern UNSUBSTITUTION_CHARACTERS_PATTERN = compile("[" + join("", SUBSTITUTION_CHARACTERS_MAP.values().stream().map(e -> e.toString()).collect(toList())) + "]");

	private static final Logger LOGGER = Logger.getLogger(SubstitueNTFSReservedCharactersEssay.class.getName());
}
