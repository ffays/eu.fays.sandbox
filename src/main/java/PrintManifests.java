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
						final Attributes mainAttributes = manifest.getMainAttributes();						
						
						// License
						String bundleLicense = mainAttributes.getValue("Bundle-License");
						final String[] licenseInformation;
						if(bundleLicense != null) {
							licenseInformation = getLicenseInformationFromBundleVersion(bundleLicense);
						} else  if(licenseText != null) {
							licenseInformation = getLicenseInformationFromText(licenseText);
						} else {
							licenseInformation = new String[3];
						}
						
						final String license = licenseInformation[0];
						final String licenseVersion = licenseInformation[1];
						final String licenseUrl =  licenseInformation[2];
						
						if(bundleLicense == null && licenseUrl != null) {
							mainAttributes.putValue("Bundle-License", licenseUrl);
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
	 * </ol>
	 */
	private static String[] getLicenseInformationFromBundleVersion(final String bundleLicense) {
		//
		assert bundleLicense != null;
		//
		final String[] result = new String[3];
		final int licenseUrlBeginIndex = bundleLicense.indexOf("http");
		
		if(licenseUrlBeginIndex != -1) {
			String licenseUrl = bundleLicense.substring(licenseUrlBeginIndex);
			int licenseUrlEndIndex = licenseUrl.indexOf('"');
			if(licenseUrlEndIndex != -1) {
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
	 * @return an array composed of 3 values:
	 * <ol start="0">
	 * <li>License
	 * <li>Version
	 * <li>URL
	 * </ol>
	 */
	private static String[] getLicenseInformationFromText(final String licenseText) {
		//
		assert licenseText != null;
		//
		final String[] result = new String[3];
		
		final String licenseOneLiner = licenseText.replaceAll("<[^>]+>","").replaceAll("\\p{Space}+", " ");
		String license = null;
		for(final String knownLicense : KNOWN_LICENSES) {
			if(licenseOneLiner.indexOf(knownLicense) != -1) {
				license = knownLicense;
				result[0] = license;
				break;
			}
		}
		
		if(license == null) {
			result[0] = licenseOneLiner;
		}
		
		final int licenseVersionBeginIndex = licenseText.indexOf("Version ");
		if(licenseVersionBeginIndex != -1) {
			String licenseVersion = licenseText.substring(licenseVersionBeginIndex + "Version ".length());
			final char[] versionDelimiters = {',', '('};
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
		
		final String[] apacheLicenseFormats = {APACHE_LICENSE_FORMAT, "{0}://www.apache.org/licenses/LICENSE-{1}.txt" }; 
		for(final String licenseVersion : APACHE_LICENSE_VERSIONS) {
			final SimpleImmutableEntry<String, String> entry = new SimpleImmutableEntry<>(APACHE_LICENSE, licenseVersion);
			for(final String scheme : schemes) {
				for(final String format : apacheLicenseFormats) {
					map.put(format(format, scheme, licenseVersion), entry);
				}
			}
		}
		BUNDLE_VERSION_MAP = unmodifiableMap(map);
	}
	
}
