package eu.fays.sandbox.streams;

import static eu.fays.sandbox.streams.Operation.operationStream;
import static eu.fays.sandbox.iterators.Fruit.fruitStream;
import static eu.fays.sandbox.combinatorics.PermutationIterator.permutationStream;
import static eu.fays.sandbox.streams.Solution.compute;
import static java.text.MessageFormat.format;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Stream;

import eu.fays.sandbox.combinatorics.PermutationIterator;

/**
 * An Essay on the combinatorics
 */
@SuppressWarnings("nls")
public class PermutationEssay {

	/**
	 * Main
	 * @param args unused
	 */
	public static void main(String[] args) {
		final int[] numbers = { 2, 5, 8, 13, 40 };
		final int goal = 3;

		cartesianProduct();
		for (Solution solution : computeSolution4(goal, numbers)) {
			LOGGER.info(solution.toString());
		}

		duration();
		computeSolution1(goal, numbers);
		LOGGER.info(format("Duration #1: {0} ms", duration()));
		computeSolution2(goal, numbers);
		LOGGER.info(format("Duration #2: {0} ms", duration()));
		computeSolution3(goal, numbers);
		LOGGER.info(format("Duration #3: {0} ms", duration()));
		computeSolution4(goal, numbers);
		LOGGER.info(format("Duration #4: {0} ms", duration()));
		computeSolution5(goal, numbers);
		LOGGER.info(format("Duration #5: {0} ms", duration()));

	}

	/**
	 * Using the given numbers, combine all of them with basic arithmetic operations (i.e. +,-,*,/) to obtain the given goal.
	 * @param goal the result of the equation
	 * @param numbers the given numbers.
	 * @return the list of solution meeting the goal.
	 */
	public static List<Solution> computeSolution1(final int goal, final int[] numbers) {
		final List<Solution> result = new ArrayList<>();
		final BigDecimal[] n = Arrays.stream(numbers).mapToObj(x -> new BigDecimal(Integer.toString(x))).toArray(BigDecimal[]::new);
		final BigDecimal expected = new BigDecimal(Integer.toString(goal));
		final Operation[] operations = Operation.values();
		for (Operation o1 : operations) {
			for (Operation o2 : operations) {
				for (Operation o3 : operations) {
					for (Operation o4 : operations) {
						for (Iterator<int[]> it = new PermutationIterator(numbers.length); it.hasNext();) {
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

	/**
	 * Using the given numbers, combine all of them with basic arithmetic operations (i.e. +,-,*,/) to obtain the given expected result of the equation.
	 * @param expected the expected result of the equation
	 * @param numbers the given numbers.
	 * @return the list of solution meeting the goal.
	 */
	public static List<Solution> computeSolution2(final int expected, final int[] numbers) {
		final List<Solution> result = new ArrayList<>();
		final Operation[] operations = Operation.values();
		for (Operation o1 : operations) {
			for (Operation o2 : operations) {
				for (Operation o3 : operations) {
					for (Operation o4 : operations) {
						for (Iterator<int[]> it = new PermutationIterator(numbers.length); it.hasNext();) {
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

	/**
	 * Using the given numbers, combine all of them with basic arithmetic operations (i.e. +,-,*,/) to obtain the given expected result of the equation.
	 * @param expected the expected result of the equation
	 * @param numbers the given numbers.
	 * @return the list of solution meeting the goal.
	 */
	public static List<Solution> computeSolution3(final int expected, final int[] numbers) {
		/** @formatter:off */
		final List<Solution> result = operationStream()
			.map(o1 -> operationStream()
				.map(o2 -> operationStream()
					.map(o3 -> operationStream()
						.map(o4 -> permutationStream(numbers.length, false)
							.map(i -> new Solution(new Operation[] { o1, o2, o3, o4 }, new int[] { numbers[i[0]], numbers[i[1]], numbers[i[2]], numbers[i[3]], numbers[i[4]] }))))))
								.reduce(Stream.empty(), Stream::concat)
									.reduce(Stream.empty(), Stream::concat)
										.reduce(Stream.empty(), Stream::concat)
											.reduce(Stream.empty(), Stream::concat)
												.filter(sol -> sol.equalsTo(expected))
													.collect(toList());
		/** @formatter:on */
		return unmodifiableList(result);
	}

	/**
	 * Using the given numbers, combine all of them with basic arithmetic operations (i.e. +,-,*,/) to obtain the given expected result of the equation.
	 * @param expected the expected result of the equation
	 * @param numbers the given numbers.
	 * @return the list of solution meeting the goal.
	 */
	public static List<Solution> computeSolution4(final int expected, final int[] numbers) {
		/** @formatter:off */
		final List<Solution> result = operationStream()
			.map(o1 -> operationStream()
				.map(o2 -> operationStream()
					.map(o3 -> operationStream()
						.map(o4 -> permutationStream(numbers.length, false)
							.map(i -> new Solution(new Operation[] { o1, o2, o3, o4 }, new int[] { numbers[i[0]], numbers[i[1]], numbers[i[2]], numbers[i[3]], numbers[i[4]] }))))))
								.flatMap(Function.identity())
									.flatMap(Function.identity())
										.flatMap(Function.identity())
											.flatMap(Function.identity())
												.filter(sol -> sol.equalsTo(expected))
													.collect(toList());
		/** @formatter:on */
		return unmodifiableList(result);
	}

	/**
	 * Using the given numbers, combine all of them with basic arithmetic operations (i.e. +,-,*,/) to obtain the given expected result of the equation.
	 * @param expected the expected result of the equation
	 * @param numbers the given numbers.
	 * @return the list of solution meeting the goal.
	 */
	public static List<Solution> computeSolution5(final int expected, final int[] numbers) {
		/** @formatter:off */
		final List<Solution> result = operationStream()
			.map(o1 -> operationStream()
				.map(o2 -> operationStream()
					.map(o3 -> operationStream()
						.map(o4 -> permutationStream(numbers.length, true)
							.map(i -> new Solution(new Operation[] { o1, o2, o3, o4 }, new int[] { numbers[i[0]], numbers[i[1]], numbers[i[2]], numbers[i[3]], numbers[i[4]] }))))))
								.flatMap(Function.identity())
									.flatMap(Function.identity())
										.flatMap(Function.identity())
											.flatMap(Function.identity())
												.parallel()
													.filter(sol -> sol.equalsTo(expected))
														.collect(toList());
		/** @formatter:on */
		return unmodifiableList(result);
	}

	/**
	 * Cartesian product of two streams.<br>
	 * Stream of Stream to Stream.<br>
	 * c.f. <a href="http://stackoverflow.com/questions/25412377/why-cant-stream-of-streams-be-reduced-un-parallel-stream-has-already-been-o">Why can't stream of streams be reduced un parallel ? / stream has already been
	 * operated upon or closed</a>
	 */
	public static void cartesianProduct() {
		final Stream<String> stream = fruitStream().map(f1 -> fruitStream().map(f2 -> f1.name() + "x" + f2.name())).flatMap(Function.identity());
		stream.forEach(s -> LOGGER.info(s));
	}

	/**
	 * Computes the elapsed time in milliseconds since the last call
	 * @return the duration in milliseconds
	 */
	public static long duration() {
		final long t1 = Calendar.getInstance().getTimeInMillis();
		final long result = t1 - _t0;
		_t0 = t1;

		return result;
	}

	/** Standard logger */
	private static final Logger LOGGER = Logger.getLogger(PermutationEssay.class.getName());

	/** Timestamp used by {@link PermutationEssay#duration()} */
	private static long _t0 = Calendar.getInstance().getTimeInMillis();

}
