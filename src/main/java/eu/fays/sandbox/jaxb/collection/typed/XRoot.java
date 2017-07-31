package eu.fays.sandbox.jaxb.collection.typed;

import static java.lang.Boolean.TRUE;
import static javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.xml.sax.SAXException;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "Root")
@XmlType
public class XRoot {

	@XmlElement
	@XmlJavaTypeAdapter(XMapAdapter.class)
	public Map<String, Object> dictionary;

	@XmlElement
	@XmlJavaTypeAdapter(XIntegerMapAdapter.class)
	public Map<String, Integer> integerDictionary;

	@XmlElement
	@XmlJavaTypeAdapter(XStringMapAdapter.class)
	public Map<String, String> stringDictionary;

	@XmlElementWrapper(name = "stringDictionaries")
	@XmlElement(name = "dictionary")
	@XmlJavaTypeAdapter(XStringMapAdapter.class)
	public List<Map<String, String>> stringDictionaries;

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

	/**
	 * Unmarshal from XML the given input file
	 * @param file the input file
	 * @return a new instance
	 * @throws JAXBException in case of unexpected error
	 * @throws SAXException in case of unexpected error
	 */
	public static XRoot unmarshal(final File file) throws JAXBException, SAXException {
		//
		assert file != null;
		assert file.isFile();
		assert file.canRead();
		//

		final JAXBContext context = JAXBContext.newInstance(XRoot.class);
		final Unmarshaller unmarshaller = context.createUnmarshaller();
		final XRoot result = (XRoot) unmarshaller.unmarshal(file);

		//
		assert result != null;
		//
		return result;
	}
}
