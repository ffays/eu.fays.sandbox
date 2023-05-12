package eu.fays.sandbox.tree;

import static java.util.stream.Collectors.joining;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@SuppressWarnings("nls")
public class NodeTest {

	public static Stream<Arguments> data() {
		// @formatter:off
		return Stream.of(
			// c.f. https://www.ius.edu.ba/sites/default/files/u1251/6._tree-traversals.pdf
			Arguments.of(buildTestTree0(), "ABDFGELCRS", "FGDLEBRSCA", "ABCDERSFGL"),
			// c.f. https://www.geeksforgeeks.org/number-of-ways-to-traverse-an-n-ary-tree/
			Arguments.of(buildTestTree1(), "ABKNMJFDGECHIL", "NMKJBFGDCHLIEA", "ABFDEKJGCHINML")
		);
		// @formatter:on
	}

	static TestNode buildTestTree0() {
		final TestNode a = new TestNode(null, "A");
		final TestNode b = new TestNode(a, "B");
		final TestNode c = new TestNode(a, "C");
		final TestNode d = new TestNode(b, "D");
		final TestNode e = new TestNode(b, "E");
		new TestNode(d, "F");
		new TestNode(d, "G");
		new TestNode(e, "L");
		new TestNode(c, "R");
		new TestNode(c, "S");

		return a;
	}

	static TestNode buildTestTree1() {
		final TestNode a = new TestNode(null, "A");
		final TestNode b = new TestNode(a, "B");
		new TestNode(a, "F");
		final TestNode d = new TestNode(a, "D");
		final TestNode e = new TestNode(a, "E");

		final TestNode k = new TestNode(b, "K");
		new TestNode(b, "J");
		new TestNode(k, "N");
		new TestNode(k, "M");

		new TestNode(d, "G");
		
		new TestNode(e, "C");
		new TestNode(e, "H");
		final TestNode i = new TestNode(e, "I");
		new TestNode(i, "L");

		return a;
	}

	@ParameterizedTest(name = "{index}")
	@MethodSource("data")
	public void preOrdrerTraversal(final TestNode node, final String preOrder, final String postOrder, final String breadthFirst) {
		final String actual = node.preOrderStream().map(Node::getData).collect(joining());
		assertEquals(preOrder, actual);
	}

	@ParameterizedTest(name = "{index}")
	@MethodSource("data")
	public void postOrdrerTraversal(final TestNode node, final String preOrder, final String postOrder, final String breadthFirst) {
		final String actual = node.postOrderStream().map(Node::getData).collect(joining());
		assertEquals(postOrder, actual);
	}

	@ParameterizedTest(name = "{index}")
	@MethodSource("data")
	public void breadthFirstTraversal(final TestNode node, final String preOrder, final String postOrder, final String breadthFirst) {
		final String actual = node.breadthFirstStream().map(Node::getData).collect(joining());
		assertEquals(breadthFirst, actual);
	}

}
