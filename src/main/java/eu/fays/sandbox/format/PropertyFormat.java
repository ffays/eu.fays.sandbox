package eu.fays.sandbox.format;

import static java.time.format.DateTimeFormatter.ofPattern;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.time.temporal.ChronoUnit.YEARS;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("nls")
public enum PropertyFormat {

	/** The string is obtained by calling String.valueOf() */
	DEFAULT,

	/** The string just shows up to 3 decimal values */
	FLOAT_LIMITED,

	/** This field is a double that represents a percentage */
	PERCENTAGE,

	/** Number of seconds since the time origin, shown as a Date Time. */
	DATE_TIME,

	/** Number of seconds since the time origin, shown as a date since the epoch, without hours and minutes. */
	DATE,

	/** Duration in seconds. */
	DURATION;

	/** Number of decimal places in the decimal format */
	public static final int DECIMAL_FORMAT_DIGITS = 3;
	
	/**  Decimal format with {{@link #DECIMAL_FORMAT_DIGITS DECIMAL_FORMAT_DIGITS} decimal places pattern */
	public static final String DECIMAL_FORMAT_PATTERN = "0." + "#".repeat(DECIMAL_FORMAT_DIGITS);
	
	/** Decimal format with one decimal place pattern */
	public static final String DECIMAL_FORMAT_ONE_DIGIT_PATTERN = "0.#";

	/** Decimal format with {{@link #DECIMAL_FORMAT_DIGITS DECIMAL_FORMAT_DIGITS} decimal places */
	public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat(DECIMAL_FORMAT_PATTERN);

	/** Decimal format with one decimal place */
	public static final DecimalFormat DECIMAL_FORMAT_ONE_DIGIT = new DecimalFormat(DECIMAL_FORMAT_ONE_DIGIT_PATTERN);

	/** Timestamp format */
	public static final DateTimeFormatter LOCAL_DATE_TIME_FORMAT = ofPattern("dd-MM-yyyy HH:mm:ss");

	/** Time origin: 1st January of 2001 */
	public static final ZonedDateTime TIME_ORIGIN = ZonedDateTime.of(2001, 1, 1, 0, 0, 0, 0, ZoneOffset.systemDefault());

	/** Origin time-stamp in seconds */
	public static final long T0 = ZonedDateTime.of(TIME_ORIGIN.getYear() + 1, 1, 1, 0, 0, 0, 0, TIME_ORIGIN.getZone()).toEpochSecond();
	
	/** Number of seconds to next new Year, expressed in seconds. */
	public static final long SECONDS_TO_NEW_YEAR = Duration.between(TIME_ORIGIN, TIME_ORIGIN.plus(1, YEARS)).toSeconds();

	/** Short date time format without year */
	
	
			
	public static final DateTimeFormatter SHORT_TIME_FORMAT = isMonthDayFormat()?is12HoursFormat()?ofPattern("MM/dd hh:mm:ss a"):ofPattern("dd/MM HH:mm:ss"):is12HoursFormat()?ofPattern("MM/dd hh:mm:ss a"):ofPattern("dd/MM HH:mm:ss");
	
	/** Long date time format with year */
	public static final DateTimeFormatter LONG_TIME_FORMAT = isMonthDayFormat()?is12HoursFormat()?ofPattern("MM/dd/YY hh:mm:ss a"):ofPattern("dd/MM/YY HH:mm:ss"):is12HoursFormat()?ofPattern("MM/dd/YY hh:mm:ss a"):ofPattern("dd/MM/YY HH:mm:ss");
	
	/** The date format without year */
	public static final DateTimeFormatter SHORT_DATE_FORMAT = isMonthDayFormat()?ofPattern("MM/dd"):ofPattern("dd/MM");

	/** The date format with year */
	public static final DateTimeFormatter LONG_DATE_FORMAT = isMonthDayFormat()?ofPattern("MM/dd/YY"):ofPattern("dd/MM/YY");

	/**
	 * Converts the given time delta into a duration that can be displayed as a number of hours, minute, seconds
	 * @param delta the time delta, expressed in seconds
	 * @return the duration
	 */
	public String durationToString(final long duration) {
		final long hours = (long) (duration / 3600L);
		final long mins = (long) ((duration / 60L) % 60L);
		final long secs = (long) (duration % 60L);
		final List<String> parts = new ArrayList<>();
		if (duration < 0L) {
			parts.add("-");
		}
		if (duration >= 3600L) {
			parts.add(MessageFormat.format("{0,number,00}h", hours));
		}
		parts.add(MessageFormat.format("{0,number,00}m", mins));

		if (secs > 0L) {
			parts.add(MessageFormat.format("{0,number,00}s", secs));
		}
		final String result = String.join(" ", parts);
		//
		assert result != null;
		assert !result.isEmpty();
		//
		return result;
	}

	/**
	 * Format as string the given value depending of its type
	 * @param value the value
	 * @return the string representation of the value
	 */
	public String format(final Object value) {
		final String result;

		if(value == null) {
			return "";
		} else if (value instanceof Boolean) {
			result = value.toString();
		} else if (this == DEFAULT) {
			if (value instanceof Double) {
				result = DECIMAL_FORMAT.format(value);
			} else if ((value instanceof Collection<?>) && ((Collection<?>)value).isEmpty()) {
				result = "";
			} else if (value instanceof LocalDateTime) {
				final LocalDateTime ldt = (LocalDateTime) value;
				result = ldt.format(LOCAL_DATE_TIME_FORMAT);
			} else {
				result = value.toString();
			}
		} else if (value instanceof Number) {
			if (this == FLOAT_LIMITED) {
				result = DECIMAL_FORMAT_ONE_DIGIT.format(value);
			} else if (this == PERCENTAGE) {
				result = String.format("%.2f%%", 100d * ((Number) value).doubleValue()); //$NON-NLS-1$
			} else if (this == DATE_TIME) {
				result = timeToString(((Number) value).longValue());
			} else if (this == DATE) {
				result = dateToString(((Number) value).longValue());
			} else if (this == DURATION) {
				result = durationToString(((Number) value).longValue());
			} else {
				result = value.toString();
			}
		} else {
			result = "";
		}
		return result;
	}

	/**
	 * Converts the given time into a time stamp that can be displayed in the user interface
	 * @param time the time, expressed in seconds since time origin
	 * @return the time stamp
	 */
	public String timeToString(final long time) {
		final String result;
		final ZonedDateTime zonedDateTime = TIME_ORIGIN.plus(time, SECONDS);
		if (time >= SECONDS_TO_NEW_YEAR) {
			result = LONG_TIME_FORMAT.format(zonedDateTime);
		} else {
			result = SHORT_TIME_FORMAT.format(zonedDateTime);
		}
		//
		assert result != null;
		assert !result.isEmpty();
		//
		return result;
	}

	/**
	 * Converts the given time into a date that can be displayed in the user interface
	 * @param time the time, expressed in seconds since time origin
	 * @return the date
	 */
	public String dateToString(final long time) {
		final String result;
		final ZonedDateTime zonedDateTime = TIME_ORIGIN.plus(time, SECONDS);
		if (time >= SECONDS_TO_NEW_YEAR) {
			result = LONG_DATE_FORMAT.format(zonedDateTime);
		} else {
			result = SHORT_DATE_FORMAT.format(zonedDateTime);
		}
		//
		assert result != null;
		assert !result.isEmpty();
		//
		return result;
	}

	/**
	 * Returns true if the current Locale requires Months before Days in the date format
	 * @return true if the current Locale requires Months before Days in the date format
	 */
	public static boolean isMonthDayFormat() {
		final String pattern = new SimpleDateFormat().toLocalizedPattern().toLowerCase();
		int dIndex = pattern.indexOf("d");
		int mIndex = pattern.indexOf("m");
		return dIndex > -1 && mIndex > -1 && mIndex < dIndex;
	}

	/**
	 * Returns true if the current Locale requires AM/PM in the date format
	 * @return true if the current Locale requires AM/PM in the date format
	 */
	public static boolean is12HoursFormat() {
		final String pattern = new SimpleDateFormat().toLocalizedPattern().toLowerCase();
		return pattern.endsWith("a");
	}
}
