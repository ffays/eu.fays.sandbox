package eu.fays.sandbox.jaxb.ms.minimified;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlType;

@XmlType
public class Part {
	@XmlID
	@XmlAttribute(name = "key-number")
	public String _keyNumber;

	public Part() {}
	public Part(int keyNumber) {_keyNumber = Integer.toString(keyNumber);}
}
