package eu.fays.sandbox.excel;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class ExcelDateEssay {

	public static void main(String[] args) {
		System.out.println(toExcelDate(LocalDateTime.now().truncatedTo(ChronoUnit.DAYS)));
		System.out.println(toLocalDateTime(toExcelDate(LocalDateTime.now().truncatedTo(ChronoUnit.DAYS))));
	}

	/**
	 * Converts the given excel date into a java date
	 * @param excelDate the excel date
	 * @return the java date
	 */
	public static LocalDateTime toLocalDateTime(final double excelDate) {
		//
		assert !Double.isNaN(excelDate);
		assert excelDate >= 0d;
		//

		final LocalDateTime excelEpoch = LocalDateTime.of(1899, 12, 31, 0, 0);
		// For DBase and thus Excel, Epoch + 60 days = "February 29th 1900"
		// ... an non-existing day because 1900 is not a leap year,
		// therefore, starting from the 61st day after the Epoch, this non-existing day has to be skipped.

		// 1 day == 24h * 60m * 60s * 1000ms == 84_400_000 ms
		final LocalDateTime result = excelEpoch.plus(new Double((excelDate - (excelDate >= 61d ? 1d : 0d)) * 86_400_000d).longValue(), ChronoUnit.MILLIS);

		return result;
	}

	/**
	 * Converts the given java date into a excel date
	 * @param localDateTime the java date
	 * @return the excel date
	 * @throws Exception in case of unexpected error
	 */
	public static double toExcelDate(final LocalDateTime localDateTime) {
		//
		assert localDateTime != null;
		final LocalDateTime excelEpoch = LocalDateTime.of(1899, 12, 31, 0, 0);

		double result = Duration.between(excelEpoch, localDateTime).toMillis() / 86_400_000d;
		if (localDateTime.compareTo(LocalDateTime.of(1900, 3, 1, 0, 0)) >= 0) {
			result += 1d;
		}
		return result;
	}

}
