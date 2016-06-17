package eu.fays.sandbox.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlValue;

/**
 * List Item of double
 * @author Frederic Fays
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ListItemOfDouble {

	/**
	 * Returns the value
	 * @return the value
	 */
	public double getLi() {
		return _li;
	}

	/**
	 * Sets the value
	 * @param li the value
	 */
	public void setLi(double li) {
		_li = li;
	}

	/**
	 * Constructor
	 */
	public ListItemOfDouble() {
		this(0d);
	}

	/**
	 * Constructor
	 * @param li value of the list item
	 */
	public ListItemOfDouble(double li) {
		_li = li;
	}

	/** list item's value */
	@XmlValue
	private double _li;
}
