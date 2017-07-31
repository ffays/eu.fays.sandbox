package eu.fays.sandbox.jaxb.collection.typed;

import static java.text.MessageFormat.format;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class XStringMapAdapter extends XmlAdapter<XStringEntry[], Map<String, String>> {
	public XStringEntry[] marshal(final Map<String, String> map) throws Exception {
		return map.entrySet().stream().map(e -> new XStringEntry(e.getKey(), e.getValue())).toArray(XStringEntry[]::new);
	}

	public Map<String, String> unmarshal(XStringEntry[] entries) throws Exception {
		// @formatter:off
    	return stream(entries).collect(
    		toMap(
    			XStringEntry::getKey, 
    			XStringEntry::getValue, 
    			(k0, k1) -> { throw new AssertionError(format("Duplicate key ''{0}''!", k0)); },
    			LinkedHashMap<String, String>::new
    		)
    	);
		// @formatter:on
	}
}
