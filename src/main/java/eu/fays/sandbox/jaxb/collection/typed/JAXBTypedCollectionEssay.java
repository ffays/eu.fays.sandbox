package eu.fays.sandbox.jaxb.collection.typed;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.io.File;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import eu.fays.sandbox.iterators.Fruit;

@SuppressWarnings("nls")
public class JAXBTypedCollectionEssay {
	public static void main(String[] args) throws JAXBException, SAXException {
		final Map<String, Object> dictionary = new LinkedHashMap<>();
		dictionary.put("Fruit", Fruit.APPLE.name());
		dictionary.put("Constant_PI", Math.PI);
		dictionary.put("Constant_E", Math.E);
		dictionary.put("Timestamp", LocalDateTime.now());

		final Map<String, Integer> intergerDictionary = stream(Fruit.values()).collect(toMap(Fruit::name, Fruit::ordinal, (a, b) -> a, LinkedHashMap::new));
		final Map<String, String> stringDictionary1 = stream(Fruit.values()).collect(toMap(e -> Integer.toString(e.ordinal()), Fruit::name, (a, b) -> a, LinkedHashMap::new));
		final Map<String, String> stringDictionary2 = stream(Color.values()).collect(toMap(e -> Integer.toString(e.ordinal()), Color::name, (a, b) -> a, LinkedHashMap::new));

		final File file = new File("output/typed-collection-0.xml");
		{
			final XRoot root = new XRoot();
			root.dictionary = dictionary;
			root.integerDictionary = intergerDictionary;
			root.stringDictionary = stringDictionary1;
			root.stringDictionaries = Stream.of(stringDictionary1, stringDictionary2).collect(toList());
			root.marshal(file);
		}

		{
			final XRoot root = XRoot.unmarshal(file);
			root.marshal(new File("output/typed-collection-1.xml"));
		}
	}
}
