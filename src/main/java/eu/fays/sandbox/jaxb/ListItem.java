package eu.fays.sandbox.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;

/**
 * List Item
 * @param <T> the type of the item
 * @author Frederic Fays
 */
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class ListItem<T> {

	/**
	 * Constructor
	 */
	public ListItem() {
		try {
			_li = getParameterizedType().newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Constructor
	 * @param li value of the list item
	 */
	public ListItem(T li) {
		_li = li;
	}

	/**
	 * Returns the value
	 * @return the value
	 */
	public T getLi() {
		return _li;
	}

	/**
	 * Sets the value
	 * @param li the value
	 */
	public void setLi(T li) {
		_li = li;
	}

	/**
	 * Returns the effective class of the parameterized type
	 * @return the class
	 */
	@XmlTransient
	public abstract Class<T> getParameterizedType();

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return getLi().hashCode();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (obj == null || !(getClass().isInstance(obj))) {
			return false;
		}

		@SuppressWarnings("unchecked")
		final ListItem<T> o = (ListItem<T>) obj;
		return getLi().equals(o.getLi());
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getLi().toString();
	}

	/** list item's value */
	@XmlValue
	private T _li;
}
