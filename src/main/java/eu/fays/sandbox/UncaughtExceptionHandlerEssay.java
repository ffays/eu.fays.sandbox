package eu.fays.sandbox;

import java.lang.Thread.UncaughtExceptionHandler;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.text.MessageFormat.format;
import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;

public class UncaughtExceptionHandlerEssay extends Thread implements UncaughtExceptionHandler {

	/** Standard logger */
	private static final Logger LOGGER = Logger.getLogger(UncaughtExceptionHandlerEssay.class.getName());

	/** Exception from thread */
	private Throwable exception = null;
	
	public static void main(String[] args) throws Exception {
		final UncaughtExceptionHandlerEssay essay = new UncaughtExceptionHandlerEssay();
		essay.start();
		final LocalDateTime t0 = now();
		LocalDateTime t = t0;
		while(essay.isAlive() && Duration.between(t0, t).get(SECONDS) < 3L) {
			LOGGER.info("Thread is running");
			Thread.sleep(1000L);
			t = now();
		}
		
		if(essay.isAlive()) {
			LOGGER.info("Thread is still alive");
			essay.interrupt();
			essay.join(1000L);
		} else {
			LOGGER.info("Thread is over");
		}
		
		if(essay.getException() != null) {
//			LOGGER.log(Level.SEVERE, format("Thread has stopped with an exception: ''{0}''", essay.getException().getMessage()));
			LOGGER.log(Level.SEVERE, format("Thread has stopped with an exception: ''{0}''!", essay.getException().getMessage()), essay.getException());
		} else {
			LOGGER.info("Thread has stopped without exceptions");	
		}
	}
	
	public UncaughtExceptionHandlerEssay() {
		setUncaughtExceptionHandler(this);
		setName(getClass().getSimpleName());
	}
	
	@Override
	public void run() {
		int[] x = new int[10];
		int i = 0, a = 0;
		try {
			while(!isInterrupted()) {
				a += x[i++]; // Triggering an ArrayIndexOutOfBoundsException
				sleep(50L);
			}
		} catch (final InterruptedException e) {
			// Do nothing
		}
		LOGGER.info("a=" + a);
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		exception = e;
	}

	public Throwable getException() {
		return exception;
	}
}
