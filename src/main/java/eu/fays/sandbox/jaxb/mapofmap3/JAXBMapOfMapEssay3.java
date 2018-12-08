package eu.fays.sandbox.jaxb.mapofmap3;

import static java.lang.Math.E;
import static java.lang.Math.PI;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

import java.time.LocalDateTime;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import eu.fays.sandbox.iterators.Fruit;

public class JAXBMapOfMapEssay3 {

	public static void main(String[] args) throws Exception {
		final String now = LocalDateTime.now().toString();
		final LinkedHashMap<String, Object> dict1 = new LinkedHashMap<>();
		dict1.put("PI", PI);
		dict1.put("E", E);
		final Map<String, Object> dict2 = stream(Fruit.values())
				.map(e -> new SimpleImmutableEntry<>(e.name(), e.ordinal()))
				.collect(toMap(Entry::getKey, Entry::getValue));
		dict1.put(Fruit.class.getSimpleName(), dict2);
		final Document document = new Document(now, dict1);
		document.marshal(System.out);
	}
}
