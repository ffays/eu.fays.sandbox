package eu.fays.sandbox.jaxb.collection;

import static java.lang.Boolean.TRUE;
import static java.util.stream.Collectors.toList;
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

@XmlType
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "Map")
public class XMap {

	@XmlElement(name = "Entry")
	public XEntry[] _entries;

	public XMap() {
	}

	public XMap(final Map<String, Integer> map) {
		_entries = map.entrySet().stream().map(e -> new XEntry(e)).collect(toList()).toArray(new XEntry[0]);
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
