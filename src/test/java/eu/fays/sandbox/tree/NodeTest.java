package eu.fays.sandbox.tree;

import static java.util.stream.Collectors.joining;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class NodeTest {

	private final TestNode _node;
	private final String _preOrder;
	private final String _postOrder;
	private final String _breadthFirst;

	@Parameters(name = "{0}")
	public static Collection<Object[]> data() {
		// @formatter:off
		return Arrays.asList(new Object[][] {
				// c.f. https://www.ius.edu.ba/sites/default/files/u1251/6._tree-traversals.pdf
				{ 0, buildTestTree0(), "ABDFGELCRS", "FGDLEBRSCA", "ABCDERSFGL"},
				// c.f. https://www.geeksforgeeks.org/number-of-ways-to-traverse-an-n-ary-tree/
				{ 1, buildTestTree1(), "ABKNMJFDGECHIL", "NMKJBFGDCHLIEA", "ABFDEKJGCHINML"}
		});
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

	
	public NodeTest(final int n, final TestNode node, final String preOrder, final String postOrder, final String breadthFirst) {
		_node = node;
		_preOrder = preOrder;
		_postOrder = postOrder;
		_breadthFirst = breadthFirst;
	}

	@Test
	public void preOrdrerTraversal() {
		final String actual = _node.preOrderStream().map(Node::getData).collect(joining());
		assertEquals(_preOrder, actual);
	}

	@Test
	public void postOrdrerTraversal() {
		final String actual = _node.postOrderStream().map(Node::getData).collect(joining());
		assertEquals(_postOrder, actual);
	}

	@Test
	public void breadthFirstTraversal() {
		final String actual = _node.breadthFirstStream().map(Node::getData).collect(joining());
		assertEquals(_breadthFirst, actual);
	}

}
