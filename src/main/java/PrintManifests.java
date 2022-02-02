import static java.nio.file.Files.newInputStream;
import static java.nio.file.Files.walk;
import static java.text.MessageFormat.format;
import static java.util.Collections.unmodifiableMap;
import static java.util.stream.Collectors.toList;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
@SuppressWarnings("nls")
public class PrintManifests {

	// VM Arguments: -Djava.util.logging.SimpleFormatter.format="%5$s%6$s%n"
	/**
	 * Recursively scan the given folder for java libraries, and for each of them, print their MANIFEST.MF file<br>
	 * 
	 * @param args first argument is the folder to be scanned for java libraries
	 * @throws IOException in case of unexpected error
	 */
	public static void main(String[] args) throws IOException {
		if(args.length != 1) {
			System.err.println(format("Usage: {0} <root folder>", PrintManifests.class.getSimpleName()));
			System.exit(1);
		}
		
		final Path root = Path.of(args[0]);
		
		
		
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
							for(final String knownLicense : KNOWN_LICENSES) {
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
										assert BUNDLE_VERSION_MAP.entrySet().stream().filter(e -> ECLIPSE_PUBLIC_LICENSE.equals(e.getValue().getKey())).map(Entry::getKey).anyMatch(url -> licenseOneLiner.contains(url));
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	private static final String ECLIPSE_PUBLIC_LICENSE = "Eclipse Public License";
	private static final String APACHE_LICENSE =  "Apache License";
	
	// @formatter:off
	private static final String[] KNOWN_LICENSES = {
		ECLIPSE_PUBLIC_LICENSE,
		APACHE_LICENSE
	};
	// @formatter:on
	
	private static final String[] APACHE_LICENSE_VERSIONS = {"1.0", "1.1", "2.0"};
	private static final String[] ECLIPSE_LICENSE_VERSIONS = {"1.0", "2.0"};


	private static final Map<String, Entry<String, String>> BUNDLE_VERSION_MAP;
	
	static {
		final Map<String, Entry<String, String>> map = new HashMap<>();
		
		final String[] schemes = {"http", "https"};
		
		for(final String licenseVersion : ECLIPSE_LICENSE_VERSIONS) {
			final SimpleImmutableEntry<String, String> entry = new SimpleImmutableEntry<>(ECLIPSE_PUBLIC_LICENSE, licenseVersion);
			for(final String scheme : schemes) {
				map.put(format("{0}://www.eclipse.org/legal/epl-v{1}.html", scheme, licenseVersion.replaceAll("\\.", "")), entry);
				map.put(format("{0}://www.eclipse.org/legal/epl-{1}", scheme, licenseVersion), entry);
			}
		}
		
		final String[] formats = {"{0}://www.apache.org/licenses/LICENSE-{1}", "{0}://www.apache.org/licenses/LICENSE-{1}.txt" }; 
		for(final String licenseVersion : APACHE_LICENSE_VERSIONS) {
			final SimpleImmutableEntry<String, String> entry = new SimpleImmutableEntry<>(APACHE_LICENSE, licenseVersion);
			for(final String scheme : schemes) {
				for(final String format : formats) {
					map.put(format(format, scheme, licenseVersion), entry);
				}
			}
		}
		BUNDLE_VERSION_MAP = unmodifiableMap(map);
	}
	
}
