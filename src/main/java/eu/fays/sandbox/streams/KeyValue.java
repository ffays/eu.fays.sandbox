package eu.fays.sandbox.streams;

import java.util.Map;
import java.util.AbstractMap.SimpleImmutableEntry;

/**
 * An entry where only the key is identifier
 * @param <K> the type of the key
 * @param <V> the type of the value
 */
@SuppressWarnings("serial")
public class KeyValue<K, V> extends SimpleImmutableEntry<K, V> {

	/**
	 * Constructor
	 * @param key the key
	 * @param value the value
	 */
	public KeyValue(K key, V value) {
		super(key, value);
	}

	/**
	 * Compares only the key
	 * @param o the object to be compared
	 * @return either true or false
	 */
	@Override
	public boolean equals(final Object o) {
		if (!(o instanceof Map.Entry)) {
			return false;
		}
		final Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
		return getKey() == null ? e.getKey() == null : getKey().equals(e.getKey());

	}

	/**
	 * Returns the hash code of the key
	 * @return the hash code of the key
	 */
	@Override
	public int hashCode() {
		return getKey() == null ? 0 : getKey().hashCode();
	}

}
