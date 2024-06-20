package eu.fays.sandbox.streams;

import static java.util.Spliterator.ORDERED;
import static java.util.Spliterators.spliteratorUnknownSize;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Iterates over the given class and its parent classes.
 */
public class LineageIterator implements Iterator<Class<?>> {

	/** Classes part of the lineage */
	Class<?> klass;
	
	/**
	 * Constructor
	 * @param klass initial class
	 */
	public LineageIterator(final Class<?> klass) {
		this.klass = klass;		
	}

	/**
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return klass != null;
	}

	/**
	 * @see java.util.Iterator#next()
	 */
	@SuppressWarnings("nls")
	@Override
	public Class<?> next() {
		if(klass == null) {
			throw new NoSuchElementException("No more parent!");
		}
		final Class<?> result = klass;
		klass = klass.getSuperclass();
		return result;
	}

	/**
	 * Convert this iterator as a stream
	 * @return the stream
	 */
	public Stream<Class<?>> stream() {
		return StreamSupport.stream(spliteratorUnknownSize(this, ORDERED),false);
	}
}
