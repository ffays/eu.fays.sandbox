package eu.fays.sandbox.iterators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * Simple exercise on iterators
 * 
 * @author Fr&eacute;d&eacute;ric Fays
 */
public class IteratorEssay {

	/**
	 * Main<br>
	 * <br>
	 * VM args :
	 * 
	 * <pre>
	 * -ea -Djava.util.logging.config.file=logging.properties
	 * </pre>
	 * 
	 * @param args unused
	 */
	public static void main(String[] args) {
		final List<Fruit> list = new ArrayList<>();
		list.addAll(Arrays.asList(Fruit.values()));
		final Iterator<Fruit> iterator = list.iterator();
		while (iterator.hasNext()) {
			Fruit fruit = iterator.next();
			if (fruit == Fruit.APPLE) {
				iterator.remove();
			}
		}

		for (Fruit fruit : list) {
			LOGGER.info(fruit.name());
		}
	}

	/** Standard logger */
	private static final Logger LOGGER = Logger.getLogger(IteratorEssay.class.getName());
}
