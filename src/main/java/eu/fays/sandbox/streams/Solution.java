package eu.fays.sandbox.streams;

/**
 * Solution for {@link StreamEssay}
 * @author Frederic Fays
 */
@SuppressWarnings("nls")
public class Solution {

	/**
	 * Constructor
	 * 
	 * @param operations sequence of operations applied on the given numbers
	 * @param numbers the given numbers
	 */
	public Solution(final Operation[] operations, final int[] numbers) {
		//
		assert operations.length > 0;
		assert numbers.length > 0;
		assert operations.length == (numbers.length - 1);
		///

		_operations = operations;
		_numbers = numbers;
	}

	/**
	 * Computes the solution
	 * @return the solution
	 */
	public int compute() {
		return compute(_operations, _numbers);
	}

	/**
	 * Computes the solution
	 * @param operations sequence of operations applied on the given numbers
	 * @param numbers the given numbers
	 * @return the solution
	 */
	public static int compute(final Operation[] operations, final int[] numbers) {
		int result = numbers[0];
		for (int ix = 0; ix < operations.length; ix++) {
			result = operations[ix].apply(result, numbers[ix + 1]);
		}
		return result;
	}

	/**
	 * Returns the Unix command-line using "bc" command to be used to compute the solution.
	 * @return a string representation
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder("bc <<< '");
		for (int i = 0; i < _operations.length - 1; i++) {
			builder.append('(');
		}
		builder.append(_numbers[0]);

		for (int i = 0; i < _operations.length; i++) {
			builder.append(_operations[i].LABEL);
			builder.append(_numbers[i + 1]);
			if (i < _operations.length - 1) {
				builder.append(')');
			}
		}
		builder.append('\'');
		return builder.toString();
	}

	final Operation[] _operations;
	final int[] _numbers;

}