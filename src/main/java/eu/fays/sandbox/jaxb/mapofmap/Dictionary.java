package eu.fays.sandbox.jaxb.mapofmap;

import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType(XmlAccessType.NONE)
public class Dictionary implements Entry<String, Map<String, Object>> {
	@XmlAttribute
	public String key;
	@XmlAttribute
	public String type = Dictionary.class.getSimpleName();
	@XmlJavaTypeAdapter(DictionaryAdapter.class)
	public Map<String, Object> value;

	public Dictionary() {
	}

	public Dictionary(Entry<String, Map<String, Object>> item) {
		this.key = item.getKey();
		this.type = Dictionary.class.getSimpleName();
		this.value = item.getValue();
	}

	public String getKey() {
		return key;
	}

	public String getType() {
		return type;
	}

	public Map<String, Object> getValue() {
		return value;
	}

	public Map<String, Object> setValue(Map<String, Object> value) {
		throw new UnsupportedOperationException();
	}
}
