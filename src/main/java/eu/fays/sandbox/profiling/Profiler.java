package eu.fays.sandbox.profiling;

import static java.time.LocalDateTime.now;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.logging.Level.SEVERE;
import static jdk.jfr.RecordingState.CLOSED;
import static jdk.jfr.RecordingState.STOPPED;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import jdk.jfr.Recording;

/**
 * Profiler relying on Java Flight Recorder.<br>
 * <br>
 * VM Args:<br>
 * <br>
 * <code>-XX:StartFlightRecording=disk=true,maxage=5m,dumponexit=true,filename=java-flight-record.jfr</code><br>
 * <code>-XX:FlightRecorderOptions=stackdepth=128</code><br>
 * <code>-XX:+HeapDumpOnOutOfMemoryError</code><br>
 * <code>-XX:HeapDumpPath=./heap-dump.hprof</code><br>
 * <code>-Djava.util.logging.SimpleFormatter.format='%1$tFT%1$tT,%1$tL	%4$s	%3$s	%5$s%6$s%n'</code><br>
 */
/*
-XX:StartFlightRecording=disk=true,maxage=5m,dumponexit=true,filename=java-flight-record.jfr
-XX:FlightRecorderOptions=stackdepth=128
-XX:+HeapDumpOnOutOfMemoryError
-XX:HeapDumpPath=./heap-dump.hprof
-Djava.util.logging.SimpleFormatter.format='%1$tFT%1$tT,%1$tL	%4$s	%3$s	%5$s%6$s%n'
*/
// jcmd (Get-Process -Name "javaw").Id JFR.dump "filename=$(Get-Date -Format "yyyy-MM-dd_HH$([char]0x00F7)mm$([char]0x00F7)ss").jfr"
public class Profiler implements Runnable, UncaughtExceptionHandler, ThreadFactory {

	/** Standard logger */
	private static final Logger LOGGER = Logger.getLogger(Profiler.class.getName());

	/** Executor service, used to identifying which instance is the Shutdown hook */
	private final ScheduledExecutorService executorService;

	/** Recording of the profiler */
	private Recording recording = null;

	/**
	 * Main
	 * @param args unused
	 * @throws InterruptedException in case of unexpected error
	 */
	public static void main(String[] args) throws InterruptedException {
		LOGGER.info("Starting");
		new Profiler(3L, SECONDS);
		Thread.sleep(5000L);
		LOGGER.info("Stopping");
		System.exit(0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		if (executorService == null) {
			// Periodic run
			assert recording != null;
			assert recording.getState() != STOPPED;
			assert recording.getState() != CLOSED;
			//
			recording.stop();
			recording.close();
			recording = newRecording();
		} else {
			// Shutdown Hook
			executorService.shutdown();
			try {
				if(!executorService.awaitTermination(5L, SECONDS)) {
					executorService.shutdownNow();
					if(!executorService.awaitTermination(5L, SECONDS)) {
						LOGGER.log(SEVERE, "Profiler didn't gracefully stop!", new Throwable().fillInStackTrace());
					}
				}
			} catch (final InterruptedException e) {
				executorService.shutdownNow();
				Thread.currentThread().interrupt();
			}
		}
	}

	/**
	 * Creates and start a new recording
	 * 
	 * @return the new recording
	 * @throws IOException in case of unexpected error
	 */
	private Recording newRecording() {
		final Recording result = new Recording();
		final String filename = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH\u00F7mm\u00F7ss'.jfr'").format(now());
		final Path path = Paths.get(filename);
		result.setDumpOnExit(true);
		try {
			result.setDestination(path);
		} catch (final IOException e) {
			LOGGER.log(SEVERE, e.getMessage(), e);
		}
		result.start();
		return result;
	}

	/**
	 * Constructor
	 * 
	 * @param period period
	 * @param unit time unit
	 */
	public Profiler(final long period, final TimeUnit unit) {
		//
		assert period > 0L;
		assert unit != null;
		//
		this.executorService = null;
		this.recording = newRecording();
		//

		final Profiler threadFactory = new Profiler(); // as both Thread Factory and Uncaught Exception Handler
		final ScheduledExecutorService executorService = newSingleThreadScheduledExecutor(threadFactory);
		executorService.scheduleAtFixedRate(this, period, period, unit);

		// Shutdown Hook
		final Profiler shutdownHookRunnable = new Profiler(executorService); // as Shutdown Hook
		final Thread shutdownHook = new Thread(shutdownHookRunnable);
		shutdownHook.setName(getClass().getSimpleName() + "ShutdownHook");
		Runtime.getRuntime().addShutdownHook(shutdownHook);
	}

	/**
	 * Constructor for both {@link ThreadFactory} and {@link UncaughtExceptionHandler}
	 */
	private Profiler() {
		this.executorService = null;
		this.recording = null;
	}

	/**
	 * Constructor for the {@link Runtime#addShutdownHook(Thread) Shutdown Hook}
	 * 
	 * @param executorService executor service
	 */
	private Profiler(final ScheduledExecutorService executorService) {
		//
		assert executorService != null;
		//
		this.executorService = executorService;
		this.recording = null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void uncaughtException(final Thread t, final Throwable e) {
		LOGGER.log(SEVERE, e.getMessage(), e);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Thread newThread(final Runnable runnable) {
		//
		assert runnable != null;
		assert executorService == null;
		//

		final Thread result = new Thread(runnable);
		result.setName(getClass().getSimpleName());
		result.setUncaughtExceptionHandler(this);
		return result;
	}
}
