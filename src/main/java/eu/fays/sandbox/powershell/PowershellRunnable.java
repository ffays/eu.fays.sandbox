package eu.fays.sandbox.powershell;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Calendar;
import java.util.function.BooleanSupplier;

/**
 * Execute a powershell command
 */
public class PowershellRunnable extends Thread implements UncaughtExceptionHandler {

	public static void main(String[] args) throws Exception {
		final PowershellRunnable runnable = new PowershellRunnable(pwsh(), "-ExecutionPolicy", "Bypass", "-NoLogo", "-NonInteractive", "-NoProfile", "-Command",
				escape("[Console]::OutputEncoding=[System.Text.Encoding]::UTF8;[Console]::WriteLine((Get-Date).ToString(\"yyyy-MM-dd_HH$([char]0x00F7)mm$([char]0x00F7)ss\"))"));
		runnable.start();
		runnable.join(runnable.timeout);
		final String stdout = runnable.standardOutputStream.toString("UTF-8");
		final String stderr = runnable.errorOutputStream.toString("UTF-8");
		System.out.println(stdout);
		if (!stderr.isEmpty()) {
			System.err.println(stderr);
		}
		if (runnable.exception != null) {
			runnable.exception.printStackTrace();
		}
	}

	/**
	 * Execute the powershell command
	 */
	@Override
	public void run() {
		try {
			final long t0 = Calendar.getInstance().getTimeInMillis();
			final Process process = new ProcessBuilder().command(command).start();

			// @formatter:off
			final BooleanSupplier isProcessAlive = () -> {try {process.exitValue(); return false;} catch(IllegalThreadStateException e) {return true;}};
			final BooleanSupplier isTimeout = () -> { return (Calendar.getInstance().getTimeInMillis() - t0) >= timeout; };
			// @formatter:on

			final InputStream standardInputStream = process.getInputStream();
			final InputStream errorInputStream = process.getErrorStream();

			while (isProcessAlive.getAsBoolean() && !isInterrupted() && !isTimeout.getAsBoolean()) {
				sleep(50);
				consumeInputStream(standardInputStream, standardOutputStream);
				consumeInputStream(errorInputStream, errorOutputStream);
			}

			if (isTimeout.getAsBoolean()) {
				returnCode = -1;
			} else {
				returnCode = process.exitValue();
			}
		} catch (final IOException e) {
			exception = e;
		} catch (final InterruptedException e) {
			// Do Nothing
		}
	}

	/**
	 * @see java.lang.Thread.UncaughtExceptionHandler#uncaughtException(java.lang.Thread, java.lang.Throwable)
	 */
	@Override
	public void uncaughtException(final Thread t, final Throwable e) {
		//
		assert t != null;
		assert e != null;
		assert t == this;
		//
		exception = e;

	}

	/**
	 * Transfer the data from the given input stream to the given output stream
	 * @param in the input stream
	 * @param out the output stream
	 * @throws IOException in case of unexpected error
	 */
	private void consumeInputStream(final InputStream in, final OutputStream out) throws IOException {
		//
		assert in != null;
		assert out != null;
		//
		int c;
		while (in.available() > 0) {
			c = in.read();
			if (c == -1) {
				break;
			}
			out.write(c);
		}
		out.flush();
	}

	public static final String escape(final String str) {
		return isWindows() ? str.replace("\"", "\\\"") : str;
	}

	public static final boolean isWindows() {
		return System.getProperty("os.name", "").startsWith("Windows");
	}

	/**
	 * Returns the name of the powershell executable
	 * @return the name of the powershell executable
	 * @throws InterruptedException
	 */
	public static final String pwsh() throws InterruptedException {
		if (isWindows()) {
			return "powershell.exe";
		}

		for (final String pwsh : new String[] { "/usr/bin/pwsh", "/usr/local/bin/pwsh" }) {
			final File file = new File(pwsh);
			if (file.exists() && file.isFile() && file.canExecute()) {
				return pwsh;
			}
		}

		throw new AssertionError("Powershell executable not found!");
	}

	/**
	 * Runs the given powershell command
	 * @param command the powershell executable along with its arguments
	 */
	public PowershellRunnable(final String... command) {
		//
		assert command != null;
		assert command.length > 0;
		//
		setName(getClass().getSimpleName());
		setUncaughtExceptionHandler(this);
		this.command = command;
	}

	/** The buffer where the Process's stdout will be copied */
	public final ByteArrayOutputStream standardOutputStream = new ByteArrayOutputStream();

	/** The buffer where the Process's stderr will be copied */
	public final ByteArrayOutputStream errorOutputStream = new ByteArrayOutputStream();

	/** Set in case of error, null otherwise */
	public Throwable exception;

	/** Powershell's command line */
	public final String[] command;

	/** Process time-out in ms */
	public long timeout = 300_000L;

	/** Powershell execution's return code */
	public int returnCode = 0;

}
