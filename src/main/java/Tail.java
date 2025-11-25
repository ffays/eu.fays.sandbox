import static java.lang.System.out;
import static java.nio.file.Files.size;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayDeque;

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

		if(args.length == 0) {
			out.print("Usage: tail <filename>");
			return;
		}

		final Path file = Paths.get(args[0]);
		for (int loop = 0;; loop++) {
			monitorFileCreation(file);
			if(loop > 0) {
				out.print("\033[2J"); // Clear screen
			}
			long size = readFile(file);
			monitorFileChange(file, size);
		}
	}

	static void monitorFileCreation(final Path file) throws IOException, InterruptedException {
		if(Files.exists(file)) {
			return;
		}

		final ArrayDeque<Path> paths = new ArrayDeque<>();
		{
			Path lookupPath = file;
			while(!Files.exists(lookupPath)) {
				paths.push(lookupPath);
				lookupPath = lookupPath.getParent();
			}
		}

		final FileSystem fs = file.getFileSystem();

		try (final WatchService service = fs.newWatchService()) {
			final WatchEvent.Kind<?>[] events = { ENTRY_CREATE };

			while(!paths.isEmpty()) {
				final Path lookupPath = paths.pop();
				final Path parentFolder = lookupPath.getParent();

				parentFolder.register(service, events);

				boolean found = false;
				while(!found) {
					final WatchKey key = service.poll(100L, MILLISECONDS);

					if (key != null) {
						for (final WatchEvent<?> watchEvent : key.pollEvents()) {
							final Kind<?> kind = watchEvent.kind();
							@SuppressWarnings("unchecked")
							final Path path = ((WatchEvent<Path>) watchEvent).context();
							if (path.equals(lookupPath.getFileName())) {
								if (kind == ENTRY_CREATE) {
									found = true;
								}
							}
						}
						key.reset();
					}
				}
			}
		}
	}

	static long readFile(final Path file) throws IOException {
		final byte[] buffer = Files.readAllBytes(file);
		out.write(buffer);
		out.flush();
		return buffer.length;
	}

	static void monitorFileChange(final Path file, long size) throws IOException, InterruptedException {
		final Path dir = file.getParent();
		final FileSystem fs = file.getFileSystem();

		try (final WatchService service = fs.newWatchService()) {
			final WatchEvent.Kind<?>[] events = { ENTRY_MODIFY, ENTRY_DELETE };
			dir.register(service, events);

			while(Files.exists(file)) {
				final WatchKey key = service.poll(100L, MILLISECONDS);

				if (key != null) {
					for (final WatchEvent<?> watchEvent : key.pollEvents()) {
						final Kind<?> kind = watchEvent.kind();
						@SuppressWarnings("unchecked")
						final Path path = ((WatchEvent<Path>) watchEvent).context();
						if (path.equals(file.getFileName())) {
							if (kind == ENTRY_DELETE) {
								return;
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
