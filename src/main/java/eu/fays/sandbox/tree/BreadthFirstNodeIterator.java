package eu.fays.sandbox.tree;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;

/**
 * Level order tree walker
 * @param <D> data type
 */
public class BreadthFirstNodeIterator<D> extends NodeIterator<D> {

	final Deque<Node<D>> _stack = new ArrayDeque<>();

	/**
	 * Constructor
	 * @param root root node
	 */
	public BreadthFirstNodeIterator(final Node<D> root) {
		push(root);
	}

	/**
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return super.hasNext() || !_stack.isEmpty();
	}

	/**
	 * @see java.util.Iterator#next()
	 */
	public Node<D> next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}

		if (_stack.isEmpty()) {
			while (super.hasNext()) {
				_stack.push(pop());
			}
		}

		final Node<D> result = _stack.pop();
		if (result.hasChildren()) {
			for (final Node<D> child : result.getChildren()) {
				push(child);
			}
		}
		return result;
	}
}
