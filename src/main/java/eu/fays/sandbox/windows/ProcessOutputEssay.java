package eu.fays.sandbox.windows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.Scanner;

import eu.fays.sandbox.text.HexDump;

/**
 * An essay on executing a windows command and retrieving its output
 * @author Frederic Fays
 */
@SuppressWarnings("nls")
public class ProcessOutputEssay {

	public static void main(String[] args) throws Exception {
		essay1();
		essay2();
		essay3();
		essay4();
	}

	/**
	 * Execute the Windows command and directly dump the resulting process output stream
	 * @throws Exception in case of unexpected error
	 */
	public static void essay1() throws Exception {
		cartouche(1);
		final Process process = new ProcessBuilder(WMIC_TEST_COMMAND_LINE).start();
		if (process.waitFor() == 0) {
			try (final InputStream in = process.getInputStream()) {
				HexDump.dump(in, System.out);
			}
		}
	}

	/**
	 * Execute the Windows command, transfer the resulting process output stream into a temporary file, and finally dump the content of the temporary file.
	 * @throws Exception in case of unexpected error
	 */
	public static void essay2() throws Exception {
		cartouche(2);
		final Process process = new ProcessBuilder(WMIC_TEST_COMMAND_LINE).start();
		if (process.waitFor() == 0) {
			final File file = File.createTempFile(ProcessOutputEssay.class.getSimpleName() + "-", ".txt");

			try (final InputStream in = process.getInputStream(); final OutputStream out = new FileOutputStream(file)) {
				for (int b = in.read(); b != -1; b = in.read()) {
					out.write(b);
				}
				out.flush();
			}
			try (final InputStream in = new FileInputStream(file)) {
				HexDump.dump(in, System.out);
			}
			file.delete();
		}
	}

	/**
	 * Execute the Windows command, redirect the output into a temporary file, and finally dump the content of the temporary file.
	 * @throws Exception in case of unexpected error
	 */
	public static void essay3() throws Exception {
		cartouche(3);
		final File file = File.createTempFile(ProcessOutputEssay.class.getSimpleName() + "-", ".txt");
		final ProcessBuilder processBuilder = new ProcessBuilder(WMIC_TEST_COMMAND_LINE);
		processBuilder.redirectOutput(file);
		final Process process = processBuilder.start();
		if (process.waitFor() == 0) {
			try (final InputStream in = new FileInputStream(file)) {
				HexDump.dump(in, System.out);
			}
		}
		file.delete();
	}

	/**
	 * Execute the Windows command, transfer the resulting process output stream into a in-memory buffer, dump the content of the in-memory buffer.
	 * @throws Exception in case of unexpected error
	 */
	public static void essay4() throws Exception {
		cartouche(4);
		final ProcessBuilder processBuilder = new ProcessBuilder(WMIC_TEST_COMMAND_LINE);
		final Process process = processBuilder.start();
		if (process.waitFor() == 0) {
			try (final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
				try (final InputStream in = process.getInputStream(); final Scanner scanner = new Scanner(in)) {
					scanner.useDelimiter("\r\r\n");
					while (scanner.hasNext()) {
						final String line = scanner.next();
						if (!line.isEmpty()) {
							out.write(line.getBytes());
							out.write(System.lineSeparator().getBytes());
						}
					}
				}
				try (final InputStream in = new ByteArrayInputStream(out.toByteArray())) {
					HexDump.dump(in, System.out);
				}
			}
		}
	}

	/**
	 * Commodity method to print out an header
	 * @param i the case number
	 */
	public static void cartouche(final int i) {
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println(MessageFormat.format("--- Essay #{0,number,#}                                                                 ---", i));
		System.out.println("--------------------------------------------------------------------------------");
	}

	/** The command line to be executed */
	public static final String[] WMIC_TEST_COMMAND_LINE = { "wmic", "diskdrive", "where", "\"DeviceID='\\\\\\\\.\\\\PHYSICALDRIVE0'\"", "get", "DeviceID" };
}
