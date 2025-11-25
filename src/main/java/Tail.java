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
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayDeque;
import java.util.logging.Logger;

/**
 * Tail 
 */
public class Tail {

	/** Standard logger */
	private static final Logger LOGGER = Logger.getLogger(Tail.class.getName());
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
		for (int loop = 1;; loop++) {
			monitorFileCreation(file);
			if(loop > 1) {
				out.print("\033[2J"); // Clear screen
				LOGGER.fine("Loop #" + loop);
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
				LOGGER.fine("Missing: " + lookupPath);
				paths.push(lookupPath);
				lookupPath = lookupPath.getParent();
			}
		}

		final FileSystem fs = file.getFileSystem();

		try (final WatchService service = fs.newWatchService()) {
			final WatchEvent.Kind<?>[] events = { ENTRY_CREATE };

			while(!paths.isEmpty()) {
				final Path lookupPath = paths.pop();
				LOGGER.fine("Looking: " + lookupPath);
				final Path parentFolder = lookupPath.getParent();

				parentFolder.register(service, events);

				boolean found = false;
				while(!found && !Files.exists(lookupPath)) {
					final WatchKey key = service.poll(100L, MILLISECONDS);

					if (key != null) {
						for (final WatchEvent<?> watchEvent : key.pollEvents()) {
							final Kind<?> kind = watchEvent.kind();
							@SuppressWarnings("unchecked")
							final Path path = ((WatchEvent<Path>) watchEvent).context();
							if (path.equals(lookupPath.getFileName())) {
								if (kind == ENTRY_CREATE) {
									LOGGER.fine("" + kind + ": " + path);
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
		LOGGER.fine("Reading: " + file);
		final byte[] buffer = Files.readAllBytes(file);
		out.write(buffer);
		out.flush();
		return buffer.length;
	}

	static void monitorFileChange(final Path file, long size) throws IOException, InterruptedException {
		final Path dir = file.getParent();
		final FileSystem fs = file.getFileSystem();
		final FileTime creationTime0 = Files.readAttributes(file, BasicFileAttributes.class).creationTime();
		try (final WatchService service = fs.newWatchService()) {
			final WatchEvent.Kind<?>[] events = { ENTRY_MODIFY, ENTRY_DELETE };
			dir.register(service, events);

			boolean exit = false;
			while(Files.exists(file) && !exit) {
				final WatchKey key = service.poll(100L, MILLISECONDS);

				if (key != null) {
					for (final WatchEvent<?> watchEvent : key.pollEvents()) {
						final Kind<?> kind = watchEvent.kind();
						@SuppressWarnings("unchecked")
						final Path path = ((WatchEvent<Path>) watchEvent).context();
						if (path.equals(file.getFileName())) {
							if (kind == ENTRY_DELETE) {
								LOGGER.fine("" + kind + ": " + path);
								exit = true; // File has been deleted
							} else if (kind == ENTRY_MODIFY) {
								LOGGER.fine("" + kind + ": " + path);
								final FileTime creationTime1 = Files.readAttributes(file, BasicFileAttributes.class).creationTime();

								if(creationTime0.equals(creationTime1)) {
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
								} else {
									LOGGER.fine("Recreated: " + path);
									exit = true; // Meanwhile the file has been re-created
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
