package eu.fays.sandbox.iterators;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * An enum to play with.
 * @author Fr&eacute;d&eacute;ric Fays
 */
public enum Fruit {
	ORANGE, BANANA, APPLE, MANGO, APRICOT;

	/**
	 * Returns its ordinal number (starting from zero)
	 * @return its ordinal number
	 */
	public int getIndex() {
		int result = values().length;
		while (values()[--result] != this);
		return result;
	}

	/**
	 * Returns all enumeration elements as a new stream
	 * @return the new stream
	 */
	public static Stream<Fruit> fruitStream() {
		return Arrays.stream(values());
	}
}
