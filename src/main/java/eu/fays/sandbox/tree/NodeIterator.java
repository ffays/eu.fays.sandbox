package eu.fays.sandbox.tree;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

/**
 * abstract tree node iterator
 *
 * @param <D>
 */
public abstract class NodeIterator<D> implements Iterator<Node<D>> {

	/**
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() {
		return !_stack.isEmpty();
	}

	/**
	 * Push node onto stack
	 * @param node node
	 */
	protected void push(final Node<D> node) {
		_stack.push(node);
	}

	/**
	 * Pop from stack
	 * @return node
	 */
	protected Node<D> pop() {
		return _stack.pop();
	}
	
	/** node stack */
	private final Deque<Node<D>> _stack = new ArrayDeque<>();
}
