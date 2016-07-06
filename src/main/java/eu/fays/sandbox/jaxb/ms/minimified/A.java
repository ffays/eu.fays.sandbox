package eu.fays.sandbox.jaxb.ms.minimified;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlType
public class A {
	@XmlElement(name = "part")
	@XmlJavaTypeAdapter(PartAdapter.class)
	public Part _part;

	public A() {}
	public A(Part part) { _part = part; }
}
