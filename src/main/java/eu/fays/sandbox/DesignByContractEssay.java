package eu.fays.sandbox;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * An essay on design by contract using assertions
 * @author ffays
 */
@SuppressWarnings("nls")
public class DesignByContractEssay {

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
	 */
	public static void main(String[] args) {
		final String validInput = "19700101";
		final String invalidInput = "xxxx";
		try {
			LOGGER.info(MessageFormat.format("{0,number}", parseInt(validInput)));
			DesignByContractEssay.parseInt(invalidInput);
		} catch (AssertionError e) {
			LOGGER.log(Level.SEVERE, e.getClass().getSimpleName(), e);
		}

		try {
			final DateFormat format = new SimpleDateFormat("yyyyMMdd");
			final DesignByContractEssay essay = new DesignByContractEssay(format);
			LOGGER.info(MessageFormat.format("{0,date,yyyy-MM-dd}", essay.parseDate(validInput)));
			essay.parseDate(invalidInput);
		} catch (AssertionError e) {
			LOGGER.log(Level.SEVERE, e.getClass().getSimpleName(), e);			
		}
	}

	/**
	 * Constructor
	 * @param dateFormat the date format used by the {@link DesignByContractEssay#parseDate(String)}
	 */
	public DesignByContractEssay(final DateFormat dateFormat) {
		_dateFormat = dateFormat;
	}

	/**
	 * Parse the given string
	 * @param intString the string representation of an integer
	 * @return the integer value
	 */
	public static int parseInt(final String intString) {
		// Pre-conditions
		/* @formatter:off */
		assert intString != null;
		assert !intString.isEmpty();
		
		// Could we do the next pre-condition using an in-line java.util.function.Predicate<T>?
		// 1st way:
		assert Optional.of(intString).filter(s -> { try { Integer.parseInt(intString); return true; } catch (NumberFormatException e) {}; return false; }).isPresent();
		// 2nd way:
		assert Stream.of(intString).map(s -> { try { Integer.parseInt(intString); return true; } catch (NumberFormatException e) {}; return false; }).findFirst().orElse(false);
		// 3rd way:
		assert Collections.singleton(intString).stream().reduce(false, (a, s) -> { try { Integer.parseInt(intString); return true; } catch (NumberFormatException e) {}; return false; }, (a, s) -> null);
		/* @formatter:on */

		return Integer.parseInt(intString);

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
		assert Optional.of(dateString).filter(s -> { try { _dateFormat.parse(s); return true; } catch (Exception e) {}; return false; }).isPresent();
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

	/** Standard logger */
	private static final Logger LOGGER = Logger.getLogger(DesignByContractEssay.class.getName());
}
