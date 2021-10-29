import static java.nio.file.Files.newInputStream;
import static java.nio.file.Files.walk;
import static java.text.MessageFormat.format;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.List;
import java.util.Map.Entry;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Recursively scan the given folder for java libraries, and for each of them, print their MANIFEST.MF file
 */
public class PrintManifests {

	/** Standard logger */
	private static final Logger LOGGER = Logger.getLogger(PrintManifests.class.getName());

	/**
	 * Recursively scan the given folder for java libraries, and for each of them, print their MANIFEST.MF file<br>
	 * <br>
	 * VM Arguments:
	 * <pre>
	 * -Djava.util.logging.SimpleFormatter.format="%5$s%6$s%n"
	 * </pre>
	 * @param args first argument is the folder to be scanned for java libraries
	 * @throws IOException in case of unexpected error
	 */
	public static void main(String[] args) throws IOException {
		if(args.length != 1) {
			LOGGER.severe(format("Usage: {0} <root folder>", PrintManifests.class.getSimpleName()));
			System.exit(1);
		}
		
		final Path root = Path.of(args[0]);
		try (final Stream<Path> stream = walk(root)) {
			final PathMatcher filter = root.getFileSystem().getPathMatcher("glob:**.{jar,war,zip}");
			final List<Path> paths = stream.filter(filter::matches).collect(toList());
			for(final Path path : paths) {
				try (final InputStream fis = newInputStream(path); final ZipInputStream zis = new ZipInputStream(fis)) {
					ZipEntry zipEntry = zis.getNextEntry();
					while (zipEntry != null) {
						if ("META-INF/MANIFEST.MF".equals(zipEntry.getName())) {
							LOGGER.info("Dash=--------------------------------------------------------------------------------");
							LOGGER.info(format("File={0}",path.toString()));
							final Manifest manifest = new Manifest(zis);
							final Attributes mainAttributes = manifest.getMainAttributes();
							
							for (final Entry<Object, Object> entry : mainAttributes.entrySet()) {
								LOGGER.info(format("{0}={1}", entry.getKey(), entry.getValue()));
							}
						}
						zipEntry = zis.getNextEntry();
					}
				}
			}
		}
	}
}
