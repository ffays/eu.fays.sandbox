package eu.fays.sandbox.format;

import static java.lang.reflect.Modifier.isPublic;
import static java.lang.reflect.Modifier.isStatic;
import static java.text.MessageFormat.format;
import static java.time.format.DateTimeFormatter.ofLocalizedDateTime;
import static java.time.format.FormatStyle.SHORT;
import static java.util.Collections.unmodifiableMap;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.zone.ZoneRules;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

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
		final Locale defaultLocale = Locale.getDefault();
		
		// Locales #1
		try {
			System.out.println(Locale.class.getSimpleName() + " #1");

			for(final Field field : Locale.class.getDeclaredFields()) {
				final String name = field.getName();
				final int modifiers = field.getModifiers();
				if(isPublic(modifiers) && isStatic(modifiers) && Locale.class.equals(field.getType())) {
					System.out.println(field.getType().getSimpleName() + "." + field.getName() + "=" + field.get(null).toString());
				}
			}
			System.out.println();
		}catch (IllegalArgumentException | IllegalAccessException e) {
			throw new AssertionError(e.getMessage(), e);
		}

		// Locales #2
		{
			System.out.println(Locale.class.getSimpleName() + " #2");
			
			// @formatter:off
			final Map<Locale, Locale> defaultlocaleMap = unmodifiableMap(Arrays.stream(Locale.class.getDeclaredFields()).filter(f -> isPublic(f.getModifiers()) && isStatic(f.getModifiers()) && Locale.class.equals(f.getType())).map(f -> {try {return (Locale) f.get(null);} catch (IllegalArgumentException | IllegalAccessException e) {throw new AssertionError(e.getMessage(), e);}}).collect(toMap(identity(), identity(), (a, b) -> a, LinkedHashMap::new)));
			// @formatter:on
			for(final Locale locale : defaultlocaleMap.keySet()) {
				Locale.setDefault(locale);
				final DateTimeFormatter dateTimeFormatter = ofLocalizedDateTime(SHORT, SHORT);
				final LocalDateTime ldt = LocalDateTime.of(2001, 12, 31, 23, 59);
				System.out.println(format("{0}: {1} ''{2}''" ,locale.getClass().getSimpleName(), locale.toString(), dateTimeFormatter.format(ldt)));
			}
			Locale.setDefault(defaultLocale);
			System.out.println();
		}

		// Locales #3
		{
			System.out.println(Locale.class.getSimpleName() + " #3");
			Arrays.stream(DateFormat.getAvailableLocales()).map(l -> l.toString()).sorted().forEach(s -> System.out.println(s));
			System.out.println();
		}

		// Zones
		{
			System.out.println(ZoneId.class.getSimpleName());
			ZoneId.getAvailableZoneIds().stream().map(z -> z.toString()).sorted().forEach(z -> System.out.println(z));
			System.out.println();
		}

		// LocalDate
		{
			final LocalDate now = LocalDate.now();
			System.out.println(now.getClass().getSimpleName());
			final DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG);
			final String formatted = formatter.format(now);
			System.out.println(formatted);
			
			final DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("'Now:' dd MMM yyyy");
			final String formatted1 = formatter1.format(now);
			System.out.println(formatted1);

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
			System.out.println(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(now));
			System.out.println(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(now));
			final LocalDateTime localDateTime = LocalDateTime.parse(formatted, formatter);
			System.out.println();
		}
		
		// ZonedDateTime
		{
			final ZonedDateTime now = ZonedDateTime.now();
			System.out.println(now.getClass().getSimpleName());
			System.out.println(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss zzz").format(now)); // HTTP Header "Expires" format
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
			final DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
			final String formatted = formatter.format(instant.atOffset(ZoneOffset.UTC));
			final boolean isDmyOrder = formatted.startsWith("31");
			System.out.println(formatted);
			System.out.println("isDmyOrder: " + isDmyOrder);
			System.out.println();
		}

		// DST
		{
			ZoneId zoneId = ZoneId.systemDefault();
			// ZoneId zoneId = ZoneId.of("Europe/Brussels");
			ZoneRules zoneRules = zoneId.getRules();
			ZonedDateTime timeStamp0 = LocalDateTime.of(2017, 3, 20, 10, 28, 38).atZone(zoneId);
			ZonedDateTime timeStamp1 = LocalDateTime.of(2017, 6, 21, 4, 24, 9).atZone(zoneId);
			ZonedDateTime timeStamp2 = LocalDateTime.of(2017, 9, 22, 20, 1, 48).atZone(zoneId);
			ZonedDateTime timeStamp3 = LocalDateTime.of(2017, 12, 21, 16, 27, 57).atZone(zoneId);
			// ZonedDateTime timeStamp = ZonedDateTime.now(zoneId);
			System.out.println(zoneId);
			System.out.println("timeStamp: " + timeStamp0.toString() + " / isDst: " + zoneRules.isDaylightSavings(timeStamp0.toInstant()));
			System.out.println("timeStamp: " + timeStamp1.toString() + " / isDst: " + zoneRules.isDaylightSavings(timeStamp1.toInstant()));
			System.out.println("timeStamp: " + timeStamp2.toString() + " / isDst: " + zoneRules.isDaylightSavings(timeStamp2.toInstant()));
			System.out.println("timeStamp: " + timeStamp3.toString() + " / isDst: " + zoneRules.isDaylightSavings(timeStamp3.toInstant()));
			System.out.println();
		}
	}

}
