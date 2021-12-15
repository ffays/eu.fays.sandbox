package eu.fays.sandbox.filesystem;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.time.LocalDateTime;

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
		final long length = file.length();
		System.out.println(LocalDateTime.now());
		try (final RandomAccessFile raf = new RandomAccessFile(file, "r"); final InputStream is = Channels.newInputStream(raf.getChannel())) {

			final Thread readProgressThread = new Thread() {

				@Override
				public void run() {
					try {
						long progress = -1L;
						do {
							final long p = raf.getFilePointer() * 100L / length;
							if (p != progress) {
								System.out.println(p);
								System.out.flush();
								progress = p;
							}
							Thread.sleep(100L);
						} while (!isInterrupted() && progress < 100L);
					} catch (final IOException | InterruptedException e) {
					}
				}

			};

			readProgressThread.start();
			is.readAllBytes();
//			readAllBytes(is);
		}
		System.out.println(LocalDateTime.now());
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
