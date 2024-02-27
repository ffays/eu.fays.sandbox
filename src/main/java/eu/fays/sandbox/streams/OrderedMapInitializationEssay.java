package eu.fays.sandbox.streams;

import static java.text.MessageFormat.format;
import static java.util.Collections.unmodifiableMap;
import static java.util.stream.Collectors.toMap;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.stream.Stream;

public class OrderedMapInitializationEssay {
	// @formatter:off
	public static final Map<String, String> MAP = unmodifiableMap(Stream.of(
		new SimpleEntry<>("key1", "value1"),
		new SimpleEntry<>("key0", "value0"),
		new SimpleEntry<>("key3", "value3"),
		new SimpleEntry<>("key2", "value2")
	).collect(toMap(Entry::getKey, Entry::getValue, (a, b) -> { throw new AssertionError(format("Duplicate key with values ''{0}'', ''{1}''!", a, b)); }, LinkedHashMap::new)));
	// @formatter:on

	public static void main(String[] args) {
		MAP.forEach((k, v) -> System.out.println(format("{0}={1}", k, v)));
	}
}
