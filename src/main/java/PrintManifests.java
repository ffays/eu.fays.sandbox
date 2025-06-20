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
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.Properties;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
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
		final boolean exportColumns = System.getProperties().containsKey("columns");
		final Set<String> columns;

		if(exportColumns) {
			columns = Arrays.stream(System.getProperty("columns").split(",")).collect(Collectors.toCollection(LinkedHashSet::new));
			boolean flag = false;
			for(final String column : columns) {
				if(flag) {
					System.out.print('\t');
				} else {
					flag = true;
				}
				System.out.print(column);
			}
			System.out.println();
		} else {
			columns = null;
		}
		
		final PathMatcher filter = root.getFileSystem().getPathMatcher("glob:**.{jar,war,zip}");
		final Predicate<? super Path> zipFilePredicate = p -> filter.matches(p) || "bundleFile".equals(p.getFileName().toString());
		final Predicate<? super Path> manifestFilePredicate = p -> "MANIFEST.MF".equals(p.getFileName().toString());
		
		try (final Stream<Path> stream = walk(root)) {
			final List<Path> paths = stream.filter(p -> zipFilePredicate.test(p) || manifestFilePredicate.test(p)).collect(toList());
			for(final Path path : paths) {
				Manifest manifest = null;
				String licenseText = null;
				Properties pluginProperties = null;
				
				if(zipFilePredicate.test(path)) {
					try (final InputStream is = newInputStream(path); final ZipInputStream zis = new ZipInputStream(is)) {
						ZipEntry zipEntry = zis.getNextEntry();
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
					}
				} else if (manifestFilePredicate.test(path)) {
					try (final InputStream is = newInputStream(path)) {
						manifest = new Manifest(is);
					}
				}

				if(manifest != null) {
					final Attributes mainAttributes = manifest.getMainAttributes();						
					
					// License
					String bundleLicense = mainAttributes.getValue("Bundle-License");
					final String[] licenseInformation;
					if(bundleLicense != null) {
						licenseInformation = getLicenseInformationFromBundleVersion(bundleLicense);
					} else  if(licenseText != null) {
						licenseInformation = getLicenseInformationFromText(licenseText);
					} else {
						licenseInformation = new String[4];
					}
					
					final String license = licenseInformation[0];
					final String licenseVersion = licenseInformation[1];
					final String licenseUrl =  licenseInformation[2];
					
					if(bundleLicense == null) {
						if(licenseUrl != null) {
							mainAttributes.putValue("Bundle-License", licenseUrl);
						} else if (licenseInformation[3] != null){
							mainAttributes.putValue("Bundle-License", licenseInformation[3]);
						}
					}
					
					if(license != null) {
						mainAttributes.putValue("License", license);
					}

					if(license != null) {
						mainAttributes.putValue("License-Version", licenseVersion);
					}

					if(licenseUrl != null) {
						mainAttributes.putValue("License-URL", licenseUrl);
					}
					
					if(exportColumns) {
						boolean flag = false;
						final Map<String, String> map = new LinkedHashMap<>();
						for (final Entry<Object, Object> entry : mainAttributes.entrySet()) {
							final String key = entry.getKey().toString();
							String value = (String) entry.getValue();
							if(value != null) {
								if("Bundle-SymbolicName".equals(key) && value.indexOf(';') != -1) {
									value = value.substring(0, value.indexOf(';'));
								}
								map.put(key, value);
							}
						}
						
						map.put("File", path.toString());
						map.put("File-Name", path.getFileName().toString());
						if((!map.containsKey("Bundle-SymbolicName") || map.get("Bundle-SymbolicName").isEmpty()) && map.containsKey("Automatic-Module-Name")) {
							map.put("Bundle-SymbolicName", map.get("Automatic-Module-Name"));
						}

						for(final String column : columns) {
							if(flag) {
								System.out.print('\t');
							} else {
								flag = true;
							}
							
							if(map.containsKey(column)) {
								System.out.print(map.get(column));
							}
						}
						System.out.println();
					} else {
						System.out.println("Dash=--------------------------------------------------------------------------------");
						System.out.println(format("File={0}",path.toString()));
						for (final Entry<Object, Object> entry : mainAttributes.entrySet()) {
							String value = (String) entry.getValue();
							if(value != null && value.startsWith("%") && pluginProperties != null && pluginProperties.containsKey(value.substring(1))) {
								value = (String) pluginProperties.get(value.substring(1));
							}
							System.out.println(format("{0}={1}", entry.getKey(), value));
						}
					}
				}
			}
		}
	}
	
	/**
	 * Returns the license information
	 * @param bundleLicense the value of "Bundle-License" Manifest entry
	 * @return an array composed of 3 values:
	 * <ol start="0">
	 * <li>License
	 * <li>Version
	 * <li>URL
	 * <li>Text
	 * </ol>
	 */
	private static String[] getLicenseInformationFromBundleVersion(final String bundleLicense) {
		//
		assert bundleLicense != null;
		//
		final String[] result = new String[4];
		result[3] = bundleLicense;
		final int licenseUrlBeginIndex = bundleLicense.indexOf("http");
		
		if(licenseUrlBeginIndex != -1) {
			String licenseUrl = bundleLicense.substring(licenseUrlBeginIndex);
			int licenseUrlEndIndex = Integer.MAX_VALUE;
			for(char delimiter : new char[] {',', '"'}) {
				final int index = licenseUrl.indexOf(delimiter);
				if(index != -1 && index < licenseUrlEndIndex) {
					licenseUrlEndIndex = index;
				}
			}
			if(licenseUrlEndIndex != Integer.MAX_VALUE) {
				licenseUrl = licenseUrl.substring(0, licenseUrlEndIndex);
			}
			
			result[2] = licenseUrl;
			if(BUNDLE_VERSION_MAP.containsKey(licenseUrl)) {
				result[0] = BUNDLE_VERSION_MAP.get(licenseUrl).getKey();
				result[1] = BUNDLE_VERSION_MAP.get(licenseUrl).getValue();
			}
		}
		
		
		return result;
	}
	
	/**
	 * Returns the license information
	 * @param licenseText the license text
	 * @return an array composed of 4 values:
	 * <ol start="0">
	 * <li>License
	 * <li>Version
	 * <li>URL
	 * <li>Text
	 * </ol>
	 */
	private static String[] getLicenseInformationFromText(final String licenseText) {
		//
		assert licenseText != null;
		//
		final String[] result = new String[4];
		
		final String licenseOneLiner = licenseText.replaceAll("<[^>]+>","").replaceAll("\\p{Space}+", " ");
		result[3] = licenseOneLiner;
		String license = null;
		for(final String knownLicense : KNOWN_LICENSES) {
			if(licenseOneLiner.indexOf(knownLicense) != -1) {
				license = knownLicense;
				result[0] = license;
				break;
			}
		}
		
		final int licenseVersionBeginIndex = licenseText.indexOf("Version ");
		if(licenseVersionBeginIndex != -1) {
			String licenseVersion = licenseText.substring(licenseVersionBeginIndex + "Version ".length());
			int licenseVersionEndIndex = Integer.MAX_VALUE;
			for(char delimiter : new char[] {',', '('}) {
				final int index = licenseVersion.indexOf(delimiter);
				if(index != -1 && index < licenseVersionEndIndex) {
					licenseVersionEndIndex = index;
				}
			}
			if(licenseVersionEndIndex != Integer.MAX_VALUE) {
				licenseVersion = licenseVersion.substring(0, licenseVersionEndIndex).trim();
			}
			result[1] = licenseVersion;
			
			if(licenseVersionEndIndex != Integer.MAX_VALUE) {
				if(APACHE_LICENSE.equals(license)) {
					final String licenseURL = format(APACHE_LICENSE_FORMAT, "https", licenseVersion);
					result[2] = licenseURL;
				} else if(ECLIPSE_PUBLIC_LICENSE.equals(license)) {
					final String licenseURL = format(ECLIPSE_LICENSE_FORMAT, "https", licenseVersion.replaceAll("\\.", ""));
					result[2] = licenseURL;
					assert BUNDLE_VERSION_MAP.entrySet().stream().filter(e -> ECLIPSE_PUBLIC_LICENSE.equals(e.getValue().getKey())).map(Entry::getKey).anyMatch(url -> licenseOneLiner.contains(url));
				}
			}
		}
		return result;
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

	private static final String APACHE_LICENSE_FORMAT = "{0}://www.apache.org/licenses/LICENSE-{1}";
	private static final String ECLIPSE_LICENSE_FORMAT = "{0}://www.eclipse.org/legal/epl-v{1}.html";

	private static final Map<String, Entry<String, String>> BUNDLE_VERSION_MAP;
	
	static {
		final Map<String, Entry<String, String>> map = new HashMap<>();
		
		final String[] schemes = {"http", "https"};
		
		for(final String licenseVersion : ECLIPSE_LICENSE_VERSIONS) {
			final SimpleImmutableEntry<String, String> entry = new SimpleImmutableEntry<>(ECLIPSE_PUBLIC_LICENSE, licenseVersion);
			for(final String scheme : schemes) {
				map.put(format(ECLIPSE_LICENSE_FORMAT, scheme, licenseVersion.replaceAll("\\.", "")), entry);
				map.put(format("{0}://www.eclipse.org/legal/epl-{1}", scheme, licenseVersion), entry);
			}
		}
		
		final String[] apacheLicenseFormats = {APACHE_LICENSE_FORMAT, "{0}://www.apache.org/licenses/LICENSE-{1}.html", "{0}://www.apache.org/licenses/LICENSE-{1}.txt" }; 
		for(final String licenseVersion : APACHE_LICENSE_VERSIONS) {
			final SimpleImmutableEntry<String, String> entry = new SimpleImmutableEntry<>(APACHE_LICENSE, licenseVersion);
			for(final String scheme : schemes) {
				for(final String format : apacheLicenseFormats) {
					map.put(format(format, scheme, licenseVersion), entry);
				}
			}
		}
		
		map.put("http://www.eclipse.org/org/documents/edl-v10.php", new SimpleImmutableEntry<>("Eclipse Distribution License", "1.0"));
		map.put("https://www.eclipse.org/org/documents/epl-2.0/EPL-2.0.txt", new SimpleImmutableEntry<>(ECLIPSE_PUBLIC_LICENSE, "1.0"));
		map.put("https://opensource.org/licenses/BSD-3-Clause", new SimpleImmutableEntry<>("BSD-3-Clause", ""));
		map.put("https://asm.ow2.io/LICENSE.txt", new SimpleImmutableEntry<>("BSD-3-Clause", ""));
		map.put("https://repository.jboss.org/licenses/apache-2.0.txt", new SimpleImmutableEntry<>(APACHE_LICENSE, "2.0"));
		map.put("http://www.mozilla.org/MPL/MPL-1.1.html", new SimpleImmutableEntry<>("Mozilla Public License", "1.1"));
		map.put("http://opensource.org/licenses/MIT", new SimpleImmutableEntry<>("MIT", ""));
		map.put("http://www.opensource.org/licenses/mit-license.php", new SimpleImmutableEntry<>("MIT", ""));
		map.put("https://h2database.com/html/license.html", new SimpleImmutableEntry<>(ECLIPSE_PUBLIC_LICENSE, "2.0"));
		BUNDLE_VERSION_MAP = unmodifiableMap(map);
	}
	
}
