package eu.fays.sandbox.jaxb.mapofmap;

import static java.text.MessageFormat.format;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

import java.time.LocalDateTime;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class DictionaryAdapter extends XmlAdapter<Item[], Map<String, Object>> {

	@Override
	public Item[] marshal(Map<String, Object> dict) throws Exception {
		if(dict==null) {
			return null;
		}
		return dict.entrySet().stream().map(Item::new).toArray(Item[]::new);
	}

	@Override
	public Map<String, Object> unmarshal(Item[] items) throws Exception {
		if (items == null) {
			return null;
		}
		
		// @formatter:off
		return stream(items).map(DictionaryAdapter::entry).collect(toMap(Entry::getKey, Entry::getValue, (k0, k1) -> { throw new AssertionError(format("Duplicate key ''{0}''!", k0)); }, LinkedHashMap<String, Object>::new));
		// @formatter:on
	}

	private static Entry<String, Object> entry(final Item item) {
		return new SimpleImmutableEntry<>(item.getKey(), cast(item.getType(), item.getValue()));
	}

	private static Object cast(final String type, final Object value) {
		Object result;

		if (Integer.class.getSimpleName().equals(type)) {
			result = Integer.parseInt((String) value);
		} else if (Double.class.getSimpleName().equals(type)) {
			result = Double.parseDouble((String) value);
		} else if (LocalDateTime.class.getSimpleName().equals(type)) {
			result = LocalDateTime.parse((String) value);
		} else if (Boolean.class.getSimpleName().equals(type)) {
			result = Boolean.valueOf((String) value);
		} else {
			result = value;
		}
		return result;
	}


}
