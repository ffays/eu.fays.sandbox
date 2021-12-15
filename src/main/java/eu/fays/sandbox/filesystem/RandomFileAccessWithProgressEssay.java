package eu.fays.sandbox.filesystem;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;

public class RandomFileAccessWithProgressEssay {

	/**
	 * Read the given file and display its reading progress to the console
	 * 
	 * @param args first argument is the file to be read
	 * @throws IOException in case of unexpected error
	 */
	public static void main(String[] args) throws Exception {
		final String filename = args[0];
		final File file = new File(filename);
		try (final RandomAccessFile raf = new RandomAccessFile(file, "r"); final InputStream is = Channels.newInputStream(raf.getChannel())) {
			final RandomFileAccessReadProgressThread readProgressThread = new RandomFileAccessReadProgressThread(raf, file.length());
			readProgressThread.start();
			is.readAllBytes();
		}
	}

	/**
	 * Slow fashion to read all bytes of the given input stream
	 * @param is input stream
	 * @return all bytes
   * @throws IOException if an I/O error occurs
	 * @see InputStream#readAllBytes()
	 */
	public static byte[] readAllBytes(final InputStream is) throws IOException {
		byte[] result = null;
		try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			int c = is.read();
			while (c != -1) {
				baos.write(c);
				c = is.read();
			}
			result = baos.toByteArray();
		}

		return result;
	}

}
