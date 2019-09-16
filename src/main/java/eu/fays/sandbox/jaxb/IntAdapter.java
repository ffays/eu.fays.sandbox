package eu.fays.sandbox.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class IntAdapter extends XmlAdapter<Integer, Integer> {

	@Override
	public Integer marshal(Integer v) throws Exception {
		if(v == null || v == 0) {
			return null;
		}
		return v;
	}
	
	@Override
	public Integer unmarshal(Integer v) throws Exception {
			if(v==null) {
				return 0;
			}
			return v;
	}


}
