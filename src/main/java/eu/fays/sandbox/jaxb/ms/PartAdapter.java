package eu.fays.sandbox.jaxb.ms;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Article: <a href="http://stackoverflow.com/questions/10150263/jaxb-annotations-how-do-i-make-a-list-of-xmlidref-elements-have-the-id-value-a">JAXB Annotations - How do I make a list of XmlIDRef elements have the id
 * value as an attribute instead of element body text?</a>
 */
public class PartAdapter extends XmlAdapter<PartRef, Part> {

	@Override
	public PartRef marshal(final Part part) throws Exception {
		return new PartRef(part);
	}

	@Override
	public Part unmarshal(final PartRef partRef) throws Exception {
		return partRef.part;
	}
}
