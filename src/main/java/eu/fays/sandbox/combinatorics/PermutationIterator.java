package eu.fays.sandbox.combinatorics;

import java.math.BigDecimal;
import java.util.Iterator;

/**
 * Sources:<br>
 * <ul>
 * <li><a href="http://stackoverflow.com/questions/2000048/stepping-through-all-permutations-one-swap-at-a-time/11916946#11916946">Stepping through all permutations one swap at a time</a>
 * <li><a href="https://en.wikipedia.org/wiki/Steinhaus%E2%80%93Johnson%E2%80%93Trotter_algorithm#Even.27s_speedup">Steinhaus–Johnson–Trotter algorithm</a>
 * </ul>
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
	 * @return
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

	// -----------------------------------------------------------------
	// Testing code:

	/**
	 * Main
	 * @param argv only first argument will be used
	 */
	public static void main(String argv[]) {
		final BigDecimal[] values = { new BigDecimal("2"), new BigDecimal("5"), new BigDecimal("8"), new BigDecimal("13"), new BigDecimal("40") };
		final BigDecimal objective = new BigDecimal("3");
		String s = argv[0];
		for (Iterator<int[]> it = new PermutationIterator(s.length()); it.hasNext();) {
			print(s, it.next());
		}
	}

	/**
	 * Applies the permutation on the given string then print it
	 * @param s the given string
	 * @param perm the permutation indexes
	 */
	protected static void print(String s, int[] perm) {
		for (int j = 0; j < perm.length; j++)
			System.out.print(s.charAt(perm[j]));
		System.out.println();
	}
}