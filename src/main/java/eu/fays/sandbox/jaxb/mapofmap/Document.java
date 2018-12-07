package eu.fays.sandbox.jaxb.mapofmap;

import static java.lang.Boolean.TRUE;
import static javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;

import java.io.File;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlType
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class Document {

	@XmlElement
	private String name;
	@XmlElement
	@XmlJavaTypeAdapter(DictionaryAdapter.class)	
	private Map<String, Object> dict;

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
		this.dict = dict;
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
		return dict;
	}

	/**
	 * Sets the dictionary
	 * @param dict the dictionary
	 */
	public void setDict(final Map<String, Object> dict) {
		this.dict = dict;
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
