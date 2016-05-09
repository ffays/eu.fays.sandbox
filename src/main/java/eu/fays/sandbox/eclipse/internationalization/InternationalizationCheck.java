package eu.fays.sandbox.eclipse.internationalization;

import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toCollection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Spots all sentences that needs to be equiped for internationalization in the given {@link InternationalizationCheck#PACKAGE_FOLDER}<br>
 * <br>
 * Parameters:
 * <ul>
 * <li>{@link InternationalizationCheck#PACKAGE_FOLDER}
 * <li>{@link InternationalizationCheck#COMMENT_SOURCE_FILES}
 * </ul>
 * Note: the parameters must be provided to the jvm as runtime properties, e.g. -DPACKAGE_FOLDER=. -DCOMMENT_SOURCE_FILES=false
 * 
 * @author Fr&eacute;d&eacute;ric Fays
 */
@SuppressWarnings("nls")
public class InternationalizationCheck {
	/** Package folder to be checked */
	public static final Path PACKAGE_FOLDER = new File(System.getProperty("PACKAGE_FOLDER", ".")).toPath();
	/** Flag to indicate if the source files needs to be amended with a line-end comment indicating that the sentence requires to be equiped for internationalization */
	public static final boolean COMMENT_SOURCE_FILES = Boolean.valueOf(System.getProperty("COMMENT_SOURCE_FILES", "false"));
	/** Standard logger */
	private static final Logger LOGGER = Logger.getLogger(InternationalizationCheck.class.getName());

	/**
	 * Spots all sentences that needs to be equiped for internationalization in the given {@link InternationalizationCheck#PACKAGE_FOLDER}
	 * 
	 * @param args unused
	 * @throws Exception in case of unexpected error
	 */
	public static void main(String[] args) throws Exception {
		//
		assert PACKAGE_FOLDER.toFile().exists();
		assert PACKAGE_FOLDER.toFile().isDirectory();
		assert PACKAGE_FOLDER.toFile().canRead();
		//
		// Build list of files to be processed
		final List<File> fileList = unmodifiableList(Files.list(PACKAGE_FOLDER).map(p -> p.toFile()).filter(f -> f.isFile() && f.getName().endsWith(".java")).collect(toCollection(ArrayList::new)));

		// Pass 1 : identify sentences to be localized
		final List<Occurence> occurenceList = new ArrayList<>();
		for (File file : fileList) {
			LOGGER.fine(MessageFormat.format("Processing ''{0}''", file.getName()));
			try (final FileReader fr = new FileReader(file); BufferedReader reader = new BufferedReader(fr);) {
				String line = reader.readLine();
				int lineNumber = 1;
				boolean skip = false;
				while (line != null) {
					if (skip || line.matches("\\s*/\\*.*")) {
						/* skip comments */
						skip = !line.matches(".*\\*/\\s*");
					} else if (line.matches("\\s*//.*")) {
						// skip comments
					} else {
						int offset = line.indexOf('"');
						int commentOffset = line.indexOf("//");
						int occurenceNumber = 1;
						while (offset >= 0) {
							final int end = line.indexOf('"', offset + 1);
							String sentence = line.substring(offset + 1, end).trim();
							if (sentence.isEmpty()) {
								// discard : empty
							} else if (commentOffset != -1 && offset > commentOffset) {
								// discard : comment
							} else if (line.matches(MessageFormat.format(".*\\$NON-NLS-{0}\\$.*", occurenceNumber))) {
								// discard : tagged NLS
							} else {
								if (sentence.indexOf(':') == sentence.length() - 1) {
									sentence = sentence.substring(0, sentence.length() - 1);
								}
								final Occurence occurence = new Occurence(file, lineNumber, offset + 1, sentence);
								occurenceList.add(occurence);
							}
							offset = line.indexOf('"', end + 1);
							occurenceNumber++;
						}
					}
					line = reader.readLine();
					lineNumber++;
				}
			}
		}

		// Pass 2 : count.
		Map<String, Integer> sentenceCounterMap = new HashMap<>();
		Map<File, List<Occurence>> occurenceListPerFileMap = new LinkedHashMap<>();
		Map<String, Set<File>> sentenceLocationMap = new LinkedHashMap<>();
		for (Occurence occurence : occurenceList) {
			sentenceCounterMap.put(occurence.SENTENCE, sentenceCounterMap.containsKey(occurence.SENTENCE) ? sentenceCounterMap.get(occurence.SENTENCE) + 1 : 1);

			{
				List<Occurence> occurenceListPerFile = null;
				if (occurenceListPerFileMap.containsKey(occurence.FILE)) {
					occurenceListPerFile = occurenceListPerFileMap.get(occurence.FILE);
				} else {
					occurenceListPerFile = new ArrayList<>();
					occurenceListPerFileMap.put(occurence.FILE, occurenceListPerFile);
				}
				occurenceListPerFile.add(occurence);
			}

			{
				Set<File> occurenceLocationList = null;
				if (sentenceLocationMap.containsKey(occurence.SENTENCE)) {
					occurenceLocationList = sentenceLocationMap.get(occurence.SENTENCE);
				} else {
					occurenceLocationList = new HashSet<>();
					sentenceLocationMap.put(occurence.SENTENCE, occurenceLocationList);
				}
				occurenceLocationList.add(occurence.FILE);

			}
		}

		// Pass 4 : report
		for (Occurence occurence : occurenceList) {
			LOGGER.info(MessageFormat.format("{0}{1,choice,0#?|1#-|2#+}:{2}", sentenceCounterMap.get(occurence.SENTENCE), sentenceLocationMap.get(occurence.SENTENCE).size(), occurence.toString()));
		}

		// Pass 5 : Alter source files : add comments
		if (COMMENT_SOURCE_FILES) {
			for (Entry<File, List<Occurence>> entry : occurenceListPerFileMap.entrySet()) {
				final Iterator<Occurence> occurenceIterator = entry.getValue().iterator();
				final List<String> contents = new ArrayList<>();
				try (final FileReader fr = new FileReader(entry.getKey()); BufferedReader reader = new BufferedReader(fr);) {
					String line = reader.readLine();
					Occurence occurence = occurenceIterator.next();
					int lineNumber = 1;
					while (line != null) {
						while (occurence != null && occurence.LINE == lineNumber) {
							line += MessageFormat.format(" // I18N:{0}{1,choice,0#?|1#-|2#+}", sentenceCounterMap.get(occurence.SENTENCE), sentenceLocationMap.get(occurence.SENTENCE).size());
							occurence = occurenceIterator.hasNext() ? occurenceIterator.next() : null;
						}
						contents.add(line);
						line = reader.readLine();
						lineNumber++;
					}
				}
				try (final FileWriter fw = new FileWriter(entry.getKey()); final PrintWriter writer = new PrintWriter(fw)) {
					for (String line : contents) {
						writer.println(line);
					}
				}
			}
		}
	}
}
