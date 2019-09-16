package eu.fays.sandbox.tree;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;

/**
 * Post order tree walker
 * @param <D> data type
 */
public class PostOrderNodeIterator<D> extends NodeIterator<D> {

	/**
	 * Constructor
	 * @param root root node
	 */
	public PostOrderNodeIterator(final Node<D> root) {
		final Deque<Node<D>> stack = new ArrayDeque<>();
		stack.push(root);
		while (!stack.isEmpty()) {
			final Node<D> node = stack.pop();
			push(node);
			if (node.hasChildren()) {
				for (final Node<D> child : node.getChildren()) {
					stack.push(child);
				}
			}
		}
	}

	/**
	 * @see java.util.Iterator#next()
	 */
	public Node<D> next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		return pop();
	}
}
