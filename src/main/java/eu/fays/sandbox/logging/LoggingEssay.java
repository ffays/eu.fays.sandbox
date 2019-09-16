package eu.fays.sandbox.logging;

import java.util.logging.Logger;

public class LoggingEssay {

	// -Djava.util.logging.config.file=${env_var:HOME}/git/eu.fays.sandbox/logging-simple.properties
	public static void main(String[] args) {
		LOGGER.info("Hello world!");
		
	}
	
	public static final Logger LOGGER = Logger.getLogger(LoggingEssay.class.getName());

}
