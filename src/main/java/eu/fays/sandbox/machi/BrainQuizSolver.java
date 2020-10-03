package eu.fays.sandbox.machi;

import static eu.fays.sandbox.machi.Quiz.B;
import static eu.fays.sandbox.machi.Quiz.G;
import static eu.fays.sandbox.machi.Quiz.M;
import static eu.fays.sandbox.machi.Quiz.Y;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

/**
 * Legend:
 * 
 * <ul>
 * <li>B: Blue Marble
 * <li>G: Green marble
 * <li>M: Magenta marble
 * <li>Y: Yellow marble
 * <li>O: Open position
 * <li>X: Closed position
 * </ul>
 * 
 */
public class BrainQuizSolver {

	// @formatter:off
	private static final String quiz1 =
		"XXYYYY" +
		"GGYXXM" +
		"GXOXXM" +
		"MMOMMM" +
		"XMXMXX" +
		"OMMMOO" ;
	// @formatter:on

	// @formatter:off
	private static final String quiz2 =
		"OOOOOO" +
		"XXXXOX" +
		"MMMMMX" +
		"XXOXGX" +
		"BBBGGY" +
		"BXXXXY" ;
	// @formatter:on

	// @formatter:off
	private static final String exit0 =
		"ÿÿÿÿÿÿ" +
		"ÿÿÿÿÿÿ" +
		"ÿÿÿÿÿÿ" +
		"ÿÿÿÿÿÿ" +
		"ÿÿÿÿÿÿ" +
		"ÿÿÿÿÿÿ" ;
	// @formatter:on

	// @formatter:off
	private static final String exit1 =
		"ÿÿÿÿÿÿ" +
		"ÿÿÿÿÿÿ" +
		"ÿÿÿÿÿÿ" +
		"ÿÿÿÿÿÿ" +
		"ÿÿÿÿÿÿ" +
		"ÿÿÿGGG" ;
	// @formatter:on

	// @formatter:off
	private static final String exit2 =
		"ÿÿÿÿÿÿ" +
		"ÿÿÿÿÿÿ" +
		"ÿÿÿÿÿÿ" +
		"ÿÿÿÿÿÿ" +
		"ÿÿÿÿÿÿ" +
		"ÿÿÿGGG" ;
	// @formatter:on

	/** Square size */
	private static final int N = 6;

	public static void main(String[] args) {
		{
			final Set<Quiz> visited = new HashSet<>();
			
			Deque<Quiz> stack = new ArrayDeque<Quiz>();

			
			int i = 1;
			Quiz solution = null;
			stack.add(new Quiz(quiz1, exit1, N));
			while (!stack.isEmpty()) {
				final Quiz quiz = stack.pop();
				i++;
				if(quiz.isSolved()) {					
					solution = quiz;
					break;
				}
				if(!visited.contains(quiz)) {
					visited.add(quiz);
					stack.addAll(quiz.moves());
				}
			}
			if(solution != null) {
				int j=1;
				for(final Quiz quiz : solution.history) {
					System.out.println(j);
					System.out.println(quiz);
					j++;
				}
				System.out.println(j);
				System.out.println(solution);
			}
			System.out.println(i);

//			System.out.println();
//			System.out.println(Quiz.chainToString('B', N, quiz.chains[B]));
//			System.out.println();
//			System.out.println(Quiz.chainToString('G', N, quiz.chains[G]));
//			System.out.println();
//			System.out.println(Quiz.chainToString('M', N, quiz.chains[M]));
//			System.out.println();
//			System.out.println(Quiz.chainToString('Y', N, quiz.chains[Y]));
//			System.out.println();
		}
		
		{
//			Quiz quiz = new Quiz(quiz2, exit2, N);
//
//			System.out.println(quiz.isSolved());
//			System.out.println(quiz);
//			System.out.println();
//			System.out.println(Quiz.chainToString('B', N, quiz.chains[B]));
//			System.out.println();
//			System.out.println(Quiz.chainToString('G', N, quiz.chains[G]));
//			System.out.println();
//			System.out.println(Quiz.chainToString('M', N, quiz.chains[M]));
//			System.out.println();
//			System.out.println(Quiz.chainToString('Y', N, quiz.chains[Y]));
//			System.out.println();
		}

	}
}