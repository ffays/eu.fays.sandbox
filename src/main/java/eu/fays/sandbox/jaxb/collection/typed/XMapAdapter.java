package eu.fays.sandbox.jaxb.collection.typed;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class XMapAdapter extends XmlAdapter<XEntry[], Map<String, Object>> {
	public XEntry[] marshal(final Map<String, Object> map) throws Exception {
		XEntry[] result = new XEntry[map.size()];
		int i = 0;
		for (final Entry<String, Object> entry : map.entrySet()) {
			result[i++] = new XEntry(entry.getKey(), entry.getValue());
		}

		return result;
	}

	public Map<String, Object> unmarshal(XEntry[] entries) throws Exception {
		Map<String, Object> result = new LinkedHashMap<>();

		for (final XEntry entry : entries) {
			if (Integer.class.getSimpleName().equals(entry.getType())) {
				result.put(entry.getKey(), Integer.parseInt(entry.getValue()));
			} else if (Double.class.getSimpleName().equals(entry.getType())) {
				result.put(entry.getKey(), Double.parseDouble(entry.getValue()));
			} else if (LocalDateTime.class.getSimpleName().equals(entry.getType())) {
				result.put(entry.getKey(), LocalDateTime.parse(entry.getValue()));
			} else {
				result.put(entry.getKey(), entry.getValue());
			}

		}
		return result;
	}
}
