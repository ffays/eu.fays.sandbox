package eu.fays.sandbox.interview;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 * <strong>Problem statement:</strong><br>
 * <br>
 * Provide a method to pivot the words from the given sentence in order to display them in columns.<br>
 * <br>
 * Input sentences:<br>
 * <br>
 * <table style="margin-left: 2em" summary="Input sentences">
 * <caption>&nbsp;</caption>
 * <tr>
 * <td>
 * This is the first sentence to print<br>
 * The second phrase<br>
 * Third one<br>
 * </td>
 * </tr>
 * </table>
 * <br>
 * Expected output:<br>
 * <br>
 * <table style="margin-left: 2em" summary="Expected output">
 * <caption>&nbsp;</caption>
 * <tr>
 * <td>This</td>
 * <td>The</td>
 * <td>Third</td>
 * </tr>
 * <tr>
 * <td>is</td>
 * <td>second</td>
 * <td>one</td>
 * </tr>
 * <tr>
 * <td>the</td>
 * <td>phrase</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>first</td>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>sentence</td>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>to</td>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>print</td>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * </tr>
 * </table>
 * 
 * @author Fr&eacute;d&eacute;ric Fays
 */
@SuppressWarnings("nls")
public class SentencePivoter {

	public static final String SENTENCE_1 = "This is the first sentence to print";
	public static final String SENTENCE_2 = "The second phrase";
	public static final String SENTENCE_3 = "Third one";
	public static final String[] SENTENCES = { SENTENCE_1, SENTENCE_2, SENTENCE_3 };

	public static void main(String[] args) {
		for (String sentence : pivot(SENTENCES)) {
			System.out.println(sentence);
		}
	}

	/**
	 * Pivots the givens sentences to appear in columns
	 * 
	 * @param sentences the given sentences
	 * @return the sentences pivoted in columns
	 */
	public static String[] pivot(final String[] sentences) {
		// pre-conditions
		assert sentences != null;
		//
		/** result : the sentences pivoted in columns */
		final List<String> result = new LinkedList<String>();

		/** sentences as a list of First In First Out queues of words */
		final List<Queue<String>> tokensAsListOfQueues = new LinkedList<Queue<String>>();
		for (final String sentence : sentences) {
			/** words for the current sentence */
			final String[] tokens = sentence.split(" ");			
			/** words of the current sentence as a First In First Out queue */
			final Queue<String> tokensAsQueue = new LinkedList<String>();
			tokensAsQueue.addAll(Arrays.asList(tokens));
			tokensAsListOfQueues.add(tokensAsQueue);
		}

		/** Tells if at least one word has been processed during the loop */
		boolean hasToken = true;
		while (hasToken) {
			hasToken = false;
			/** Builds the current pivoted sentence */
			final StringBuilder builder = new StringBuilder();
			for (final Queue<String> tokensAsQueue : tokensAsListOfQueues) {
				/** Current word to process */
				String token = "";
				if (!tokensAsQueue.isEmpty()) {
					hasToken = true;
					token = tokensAsQueue.poll();
				}
				builder.append(String.format("%1$-10s", token));
			}
			if (hasToken) {
				result.add(builder.toString());
			}
		}

		return result.toArray(new String[result.size()]);
	}

	public static String[] pivot2(final String[] sentences) {
		// pre-conditions
		assert sentences != null;
		//
		/** result : the sentences pivoted in columns */
		final List<String> result = new LinkedList<String>();

		/** a table of words of each sentence */
		final String[][] data = new String[sentences.length][];
		// Tokenization of the sentences 
		for (int i = 0; i < sentences.length; i++) {
			data[i] = sentences[i].split(" ");
		}

		/** n is the number of words of the longest sentence */
		final int n = Arrays.stream(data).reduce((tokens0, tokens1) -> tokens0.length > tokens1.length ? tokens0 : tokens1).orElse(new String[0]).length;

		// No java8? ... do the classic way.
		// int n = 0;
		// for (String[] tokens : data) n = Math.max(n, tokens.length);

		final TableModel model = new DefaultTableModel(data, new Object[n]);
		for (int columnIndex = 0; columnIndex < model.getColumnCount(); columnIndex++) {
			final StringBuilder builder = new StringBuilder();
			for (int rowIndex = 0; rowIndex < model.getRowCount(); rowIndex++) {
				final Object token = model.getValueAt(rowIndex, columnIndex);
				builder.append(String.format("%1$-10s", token != null ? token : ""));
			}
			result.add(builder.toString());
		}

		return result.toArray(new String[result.size()]);
	}

}
