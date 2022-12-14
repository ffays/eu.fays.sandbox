package eu.fays.sandbox.tree;

import static java.util.AbstractMap.SimpleImmutableEntry;
import static java.util.Collections.emptyList;
import static java.util.Spliterator.ORDERED;
import static java.util.Spliterators.spliteratorUnknownSize;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * A Node of a Tree
 * @param <D> data type
 */
public class Node<D> implements Iterable<Node<D>> {

	/**
	 * Constructor<br>
	 * Node: if the parent is defined, adds this node as child to the parent.
	 * @param parent parent node, may be null
	 * @param data data, may be null
	 */
	public Node(final Node<D> parent, final D data) {
		_parent = parent;
		_data = data;
		if (parent != null) {
			parent.add(this);
		}
	}

	/**
	 * Constructor
	 * @param data data, may be null
	 */
	public Node(final D data) {
		this(null, data);
	}

	/**
	 * Constructor
	 */
	public Node() {
		this(null, null);
	}

	/**
	 * Returns the data, may be null
	 * @return the data
	 */
	public D getData() {
		return _data;
	}

	/**
	 * Sets the data, may be null
	 * @param data the data
	 */
	public void setData(final D data) {
		_data = data;
	}

	/**
	 * Returns the parent, may be null (i.e. in case of root node)
	 * @return the parent
	 */
	public Node<D> getParent() {
		return _parent;
	}

	/**
	 * Sets the parent
	 * @param parent the parent
	 */
	public void setParent(final Node<D> parent) {
		_parent = parent;
	}

	/**
	 * Returns the children, never null, may be empty.
	 * @return the children
	 */
	public List<Node<D>> getChildren() {
		if (_children == null) {
			return emptyList();
		}
		return _children;
	}
	
	/**
	 * Returns the first child, may be null;
	 * @return the first child
	 */
	public Node<D> getFirstChild() {
		if(hasChildren()) {
			return getChildren().get(0);
		}
		return null;
	}

	/**
	 * Returns the direct right sibling to the given child, may be null
	 * @param child the child
	 * @return the direct right sibling to the given child
	 */
	protected Node<D> getSibling(final Node<D> child) {
		if(!hasChildren()) {
			return null; // No children
		}

		final int ix = getChildren().indexOf(child);
		if(ix == -1) {
			return null; // Not found
		}

		if((ix + 1) == getChildren().size()) {
			return null; // Last child
		}

		return getChildren().get(ix + 1);
	}

	/**
	 * Returns the right sibling to this node, may be null
	 * @return the right sibling to this node, may be null
	 */
	public Node<D> getSibling() {
		if(isRoot()) {
			return null;
		}

		return getParent().getSibling(this);
	}

	/**
	 * Returns the depth of the node
	 * @return the depth of the node
	 */
	public int getDepth() {
		int result = 0;
		Node<D> parent = getParent();
		while(parent != null) {
			result++;
			parent = parent.getParent();
		}
		
		return result;
	}

	/**
	 * Adds a child
	 * @param child the child
	 */
	public void add(final Node<D> child) {
		//
		assert child != null;
		//
		if (_children == null) {
			_children = new ArrayList<Node<D>>();
		}
		_children.add(child);
	}

	/**
	 * Indicates if this is a root node
	 * @return either true or false
	 */
	public boolean isRoot() {
		return _parent == null;
	}

	/**
	 * Indicates if this is a leaf node
	 * @return either true or false
	 */
	public boolean isLeaf() {
		return !hasChildren();
	}

	/**
	 * Indicates if this node has children
	 * @return either true or false
	 */
	public boolean hasChildren() {
		return _children != null && !_children.isEmpty();
	}

	/**
	 * In order traversal
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<Node<D>> iterator() {
		return preOrderIterator();
	}

	/**
	 * Pre order traversal
	 * @return the iterator
	 */
	public Iterator<Node<D>> preOrderIterator() {
		return new PreOrderNodeIterator<D>(this);
	}

	/**
	 * Post order traversal
	 * @return the iterator
	 */
	public Iterator<Node<D>> postOrderIterator() {
		return new PostOrderNodeIterator<D>(this);
	}

	/**
	 * Breadth-first traversal
	 * @return the iterator
	 */
	public Iterator<Node<D>> breadthFirstIterator() {
		return new BreadthFirstNodeIterator<D>(this);
	}

	/**
	 * Pre order traversal
	 * @return the stream
	 */
	public Stream<Node<D>> preOrderStream() {
		return StreamSupport.stream(spliteratorUnknownSize(preOrderIterator(), ORDERED), false);
	}

	/**
	 * Post order traversal
	 * @return the stream
	 */
	public Stream<Node<D>> postOrderStream() {
		return StreamSupport.stream(spliteratorUnknownSize(postOrderIterator(), ORDERED), false);
	}

	/**
	 * Breadth-first traversal
	 * @return the stream
	 */
	public Stream<Node<D>> breadthFirstStream() {
		return StreamSupport.stream(spliteratorUnknownSize(breadthFirstIterator(), ORDERED), false);
	}

	/**
	 * Builds the tree
	 * @param <D> data type
	 * @param data data value of the root node
	 * @param map input data representing a tree, each value being either a D or a Map<D, ?>
	 * @return the root of the tree
	 */
	public static <D> Node<D> buildTree(final D data, final Map<D, ?> map) {
		final Node<D> result = new Node<D>(data);
		final Deque<Entry<Node<D>, Map<D, ?>>> stack = new ArrayDeque<>();
		stack.push(new SimpleImmutableEntry<Node<D>, Map<D, ?>>(result, map));

		while (!stack.isEmpty()) {
			final Entry<Node<D>, Map<D, ?>> el0 = stack.pop();
			final Node<D> node0 = el0.getKey();
			final Map<D, ?> v0 = el0.getValue();
			for (final Entry<D, ?> e : v0.entrySet()) {
				final Object v1 = e.getValue();
				final Node<D> node1;
				if (v1 instanceof Map) {
					node1 = new Node<D>(node0, e.getKey());
					@SuppressWarnings("unchecked")
					final Entry<Node<D>, Map<D, ?>> el1 = new SimpleImmutableEntry<Node<D>, Map<D, ?>>(node1, (Map<D, ?>) v1);
					stack.push(el1);
				} else {
					@SuppressWarnings("unchecked")
					final D data1 = (D) v1;
					node1 = new Node<D>(node0, data1);
				}
			}
		}
		return result;
	}
	
	/** data, i.e. node payload, may be null */
	private D _data;
	
	/** parent node, may be null */
	private Node<D> _parent;

	/** child nodes, may be null */
	private List<Node<D>> _children;
}
