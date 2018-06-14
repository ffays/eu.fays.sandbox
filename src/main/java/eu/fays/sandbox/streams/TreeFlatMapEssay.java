package eu.fays.sandbox.streams;

import static java.lang.System.out;
import static java.text.MessageFormat.format;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import eu.fays.sandbox.tuples.Triplet;

public class TreeFlatMapEssay {

	final static SortedMap<Integer, Set<Entry<Character, String>>> map = new TreeMap<>();

	@SuppressWarnings("nls")
	public static void main(String[] args) {
		// c.f. https://stackoverflow.com/questions/40484985/flatten-a-mapinteger-liststring-to-mapstring-integer-with-stream-and-lam
		
		add(1, 'B', "Not a number!");
		add(1, 'C', "Not divisible by 3!");
		add(2, 'B', "Empty!");
		add(2, 'A', "Not a number!");
		add(1, 'C', "Not divisible by 7!");
		add(1, 'C', "Not divisible by 11!");
		add(2, 'C', "Not divisible by 5!");
		add(2, 'C', "Not divisible by 13!");
		add(1, 'A', "Empty!");
		add(2, 'C', "Not divisible by 17!");

		// @formatter:off
		map.entrySet()
			.stream()
			.flatMap(e0 -> e0.getValue()
				.stream()
				.map(e1 -> new Triplet<Character, Integer, String>(e1.getKey(), e0.getKey(), e1.getValue()))
			)
			.forEach(e -> out.println(format("{0}{1,number,0}: {2}", e.t, e.u, e.v)));
		// @formatter:on
	}

	public static void add(final int row, final char column, final String message) {
		if (!map.containsKey(row)) {
			map.put(row, new LinkedHashSet<>());
		}
		map.get(row).add(new SimpleImmutableEntry<>(column, message));
	}

}
