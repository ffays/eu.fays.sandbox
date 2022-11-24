package eu.fays.sandbox.logging;

import static java.util.logging.Level.SEVERE;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A thread to filter by timestamp the given log file based on a start timestamp and and end timestamp.<br>
 * <br>
 * Both start timestamp and and end timestamp are optional.<br>
 * To filter, the timestamp must be located at the begining of the each log line, and must be in ISO 8601 format, e.g. "1970-01-01T00:00:00".<br>
 */
public class TimeStampFilterThread extends Thread implements UncaughtExceptionHandler {
	/** Standard logger */
	private static final Logger LOGGER = Logger.getLogger(TimeStampFilterThread.class.getName());

	/** Log file */
	final private File logFile;

	/** Start */
	final private LocalDateTime start;

	/** End */
	final private LocalDateTime end;

	/** Filtered input stream */
	final private PipedInputStream inputStream;

	/** Readyness state */
	private boolean ready = false;

	/** Timestamp length */
	private static int TIMESTAMP_LENGTH = "1970-01-01T00:00:00".length();

	public static void main(String[] args) throws Exception {
		final File logFile = new File(args[0]);

		final TimeStampFilterThread thread = new TimeStampFilterThread(logFile, LocalDateTime.parse("2022-11-15T15:58:00"), LocalDateTime.parse("2022-11-15T15:59:00"));
		thread.start();
		while(!thread.isReady()) {
			try {
				Thread.sleep(20L);
			} catch (InterruptedException e) {
				// Do Nothing
			}
		}
		
		try(final InputStreamReader isr = new InputStreamReader(thread.getInputStream()); final BufferedReader reader = new BufferedReader(isr)) {
			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}
		}
	}
	
	/**
	 * Constructor
	 * @param logFile input log file
	 * @param start start timestamp, optional, may be null
	 * @param end end timestamp, optional, may be null
	 */
	public TimeStampFilterThread(final File logFile, final LocalDateTime start, final LocalDateTime end) {
		this.logFile = logFile;
		this.start = start;
		this.end = end;
		inputStream = new PipedInputStream();
		setUncaughtExceptionHandler(this);
		setName(getClass().getSimpleName());
	}

	/**
	 * Returns the filtered input stream
	 * @return the filtered input stream
	 */
	public PipedInputStream getInputStream() {
		return inputStream;
	}

	/**
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		final byte[] nl = System.lineSeparator().getBytes();
		try (final PipedOutputStream out = new PipedOutputStream(inputStream); final FileReader fileReader = new FileReader(logFile); final BufferedReader reader = new BufferedReader(fileReader);) {
			LocalDateTime ts = LocalDateTime.ofInstant(Files.readAttributes(logFile.toPath(), BasicFileAttributes.class).creationTime().toInstant(), ZoneId.systemDefault());
			String line = null;
			line = reader.readLine();
			setReady(true);
			while (line != null) {
				if (((start != null) || (end != null)) && line.length() >= TIMESTAMP_LENGTH) {
					try {
						ts = LocalDateTime.parse(line.substring(0, TIMESTAMP_LENGTH));
					} catch (final DateTimeParseException e) {
						// Do Nothing
					}
				}
				if ((start == null || (start != null && ts.compareTo(start) >= 0)) && (end == null || (end != null && ts.compareTo(end) < 0))) {
					out.write(line.getBytes());
					out.write(nl);
				}
				line = reader.readLine();
			}
		} catch (final IOException e) {
			LOGGER.log(SEVERE, e.getMessage(), e);
		}
	}

	/**
	 * Returns the readiness state
	 * @return either true or false
	 */
	public boolean isReady() {
		return ready;
	}

	/**
	 * Sets the readiness state
	 * @param ready the readiness state
	 */
	synchronized protected void setReady(final boolean ready) {
		this.ready = ready;
	}

	/**
	 * @see java.lang.Thread.UncaughtExceptionHandler#uncaughtException(java.lang.Thread, java.lang.Throwable)
	 */
	@Override
	public void uncaughtException(final Thread t, final Throwable e) {
		LOGGER.log(Level.SEVERE, e.getMessage(), e);
	}
}