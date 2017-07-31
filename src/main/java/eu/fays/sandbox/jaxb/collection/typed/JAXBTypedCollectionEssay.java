package eu.fays.sandbox.jaxb.collection.typed;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.xml.bind.JAXBException;

import eu.fays.sandbox.iterators.Fruit;

@SuppressWarnings("nls")
public class JAXBTypedCollectionEssay {
	public static void main(String[] args) throws JAXBException {
		final Map<String, Integer> intergerDictionary = stream(Fruit.values()).collect(toMap(Fruit::name, Fruit::ordinal, (a, b) -> a, LinkedHashMap::new));
		final Map<String, String> stringDictionary1 = stream(Fruit.values()).collect(toMap(e -> Integer.toString(e.ordinal()), Fruit::name, (a, b) -> a, LinkedHashMap::new));
		final Map<String, String> stringDictionary2 = stream(Color.values()).collect(toMap(e -> Integer.toString(e.ordinal()), Color::name, (a, b) -> a, LinkedHashMap::new));

		final File file = new File("output/typed-collection.xml");
		final XRoot root = new XRoot();
		root.integerDictionary = intergerDictionary;
		root.stringDictionary = stringDictionary1;

		root.stringDictionaries = Stream.of(stringDictionary1, stringDictionary2).collect(toList());
		root.marshal(file);
	}
}
