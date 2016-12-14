package eu.fays.sandbox.streams;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An essay on the streams and sets
 */
@SuppressWarnings("nls")
public class StreamSetEssay {

	public static void main(String[] args) {

		final Set<Integer> set1 = Stream.of(1, 2, 3).collect(Collectors.toSet());
		final Set<Integer> set2 = Stream.of(3, 4, 5).collect(Collectors.toSet());
		final Set<Integer> set3 = Stream.of(3, 6, 7).collect(Collectors.toSet());

		final Set<Integer> union = Stream.of(set1, set2, set3).collect(HashSet::new, Set::addAll, Set::addAll);
		final Set<Integer> intersection = Stream.of(set1, set2, set3).collect(() -> new HashSet<Integer>(set1), Set::retainAll, Set::retainAll);

		System.out.println("union: " + union);
		System.out.println("intersection: " + intersection);
	}

}
