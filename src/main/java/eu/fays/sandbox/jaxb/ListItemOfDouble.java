package eu.fays.sandbox.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlValue;

/**
 * List Item of double
 * @author Frederic Fays
 */
@XmlAccessorType(XmlAccessType.NONE)
public class ListItemOfDouble {

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
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return new Double(_li).hashCode();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (obj == null || !(obj instanceof ListItemOfDouble)) {
			return false;
		}
		final ListItemOfDouble o = (ListItemOfDouble) obj;
		return ((Double) getLi()).equals((Double) o.getLi());
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Double.toString(_li);
	}

	/** list item's value */
	@XmlValue
	private double _li;
}
