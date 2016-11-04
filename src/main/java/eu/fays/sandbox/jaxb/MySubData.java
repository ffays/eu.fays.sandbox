package eu.fays.sandbox.jaxb;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "myCity" })
@SuppressWarnings("nls")
public class MySubData {

	public MySubData() {

	}

	public MySubData(final City city) {
		myCity = city;
	}

	public City getMyCity() {
		if (myCity == null) {
			return City.DEFAULT;
		}
		return myCity;
	}

	public void setMyCity(final City myFruit) {
		this.myCity = myFruit;
	}

	/** A city */
	@XmlElement
	private City myCity = null;

	@XmlID
	@XmlAttribute
	// IDREF is of type NCName
	// NCName definition: https://www.w3.org/TR/1999/WD-xmlschema-2-19990924/#NCName
	// Name definition : https://www.w3.org/TR/REC-xml/#d0e804
	// U+01C0 : LATIN LETTER DENTAL - http://www.fileformat.info/info/unicode/char/01c0/index.htm
	private String myId = "id:u01C0" + UUID.randomUUID().toString();

}
