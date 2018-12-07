package eu.fays.sandbox.jaxb.mapofmap2;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import static java.lang.Math.PI;
import static java.lang.Math.E;
import eu.fays.sandbox.iterators.Fruit;

public class JAXBMapOfMapEssay2 {

	public static void main(String[] args) throws Exception {
		final File file = File.createTempFile(JAXBMapOfMapEssay2.class.getSimpleName() + "-", ".xml");
		final String now = LocalDateTime.now().toString();
		System.out.println(now);

		final Map<String, Map<String, Object>> dict0 = new LinkedHashMap<>();

		final LinkedHashMap<String, Object> dict1 = new LinkedHashMap<>();
		dict1.put("PI", PI);
		dict1.put("E", E);
		final Map<String, Object> dict2 = stream(Fruit.values()).map(e -> new SimpleImmutableEntry<>(e.name(), e.ordinal())).collect(toMap(Entry::getKey, Entry::getValue));

		dict0.put("", dict1);
		dict1.put(Fruit.class.getSimpleName(), dict2);

		final Document document = new Document(now, dict0);
		document.marshal(file);
		System.out.println(file);
		try (final FileInputStream in = new FileInputStream(file)) {
			int c = in.read();
			while (c != -1) {
				System.out.write(c);
				c = in.read();
			}
		}
	}
}
