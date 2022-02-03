
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Proof of concept to transform a Comma-Separated Values into Tab-Separated Values
 */
@SuppressWarnings("nls")
public class CSV2TSV {

	/**
	 * Converts either the input stream or the given input file from Comma-Separated Values to Tab-Separated Values, print to standard output.
	 * @param args args[0]: the input file (optional)
	 * @throws Exception in case of unexpected error
	 */
	public static void main(String[] args) throws Exception {
		final File file = args.length > 0 ? new File(args[0]) : null;
		final Charset encoding = UTF_8;
		
		final char decimalSeparator = new DecimalFormatSymbols().getDecimalSeparator();
		final char separator = decimalSeparator == ',' ? ';' : ','; // Infer list separator (e.g. ",") based on the decimal separator (e.g. ".")
		final char quoteChar = '"';
		try (final InputStream is =  file != null ? new FileInputStream(file) : System.in; final PushbackInputStream pis = new PushbackInputStream(is);) {
			removeByteOrderMark(pis);
			try(final InputStreamReader isr = new InputStreamReader(pis, encoding)) {
				List<String> record = null;
				while((record = readRecord(isr, separator, quoteChar)) != null) {
					System.out.println(record.stream().collect(Collectors.joining("\t")));
				}
			}
		}
	}

	/**
	 * Reads a "Comma-Separated Values" record.<br>
	 * Records must be compliant to <a href="https://datatracker.ietf.org/doc/html/rfc4180">RFC 4180</a>. 
	 * @param reader the reader
	 * @param separator field separator (e.g. comma, semicolon, tab)
	 * @param quoteChar quoting character (e.g. double quote, simple quote) 
	 * @return the record
	 * @throws IOException in case of unexpected error
	 */
	private static List<String> readRecord(final Reader reader, final char separator, final char quoteChar) throws IOException {
		final StringBuilder builder = new StringBuilder(); // builder for the current field
		int p = -1; // previous char
		int c = reader.read(); // current char
		boolean quoteless = true; // quote-less line flag
		boolean quoting = false; // quoting flag
		if(c == -1) {
			return null; // end of file
		}
		final List<String> record = new ArrayList<>();
		while(c != -1) {
			if(c == separator || c == '\r' || c == '\n') {
				if(quoting) {
					// quoting ... may span over multiple lines
					builder.append((char) c);
				} else if (c == separator) {
					// end of field
					record.add(builder.toString());
					quoteless=true;
					builder.setLength(0);
				} else if (c == '\n') {
					// end of record
					break; 
				}
				// Skip '\r'
			} else {
				if(c == quoteChar) {
					if(quoteless) {
						quoting = true;
						quoteless = false;
						builder.setLength(0); // strip everything before the first quote
					} else if(quoting) {
						quoting = false;
					} else {
						quoting = true;
						if(p == quoteChar) {
							// keep escaped quote character
							builder.append((char) c);
						}
					}
				} else if(quoteless || quoting) {
					builder.append((char) c);
				}
			}
			p = c;
			c = reader.read();
		}
		record.add(builder.toString());
		return record;
	}

	/**
	 * Removes the Byte Order Mark if present
	 * @param pushbackInputStream the push-back input stream
	 * @throws IOException in case of unexpected error
	 */
	private static void removeByteOrderMark(final PushbackInputStream pushbackInputStream) throws IOException {
		final int n=3;
		byte[] bom = new byte[n];
		if (pushbackInputStream.read(bom) != -1) {
			if (bom[n - 3] == (byte) 0xEF && bom[n - 2] == (byte) 0xBB && bom[n - 1] == (byte) 0xBF) {
				// BOM discarded !
			} else {
				// Not a BOM, send back the data to the stream and move forward
				pushbackInputStream.unread(bom);
			}
		}
	}
}
