package eu.fays.sandbox.machi;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

/**
 * Legend:
 * 
 * <ul>
 * <li>b: Blue Marble
 * <li>G: Green marble
 * <li>m: Magenta marble
 * <li>y: Yellow marble
 * <li>O: Open position
 * <li>X: Closed position
 * </ul>
 * 
 */
public class BrainQuizSolver {

	// @formatter:off
	private static final String[] quizz = {
		"XXmmmm" +
		"GGGXXm" +
		"OXOXXm" +
		"yyOmmm" +
		"XyXmXX" +
		"yyOmmO" ,

		"OOOOmO" +
		"XXXXmX" +
		"bbbbmX" +
		"XXOXmX" +
		"yGGGmO" +
		"yXXXXO" 
	};

	// @formatter:off
	public static final String[] quizz1 = {
		"XXyyyy" +
		"GGyXXm" +
		"GXOXXm" +
		"mmOmmm" +
		"XmXmXX" +
		"OmmmOO" ,

		"OOOOOO" +
		"XXXXOX" +
		"mmmmmX" +
		"XXOXGX" +
		"bbbGGy" +
		"bXXXXy" 
	};

	private static final String[] exitz = {
		"ÿÿÿÿÿÿ" +
		"ÿÿÿÿÿÿ" +
		"ÿÿÿÿÿÿ" +
		"ÿÿÿÿÿÿ" +
		"ÿÿÿÿÿÿ" +
		"ÿÿÿGGG" ,

		"GGGÿÿÿ" +
		"ÿÿÿÿÿÿ" +
		"ÿÿÿÿÿÿ" +
		"ÿÿÿÿÿÿ" +
		"ÿÿÿÿÿÿ" +
		"ÿÿÿÿÿÿ"
	};
	// @formatter:on

	/** Square size */
	private static final int N = 6;

	public static void main(String[] args) {
		for (int quizIx = 0; quizIx < quizz.length; quizIx++) {
			final Set<Quiz> visited = new HashSet<>();

			final Deque<Quiz> stack = new ArrayDeque<Quiz>();

			int movesCount = 1;
			Quiz solution = null;
			stack.add(new Quiz(quizz[quizIx], exitz[quizIx], N));
			while (!stack.isEmpty()) {
				final Quiz quiz = stack.pop();
				movesCount++;
				if (quiz.isSolved()) {
					if (solution == null || solution.history.size() > quiz.history.size()) {
						solution = quiz;
					}
					break;
				}
				if (!visited.contains(quiz)) {
					visited.add(quiz);
					stack.addAll(quiz.moves());
				}
			}
			if (solution != null) {
				int n = solution.history.size();
				for (int i = 1; i < n; i++) {
					System.out.println(i);
					System.out.println(solution.history.get(i).diff(solution.history.get(i - 1)));
				}
				System.out.println(solution);
			}
			System.out.println(movesCount);
			for (int i = 0; i < 72; i++) {
				System.out.print('=');
			}
			System.out.println();
		}

	}
}