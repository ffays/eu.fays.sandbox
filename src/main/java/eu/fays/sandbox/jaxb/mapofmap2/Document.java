package eu.fays.sandbox.jaxb.mapofmap2;

import static java.lang.Boolean.TRUE;
import static javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;

import java.io.OutputStream;
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
	
	@XmlJavaTypeAdapter(DictionariesAdapter.class)
	private Map<String, Map<String, Object>> dictionaries;



	/**
	 * Constructor
	 */
	public Document() {

	}

	/**
	 * Constructor
	 * @param name name of the document
	 * @param dictionaries the dictionaries
	 */
	public Document(final String name, final Map<String, Map<String, Object>> dictionaries) {
		this.name = name;
		this.dictionaries =dictionaries;
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
	 * Marshal to XML this instance into the given output file
	 * @param out the output stream
	 * @throws JAXBException in case of unexpected error
	 */
	public void marshal(final OutputStream out) throws JAXBException {
		//
		assert out != null;
		//

		final JAXBContext context = JAXBContext.newInstance(getClass());
		final Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(JAXB_FORMATTED_OUTPUT, TRUE);
		marshaller.marshal(this, out);
	}

}
