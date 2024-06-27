package eu.fays.sandbox.format;

import static eu.fays.sandbox.format.PropertyFormat.DATE_TIME;
import static eu.fays.sandbox.format.PropertyFormat.DEFAULT;
import static eu.fays.sandbox.format.PropertyFormat.DURATION;
import static eu.fays.sandbox.format.PropertyFormat.FLOAT_LIMITED;
import static eu.fays.sandbox.format.PropertyFormat.PERCENTAGE;
import static java.time.format.DateTimeFormatter.ofLocalizedDateTime;
import static java.time.format.FormatStyle.SHORT;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@SuppressWarnings("nls")
public class PropertyFormatTest {

	private static Stream<Arguments> data() {
		final Integer i10m = Integer.valueOf(10 * 60);
		final Double d10m = Double.valueOf(10d * 60d);
		final Double d10_123456m = Double.valueOf(10.123456d * 60d);
		final Double d10 = Double.valueOf(10d);
		final Double d10_123456 = Double.valueOf(10.123456d);

		final DateTimeFormatter dateTimeFormatter = ofLocalizedDateTime(SHORT, SHORT);
		final String timestamp = dateTimeFormatter.format(LocalDateTime.of(2001, 12, 31, 23, 59, 59));
//		final boolean isMonthDayFormat = timestamp.startsWith("12");
		final boolean is12HFormat = timestamp.endsWith("PM");
		final char decimalSeparator = DecimalFormatSymbols.getInstance().getDecimalSeparator();

		// @formatter:off
		return Stream.of(
		/*  1 */ Arguments.of(DEFAULT, "", null),
		/*  2 */ Arguments.of(DEFAULT, "Root", Path.of("Root")),
		/*  3 */ Arguments.of(DATE_TIME, "", null),
		/*  4 */ Arguments.of(DATE_TIME, is12HFormat?"01/01 12:10:00 AM":"01/01 00:10:00", i10m),
		/*  5 */ Arguments.of(DURATION, "", null),
		/*  6 */ Arguments.of(DURATION, "10m", i10m),
		/*  7 */ Arguments.of(DURATION, "10m", d10m),
		/*  8 */ Arguments.of(DURATION, "10m 07s", d10_123456m),
		/*  9 */ Arguments.of(PERCENTAGE, "", null),
		/* 10 */ Arguments.of(PERCENTAGE, "1000.00%".replace('.', decimalSeparator), d10),
		/* 11 */ Arguments.of(FLOAT_LIMITED, "", null),
		/* 12 */ Arguments.of(FLOAT_LIMITED, "10.1".replace('.', decimalSeparator), d10_123456)
		);
		// @formatter:on
	}

	@ParameterizedTest(name = "format({index})")
	@Tag("regular")
	@MethodSource("data")
	public void format(final PropertyFormat format, final String expected, final Object data) {
		final String actual = format.format(data);
		assertEquals(expected, actual);
	}
}
