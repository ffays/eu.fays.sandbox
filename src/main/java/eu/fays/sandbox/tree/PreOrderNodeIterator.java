package eu.fays.sandbox.tree;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Pre order tree walker
 * @param <D> data type
 */

public class PreOrderNodeIterator<D> extends NodeIterator<D> {

	/**
	 * Constructor
	 * @param root root node
	 */
	public PreOrderNodeIterator(final Node<D> root) {
		push(root);
	}

	/**
	 * @see java.util.Iterator#next()
	 */
	public Node<D> next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}

		final Node<D> result = pop();
		if (result.hasChildren()) {
			final List<Node<D>> children = result.getChildren();
			for (int i = children.size() - 1; i >= 0; i--) {
				push(children.get(i));
			}
		}

		return result;
	}
}
