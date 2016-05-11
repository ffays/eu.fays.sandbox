package eu.fays.sandbox.streams;

import java.math.BigDecimal;

@SuppressWarnings("nls")
public enum Operation {
	ADD("+"), SUBTRACT("-"), MULTIPLY("*"), DIVIDE("/");

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
			return (a % b) == 0 ? a / b : Integer.MAX_VALUE;
		}
		return Integer.MAX_VALUE;
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
