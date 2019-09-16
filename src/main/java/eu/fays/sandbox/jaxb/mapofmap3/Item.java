package eu.fays.sandbox.jaxb.mapofmap3;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.NONE)
public class Item implements Entry<String, String> {
	@XmlAttribute
	public String key;
	@XmlAttribute
	public String type;
	@XmlIDREF
	@XmlAttribute
	private Dictionary dictionary;
	@XmlValue
	public String value;

	public Item() {
	}

	public Item(final Entry<String, Object> entry) {
		key = entry.getKey();

		final Object v = entry.getValue();
		if (v != null) {
			if (v instanceof Map) {
				type = Dictionary.class.getSimpleName();
				@SuppressWarnings("unchecked")
				final Entry<String, Map<String, Object>> entry2 = new SimpleImmutableEntry<>(entry.getKey(), (Map<String, Object>) entry.getValue());
				dictionary = new Dictionary(entry2);
			} else {
				type = v.getClass().getSimpleName();
				value = v.toString();
			}
		}
	}

	@Override
	public String getKey() {
		return key;
	}

	public String getType() {
		return type;
	}

	public boolean hasValue() {
		return value != null;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public String setValue(String value) {
		throw new UnsupportedOperationException();
	}

	public boolean hasDictionary() {
		return dictionary != null;
	}

	public Dictionary getDictionary() {
		return dictionary;
	}

	public void setDictionary(Dictionary dictionary) {
		this.dictionary = dictionary;
	}
}
