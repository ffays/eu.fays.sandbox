package eu.fays.sandbox.jaxb.mapofmap;

import static java.text.MessageFormat.format;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class DictionaryDictionaryAdapter extends XmlAdapter<Dictionary[], Map<String, Map<String, Object>>> {

	// Dictionary

	@Override
	public Dictionary[] marshal(Map<String, Map<String, Object>> dict) throws Exception {
		if (dict == null) {
			return null;
		}
		return dict.entrySet().stream().map(Dictionary::new).toArray(Dictionary[]::new);
	}

	@Override
	public Map<String, Map<String, Object>> unmarshal(Dictionary[] items) throws Exception {
		if (items == null) {
			return null;
		}

		// @formatter:off
		return stream(items).map(DictionaryDictionaryAdapter::entry).collect(toMap(Entry::getKey, Entry::getValue, (k0, k1) -> { throw new AssertionError(format("Duplicate key ''{0}''!", k0)); }, LinkedHashMap<String, Map<String, Object>>::new));
		// @formatter:on
	}

	private static Entry<String, Map<String, Object>> entry(final Dictionary item) {
		return new SimpleImmutableEntry<>(item.getKey(), item.getValue());
	}
}
