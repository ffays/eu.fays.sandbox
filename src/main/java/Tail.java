import static java.lang.Math.min;
import static java.lang.System.out;
import static java.nio.file.Files.size;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.util.Arrays.copyOfRange;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

/**
 * Tail 
 */
public class Tail {

	/**
	 * Tails a file
	 * @param args args[0] : input file
	 * @throws Exception in case of unexpected error
	 */
	public static void main(final String[] args) throws Exception {

		final Path file = Paths.get(args[0]);
		final Path dir = file.getParent();
		final FileSystem fs = file.getFileSystem();
		long size = size(file);

		// tail (first read)
		{
			final long count = min(5000L, size);
			final long offset = size - count;
			final byte[] buffer = read(file, offset, (int) count);

			if (buffer.length > 0) {
				byte[] buffer1 = buffer;
				for (int i = 0; i < buffer.length; i++) {
					if (buffer[i] == '\n') {
						buffer1 = copyOfRange(buffer, i + 1, buffer.length);
						break;
					}
				}
				out.write(buffer1);
				out.flush();
			}
		}

		// tail -f
		try (final WatchService service = fs.newWatchService()) {
			final WatchEvent.Kind<?>[] events = { ENTRY_MODIFY, ENTRY_DELETE };
			dir.register(service, events);

			for (;;) {
				final WatchKey key = service.poll(100L, MILLISECONDS);

				if (key != null) {
					for (final WatchEvent<?> watchEvent : key.pollEvents()) {
						final Kind<?> kind = watchEvent.kind();
						@SuppressWarnings("unchecked")
						final Path path = ((WatchEvent<Path>) watchEvent).context();
						if (path.equals(file.getFileName())) {
							if (kind == ENTRY_DELETE) {
								System.exit(0);
							}

							if (kind == ENTRY_MODIFY) {
								final long newSize = size(file);
								final long delta = newSize - size;
								if (delta > 0L) {
									final byte[] buffer = read(file, size, (int) delta);
									if (buffer.length > 0) {
										out.write(buffer);
										out.flush();
									}
									size = newSize;
								}
							}
						}
					}
					key.reset();
				}
			}
		}
	}

	/**
	 * Reads from the given file, the given count of bytes, starting at the given offset
	 * @param file the file to be read
	 * @param offset the offset in bytes within the file to start the read operation
	 * @param count the number of bytes to read
	 * @return the bytes that have been read
	 * @throws IOException in case of unexpected error
	 */
	static byte[] read(final Path file, final long offset, final int count) throws IOException {
		byte[] result = new byte[0];
		try (final FileChannel fc = FileChannel.open(file, READ)) {
			fc.position(offset);
			final ByteBuffer buffer = ByteBuffer.allocate(count);
			int rc = fc.read(buffer);
			if (rc > 0) {
				result = buffer.array();
			}
		}
		return result;
	}
}
