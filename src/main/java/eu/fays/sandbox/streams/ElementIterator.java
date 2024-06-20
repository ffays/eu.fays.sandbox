package eu.fays.sandbox.streams;

import static java.util.Spliterator.ORDERED;
import static java.util.Spliterators.spliteratorUnknownSize;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ElementIterator<T> implements Iterator<T>  {

	/** Current element */
	T element;
	
	/** Given the current element, provide the next element */
	final Function<T, T> nextElementFunction;
	
	/**
	 * Constructor
	 * @param element initial element
	 * @param nextElementFunction given the current element, provide the next element
	 */
	public ElementIterator(final T element, final Function<T, T> nextElementFunction) {
		this.element = element;
		this.nextElementFunction = nextElementFunction;
	}

	/**
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return element != null;
	}

	/**
	 * @see java.util.Iterator#next()
	 */
	@SuppressWarnings("nls")
	@Override
	public T next() {
		if(element == null) {
			throw new NoSuchElementException("No more element!");
		}
		final T result = element;
		element = nextElementFunction.apply(element);
		return result;
	}

	/**
	 * Convert this iterator as a stream
	 * @return the stream
	 */
	public Stream<T> stream() {
		return StreamSupport.stream(spliteratorUnknownSize(this, ORDERED),false);
	}
}
