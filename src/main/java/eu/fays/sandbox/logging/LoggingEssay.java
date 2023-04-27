package eu.fays.sandbox.logging;

import static eu.fays.sandbox.logging.S.t;
import static java.lang.Boolean.TRUE;
import static java.util.logging.Level.CONFIG;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.FINER;
import static java.util.logging.Level.FINEST;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.WARNING;

import java.util.function.BooleanSupplier;
import java.util.logging.Level;
import java.util.logging.Logger;
public class LoggingEssay {

	// -Djava.util.logging.SimpleFormatter.format="%1$tFT%1$tT,%1$tL	%4$s	%3$s	%5$s%6$s%n"
	// -Djava.util.logging.config.file=${env_var:HOME}/git/eu.fays.sandbox/logging-simple.properties
	@SuppressWarnings("nls")
	public static void main(String[] args) {
		final Level[] levels = { FINEST, FINER, FINE, CONFIG, INFO, WARNING, SEVERE };
		
		for(final Level level : levels) {
			LOGGER.log(level, level.getName().substring(0, 1) + level.getName().substring(1).toLowerCase());
		}
		
		//
		// The goal is to defer the most possible the construction of the String argument of the logger call
		// And to disable as a whole the logger calls if the assertions are not enabled.
		// 
		for(final Level level : levels) {
			assert (LOGGER.isLoggable(level) && ((BooleanSupplier) () -> {LOGGER.log(level, "Assert " + level.getName().substring(0, 1) + level.getName().substring(1).toLowerCase()); return true;}).getAsBoolean()) || TRUE;
		}

		for(final Level level : levels) {
			assert (LOGGER.isLoggable(level) && ((B) () -> {LOGGER.log(level, "Assert " + level.getName().substring(0, 1) + level.getName().substring(1).toLowerCase()); return true;}).b()) || TRUE;
		}
		
		for(final Level level : levels) {
			assert (LOGGER.isLoggable(level) && t(()->LOGGER.log(level, "Assert " + level.getName().substring(0, 1) + level.getName().substring(1).toLowerCase()))) || TRUE;
		}
		
		assert (LOGGER.isLoggable(FINE) && t(()->LOGGER.fine("FINE Template"))) || TRUE;
	}
	
	public static final Logger LOGGER = Logger.getLogger(LoggingEssay.class.getName());

}
