package eu.fays.sandbox.jaxb.mapofmap2;

import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class DictionariesAdapter extends XmlAdapter<Dictionary[], Map<String, Map<String, Object>>> {

	@Override
	public Dictionary[] marshal(Map<String, Map<String, Object>> dictionaries) throws Exception {
		// @formatter:off
		Dictionary[] result = dictionaries.entrySet().stream()
			.map(Dictionary::new)
			.flatMap(Dictionary::dictionaryStream)
			.toArray(Dictionary[]::new);
		// @formatter::on

		return result;
	}

	@Override
	public Map<String, Map<String, Object>> unmarshal(Dictionary[] v) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
