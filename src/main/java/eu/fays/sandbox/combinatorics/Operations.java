package eu.fays.sandbox.combinatorics;

import java.math.BigDecimal;

public enum Operations {
	ADD, SUBTRACT, MULTIPLY, DIVIDE;

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
}
