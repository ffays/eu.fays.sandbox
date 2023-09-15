package eu.fays.sandbox;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

/**
 * Check if the Maven "basedir" system property is defined and matches an existing folder
 */
public class BasedirTest {

	// -ea -Dbasedir=${project_loc}

	@Test
	@SuppressWarnings("nls")
	public void basedirTest() {
		final String basedir = System.getProperty("basedir");
		assertNotNull(basedir);

		final Path basedirPath = Path.of(System.getProperty("user.home"), "git", getClass().getPackageName());
		assertEquals(basedirPath.toString(), basedir);
		assertTrue(Files.exists(basedirPath));
		assertTrue(Files.isDirectory(basedirPath));
	}
}
