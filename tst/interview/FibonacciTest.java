package interview;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Enclosed.class)
public class FibonacciTest {

	@RunWith(Parameterized.class)
	public static class FibonacciSuiteTest {

		private int _n;
		private List<Integer> _expected;

		@Parameters
		public static Collection<Object[]> data() {
			return Arrays.asList(new Object[][] {
					{ 1, Arrays.asList(new Integer[] { 1 }) },
					{ 2, Arrays.asList(new Integer[] { 1, 1 }) },
					{ 3, Arrays.asList(new Integer[] { 1, 1, 2 }) },
					{ 4, Arrays.asList(new Integer[] { 1, 1, 2, 3 }) },
					{ 5, Arrays.asList(new Integer[] { 1, 1, 2, 3, 5 }) },
					{ 6, Arrays.asList(new Integer[] { 1, 1, 2, 3, 5, 8 }) },
					{ 7, Arrays.asList(new Integer[] { 1, 1, 2, 3, 5, 8, 13 }) },

			});
		}

		public FibonacciSuiteTest(final int n, final List<Integer> expected) {
			_n = n;
			_expected = expected;

		}

		@Test
		public void fibonacciTest() {
			final List<Integer> computed = Fibonacci.fibonacci(_n);

			assertEquals(_expected.size(), computed.size());
			for (int i = 0; i < _expected.size(); i++) {
				assertEquals(_expected.get(i), computed.get(i));
			}
		}

	}

	@RunWith(Parameterized.class)
	public static class FibonacciExceptionTest {
		private int _n;

		@Rule
		public ExpectedException _thrown = ExpectedException.none();

		@Parameters
		public static Collection<Object[]> data() {
			return Arrays.asList(new Object[][] {
					{ -3 },
					{ -2 },
					{ -1 },
					{ 0 }

			});
		}

		public FibonacciExceptionTest(final int input) {
			_n = input;
		}

		@Test
		public void fibonacciTest() {
			_thrown.expect(IllegalArgumentException.class);
			_thrown.expectMessage("n must be strictly positive");
			Fibonacci.fibonacci(_n);
		}
	}

}
