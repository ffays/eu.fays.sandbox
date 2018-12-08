package eu.fays.sandbox.jaxb.mapofmap3;

import static java.lang.Boolean.TRUE;
import static java.text.MessageFormat.format;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;

import java.io.OutputStream;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class Document {

	@XmlElement
	private String name;

	@XmlElementWrapper(name = "dictionaries")
	@XmlElement(name = "dictionary")
	private List<Dictionary> dictionaries;

	@XmlTransient
	private Map<String, Object> dictionary;

	/**
	 * Constructor
	 */
	public Document() {

	}

	/**
	 * Constructor
	 * 
	 * @param name
	 *            name of the document
	 * @param dictionary
	 *            the dictionaries
	 */
	public Document(final String name, final Map<String, Object> dictionary) {
		this.name = name;
		this.dictionary = dictionary;
	}

	/**
	 * Returns the name of the document
	 * 
	 * @return the name of the document
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the document
	 * 
	 * @param name
	 *            the name of the document
	 */
	public void setName(final String name) {
		this.name = name;
	}

	public Map<String, Object> getDictionary() {
		return dictionary;
	}

	public void setDictionary(Map<String, Object> dictionary) {
		this.dictionary = dictionary;
	}

	/**
	 * Marshal to XML this instance into the given output file
	 * 
	 * @param out
	 *            the output stream
	 * @throws JAXBException
	 *             in case of unexpected error
	 */
	public void marshal(final OutputStream out) throws JAXBException {
		//
		assert out != null;
		//

		// @formatter:off
		final Map<Boolean, List<Entry<String, Object>>> collect = getDictionary().entrySet().stream().collect(groupingBy(e -> e.getValue() instanceof Map));		
		// @formatter:on

		if (!collect.isEmpty()) {
			final List<Dictionary> dictionaries = new ArrayList<Dictionary>();
			final Map<String, Object> map0;
			if (collect.containsKey(false)) {
				// @formatter:off
				map0 = collect.get(false).stream().collect(toMap(Entry::getKey, Entry::getValue, (k0, k1) -> { throw new AssertionError(format("Duplicate key ''{0}''!", k0)); }, LinkedHashMap<String, Object>::new));
				// @formatter:on
			} else {
				map0 = new LinkedHashMap<>();
			}
			final Entry<String, Map<String, Object>> entry = new SimpleImmutableEntry<>(null, map0);
			final Dictionary dictionary = new Dictionary(entry);
			dictionaries.add(dictionary);
			if (collect.containsKey(true)) {
				// @formatter:off
				@SuppressWarnings("unchecked")
				final List<Dictionary> list = collect.get(true).stream()
					.map(e -> new SimpleImmutableEntry<String, Map<String, Object>>(e.getKey(), (Map<String, Object>)e.getValue()))
					.map(Dictionary::new)
					.flatMap(Dictionary::dictionaryStream)
					.collect(toList());
				// @formatter::on
				dictionaries.addAll(list);
			}
			

			this.dictionaries = dictionaries;
		}
		
		
		final JAXBContext context = JAXBContext.newInstance(getClass());
		final Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(JAXB_FORMATTED_OUTPUT, TRUE);
		marshaller.marshal(this, out);
	}

}
