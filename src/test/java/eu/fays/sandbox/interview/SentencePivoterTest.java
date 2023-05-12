package eu.fays.sandbox.interview;

import static org.junit.jupiter.api.Assertions.assertArrayEquals ;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@SuppressWarnings("nls")
public class SentencePivoterTest {

	public static Stream<Arguments> data() {
		return Stream.of(
			Arguments.of( new String[0], new String[0] ),
			Arguments.of(
				new String[] {
					"This is the first sentence to print"
				},
				new String[] {
					"This      ",
					"is        ",
					"the       ",
					"first     ",
					"sentence  ",
					"to        ",
					"print     "
				}
			),
			Arguments.of(
				new String[] {
					"This is the first sentence to print",
					"The second phrase",
					"Third one"
				},
				new String[] {
					"This      The       Third     ",
					"is        second    one       ",
					"the       phrase              ",
					"first                         ",
					"sentence                      ",
					"to                            ",
					"print                         "
				}
			),
			Arguments.of(
				new String[] {
					"The second phrase",
					"This is the first sentence to print",
					"Third one"
				},
				new String[] {
					"The       This      Third     ",
					"second    is        one       ",
					"phrase    the                 ",
					"          first               ",
					"          sentence            ",
					"          to                  ",
					"          print               "
				}
			),
			Arguments.of(
				new String[] {
					"The second phrase",
					"Third one",
					"This is the first sentence to print"
				},
				new String[] {
					"The       Third     This      ",
					"second    one       is        ",
					"phrase              the       ",
					"                    first     ",
					"                    sentence  ",
					"                    to        ",
					"                    print     "
				}
			)
		);
	}

	@ParameterizedTest(name = "{index}")
	@MethodSource("data")	
	public void pivotTest(final String[] sentences, final String[] expected) throws Exception {
		final String[] computed = SentencePivoter.pivot2(sentences);
		assertEquals(expected.length, computed.length);
		assertArrayEquals (expected, computed);
	}
}
