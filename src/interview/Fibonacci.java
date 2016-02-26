package interview;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * <strong>Problem statement:</strong><br>
 * <br>
 * Write a method that produces Fibonacci sequence.<br>
 * Below the first 7th values.<br>
 * <br>
 * <table style="border-collapse:collapse">
 * <tr>
 * <th style="border: 1px solid">1</th>
 * <th style="border: 1px solid">2</th>
 * <th style="border: 1px solid">3</th>
 * <th style="border: 1px solid">4</th>
 * <th style="border: 1px solid">5</th>
 * <th style="border: 1px solid">6</th>
 * <th style="border: 1px solid">7</th>
 * </tr>
 * <tr>
 * <td style="border: 1px solid">1</td>
 * <td style="border: 1px solid">1</td>
 * <td style="border: 1px solid">2</td>
 * <td style="border: 1px solid">3</td>
 * <td style="border: 1px solid">5</td>
 * <td style="border: 1px solid">8</td>
 * <td style="border: 1px solid">13</td>
 * </tr>
 * </table>
 * 
 * @author Frederic Fays
 */
public class Fibonacci {

	/**
	 * Outputs the Fibonacci sequence.
	 * 
	 * @param args unused
	 */
	public static void main(String[] args) {
		System.out.println(fibonacci(7));
	}

	/**
	 * Computes the Fibonacci sequence up to the given Nth value.
	 * 
	 * @param n the length of the sequence to be computed. n must be strictly positive.
	 * @return the Fibonacci sequence.
	 * @throws IllegalArgumentException in case n is either a negative value or zero.
	 */
	public static List<Integer> fibonacci(final int n) throws IllegalArgumentException {
		if (n <= 0) {
			throw new IllegalArgumentException("n must be strictly positive");
		}

		final List<Integer> result = new LinkedList<Integer>();

		// Initialize the list with the first element
		result.add(1);

		if (n > 1) {
			// Initialize the list with the second element
			result.add(1);
		}

		// Computes the fibonacci suite.
		for (int i = 2; i < n; i++) {
			result.add(result.get(i - 1) + result.get(i - 2));
		}
		return Collections.unmodifiableList(result);
	}

	/**
	 * Another way to compute the Fibonacci sequence, using Java 8 functional aspects.
	 * 
	 * @see Fibonacci#fibonacci(int)
	 */
	public static List<Integer> fibonacci2(final int n) throws IllegalArgumentException {
		if (n <= 0) {
			throw new IllegalArgumentException("n must be strictly positive");
		}

		final List<Integer> result = new LinkedList<Integer>();

		// C.f. https://dzone.com/articles/do-it-java-8-recursive-and
		final Map.Entry<Integer, Integer> seed = new AbstractMap.SimpleEntry<>(1, 1);
		final UnaryOperator<Map.Entry<Integer, Integer>> f = x -> new AbstractMap.SimpleEntry<>(x.getValue(), x.getKey() + x.getValue());
		Stream.iterate(seed, f).map(x -> x.getKey()).limit(n).forEach(result::add);

		return Collections.unmodifiableList(result);
	}

}
