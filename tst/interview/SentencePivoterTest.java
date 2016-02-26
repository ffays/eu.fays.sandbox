package interview;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class SentencePivoterTest {

	private String[] _sentences;
	private String[] _expected;

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{ new String[0], new String[0] },
				{
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
				},
				{
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
				},
				{
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
				},
				{
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
				}
		});
	}

	public SentencePivoterTest(final String[] sentences, final String[] expected) {
		_sentences = sentences;
		_expected = expected;

	}

	@Test
	public void pivotTest() throws Exception {
		final String[] computed = SentencePivoter.pivot2(_sentences);
		assertEquals(_expected.length, computed.length);
		for (int i = 0; i < _expected.length; i++) {
			assertEquals(_expected[i], computed[i]);
		}

	}
}
