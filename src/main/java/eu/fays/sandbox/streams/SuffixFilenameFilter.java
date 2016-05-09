package eu.fays.sandbox.streams;

import java.io.File;
import java.io.FilenameFilter;
import static java.util.Arrays.stream;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import static java.util.stream.Collectors.toCollection;

/**
 * A file filter based on a set of file name extensions
 * 
 * @author Fr&eacute;d&eacute;ric Fays
 */
@SuppressWarnings("nls")
public class SuffixFilenameFilter implements FilenameFilter {

	/**
	 * Constructor
	 * 
	 * @param extensions the file name extensions
	 */
	public SuffixFilenameFilter(final Set<String> extensions) {
		//
		assert extensions != null;
		assert !extensions.isEmpty();
		assert extensions.stream().map(v -> v != null && !v.isEmpty()).reduce(true, Boolean::logicalAnd);
		//
		_extensions = extensions;
	}

	/**
	 * Constructor
	 * 
	 * @param enumType type of the enumeration that contains the file name extensions
	 */
	public <T extends Enum<T>> SuffixFilenameFilter(Class<T> enumType) {
		this(Collections.unmodifiableSet(stream(enumType.getEnumConstants()).map(v -> v.name()).collect(toCollection(LinkedHashSet::new))));
	}

	/**
	 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
	 */
	@Override
	public boolean accept(final File dir, final String name) {
		final int i = name.lastIndexOf('.');
		final String ext = i != -1 ? name.substring(i + 1) : "";

		return _extensions.contains(ext.toUpperCase());
	}

	/** The file name extensions */
	private final Set<String> _extensions;

}