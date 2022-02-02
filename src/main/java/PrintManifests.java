import static java.nio.file.Files.newInputStream;
import static java.nio.file.Files.walk;
import static java.text.MessageFormat.format;
import static java.util.stream.Collectors.toList;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Recursively scan the given folder for java libraries, and for each of them, print their MANIFEST.MF file
 */
public class PrintManifests {

	// VM Arguments: -Djava.util.logging.SimpleFormatter.format="%5$s%6$s%n"
	/**
	 * Recursively scan the given folder for java libraries, and for each of them, print their MANIFEST.MF file<br>
	 * 
	 * @param args first argument is the folder to be scanned for java libraries
	 * @throws IOException in case of unexpected error
	 */
	@SuppressWarnings("nls")
	public static void main(String[] args) throws IOException {
		if(args.length != 1) {
			System.err.println(format("Usage: {0} <root folder>", PrintManifests.class.getSimpleName()));
			System.exit(1);
		}
		
		final Path root = Path.of(args[0]);
		
		
		// @formatter:off
		final String[] knownLicenses = {
			"Eclipse Public License",
			"Apache License"
		};
		// @formatter:on
		
		final char[] versionDelimiters = {',', '('};

		
		try (final Stream<Path> stream = walk(root)) {
			final PathMatcher filter = root.getFileSystem().getPathMatcher("glob:**.{jar,war,zip}");
			final List<Path> paths = stream.filter(p -> filter.matches(p) || "bundleFile".equals(p.getFileName().toString())).collect(toList());
			for(final Path path : paths) {
				try (final InputStream fis = newInputStream(path); final ZipInputStream zis = new ZipInputStream(fis)) {
					ZipEntry zipEntry = zis.getNextEntry();
					Properties pluginProperties = null;
					Manifest manifest = null;
					String licenseText = null;
					while (zipEntry != null) {
						if("plugin.properties".equals(zipEntry.getName())) {
							pluginProperties = new Properties();
							pluginProperties.load(zis);
						} else if ("META-INF/MANIFEST.MF".equals(zipEntry.getName())) {
							manifest = new Manifest(zis);
						} else if ("about.html".equals(zipEntry.getName()) || "LICENSE".equals(zipEntry.getName())) {
							final ByteArrayOutputStream baos = new ByteArrayOutputStream();
							zis.transferTo(baos);
							licenseText = baos.toString();
						}
						zipEntry = zis.getNextEntry();
					}
					if(manifest != null) {
						System.out.println("Dash=--------------------------------------------------------------------------------");
						System.out.println(format("File={0}",path.toString()));
						final Attributes mainAttributes = manifest.getMainAttributes();
						for (final Entry<Object, Object> entry : mainAttributes.entrySet()) {
							String value = (String) entry.getValue();
							if(value.startsWith("%") && pluginProperties != null && pluginProperties.containsKey(value.substring(1))) {
								value = (String) pluginProperties.get(value.substring(1));
							}
							System.out.println(format("{0}={1}", entry.getKey(), value));
						}
						if(licenseText != null) {
							final String licenseOneLiner = licenseText.replaceAll("<[^>]+>","").replaceAll("\\p{Space}+", " ");
							String license = null;
							for(final String knownLicense : knownLicenses) {
								if(licenseOneLiner.indexOf(knownLicense) != -1) {
									license = knownLicense;
									break;
								}
							}
							
							if(license != null) {
								System.out.println(format("License={0}", license));
							} else {
								System.out.println(format("License-Text={0}", licenseOneLiner));
							}
							
							final int licenseVersionBeginIndex = licenseText.indexOf("Version ");
							if(licenseVersionBeginIndex != -1) {
								String licenseVersion = licenseText.substring(licenseVersionBeginIndex + "Version ".length());
								
								int licenseVersionEndIndex = Integer.MAX_VALUE;
								for(char delimiter : versionDelimiters) {
									final int index = licenseVersion.indexOf(delimiter);
									if(index != -1 && index < licenseVersionEndIndex) {
										licenseVersionEndIndex = index;
									}
								}
								if(licenseVersionEndIndex != Integer.MAX_VALUE) {
									licenseVersion = licenseVersion.substring(0, licenseVersionEndIndex).trim();
								}
								System.out.println(format("License-Version={0}", licenseVersion));
								
								if(licenseVersionEndIndex != Integer.MAX_VALUE) {
									if(("Apache License").equals(license)) {
										final String licenseURL = format("https://www.apache.org/licenses/LICENSE-{0}", licenseVersion);
										System.out.println(format("License-URL={0}", licenseURL));
									} else if(("Eclipse Public License").equals(license)) {
										final String licenseURL = format("https://www.eclipse.org/legal/epl-v{0}.html", licenseVersion.replaceAll("\\.", ""));
										System.out.println(format("License-URL={0}", licenseURL));
										final String licenseAltURL = format("https://www.eclipse.org/legal/epl-{0}", licenseVersion);
										assert licenseOneLiner.indexOf(licenseURL.substring("https://".length())) != -1 || licenseOneLiner.indexOf(licenseAltURL.substring("https://".length())) != -1; 
									}
								}
							}
						}
					}
				}
			}
		}
	}
}
