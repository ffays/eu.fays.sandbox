package eu.fays.sandbox.streams;

import static java.text.MessageFormat.format;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.UUID;
import java.util.function.Function;
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
		LOGGER.info(format("areAllValuesIntegers1 #1: {0}", areAllValuesIntegers1(new double[] { 1d, 2d, 3d })));
		LOGGER.info(format("areAllValuesIntegers1 #2: {0}", areAllValuesIntegers1(new double[] { 1d, Math.PI, Math.E })));
		LOGGER.info(format("areAllValuesIntegers1 #3: {0}", areAllValuesIntegers1(new double[] { 1d, Double.NaN })));
		LOGGER.info(format("areAllValuesIntegers1 #4: {0}", areAllValuesIntegers1(new double[] { 1d, Double.POSITIVE_INFINITY })));
		LOGGER.info(format("areAllValuesIntegers1 #5: {0}", areAllValuesIntegers1(new double[] { 1d, 3.14d })));
		LOGGER.info(format("areAllValuesIntegers2 #1: {0}", areAllValuesIntegers2(new double[] { 1d, 2d, 3d })));
		LOGGER.info(format("areAllValuesIntegers2 #2: {0}", areAllValuesIntegers2(new double[] { 1d, Math.PI, Math.E })));
		LOGGER.info(format("areAllValuesIntegers2 #3: {0}", areAllValuesIntegers2(new double[] { 1d, Double.NaN })));
		LOGGER.info(format("areAllValuesIntegers2 #4: {0}", areAllValuesIntegers2(new double[] { 1d, Double.POSITIVE_INFINITY })));
		LOGGER.info(format("areAllValuesIntegers2 #5: {0}", areAllValuesIntegers2(new double[] { 1d, 3.14d })));
		LOGGER.info(format("enumLookup              : {0}", enumLookup(Fruit.class, Fruit.APPLE)));

		{
			final Set<UUID> set = IntStream.range(0, 5).mapToObj(i -> UUID.randomUUID()).collect(Collectors.toSet());
			final Map<String, UUID> map = setToMap(set);
			map.entrySet().forEach(e -> LOGGER.info(format("setToMap: \"{0}\" : {1}", e.getKey(), e.getValue())));
		}

		{
			final Map<KeyValue<Integer, Integer>, KeyValue<Integer, Integer>> map = new HashMap<>();
			KeyValue<Integer, Integer> kv11 = new KeyValue<>(1, 1);
			KeyValue<Integer, Integer> kv12 = new KeyValue<>(1, 2);
			KeyValue<Integer, Integer> kv13 = new KeyValue<>(1, 3);
			KeyValue<Integer, Integer> kv22 = new KeyValue<>(2, 2);
			KeyValue<Integer, Integer> kv21 = new KeyValue<>(2, 1);

			LOGGER.info(format("addIfAbsent({0}) : {1}", kv11, addIfAbsent(map, kv11)));
			LOGGER.info(format("addIfAbsent({0}) : {1}", kv12, addIfAbsent(map, kv12)));
			LOGGER.info(format("addIfAbsent({0}) : {1}", kv13, addIfAbsent(map, kv13)));
			LOGGER.info(format("addIfAbsent({0}) : {1}", kv22, addIfAbsent(map, kv22)));
			LOGGER.info(format("addIfAbsent({0}) : {1}", kv21, addIfAbsent(map, kv21)));
		}

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
	 * Lookup for the given enumeration value into the values of give enumeration type and returns its index.
	 * @param enumType the type of the enumeration
	 * @param enumValue the value to search for.
	 * @return the index within the enumeration.
	 */
	public static <T extends Enum<T>> int enumLookup(Class<T> enumType, T enumValue) {
		T[] values = enumType.getEnumConstants();
		return IntStream.range(0, values.length).reduce(-1, (a, v) -> values[v] == enumValue ? v : a);
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

	/**
	 * Tells if all given values are integer values.<br>
	 * Source: <a href="http://stackoverflow.com/questions/9898512/how-to-test-if-a-double-is-an-integer">Stackoverflow - How to test if a double is an integer</a>
	 * @param values the values
	 * @return either true or false.
	 */
	public static boolean areAllValuesIntegers1(final double[] values) {
		return Arrays.stream(values).mapToObj(v -> new Double(v)).reduce(true, (a, v) -> a && (v == Math.floor(v)) && Double.isFinite(v), (a0, a1) -> a0 && a1);
	}

	/**
	 * Tells if all given values are integer values.<br>
	 * Source: <a href="http://stackoverflow.com/questions/9898512/how-to-test-if-a-double-is-an-integer">Stackoverflow - How to test if a double is an integer</a>
	 * @param values the values
	 * @return either true or false.
	 */
	public static boolean areAllValuesIntegers2(final double[] values) {
		return Arrays.stream(values).mapToObj(v -> new Double(v)).reduce(true, (a, v) -> a && ((v % 1) == 0), (a0, a1) -> a0 && a1);
	}

	/**
	 * Creates a dictionary from a given list of objects
	 * @param set the set of objects
	 * @return the dictionary
	 */
	public static Map<String, UUID> setToMap(final Set<UUID> set) {
		return set.stream().collect(Collectors.toMap(UUID::toString, Function.identity()));
	}

	/**
	 * Adds the given integer to the map as both key an value if there is no
	 * @param map
	 * @param i
	 * @return
	 */
	public static KeyValue<Integer, Integer> addIfAbsent(final Map<KeyValue<Integer, Integer>, KeyValue<Integer, Integer>> map, final KeyValue<Integer, Integer> kv) {
		return map.computeIfAbsent(kv, Function.identity());
	}

	/** Standard logger */
	private static final Logger LOGGER = Logger.getLogger(StreamEssay.class.getName());

}
