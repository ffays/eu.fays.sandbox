package eu.fays.sandbox.profilng;

import static java.time.LocalDateTime.now;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.logging.Level.SEVERE;

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

public class Profiler implements Runnable, UncaughtExceptionHandler, ThreadFactory {

	/** Standard logger */
	private static final Logger LOGGER = Logger.getLogger(Profiler.class.getName());

	/** Executor service, used to identifying which instance is the Shutdown hook */
	private final ScheduledExecutorService executorService;

	/** Recording of the profiler */
	private Recording recording = null;
	
	
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
			recording = new Recording();
			recording.start();
		} else {
			// Shutdown Hook 
			try {
				executorService.shutdown();
				executorService.awaitTermination(5L, SECONDS);
			} catch (final InterruptedException e) {
				// Do nothing
			}
		}
	}

	
	/**
	 * Dump the recording to the file system and stop it.
	 */
	private void dump() {
		assert recording != null;
		try {
			final String filename = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH\u00F7mm\u00F7ss'.jfr'").format(now());
			final Path path = Paths.get(filename);
			recording.dump(path);
			recording.stop();
			recording = null;
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
		//
		final ScheduledExecutorService executorService = newSingleThreadScheduledExecutor(this);
		executorService.scheduleAtFixedRate(this, period, period, unit);
		new Profiler(executorService); // as Shutdown Hook
	}
	
	/**
	 * Constructor for the Shutdown Hook
	 * @param executorService executor service
	 */
	private Profiler(final ScheduledExecutorService executorService) {
		//
		assert executorService != null;
		//
		this.executorService = executorService;
		final Thread shutdownHook = new Thread(this);
		shutdownHook.setName(getClass().getSimpleName() + "ShutdownHook");
		Runtime.getRuntime().addShutdownHook(shutdownHook);
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
		assert runnable == this;
		assert recording == null;
		//
		recording = new Recording();
		recording.start();
		
		final Thread result = new Thread(runnable);
		result.setName(getClass().getSimpleName());
		result.setUncaughtExceptionHandler(this);
		return result;
	}
}
