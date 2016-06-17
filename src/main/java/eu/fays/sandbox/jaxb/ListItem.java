package eu.fays.sandbox.jaxb;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlValue;

/**
 * List Item
 * @param <T> the type of the item
 * @author Frederic Fays
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ListItem<T> {

	/**
	 * Constructor
	 */
	public ListItem() {
		try {
			_li = newLi();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {

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
	 * Instanciate a new list item:<br>
	 * <br>
	 * Article: <a href="http://stackoverflow.com/questions/3437897/how-to-get-a-class-instance-of-generics-type-t">How to get a class instance of generics type T</a><br>
	 * @return the new list item
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	@SuppressWarnings("unchecked")
	private T newLi() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		final Type gsc = getClass().getGenericSuperclass();
		final Type t = ((ParameterizedType) gsc).getActualTypeArguments()[0];
		return (T) (Class.forName(t.toString()).newInstance());
	}

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
