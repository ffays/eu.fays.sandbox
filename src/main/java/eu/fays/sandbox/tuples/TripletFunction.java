package eu.fays.sandbox.tuples;

/**
 * A boiler plate function used to re-order keys (can be done when classes T, U and V are either identical or a conversion exists amongst them)
 * @param <T> type of the first key
 * @param <U> type of the second key
 * @param <V> type of the third key
 */
@FunctionalInterface
interface TripletFunction<T extends Comparable<T>, U extends Comparable<U>, V extends Comparable<V>> {
	/**
	 * Apply the re-order function
	 * @param t first key
	 * @param u second key
	 * @param v third key
	 * @return the triplet
	 */
	Triplet<T, U, V> apply(final T t, final U u, final V v);
}