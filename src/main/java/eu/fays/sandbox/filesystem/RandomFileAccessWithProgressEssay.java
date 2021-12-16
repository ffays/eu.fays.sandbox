package eu.fays.sandbox.filesystem;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.util.function.DoubleConsumer;

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
			final RandomFileAccessReadProgressThread readProgressThread = new RandomFileAccessReadProgressThread(raf, file.length(), p -> System.out.printf("%.0f%%\n", p));
			readProgressThread.start();
			is.readAllBytes();
		}

		System.out.println("2nd way");
		final DoubleConsumer callBack = new DoubleConsumer() {
			long prev = -1;
			@Override public void accept(double p) {
				if (prev != (long) p) System.out.println((prev = (long) p) + "%");
			}
		};

		try (final FileInputStream fis = new FileInputStream(file); final InputStreamWithProgressDecorator is = new InputStreamWithProgressDecorator(fis, file.length(), callBack)) {
			readAllBytes(is, 1 << 20);
		}
	}

	/**
	 * Very slow fashion to read all bytes of the given input stream
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

	/**
	 * Slow fashion to read all bytes of the given input stream
	 * @param is input stream
	 * @param n the buffer size
	 * @return all bytes
	 * @throws IOException if an I/O error occurs
	 * @see InputStream#readAllBytes()
	 */
	public static byte[] readAllBytes(final InputStream is, int n) throws IOException {
		byte[] result = null;
		try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			byte[] buffer = is.readNBytes(n);
			while (buffer.length != 0) {
				baos.write(buffer);
				buffer = is.readNBytes(n);
			}
			result = baos.toByteArray();
		}

		return result;
	}

}
