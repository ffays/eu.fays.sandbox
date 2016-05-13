package eu.fays.sandbox.format;

import static java.text.MessageFormat.format;

import java.math.BigDecimal;
import java.text.MessageFormat;
import static java.util.Locale.FRANCE;

import java.util.Calendar;
import java.util.logging.Logger;

/**
 * A cheat sheet for {@link MessageFormat#format(String, Object[])}
 * @author Fr&eacute;d&eacute;ric Fays
 */
@SuppressWarnings("nls")
public class FormatEssay {

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
		LOGGER.info(format("Double   : {0,number,.00} (2 decimal places)", Math.PI));
		LOGGER.info((new MessageFormat("Currency : {0,number,\u00A4.00} (2 decimal places)", FRANCE)).format(new Object[] { new BigDecimal("9.9") }));

		for (int i : new int[] { 1, 12, 123 }) {
			// c.f. http://javarevisited.blogspot.be/2013/02/add-leading-zeros-to-integers-Java-String-left-padding-example-program.html
			LOGGER.info(format("Integer  : ''{0}'' / ''{1}'' (left-padded / right-padded on 3 places)", String.format("%3d", i), String.format("%-3d", i)));
		}

		for (int i = 0; i <= 3; i++) {
			LOGGER.info(format("Choice #{0}: {0,choice,0#none|1#one|2#several}", i));
		}

		LOGGER.info(format("Timestamp: {0,date,yyyy-MM-dd'T'HH:mm:ss.SSSXXX}", Calendar.getInstance().getTime()));
		LOGGER.info(format("Date     : {0,date,yyyy-MM-dd}", Calendar.getInstance().getTime()));
		LOGGER.info(format("Time     : {0,date,HH:mm:ss}", Calendar.getInstance().getTime()));
		LOGGER.info(format("Date #2  : {0,date,dd-MMM-yyyy}", Calendar.getInstance().getTime()));
		LOGGER.info(format("Integer#2:{0,number,00}", -1));
		LOGGER.info(format("Integer#3:{0,number,00}", 1));
		LOGGER.info(String.format("Integer#4:%02d", -1));
		LOGGER.info(String.format("Integer#5:%02d", 1));
		LOGGER.info(format("Integer#6:''{0,number,#;(#)}''", -1));
		LOGGER.info(format("Integer#7:''{0,number,#;(#)}''", 1));
	}

	/** Standard logger */
	private static final Logger LOGGER = Logger.getLogger(FormatEssay.class.getName());
}
