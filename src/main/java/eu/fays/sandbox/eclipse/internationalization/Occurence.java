package eu.fays.sandbox.eclipse.internationalization;

import java.io.File;
import java.text.MessageFormat;

/**
 * An occurence of a sentence that needs to be equiped for the internationalization
 */
public final class Occurence implements Comparable<Occurence> {
	/** File containing the occurence */
	final File FILE;
	/** Line number within the file */
	final int LINE;
	/** Character offset within the line */
	final int OFFSET;
	/** Sentence to be localized */
	final String SENTENCE;

	/**
	 * Constructor
	 * 
	 * @param file File containing the occurence
	 * @param line Line number within the file
	 * @param offset Character offset within the line
	 * @param sentence Sentence to be localized
	 */
	public Occurence(final File file, final int line, final int offset, final String sentence) {
		FILE = file;
		LINE = line;
		OFFSET = offset;
		SENTENCE = sentence;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (obj == null || !(obj instanceof Occurence)) {
			return false;
		}

		final Occurence o = (Occurence) obj;
		return FILE == o.FILE && LINE == o.LINE && OFFSET == o.OFFSET;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return FILE.hashCode() + LINE * 3 + OFFSET * 7;
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final Occurence o) {
		if (!FILE.equals(o.FILE)) {
			return FILE.compareTo(o.FILE);
		} else if (LINE != o.LINE) {
			return Integer.compare(LINE, o.LINE);
		} else if (OFFSET != o.OFFSET) {
			return Integer.compare(OFFSET, o.OFFSET);
		}
		return 0;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@SuppressWarnings("nls")
	@Override
	public String toString() {
		return MessageFormat.format("{0}:{1}:{2}:{3}", FILE.getName(), LINE, OFFSET, SENTENCE);
	}
}
