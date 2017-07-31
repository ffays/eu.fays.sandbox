package eu.fays.sandbox.jaxb.collection.typed;

import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.NONE)
public final class XIntegerEntry implements Entry<String, Integer> {
	@XmlAttribute
	public final String key;
	@XmlValue
	public final Integer value;

	public XIntegerEntry(final String key, final Integer value) {
		//
		assert key != null;
		assert !key.isEmpty();
		//
		this.key = key;
		this.value = value;
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public Integer getValue() {
		return value;
	}

	@Override
	public Integer setValue(final Integer value) {
		throw new UnsupportedOperationException();
	}
}
