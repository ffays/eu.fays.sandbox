import static java.nio.file.Files.newInputStream;
import static java.nio.file.Files.walk;
import static java.text.MessageFormat.format;
import static java.util.stream.Collectors.toList;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class PrintManifestAttribute {

	@SuppressWarnings("nls")
	public static void main(String[] args) throws Exception {
		final String attribute = System.getProperty("attribute");
		final Set<String> attributes;
		if(attribute != null) {
			attributes = new HashSet<>(Arrays.asList(attribute.split(",")));
		} else {
			attributes = Collections.emptySet();
		}
		final String prefix = System.getProperty("prefix");
		final String dir = System.getProperty("dir");
		final String append = System.getProperty("append");
		final Path root = Path.of(dir);

		try (final Stream<Path> stream = walk(root)) {
			// "+(prefix!=null?prefix:"")+"
			final PathMatcher filter = root.getFileSystem().getPathMatcher("glob:**.{jar,war,zip}");
			final List<Path> paths = stream.filter(p -> filter.matches(p) || "bundleFile".equals(p.getFileName().toString())).collect(toList());
			for (final Path path : paths) {
				if(prefix != null && !path.getFileName().toString().startsWith(prefix)) {
					continue;
				}
				try (final InputStream fis = newInputStream(path); final ZipInputStream zis = new ZipInputStream(fis)) {
					ZipEntry zipEntry = zis.getNextEntry();
					Manifest manifest = null;
					while (zipEntry != null) {
						if ("META-INF/MANIFEST.MF".equals(zipEntry.getName())) {
							manifest = new Manifest(zis);
						}
						zipEntry = zis.getNextEntry();
					}
					if (manifest != null) {
						
						System.out.println("Dash=--------------------------------------------------------------------------------");
						System.out.println(format("File={0}", path.toString()));
						final Attributes mainAttributes = manifest.getMainAttributes();
						for (final Entry<Object, Object> attr : mainAttributes.entrySet()) {
							final String key = attr.getKey().toString();
							if(attribute != null && !attributes.contains(key)) {
								continue;
							}
							final String value = (String) attr.getValue();
							final List<String> values = values(value);
							if(values.isEmpty()) {
								// Do nothing
							} else if(values.size() == 1) {
								System.out.println(format("{0}={1}", key, value));
							} else {
								int i = 1;
								for(final String v : values) {
									if(i == 1) {
										System.out.print(format("{0}=", key));
									} else {
										System.out.print(" ");
									}
									System.out.print(v);
									if(append != null && v.startsWith(prefix)) {
										System.out.print(append);
									}
									if(i < values.size()) {
										System.out.println(",");
									} else {
										System.out.println();
									}
									i++;
								}
							}
						}
					}
				}
			}
		}
	}
	
	@SuppressWarnings("nls")
	private static List<String> values(final String value) {
		List<String> result = new ArrayList<>();
		final Pattern pattern = Pattern.compile(",\\p{Alpha}\\p{Alnum}");
		
		String s = value;
		Matcher matcher = pattern.matcher(s);
		while(matcher.find()) {
			int end = matcher.end() - 3;
			String v = s.substring(0, end);
			int dq = v.indexOf('"');
			if(dq != -1) {
				end = s.indexOf('"', dq + 1);
				int c = s.indexOf(',', end + 1);
				if(c != -1) {
					end = c;
					v = s.substring(0, c);
				} else {
					v = s.substring(0, end + 1);	
				}
				
			}
			result.add(v);
			s = s.substring(end + 1);
			if(s.startsWith(",")) {
				s = s.substring(1);
			}
			matcher = pattern.matcher(s);
		}
		
		if(result.isEmpty()) {
			result.add(value);
		}
		return result;
	}

}
