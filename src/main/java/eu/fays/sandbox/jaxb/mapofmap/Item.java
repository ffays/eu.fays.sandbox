package eu.fays.sandbox.jaxb.mapofmap;

import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.NONE)
public class Item implements Entry<String, String> {
	@XmlAttribute
	public String key;
	@XmlAttribute
	public String type;
	@XmlValue
	public String value;

	public Item() {
	}

	public Item(Entry<String, Object> entry) {
		key = entry.getKey();
		value = entry.getValue().toString();
		type = entry.getValue().getClass().getSimpleName();
	}

	@Override
	public String getKey() {
		return key;
	}

	public String getType() {
		return type;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public String setValue(String value) {
		throw new UnsupportedOperationException();
	}
}
