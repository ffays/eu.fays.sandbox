package eu.fays.sandbox.datetime;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.WEEKS;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Locale;

public class DateTimeEssay {

	public static void main(String[] args) {
		final ZonedDateTime now = ZonedDateTime.now();
		System.out.println("Now: " + now);
		System.out.println("startOfDay: " + startOfDay(now));
		System.out.println("startOfWeek0: " + startOfWeek0(now));
		System.out.println("startOfWeek: " + startOfWeek(now));
		System.out.println("startOfQuarter: " + startOfQuarter(now));
		System.out.println("startOfYear: " + startOfYear(now));

	}

	public static ZonedDateTime startOfWeek0(final ZonedDateTime datetime) {
		final ZonedDateTime firstDayOfYear = ZonedDateTime.of(datetime.getYear(), 1, 1, 0, 0, 0, 0, datetime.getZone());
		final TemporalField weekOfWeekBasedYear = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
		final int weekNumber = datetime.get(weekOfWeekBasedYear);
		final ZonedDateTime firstDayOfWeekOfYear = firstDayOfYear.with(TemporalAdjusters.dayOfWeekInMonth(1, DayOfWeek.MONDAY));
		final int firstDayOfYearWeekNumber = firstDayOfWeekOfYear.get(weekOfWeekBasedYear);
		final ZonedDateTime result = firstDayOfWeekOfYear.plus(weekNumber - firstDayOfYearWeekNumber, WEEKS);
		return result;
	}

	public static ZonedDateTime startOfDay(final ZonedDateTime datetime) {
		return datetime.truncatedTo(DAYS);
	}

	public static ZonedDateTime startOfWeek(final ZonedDateTime datetime) {
		return datetime.truncatedTo(DAYS).minus(datetime.getDayOfWeek().ordinal(), DAYS);
	}

	public static ZonedDateTime startOfMonth(final ZonedDateTime datetime) {
		return datetime.truncatedTo(DAYS).minus(datetime.getDayOfMonth(), DAYS);
	}

	public static ZonedDateTime startOfQuarter(final ZonedDateTime datetime) {
		return ZonedDateTime.of(datetime.getYear(), datetime.getMonth().firstMonthOfQuarter().ordinal(), 1, 0, 0, 0, 0, datetime.getZone());
	}

	public static ZonedDateTime startOfYear(final ZonedDateTime datetime) {
		return ZonedDateTime.of(datetime.getYear(), 1, 1, 0, 0, 0, 0, datetime.getZone());
	}

}
