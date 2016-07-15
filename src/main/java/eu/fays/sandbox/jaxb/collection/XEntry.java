package eu.fays.sandbox.jaxb.collection;

import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlAccessorType(XmlAccessType.NONE)
public class XEntry {
	@XmlAttribute
	private String key;
	@XmlAttribute
	private String value;
	
	public XEntry() {
		
	}
	
	public XEntry(final Entry<String, Integer> entry) {
		key = entry.getKey();
		value = entry.getValue().toString();
	}
}
