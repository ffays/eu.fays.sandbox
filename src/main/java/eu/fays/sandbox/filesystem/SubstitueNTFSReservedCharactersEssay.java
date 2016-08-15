package eu.fays.sandbox.filesystem;

import static java.util.Collections.unmodifiableMap;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SubstitueNTFSReservedCharactersEssay {

	public static void main(String[] args) {
		final String x = "\"*/:<>?\\|";
		final String y = a2b(x);
		final String z = b2a(y);

		LOGGER.info(new Boolean(x.equals(y)).toString()); // false
		LOGGER.info(new Boolean(y.equals(z)).toString()); // false
		LOGGER.info(new Boolean(x.equals(z)).toString()); // true

	}

	public static String a2b(final String input) {
		String result = input;

		for (Entry<Character, Character> e : A2B.entrySet()) {
			result = result.replace(e.getKey(), e.getValue());
		}
		return result;
	}

	public static String b2a(final String input) {
		String result = input;

		for (Entry<Character, Character> e : A2B.entrySet()) {
			result = result.replace(e.getValue(), e.getKey());
		}
		return result;
	}

	public static final Pattern PATTERN = Pattern.compile("[\\x00-\\x1F\\x22\\x2A\\x3A\\x3C\\x3E\\x3F\\x5C\\x7C]");
	@SuppressWarnings("serial")
	private static final Map<Character, Character> A2B = unmodifiableMap(new LinkedHashMap<Character, Character>() {
		{
			put('"', (char) 0xa8 /* '¨' */);
			put('*', (char) 0xa4 /* '¤' */);
			put('/', (char) 0xf8 /* 'ø' */);
			put(':', (char) 0xf7 /* '÷' */);
			put('<', (char) 0xab /* '«' */);
			put('>', (char) 0xbb /* '»' */);
			put('?', (char) 0xbf /* '¿' */);
			put('\\', (char) 0xff /* 'ÿ' */);
			put('|', (char) 0xa6 /* '¦' */);
		}
	});

	private static final Map<Character, Character> B2A = unmodifiableMap(A2B.entrySet().stream().collect(Collectors.toMap(Entry::getValue, Entry::getKey, (k, v) -> null, LinkedHashMap::new)));

	private static final Logger LOGGER = Logger.getLogger(SubstitueNTFSReservedCharactersEssay.class.getName());
}
