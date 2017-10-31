package eu.fays.sandbox.process;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

public interface ExecuteCommandTrait {

	/**
	 * Executes the given command<br>
	 * Note: the command time-out is 3 seconds
	 * @param command the command, and its parameters, to be executed
	 * @return the command output, or null in case of failure
	 */
	public static String executeCommand(final String... command) {
		final ProcessBuilder processBuilder = new ProcessBuilder(command);
		try {
			final Process process = processBuilder.start();
			if (process.waitFor(3, TimeUnit.SECONDS)) {
				if (process.exitValue() == 0) {
					// http://javasampleapproach.com/java/ways-to-convert-inputstream-to-string
					try (final InputStream in = process.getInputStream(); final ByteArrayOutputStream os = new ByteArrayOutputStream()) {
						for (int c = in.read(); c != -1; c = in.read()) {
							os.write(c);
						}
						return os.toString();
					}
				}
			}
		} catch (final Exception e) {
			// Do Nothing
		}
		return null;
	}
}
