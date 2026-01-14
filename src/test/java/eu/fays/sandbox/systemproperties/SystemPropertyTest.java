package eu.fays.sandbox.systemproperties;

import static java.lang.System.getProperties;
import static java.lang.System.getProperty;
import static java.text.MessageFormat.format;
import static java.util.Spliterator.ORDERED;
import static java.util.Spliterators.spliteratorUnknownSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class SystemPropertyTest {

	/** Standard logger */
	private static final Logger LOGGER = Logger.getLogger(SystemPropertyTest.class.getName());

	private static Stream<Object> data() {
		return StreamSupport.stream(spliteratorUnknownSize(getProperties().keys().asIterator(), ORDERED), false);
	}

	@ParameterizedTest
	@MethodSource("data")
	public void testSystemProperty(final String name) {
		final String value = getProperty(name);
		LOGGER.info(format("{0}={1}", name, value));
		assertNotNull(value, () -> format("System property ''{0}'' is null!", name));
	}
}
