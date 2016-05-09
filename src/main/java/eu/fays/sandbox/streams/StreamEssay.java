package eu.fays.sandbox.streams;

import static java.nio.file.Files.walk;
import static java.text.MessageFormat.format;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toCollection;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import eu.fays.sandbox.eclipse.internationalization.InternationalizationCheck;
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
		listFiles(new File("."), new SuffixFilenameFilter(FileNameExtension.class)).forEach(f -> LOGGER.info(format("listFiles: {0}", f.getPath())));
	}

	/**
	 * Search all files under the given root directory matching the given file filter.
	 * @param root the root directory
	 * @param filter the file filter
	 * @return the list of file.
	 * @throws IOException if an I/O error is thrown when accessing the starting file.
	 */
	public static List<File> listFiles(final File root, final FilenameFilter filter) throws IOException {
		return unmodifiableList(walk(root.toPath()).filter(p -> filter.accept(p.toFile().getParentFile(), p.toFile().getName())).map(p -> p.toFile()).collect(toCollection(ArrayList::new)));
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
	 * @return the set of names.
	 */
	public static <T extends Enum<T>> Set<String> enumToSetOfString(Class<T> enumType) {
		return Collections.unmodifiableSet(Arrays.stream(enumType.getEnumConstants()).map(v -> v.name()).collect(Collectors.toCollection(LinkedHashSet::new)));
	}

	/** Standard logger */
	private static final Logger LOGGER = Logger.getLogger(StreamEssay.class.getName());

}
