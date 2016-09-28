package eu.fays.sandbox.text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Utility method to print-out a combined Hexadecimal + ASCII dump
 * @author Frederic Fays
 */
@SuppressWarnings("nls")
public class HexDump {

	/**
	 * Converts the given the input stream into a combined Hexadecimal + ASCII dump
	 * @param in the input stream
	 * @param out the output
	 * @throws IOException in case of unexpected error
	 */
	public static void dump(final InputStream in, final OutputStream out) throws IOException {
		final StringBuilder hex = new StringBuilder();
		final StringBuilder txt = new StringBuilder();
		final PrintStream ps = new PrintStream(out);
		int i = 0;
		for (int b = in.read(); b != -1; b = in.read(), i++) {
			if (i % DATA_ROW_WIDTH == 0) {
				if (i > 0) {
					dump(ps, i, hex.toString(), txt.toString());
					hex.setLength(0);
					txt.setLength(0);
					out.flush();
				}
			} else {
				hex.append(' ');
			}
			hex.append(String.format("%02x", b));
			txt.append((new String(new char[] { (char) b }).matches("\\p{Print}")) ? (char) b : '.');
		}
		dump(ps, i, hex.toString(), txt.toString());
		out.flush();
	}

	/**
	 * Prints out current row of data
	 * @param ps the print stream
	 * @param i the offset within the input stream
	 * @param hex the hexadecimal print-out
	 * @param txt the text print-out
	 */
	protected static void dump(final PrintStream ps, final int i, final String hex, final String txt) {
		int observedHexLength = hex.length();
		ps.print(String.format("%08x:\t", i - txt.length()));
		ps.print(hex);
		final int expectedHexLength = (DATA_ROW_WIDTH * 3) - 1;
		while (observedHexLength < expectedHexLength) {
			ps.print(' ');
			observedHexLength++;
		}
		ps.print('\t');
		ps.println(txt);
	}

	/** Data row width, express in # characters */
	protected static final int DATA_ROW_WIDTH = 16;
}
