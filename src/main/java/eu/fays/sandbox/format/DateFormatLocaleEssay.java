package eu.fays.sandbox.format;

import java.text.DateFormat;
import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Lists all available locales for date format<br>
 * Articles<br>
 * <ul>
 * <li><a href="https://docs.oracle.com/javase/tutorial/i18n/locale/identify.html">Identifying Available Locales</a><br>
 * <li><a href="http://stackoverflow.com/questions/27454025/unable-to-obtain-localdatetime-from-temporalaccessor-when-parsing-localdatetime">Unable to obtain LocalDateTime from TemporalAccessor when parsing LocalDateTime
 * (Java 8)</a><br>
 * <li><a href="http://stackoverflow.com/questions/838590/how-to-read-list-separator-from-os-in-java">How to read 'List separator' from OS in Java?</a><br>
 * <li><a href="http://stackoverflow.com/questions/4713166/decimal-separator-in-numberformat">Decimal separator in NumberFormat</a><br>
 * </ul>
 */
public class DateFormatLocaleEssay {

	/**
	 * VM Arguments:
	 * <ul>
	 * <li>-ea -Duser.language=en -Duser.country=US
	 * <li>-ea -Duser.language=en -Duser.country=AU
	 * <li>-ea -Duser.language=fr -Duser.country=FR
	 * </ul>
	 * @param args
	 */
	@SuppressWarnings({ "nls", "unused" })
	static public void main(String[] args) {

		// LocalDate
		{
			final LocalDate now = LocalDate.now();
			System.out.println(now.getClass().getSimpleName());
			final DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG);
			final String formatted = formatter.format(now);
			System.out.println(formatted);
			final LocalDate localDate = LocalDate.parse(formatted, formatter);
			System.out.println();
		}

		// LocalDateTime
		{
			final LocalDateTime now = LocalDateTime.now();
			System.out.println(now.getClass().getSimpleName());
			final DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.SHORT);
			final String formatted = formatter.format(now);
			System.out.println(formatted);
			final LocalDateTime localDateTime = LocalDateTime.parse(formatted, formatter);
			System.out.println();
		}

		// Date
		{
			final Date now = Calendar.getInstance().getTime();
			System.out.println(now.getClass().getSimpleName());
			final DateFormat formatter = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
			final String formatted = formatter.format(now);
			System.out.println(formatted);

			final String pattern = ((SimpleDateFormat) formatter).toPattern();
			final String localPattern = ((SimpleDateFormat) formatter).toLocalizedPattern();
			System.out.println(pattern);
			System.out.println(localPattern);
			System.out.println();
		}

		// Date (and Time)
		{
			final Date now = Calendar.getInstance().getTime();
			System.out.println(now.getClass().getSimpleName());
			final DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT, Locale.getDefault());
			final String formatted = formatter.format(now);
			System.out.println(formatted);

			final String pattern = ((SimpleDateFormat) formatter).toPattern();
			final String localPattern = ((SimpleDateFormat) formatter).toLocalizedPattern();
			System.out.println(pattern);
			System.out.println(localPattern);
			System.out.println();
		}

		// Decimal separator
		{
			final DecimalFormatSymbols symbols = new DecimalFormatSymbols();
			System.out.println(symbols.getClass().getSimpleName());
			final char decimalSeparator = symbols.getDecimalSeparator();
			System.out.println(MessageFormat.format("Decimal separator: \u00AB{0}\u00BB", decimalSeparator));
			System.out.println(MessageFormat.format("List separator: \u00AB{0}\u00BB", decimalSeparator == ',' ? ';' : ','));
			System.out.println();
		}

		// DMY or MDY?
		{
			final Instant instant = Instant.parse("2001-12-31T00:00:00Z");
			System.out.println(instant.getClass().getSimpleName());
			final DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.SHORT);
			final String formatted = formatter.format(instant.atOffset(ZoneOffset.UTC));
			final boolean isDmyOrder = formatted.startsWith("31");
			System.out.println(formatted);
			System.out.println("isDmyOrder: " + isDmyOrder);
			System.out.println();
		}

		// Locales
		{
			System.out.println(Locale.class.getSimpleName());
			Arrays.stream(DateFormat.getAvailableLocales()).map(l -> l.toString()).sorted().forEach(s -> System.out.println(s));
		}
	}

}
