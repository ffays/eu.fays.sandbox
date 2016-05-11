package eu.fays.sandbox.streams;

import static eu.fays.sandbox.streams.Solution.compute;
import static java.text.MessageFormat.format;
import static java.util.Arrays.stream;
import static java.util.Collections.unmodifiableList;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import eu.fays.sandbox.combinatorics.PermutationIterator;

@SuppressWarnings("nls")
public class PermutationEssay {

	public static void main(String[] args) {
		final int[] numbers = { 2, 5, 8, 13, 40 };
		final int goal = 3;

		duration();
		computeSolution1(goal, numbers);
		LOGGER.info(format("Duration #1: {0} ms", duration()));
		duration();
		computeSolution2(goal, numbers);
		LOGGER.info(format("Duration #2: {0} ms", duration()));

		for (Solution solution : computeSolution2(goal, numbers)) {
			LOGGER.info(solution.toString());
		}

	}

	public static List<Solution> computeSolution1(final int goal, final int[] numbers) {
		final List<Solution> result = new ArrayList<>();
		final BigDecimal[] n = stream(numbers).mapToObj(x -> new BigDecimal(Integer.toString(x))).toArray(BigDecimal[]::new);
		final BigDecimal expected = new BigDecimal(Integer.toString(goal));
		for (Operation o1 : Operation.values()) {
			for (Operation o2 : Operation.values()) {
				for (Operation o3 : Operation.values()) {
					for (Operation o4 : Operation.values()) {
						for (Iterator<int[]> it = new PermutationIterator(5); it.hasNext();) {
							int[] i = it.next();
							try {
								final BigDecimal computed = o4.apply(o3.apply(o2.apply(o1.apply(n[i[0]], n[i[1]]), n[i[2]]), n[i[3]]), n[i[4]]);
								if (expected.equals(computed)) {
									final Operation[] solutionOperations = { o1, o2, o3, o4 };
									final int[] solutionNumbers = new int[numbers.length];
									for (int ix = 0; ix < i.length; ix++) {
										solutionNumbers[ix] = n[i[ix]].intValue();
									}
									result.add(new Solution(solutionOperations, solutionNumbers));
								}
							} catch (ArithmeticException e) {
								// Do Nothing;
							}

						}
					}
				}
			}
		}
		return unmodifiableList(result);
	}

	public static List<Solution> computeSolution2(final int expected, final int[] numbers) {
		final List<Solution> result = new ArrayList<>();
		for (Operation o1 : Operation.values()) {
			for (Operation o2 : Operation.values()) {
				for (Operation o3 : Operation.values()) {
					for (Operation o4 : Operation.values()) {
						for (Iterator<int[]> it = new PermutationIterator(5); it.hasNext();) {
							int[] i = it.next();
							final Operation[] os = { o1, o2, o3, o4 };
							final int[] ns = { numbers[i[0]], numbers[i[1]], numbers[i[2]], numbers[i[3]], numbers[i[4]] };
							int computed = compute(os, ns);
							if (expected == computed) {
								result.add(new Solution(os, ns));
							}
						}
					}
				}
			}
		}

		return unmodifiableList(result);
	}

	public static List<Solution> computeSolution3(final int expected, final int[] numbers) {
		final List<Solution> result = new ArrayList<>();
		Operation[] os = Operation.values();
		return unmodifiableList(result);
	}

	public static long duration() {
		final long t1 = Calendar.getInstance().getTimeInMillis();
		final long result = t1 - _t0;
		_t0 = t1;

		return result;
	}

	/** Standard logger */
	private static final Logger LOGGER = Logger.getLogger(PermutationEssay.class.getName());

	static long _t0 = Calendar.getInstance().getTimeInMillis();

}
