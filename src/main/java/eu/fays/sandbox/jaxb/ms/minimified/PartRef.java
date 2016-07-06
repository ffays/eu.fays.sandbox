package eu.fays.sandbox.jaxb.ms.minimified;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;

public class PartRef {
	@XmlIDREF
	@XmlAttribute(name = "ref-number")
	public Part _part;

	public PartRef(Part part) {_part = part;}
	public PartRef() {}	
}
