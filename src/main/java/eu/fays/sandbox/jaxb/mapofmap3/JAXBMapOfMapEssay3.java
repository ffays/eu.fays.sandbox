package eu.fays.sandbox.jaxb.mapofmap3;

import static java.lang.Math.E;
import static java.lang.Math.PI;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.IntStream;

import eu.fays.sandbox.iterators.Fruit;

public class JAXBMapOfMapEssay3 {

	public static void main(String[] args) throws Exception {
		final String now = LocalDateTime.now().toString();
		final LinkedHashMap<String, Object> dict1 = new LinkedHashMap<>();
		dict1.put("PI", PI);
		dict1.put("E", E);
		final Map<String, Object> dict2 = stream(Fruit.values())
				.map(e -> new SimpleImmutableEntry<>(e.name(), e.ordinal()))
				.collect(toMap(Entry::getKey, Entry::getValue, (k0, k1) -> null, LinkedHashMap::new));
		dict1.put(Fruit.class.getSimpleName(), dict2);

		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM");
		// @formatter:off
		final Map<String, Integer> dict3 = IntStream
			.rangeClosed(1, 12)
			.mapToObj(m -> LocalDate.of(2001, m, 1))
			.map(d -> new SimpleImmutableEntry<>(formatter.format(d), d.getMonthValue()))
			.collect(toMap(Entry::getKey, Entry::getValue, (k0, k1) -> null, LinkedHashMap::new));
		// @formatter:on
		dict2.put("Months", dict3);

		final Document document = new Document(JAXBMapOfMapEssay3.class.getSimpleName() + "_" + now, dict1);
		document.marshal(System.out);
	}
}
