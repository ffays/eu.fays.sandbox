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
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

import jdk.jfr.Recording;

public class Profiler implements Runnable, UncaughtExceptionHandler, ThreadFactory {

	/** Standard logger */
	private static final Logger LOGGER = Logger.getLogger(Profiler.class.getName());

	/** Executor service, used to identifying which instance is the Shutdown hook */
	private final ScheduledExecutorService executorService;

	/** Recording of the profiler */
	private final AtomicReference<Recording> recordingReference;
	
	public static void main(String[] args) throws InterruptedException {
		LOGGER.info("Starting");
		new Profiler(3L, SECONDS);
		Thread.sleep(5000L);
		LOGGER.info("Stopping");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		dump();
		if(executorService == null) {
			// Periodic run
			final Recording recording = recordingReference.get();
			recording.stop();
			recording.close();
			recordingReference.set(newRecording());
		} else {
			// Shutdown Hook 
			try {
				executorService.shutdown();
				executorService.awaitTermination(5L, SECONDS);
			} catch (final InterruptedException e) {
				// Do nothing
			} finally {
				executorService.shutdownNow();
			}
		}
	}

	
	/**
	 * Creates and start a new recording
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
	 * Dump the recording to the file system and stop it.
	 */
	private void dump() {
		try {
			final Recording recording = recordingReference.get();
			assert recording != null;
			if(recording.getState() != STOPPED) {
				recording.stop();
			}
			if(recording.getState() != CLOSED) {
				final String format = executorService !=null ? "yyyy-MM-dd_HH\u00F7mm\u00F7ss'-final.jfr'":"yyyy-MM-dd_HH\u00F7mm\u00F7ss'.jfr'";
				final String filename = DateTimeFormatter.ofPattern(format).format(now());
				final Path path = Paths.get(filename);
				recording.dump(path);
				recording.close();
			}
		} catch (final IOException e) {
			LOGGER.log(SEVERE, e.getMessage(), e);
		}
	}
	
	/**
	 * Constructor
	 * @param period period
	 * @param unit time unit
	 */
	public Profiler(final long period, final TimeUnit unit) {
		//
		assert period > 0L;
		assert unit != null;
		//
		this.executorService = null;
		this.recordingReference = new AtomicReference<>(newRecording());
		//
		
		final Profiler threadFactory = new Profiler(); // as both Thread Factory and Uncaught Exception Handler
		final ScheduledExecutorService executorService = newSingleThreadScheduledExecutor(threadFactory);  
		executorService.scheduleAtFixedRate(this, period, period, unit);
		
		// Shutdown Hook
		final Profiler shutdownHookRunnable = new Profiler(executorService, recordingReference); // as Shutdown Hook
		final Thread shutdownHook = new Thread(shutdownHookRunnable);
		shutdownHook.setName(getClass().getSimpleName() + "ShutdownHook");
		Runtime.getRuntime().addShutdownHook(shutdownHook);
	}
	
	/**
	 * Constructor for both {@link ThreadFactory} and {@link UncaughtExceptionHandler}
	 */
	private Profiler() {
		this.executorService = null;
		this.recordingReference = null;
	}
	
	/**
	 * Constructor for the {@link Runtime#addShutdownHook(Thread) Shutdown Hook}
	 * @param executorService executor service
	 * @param recordingReference pointer to effective {@link Recording}
	 */
	private Profiler(final ScheduledExecutorService executorService, AtomicReference<Recording> recordingReference) {
		//
		assert executorService != null;
		assert recordingReference != null;
		assert recordingReference.get() != null;
		//
		this.executorService = executorService;
		this.recordingReference = recordingReference;
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
		assert recordingReference == null;
		//
		
		final Thread result = new Thread(runnable);
		result.setName(getClass().getSimpleName());
		result.setUncaughtExceptionHandler(this);
		return result;
	}
}
