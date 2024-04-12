package eu.fays.sandbox.interview;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@SuppressWarnings("nls")
public class FibonacciTest {

	public static Stream<Arguments> data() {
		// @formatter:off
		return Stream.of(
			Arguments.of(1, Arrays.asList(new Integer[] {1 })),
			Arguments.of(2, Arrays.asList(new Integer[] {1, 1 })),
			Arguments.of(3, Arrays.asList(new Integer[] {1, 1, 2 })),
			Arguments.of(4, Arrays.asList(new Integer[] {1, 1, 2, 3 })),
			Arguments.of(5, Arrays.asList(new Integer[] {1, 1, 2, 3, 5 })),
			Arguments.of(6, Arrays.asList(new Integer[] {1, 1, 2, 3, 5, 8 })),
			Arguments.of(7, Arrays.asList(new Integer[] {1, 1, 2, 3, 5, 8, 13 }))
		);
		// @formatter:on		
	}

	public static IntStream data2() {
		return IntStream.range(-3, 1);
	}
	
	@ParameterizedTest(name = "{index}")
	@MethodSource("data")
	public void fibonacciTest(final int n, final List<Integer> expected) {
		final List<Integer> computed = Fibonacci.fibonacci(n);
		assertEquals(expected.size(), computed.size());
		assertIterableEquals(expected, computed);
	}
	
	@ParameterizedTest(name = "{index}")
	@MethodSource("data2")
	public void fibonacciExceptionTest(final int n) {
		final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> Fibonacci.fibonacci(n));
		assertEquals("n must be strictly positive", thrown.getMessage());
	}
}
