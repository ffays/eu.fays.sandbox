package eu.fays.sandbox.streams;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Basic arithmetic operations
 */
@SuppressWarnings("nls")
public enum Operation {
	/** Addition */
	ADD("+"),
	/** Substraction */
	SUBTRACT("-"),
	/** Multiplication */
	MULTIPLY("*"),
	/** Divistion */
	DIVIDE("/");

	/**
	 * Applies the arithmetic operation on the given number
	 * @param a first number
	 * @param b second number
	 * @return the result of the arithmetic operation
	 */
	public BigDecimal apply(final BigDecimal a, final BigDecimal b) {
		switch (this) {
		case ADD:
			return a.add(b);
		case SUBTRACT:
			return a.subtract(b);
		case MULTIPLY:
			return a.multiply(b);
		case DIVIDE:
			return a.divide(b);
		}
		return BigDecimal.ZERO;
	}

	/**
	 * Applies the arithmetic operation on the given number.<br>
	 * <br>
	 * Corner cases:
	 * <ol>
	 * <li>If either a or b equals to {@link Integer#MAX_VALUE}, then {@link Integer#MAX_VALUE} is returned
	 * <li>If the operation is {@link Operation#DIVIDE} and b equals to 0, then {@link Integer#MAX_VALUE} is returned
	 * <li>If the operation is {@link Operation#DIVIDE} and a divided by b has a remainder, then {@link Integer#MAX_VALUE} is returned
	 * </ol>
	 * @param a first number
	 * @param b second number
	 * @return the result of the arithmetic operation
	 */
	public int apply(final int a, final int b) {
		if (a == Integer.MAX_VALUE || b == Integer.MAX_VALUE) {
			return Integer.MAX_VALUE;
		}
		switch (this) {
		case ADD:
			return a + b;
		case SUBTRACT:
			return a - b;
		case MULTIPLY:
			return a * b;
		case DIVIDE:
			return b != 0 && (a % b) == 0 ? a / b : Integer.MAX_VALUE;
		}
		return Integer.MAX_VALUE;
	}

	/**
	 * Returns all operations as a new stream
	 * @return the new stream
	 */
	public static Stream<Operation> operationStream() {
		return Arrays.stream(values());
	}

	/** Display label */
	public final String LABEL;

	/**
	 * Constructor
	 * @param label display label
	 */
	private Operation(final String label) {
		LABEL = label;
	}
}
