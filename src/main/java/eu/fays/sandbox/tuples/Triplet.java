package eu.fays.sandbox.tuples;

import java.util.stream.IntStream;

/**
 * A boiler plate class to perform sorts based on 3 keys
 * @param <T> type of the first key
 * @param <U> type of the second key
 * @param <V> type of the third key
 * @author Frederic Fays
 */
class Triplet<T extends Comparable<T>, U extends Comparable<U>, V extends Comparable<V>> implements Comparable<Triplet<T, U, V>> {

	/** First key */
	public final T t;
	/** Second key */
	public final U u;
	/** Third key */
	public final V v;

	/** Prime number used to compute the hash code */
	static final int PRIME[] = { 3, 7, 11 };

	/**
	 * Constructor
	 * @param t first key
	 * @param u second key
	 * @param v third key
	 */
	public Triplet(final T t, final U u, final V v) {
		//
		assert t != null;
		assert u != null;
		assert v != null;
		//
		this.t = t;
		this.u = u;
		this.v = v;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final Comparable<?>[] k = new Comparable[] { t, u, v };
		return IntStream.range(0, k.length).reduce(0, (a, i) -> a += k[i].hashCode() * PRIME[i]);
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object o) {
		return o != null && (o instanceof Triplet) && t.equals(((Triplet<?, ?, ?>) o).t) && u.equals(((Triplet<?, ?, ?>) o).u) && v.equals(((Triplet<?, ?, ?>) o).v);
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final Triplet<T, U, V> o) {
		int result = t.compareTo(o.t);
		if (result == 0) {
			result = u.compareTo(o.u);
			if (result == 0) {
				result = v.compareTo(o.v);
			}
		}
		return result;
	}
}