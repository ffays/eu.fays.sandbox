package eu.fays.sandbox.jaxb.ms.minimified;

import static java.lang.Boolean.TRUE;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import static javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;

import java.io.File;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.util.ValidationEventCollector;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

@XmlRootElement(name = "root")
@XmlType
public class Root {
	@XmlElement(name = "A")
	public A[] _a;
	
	@XmlElementWrapper(name = "B")
	@XmlElement(name = "part")
	public List<Part> _parts;

	public void marshal(final File outputFile) throws JAXBException {
		final JAXBContext context = JAXBContext.newInstance(getClass());
		final Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(JAXB_FORMATTED_OUTPUT, TRUE);
		marshaller.marshal(this, outputFile);
	}

	public static Root unmarshal(final File file) throws JAXBException, SAXException {
		final JAXBContext context = JAXBContext.newInstance(Root.class);
		final SchemaFactory factory = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
		final Unmarshaller unmarshaller = context.createUnmarshaller();
		final Schema schema = factory.newSchema(new File("key.xsd"));
		unmarshaller.setSchema(schema);
		final ValidationEventCollector handler = new ValidationEventCollector();
		unmarshaller.setEventHandler(handler);
		final Root result = (Root) unmarshaller.unmarshal(file);
		return result;
	}
}
