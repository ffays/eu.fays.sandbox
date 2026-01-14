package eu.fays.sandbox;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Check if the Maven "basedir" system property is defined and matches an existing folder
 */
public class BasedirTest {

	// -ea -Dbasedir=${project_loc}

	@SuppressWarnings("nls")
	public static Stream<Arguments> data() {
		// @formatter:off
		return Stream.of(
			Arguments.of("basedir", Path.of(System.getProperty("basedir", "")))
		);
		// @formatter:on
	}

	@ParameterizedTest(name = "{0} \u21D2 {1}")
	@MethodSource("data")
	public void basedirTest(final String property, final Path path) {
		final String value = System.getProperty(property);
		assertNull(value);
		assertTrue(Files.exists(path));
		assertTrue(Files.isDirectory(path));
	}
}
