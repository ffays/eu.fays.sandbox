package eu.fays.sandbox.objsize;

import static java.text.MessageFormat.format;

import java.lang.instrument.Instrumentation;
import java.util.HashMap;
import java.util.logging.Logger;

// https://stackoverflow.com/questions/52353/in-java-what-is-the-best-way-to-determine-the-size-of-an-object#52682
// https://stackoverflow.com/questions/20175225/hash-map-memory-overhead
// java -javaagent:eu.fays.sandbox.jar -jar eu.fays.sandbox.jar
public class ObjectSizeEssay {
	private static Instrumentation instrumentation;

	public static void premain(final String args, final Instrumentation inst) {
		instrumentation = inst;
	}

	public static void main(String[] args) throws Exception {
		final Object object = new HashMap<String, String>();
		LOGGER.info(format("{0}: {1,number,0} bytes", object.getClass().getSimpleName(), instrumentation.getObjectSize(object)));
		// Answer : 48 bytes
	}

	/** Standard logger */
	private static final Logger LOGGER = Logger.getLogger(ObjectSizeEssay.class.getName());
}
