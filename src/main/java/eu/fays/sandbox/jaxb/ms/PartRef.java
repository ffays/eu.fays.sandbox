package eu.fays.sandbox.jaxb.ms;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "part" })
public class PartRef {
	@XmlIDREF
	@XmlAttribute(name = "ref-number")
	public Part part;

	public PartRef() {
	}

	public PartRef(final Part part) {
		this.part = part;
	}
}
