package eu.fays.sandbox.logging;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;

/**
 * Filters a log file based on a timestamp based window.<br>
 * The timestamps must be in ISO 8601 format.
 */
public class TimeStampFilterReader extends BufferedReader {
	/** Start */
	final private LocalDateTime start;

	/** End */
	final private LocalDateTime end;

	/** Current timestamp */
	private LocalDateTime ts;

	/** Timestamp length */
	private static int TIMESTAMP_LENGTH = "1970-01-01T00:00:00".length(); //$NON-NLS-1$

	public static void main(String[] args) throws IOException {
		final File logFile = new File(args[0]);

		try (final TimeStampFilterReader reader = new TimeStampFilterReader(logFile, LocalDateTime.parse("2001-01-01T00:00:00"), LocalDateTime.parse("2100-12-31T23:59:59"));) {
			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}
		}

		try (final TimeStampFilterReader reader = new TimeStampFilterReader(logFile, LocalDateTime.parse("2001-01-01T00:00:00"), LocalDateTime.parse("2100-12-31T23:59:59"));) {
			final int defaultCharBufferSize = 8192;
			char[] cbuf = new char[defaultCharBufferSize];
			int l;
			while ((l = reader.read(cbuf, 0, defaultCharBufferSize)) != -1) {
				String line = String.copyValueOf(cbuf, 0, l);
				System.out.print(line);
			}
		}
	}

	/**
	 * Constructor
	 * @param logFile input log file
	 * @param start start timestamp, optional, may be null
	 * @param end end timestamp, optional, may be null
	 * @throws IOException if the file does not exist
	 */
	public TimeStampFilterReader(final File logFile, final LocalDateTime start, final LocalDateTime end) throws IOException {
		super(new FileReader(logFile));
		this.start = start;
		this.end = end;
		ts = LocalDateTime.ofInstant(Files.readAttributes(logFile.toPath(), BasicFileAttributes.class).creationTime().toInstant(), ZoneId.systemDefault());
	}

	@Override
	public String readLine() throws IOException {
		String line = null;
		while ((line = super.readLine()) != null) {
			if (((start != null) || (end != null)) && line.length() >= TIMESTAMP_LENGTH) {
				try {
					ts = LocalDateTime.parse(line.substring(0, TIMESTAMP_LENGTH));
				} catch (final DateTimeParseException e) {
					// Do Nothing
				}
			}
			if ((start == null || (start != null && ts.compareTo(start) >= 0)) && (end == null || (end != null && ts.compareTo(end) < 0))) {
				return line;
			}
		}

		return null;
	}

	/**
	 * @see java.io.BufferedReader#read(char[], int, int)
	 */
	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		final String line = this.readLine();

		if (line == null) {
			return -1;
		}

		if (off != 0) {
			throw new IOException("Offset > 0 not supported!");
		}

		final String lineSeparator = System.lineSeparator();
		final int l = line.length(), ls = lineSeparator.length(), tl = l + ls;

		if (tl > len) {
			throw new IOException("Line too long!");
		}

		line.getChars(0, l, cbuf, 0);
		lineSeparator.getChars(0, ls, cbuf, l);

		return tl;
	}

}