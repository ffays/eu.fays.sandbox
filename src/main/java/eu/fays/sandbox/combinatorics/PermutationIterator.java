package eu.fays.sandbox.combinatorics;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Sources:<br>
 * <ul>
 * <li><a href="http://stackoverflow.com/questions/2000048/stepping-through-all-permutations-one-swap-at-a-time/11916946#11916946">Stepping through all permutations one swap at a time</a>
 * <li><a href="https://en.wikipedia.org/wiki/Steinhaus%E2%80%93Johnson%E2%80%93Trotter_algorithm#Even.27s_speedup">Steinhaus�Johnson�Trotter algorithm</a>
 * </ul>
 * <strong>Strong advice</strong>: do not use this class! It is far more wise to rely on a well-tested library such as <a href="http://commons.apache.org/proper/commons-math/">The Apache Commons Mathematics Library</a>, and to use the class
 * <a href="http://commons.apache.org/proper/commons-math/apidocs/org/apache/commons/math3/util/CombinatoricsUtils.html">CombinatoricsUtils</a>
 */
public class PermutationIterator implements Iterator<int[]> {
	private int[] _next = null;

	private final int _n;
	private int[] _perm;
	private int[] _dirs;

	/**
	 * Constructor
	 * @param size number of elements
	 */
	public PermutationIterator(int size) {
		_n = size;
		if (_n <= 0) {
			_perm = (_dirs = null);
		} else {
			_perm = new int[_n];
			_dirs = new int[_n];
			for (int i = 0; i < _n; i++) {
				_perm[i] = i;
				_dirs[i] = -1;
			}
			_dirs[0] = 0;
		}

		_next = _perm;
	}

	/**
	 * @see java.util.Iterator#next()
	 */
	@Override
	public int[] next() {
		int[] r = makeNext();
		_next = null;
		return r;
	}

	/**
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return (makeNext() != null);
	}

	/**
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Converts this iterator into a stream
	 * @param parallel if {@code true} then the returned stream is a parallel stream; if {@code false} the returned stream is a sequential stream.
	 * @return the stream
	 */
	public Stream<int[]> permutationStream(final boolean parallel) {
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(this, Spliterator.ORDERED), parallel);
	}

	/**
	 * Factory method : provides a new stream of permutations
	 * @param size number of elements
	 * @param parallel if {@code true} then the returned stream is a parallel stream; if {@code false} the returned stream is a sequential stream.
	 * @return the new stream
	 */
	public static Stream<int[]> permutationStream(final int size, final boolean parallel) {
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(new PermutationIterator(size), Spliterator.ORDERED), parallel);
	}

	/**
	 * @return next permutation
	 */
	private int[] makeNext() {
		if (_next != null)
			return _next;
		if (_perm == null)
			return null;

		// find the largest element with != 0 direction
		int i = -1, e = -1;
		for (int j = 0; j < _n; j++)
			if ((_dirs[j] != 0) && (_perm[j] > e)) {
				e = _perm[j];
				i = j;
			}

		if (i == -1) // no such element -> no more premutations
			return (_next = (_perm = (_dirs = null))); // no more permutations

		// swap with the element in its direction
		int k = i + _dirs[i];
		swap(i, k, _dirs);
		swap(i, k, _perm);
		// if it's at the start/end or the next element in the direction
		// is greater, reset its direction.
		if ((k == 0) || (k == _n - 1) || (_perm[k + _dirs[k]] > e))
			_dirs[k] = 0;

		// set directions to all greater elements
		for (int j = 0; j < _n; j++)
			if (_perm[j] > e)
				_dirs[j] = (j < k) ? +1 : -1;

		return (_next = _perm);
	}

	/**
	 * Swaps two elements
	 * @param i first index
	 * @param j second index;
	 * @param arr the array holding the elements
	 */
	protected static void swap(int i, int j, int[] arr) {
		int v = arr[i];
		arr[i] = arr[j];
		arr[j] = v;
	}

	/**
	 * Main
	 * @param argv only first argument will be used
	 */
	public static void main(String argv[]) {
		String s = argv[0];
		for (Iterator<int[]> it = new PermutationIterator(s.length()); it.hasNext();) {
			final int[] perm = it.next();
			final StringBuilder builder = new StringBuilder(s.length());
			for (int j = 0; j < perm.length; j++) {
				builder.append(s.charAt(perm[j]));
				LOGGER.info(builder.toString());
			}
		}
	}

	/** Standard logger */
	private static final Logger LOGGER = Logger.getLogger(PermutationIterator.class.getName());
}