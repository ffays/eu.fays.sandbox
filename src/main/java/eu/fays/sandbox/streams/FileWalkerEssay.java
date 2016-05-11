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
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * An essay to collect all files under a given folder
 * 
 * @author Fr&eacute;d&eacute;ric Fays
 */
@SuppressWarnings("nls")
public class FileWalkerEssay {

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
		listFiles1(new File("."), new SuffixFilenameFilter(FileNameExtension.class)).forEach(f -> LOGGER.info(format("listFiles #1: {0}", f.getPath())));
		listFiles2(Paths.get("."), FileNameExtension.class).forEach(f -> LOGGER.info(format("listFiles #2: {0}", f.getPath())));
		listFiles3(Paths.get("."), new SuffixFilenameFilter(FileNameExtension.class)).forEach(f -> LOGGER.info(format("listFiles #3: {0}", f.getPath())));
	}

	/**
	 * Search all files under the given root directory matching the given file filter.
	 * @param root the root directory
	 * @param filter the file filter
	 * @return the list of file.
	 * @throws IOException if an I/O error is thrown when accessing the starting file.
	 */
	public static List<File> listFiles1(final File root, final FilenameFilter filter) throws IOException {
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
	public static <T extends Enum<T>> List<File> listFiles2(final Path root, final Class<T> enumType) throws IOException {
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
	public static List<File> listFiles3(final Path root, final FilenameFilter filter) throws IOException {
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

	/** Standard logger */
	private static final Logger LOGGER = Logger.getLogger(FileWalkerEssay.class.getName());

}
