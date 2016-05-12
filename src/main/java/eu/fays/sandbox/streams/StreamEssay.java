package eu.fays.sandbox.streams;

import static java.text.MessageFormat.format;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import eu.fays.sandbox.iterators.Fruit;

/**
 * An essay on the Java 8 streams
 * 
 * @author Fr&eacute;d&eacute;ric Fays
 */
@SuppressWarnings("nls")
public class StreamEssay {

	/**
	 * Main<br>
	 * <br>
	 * VM args :
	 * 
	 * <pre>
	 * -ea -Djava.util.logging.config.file=logging.properties
	 * </pre>
	 * 
	 * @param args unused
	 * @throws Exception in case of unexpected error
	 */
	public static void main(String[] args) throws Exception {
		LOGGER.info(format("booleanAny #1: {0}", booleanAny(new Boolean[] { false, true, false })));
		LOGGER.info(format("booleanAny #2: {0}", booleanAny(new Boolean[] { false, false, false })));
		LOGGER.info(format("booleanAll #1: {0}", booleanAll(new Boolean[] { true, true, true })));
		LOGGER.info(format("booleanAll #2: {0}", booleanAll(new Boolean[] { false, true, false })));
		LOGGER.info(format("enumToSetOfString: {0}", enumToSetOfString(Fruit.class)));
		LOGGER.info(format("intArrayToBigDecimalArray: {0}", asList(intArrayToBigDecimalArray(new int[] { 2, 5, 8, 13, 40 }))).toString());
		IntStream.range(0, 3).forEach(n -> LOGGER.info("IntStream: #" + n));
	}

	/**
	 * Performs the "Any" operation on the given list of values
	 * @param values the values
	 * @return either true: if at least one value is true, or false: if all values are false
	 */
	public static boolean booleanAny(final Boolean[] values) {
		return Arrays.asList(values).stream().reduce(false, Boolean::logicalOr);
	}

	/**
	 * Performs the "All" operation on the given list of values
	 * @param values the values
	 * @return either true: if all the values are true, or false: if at least one value is false
	 */
	public static boolean booleanAll(final Boolean[] values) {
		return Arrays.asList(values).stream().reduce(true, (a, v) -> a = a && v);
	}

	/**
	 * Returns a immutable set of the given enum values's names.
	 * @param enumType the class of the enum type
	 * @param <T> the enum's type
	 * @return the set of names.
	 */
	public static <T extends Enum<T>> Set<String> enumToSetOfString(Class<T> enumType) {
		return Collections.unmodifiableSet((Set<String>) Arrays.stream(enumType.getEnumConstants()).map(v -> v.name()).collect(Collectors.toCollection(LinkedHashSet::new)));
	}

	/**
	 * Converts the given array of integers into an array of {@link BigDecimal}
	 * @param numbers the given array of numbers
	 * @return the array of {@link BigDecimal}
	 */
	public static BigDecimal[] intArrayToBigDecimalArray(int[] numbers) {
		return stream(numbers).mapToObj(x -> new BigDecimal(Integer.toString(x))).toArray(BigDecimal[]::new);
	}

	/**
	 * Converts the given iterator into a stream<br>
	 * <br>
	 * Source: <a href="http://stackoverflow.com/questions/24511052/how-to-convert-an-iterator-to-a-stream">How to convert an iterator to a stream?</a>
	 * @param iterator the given iterator
	 * @param <T> the type of both the given iterator and the resulting stream
	 * @return the stream
	 */
	public static <T> Stream<T> iteratorToStream1(final Iterator<T> iterator) {
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false);
	}

	/**
	 * Converts the given iterator into a stream<br>
	 * <br>
	 * Source: <a href="http://stackoverflow.com/questions/24511052/how-to-convert-an-iterator-to-a-stream">How to convert an iterator to a stream?</a>
	 * @param iterator the given iterator
	 * @param <T> the type of both the given iterator and the resulting stream
	 * @return the stream
	 */
	public static <T> Stream<T> iteratorToStream2(final Iterator<T> iterator) {
		final Iterable<T> iterable = () -> iterator;
		final Stream<T> result = StreamSupport.stream(iterable.spliterator(), false);
		return result;
	}

	/** Standard logger */
	private static final Logger LOGGER = Logger.getLogger(StreamEssay.class.getName());

}
