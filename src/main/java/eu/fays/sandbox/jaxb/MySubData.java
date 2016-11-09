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
	private String myId = "id\u01C0" + UUID.randomUUID().toString();

	/** Invalid characters for NCName (i.e. IDREF) */
	public static final String NCNAME_INVALID_CHARACTER_REGEX = "[^\\p{Alnum}_\\-.\\u00B7\\u00C0-\\u00D6\\u00D8-\\u00F6\\u00F8-\\u02FF\\u0300-\\u036F\\u0370-\\u037D\\u037F-\\u1FFF\\u200C-\\u200D\\u203F-\\u2040\\u2070-\\u218F\\u2C00-\\u2FEF\\u3001-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFFD]";
}
