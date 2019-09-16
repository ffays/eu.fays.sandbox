package eu.fays.sandbox.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Generic XML Adapter
 * 
 * Allowed mappings:
 * 
 * @param <T> boxed type, one of {@link Boolean}, {@link Integer}, {@link Long}, {@link Float}, {@link Double}
 */
public abstract class GenericXmlAdapter<T> extends XmlAdapter<T, T> {

	/** Default value */
	public final T d;

	/**
	 * Constructor
	 * @param defaultValue default value
	 */
	public GenericXmlAdapter(final T d) {
		//
		assert d != null : "Default value cannot be null!";
		assert d.getClass() == Boolean.class || d.getClass() == Integer.class || d.getClass() == Long.class || d.getClass() == Float.class || d.getClass() == Double.class;
		//
		this.d = d;
	}

	/**
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	@Override
	public T marshal(final T v) throws Exception {
		if (v == null || v.equals(d)) {
			return null;
		}
		return v;
	}

	/**
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
	 */
	@Override
	public T unmarshal(final T v) throws Exception {
		if (v == null) {
			return d;
		}
		return v;
	}
}
