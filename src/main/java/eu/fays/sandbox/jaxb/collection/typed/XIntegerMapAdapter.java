package eu.fays.sandbox.jaxb.collection.typed;

import static java.text.MessageFormat.format;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class XIntegerMapAdapter extends XmlAdapter<XIntegerEntry[], Map<String, Integer>> {
	public XIntegerEntry[] marshal(final Map<String, Integer> map) throws Exception {
		return map.entrySet().stream().map(e -> new XIntegerEntry(e.getKey(), e.getValue())).toArray(XIntegerEntry[]::new);
	}

	public Map<String, Integer> unmarshal(XIntegerEntry[] entries) throws Exception {
    	return stream(entries).collect(toMap(XIntegerEntry::getKey, XIntegerEntry::getValue, (k0, k1) -> { throw new AssertionError(format("Duplicate key ''{0}''!", k0)); }, LinkedHashMap<String, Integer>::new));
    }
}
