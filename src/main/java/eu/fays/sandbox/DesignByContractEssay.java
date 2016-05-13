package eu.fays.sandbox;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Stream;

/**
 * An essay on design by contract using assertions
 * @author ffays
 */
@SuppressWarnings("nls")
public class DesignByContractEssay {

	/**
	 * Main
	 * @param args unused
	 */
	public static void main(String[] args) {
		final DateFormat format = new SimpleDateFormat("yyyyMMdd");
		final DesignByContractEssay essay = new DesignByContractEssay(format);
		essay.parseDate("19700101");
		essay.parseDate("xxxx");
	}

	/**
	 * Constructor
	 * @param dateFormat the date format used by the {@link DesignByContractEssay#parseDate(String)}
	 */
	public DesignByContractEssay(final DateFormat dateFormat) {
		_dateFormat = dateFormat;
	}

	/**
	 * Parse the given date
	 * @param dateString the given date
	 * @return the date
	 */
	public Date parseDate(final String dateString) {

		// Pre-conditions
		/* @formatter:off */
		assert dateString != null;
		assert !dateString.isEmpty();
		// Could we do the next pre-condition using an in-line java.util.function.Predicate<T>?
		assert Stream.of(dateString).map(s -> { try { _dateFormat.parse(s); return true; } catch (Exception e) {}; return false; }).findFirst().orElse(false);
		// Yet another way
		assert Collections.singleton(dateString).stream().reduce(false, (a, s) -> { try { _dateFormat.parse(s); return true; } catch (Exception e) {}; return false; }, (a, s) -> null);
		/* @formatter:on */

		Date result = null;
		try {
			result = _dateFormat.parse(dateString);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}

		// Post-conditions
		assert result != null;
		assert _dateFormat.format(result).equals(dateString);
		//
		return result;
	}

	/** A date format */
	private final DateFormat _dateFormat;
}
