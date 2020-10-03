package eu.fays.sandbox.machi;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Quiz {
	public final String quiz;
	public final int n;

	private final char q[][];
	private final char e[][];

	public final int chains[][][];
	
	public final List<Quiz> history;

	/** Color codes */
	public static final char[] CC = new char [] { 'B', 'G', 'M', 'Y'};
	public static final int B = 0;
	public static final int G = 1;
	public static final int M = 2;
	public static final int Y = 3;
	
	

	public Quiz(final String quiz, final String exit, final int n) {
		//
		assert quiz != null;
		assert n > 0;
		assert quiz.matches("[BGMYOX]+");
		assert exit.matches("[Gÿ]+");
		assert quiz.length() == n * n;
		assert exit.length() == n * n;
		//
		this.quiz = quiz;
		this.n = n;
		q = toChar(quiz, n);
		e = toChar(exit, n);
		chains = new int[CC.length][][];
		chains[B] = identifyChain(CC[B], q);
		chains[G] = identifyChain(CC[G], q);
		chains[M] = identifyChain(CC[M], q);
		chains[Y] = identifyChain(CC[Y], q);
		history = Collections.emptyList();
		
		//
		assert quiz.equals(toString(q, false));
		assert exit.equals(toString(e, false));
		//
	}

	private Quiz(final char q[][], final char e[][], final int chains[][][], final List<Quiz> history) {
		this.quiz = toString(q, false);
		this.n = q.length;
		this.q = q;
		this.e = e;
		this.chains = chains;
		this.history = history;
	}

	@Override
	public int hashCode() {
		return quiz.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (!(o instanceof Quiz)) {
			return false;
		}

		return quiz.equals(((Quiz) o).quiz);
	}

	private static int[][] identifyChain(final char color, final char q[][]) {
		final int n = q.length;

		// Compute length of the chain
		int len = 0;
		for (int r = 0; r < n; r++) {
			for (int c = 0; c < n; c++) {
				if (q[r][c] == color) {
					len++;
				}
			}
		}
		int[][] result = new int[len][2];

		// Identify marbles of the chain
		int i = 0;
		for (int r = 0; r < n; r++) {
			for (int c = 0; c < n; c++) {
				if (q[r][c] == color) {
					result[i][0] = r;
					result[i][1] = c;
					i++;
				}
			}
		}

		// Identify first marble of the chain
		int[] beg = null;
		for (i = 0; i < len; i++) {
			if (adjacentSameBlockCount(color, q, result[i][0], result[i][1]) == 1) {
				beg = result[i];
				int[] tmp = result[0];
				result[0] = beg;
				result[i] = tmp;
				break;
			}
		}

		// Order marbles inside the chain
		for (i = 1; i < len - 1; i++) {
			for (int j = i + 1; j < len; j++) {
				if (areConnected(result[i - 1][0], result[i - 1][1], result[j][0], result[j][1])) {
					int[] tmp = result[i];
					result[i] = result[j];
					result[j] = tmp;
				}
			}
		}

		return result;
	}

	private static boolean areConnected(final int ar, final int ac, final int br, final int bc) {
		// Top
		// .b.
		// .a.
		// ...
		if ((ar == (br + 1)) && (ac == bc)) {
			return true;
		}

		// Left
		// ...
		// ba.
		// ...
		if ((ar == br) && (ac == bc + 1)) {
			return true;
		}

		// Bottom
		// ...
		// .a.
		// .b.
		if ((ar == (br - 1)) && (ac == bc)) {
			return true;
		}

		// Right
		// ...
		// .ab
		// ...
		if ((ar == br) && (ac == (bc - 1))) {
			return true;
		}

		return false;
	}

	private static int adjacentSameBlockCount(final char color, final char q[][], final int r, final int c) {
		final int n = q.length;
		int result = 0;

		// Top
		if (r > 0 && q[r - 1][c] == color) {
			result++;
		}

		// Left
		if (c > 0 && q[r][c - 1] == color) {
			result++;
		}

		// Bottom
		if (r < (n - 1) && q[r + 1][c] == color) {
			result++;
		}

		// Right
		if (c < (n - 1) && q[r][c + 1] == color) {
			result++;
		}

		return result;
	}

	private static char[][] toChar(final String state, final int n) {
		final char[][] result = new char[n][n];
		for (int r = 0; r < n; r++) {
			for (int c = 0; c < n; c++) {
				result[r][c] = state.charAt(r * n + c);
			}
		}

		return result;
	}

	public static String toString(final char[][] q, boolean nl) {
		final int n = q.length;
		final StringBuilder result = new StringBuilder();
		for (int r = 0; r < n; r++) {
			result.append(String.valueOf(q[r]));
			if (nl) {
				result.append(System.lineSeparator());
			}
		}
		return result.toString();
	}

	public String toString() {
		return toString(q, true);
	}

	public void print(final PrintStream out) {
		for (int r = 0; r < n; r++) {
			out.println(String.valueOf(q[r]));
		}
	}

	public static String chainToString(final char color, final int n, final int[][] chain) {
		final char[][] q = new char[n][n];
		for (int r = 0; r < n; r++) {
			for (int c = 0; c < n; c++) {
				q[r][c] = '.';
			}
		}

		for (int i = 0; i < chain.length; i++) {
			q[chain[i][0]][chain[i][1]] = color;
			// @formatter:off
//			if(i==0) {
//				q[chain[i][0]][chain[i][1]] =  Character.toLowerCase(color);
//			} else if(i == chain.length - 1) {
//				q[chain[i][0]][chain[i][1]] = color;
//			} else {
//				q[chain[i][0]][chain[i][1]] =  String.format("%x", i%16).charAt(0);
//			}
			// @formatter:on
		}

		final String result = toString(q, true);
		return result;
	}

	public boolean isSolved() {
		for (int r = 0; r < n; r++) {
			for (int c = 0; c < n; c++) {
				if (((int) q[r][c] & (int) e[r][c]) != (int) q[r][c]) {
					return false;
				}
			}
		}
		return true;
	}
	
	public List<Quiz> moves() {
		// Loop over chains
		final int[][] deltas = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}}; 
		final List<Quiz> result = new ArrayList<>();
		for (int chainIx = 0; chainIx < chains.length; chainIx++) {
			// Loop over both first marble and last marble
			if(chains[chainIx].length == 0) {
				continue;
			}
			final int lastMarbleIx = chains[chainIx].length - 1;
			for(int marbleIx : new int[] {0, lastMarbleIx}) {
				for(int[] delta : deltas) {
					final int r = chains[chainIx][marbleIx][0] + delta[0]; 
					final int c = chains[chainIx][marbleIx][1] + delta[1];
					if (r >= 0 && c >= 0 && r < n && c < n && q[r][c] == 'O') {
						// Possible move
						final int[][] chain2 = deepClone(chains[chainIx]);
						final int[][][] chains2 = Arrays.copyOf(chains, chains.length);
						chains2[chainIx] = chain2;
						char[][] q2 = deepClone(q);
						q2[r][c] = CC[chainIx];
						if (marbleIx == 0) {
							for (int i = 1; i <= lastMarbleIx; i++) {
								chain2[i][0] = chains[chainIx][i - 1][0];
								chain2[i][1] = chains[chainIx][i - 1][1];
							}
							chain2[0][0] = r;
							chain2[0][1] = c;
							q2[chains[chainIx][lastMarbleIx][0]][chains[chainIx][lastMarbleIx][1]] = 'O';
						} else {
							for (int i = lastMarbleIx - 1; i >= 0; i--) {
								chain2[i][0] = chains[chainIx][i + 1][0];
								chain2[i][1] = chains[chainIx][i + 1][1];
							}
							chain2[lastMarbleIx][0] = r;
							chain2[lastMarbleIx][1] = c;
							q2[chains[chainIx][0][0]][chains[chainIx][0][1]] = 'O';
						}
						final List<Quiz> history2 = new ArrayList<Quiz>(history);
						history2.add(this);
						final Quiz quiz2 = new Quiz(q2, e, chains2, history2);
						result.add(quiz2);
					}
				}
			}
		}
		return result;
	}
	
	private static int[][] deepClone(final int[][] o) {
		int[][] result = new int[o.length][];
		for(int i=0; i<o.length; i++) {
			result[i] = Arrays.copyOf(o[i], o[i].length);
		}
		return result;
	}
	
	private static char[][] deepClone(final char[][] o) {
		char[][] result = new char[o.length][];
		for(int i=0; i<o.length; i++) {
			result[i] = Arrays.copyOf(o[i], o[i].length);
		}
		return result;
	}

}