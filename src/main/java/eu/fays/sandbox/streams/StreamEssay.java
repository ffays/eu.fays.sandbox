package eu.fays.sandbox.streams;

import static java.lang.String.join;
import static java.nio.file.Files.walk;
import static java.text.MessageFormat.format;
import static java.util.Arrays.stream;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
		listFiles(new File("."), new SuffixFilenameFilter(FileNameExtension.class)).forEach(f -> LOGGER.info(format("listFiles #1: {0}", f.getPath())));
		listFiles(Paths.get("."), FileNameExtension.class).forEach(f -> LOGGER.info(format("listFiles #2: {0}", f.getPath())));
		listFiles(Paths.get("."), new SuffixFilenameFilter(FileNameExtension.class)).forEach(f -> LOGGER.info(format("listFiles #3: {0}", f.getPath())));
	}

	/**
	 * Search all files under the given root directory matching the given file filter.
	 * @param root the root directory
	 * @param filter the file filter
	 * @return the list of file.
	 * @throws IOException if an I/O error is thrown when accessing the starting file.
	 */
	public static List<File> listFiles(final File root, final FilenameFilter filter) throws IOException {
		List<File> result = null;
		try (final Stream<Path> stream = walk(root.toPath())) {
			result = walk(root.toPath()).filter(p -> filter.accept(p.toFile().getParentFile(), p.toFile().getName())).map(p -> p.toFile()).collect(toCollection(ArrayList::new));
		}
		return unmodifiableList(result);
	}

	/**
	 * Search all files under the given root directory matching the given file filter.
	 * @param root the root directory
	 * @param enumType the enum's type
	 * @param <T> the enum's type
	 * @return the list of file.
	 * @throws IOException if an I/O error is thrown when accessing the starting file.
	 */
	public static <T extends Enum<T>> List<File> listFiles(final Path root, final Class<T> enumType) throws IOException {
		List<File> result = null;
		final String syntaxAndPattern = format("glob:**.'{'{0}'}'", join(",", stream(enumType.getEnumConstants()).map(e -> e.name().toLowerCase()).collect(toList())));
		final PathMatcher filter = root.getFileSystem().getPathMatcher(syntaxAndPattern);
		try (final Stream<Path> stream = walk(root)) {
			result = stream.filter(filter::matches).map(p -> p.toFile()).collect(toCollection(ArrayList::new));
		}
		return unmodifiableList(result);
	}

	/**
	 * Search all files under the given root directory matching the given file filter.
	 * @param root the root directory
	 * @param filter the file filter
	 * @return the list of file.
	 * @throws IOException - if an I/O error is thrown by a visitor method
	 */
	private static List<File> listFiles(final Path root, final FilenameFilter filter) throws IOException {
		final List<File> result = new ArrayList<>();
		Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(final Path path, final BasicFileAttributes attrs) throws IOException {
				final File file = path.toFile();
				if (filter.accept(file.getParentFile(), file.getName())) {
					result.add(file);
				}
				return FileVisitResult.CONTINUE;
			}
		});

		return unmodifiableList(result);
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

	/** Standard logger */
	private static final Logger LOGGER = Logger.getLogger(StreamEssay.class.getName());

}
