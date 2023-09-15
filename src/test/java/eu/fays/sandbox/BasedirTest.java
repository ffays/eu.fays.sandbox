package eu.fays.sandbox;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
			Arguments.of("basedir", Path.of(System.getProperty("user.home"), "git", BasedirTest.class.getPackageName()))
		);
		// @formatter:on
	}

	@ParameterizedTest
	@MethodSource("data")
	public void basedirTest(final String property, final Path path) {
		final String value = System.getProperty(property);
		assertNotNull(value);
		assertEquals(path.toString(), value);
		assertTrue(Files.exists(path));
		assertTrue(Files.isDirectory(path));
	}
}
