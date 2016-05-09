package eu.fays.sandbox;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.Files.delete;
import static java.nio.file.Files.walkFileTree;
import static java.text.MessageFormat.format;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * A shutdown hook to tidy-up generated temporary files in case of abrupt program termination (i.e. due to an uncaught exception)
 */
public interface RecursiveDeleteShutdownHook {
	/**
	 * When the JVM stops, Performs a recursive deletion of the given directory.
	 * @param path the directory to be deleted when the JVM stops
	 */
	default void addRecursiveDeleteShutdownHook(final Path path) {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@SuppressWarnings("nls")
			@Override
			public void run() {
				try {
					walkFileTree(path, new SimpleFileVisitor<Path>() {
						@Override
						public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
							delete(file);
							return CONTINUE;
						}

						@Override
						public FileVisitResult postVisitDirectory(final Path dir, final IOException e) throws IOException {
							if (e == null) {
								delete(dir);
								return CONTINUE;
							}
							// directory iteration failed
							throw e;
						}
					});
				} catch (IOException e) {
					throw new RuntimeException(format("Failed to delete ''{0}''", path), e);
				}
			}
		}));
	}
}
