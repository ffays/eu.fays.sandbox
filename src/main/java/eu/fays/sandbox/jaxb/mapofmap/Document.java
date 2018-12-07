package eu.fays.sandbox.jaxb.mapofmap;

import static java.lang.Boolean.TRUE;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import static javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import static java.text.MessageFormat.format;
import static java.util.AbstractMap.SimpleImmutableEntry;

@XmlType
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class Document {

	@XmlElement
	private String name;
	@XmlElement
	@XmlJavaTypeAdapter(DictionaryAdapter.class)
	private Map<String, Object> dictionary;
	@XmlJavaTypeAdapter(DictionaryDictionaryAdapter.class)
	private Map<String, Map<String, Object>> dictionary2;

	/**
	 * Constructor
	 */
	public Document() {

	}

	/**
	 * Constructor
	 * @param name name of the document
	 * @param dict dictionary
	 */
	public Document(final String name, final Map<String, Object> dict) {
		this.name = name;

		// @formatter:off
		final Map<Boolean, Map<String, Object>> partition = dict.entrySet().stream().collect(groupingBy(e -> e.getValue() instanceof Map, toMap(Entry::getKey,Entry::getValue)));
		
		if(partition.containsKey(false)) {
			this.dictionary = partition.get(false);
		}
		if(partition.containsKey(true)) {
			@SuppressWarnings("unchecked")
			final LinkedHashMap<String, Map<String, Object>> dictDict = partition.get(true)
				.entrySet()
				.stream()
				.map(e -> new SimpleImmutableEntry<String, Map<String, Object>>(e.getKey(), (Map<String, Object>) e.getValue()))
				.collect(toMap(Entry::getKey, Entry::getValue, (k0, k1) -> { throw new AssertionError(format("Duplicate key ''{0}''!", k0)); }, LinkedHashMap<String, Map<String,Object>>::new));
			this.dictionary2 = dictDict;
		}
		// @formatter:on
	}

	/**
	 * Returns the name of the document
	 * @return the name of the document
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the document
	 * @param name the name of the document
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Returns the dictionary
	 * @return the dictionary
	 */
	public Map<String, Object> getDict() {
		return dictionary;
	}

	/**
	 * Sets the dictionary
	 * @param dict the dictionary
	 */
	public void setDict(final Map<String, Object> dict) {
		this.dictionary = dict;
	}

	/**
	 * Marshal to XML this instance into the given output file
	 * @param file the output file
	 * @throws JAXBException in case of unexpected error
	 */
	public void marshal(final File file) throws JAXBException {
		//
		assert file != null;
		//

		final JAXBContext context = JAXBContext.newInstance(getClass());
		final Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(JAXB_FORMATTED_OUTPUT, TRUE);
		marshaller.marshal(this, file);
	}

}
