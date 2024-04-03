import java.util.logging.Logger;

public class Int2Char {
	// -Dlogger=true
	// 10629 10630 10
	public static void main(String[] args) {
		if(Boolean.parseBoolean(System.getProperty("logger"))) {
			final Logger logger = Logger.getLogger(Int2Char.class.getName());
			final StringBuilder builder = new StringBuilder();
			for(String arg: args) builder.append((char) Integer.parseInt(arg));
			logger.info(builder.toString());
		} else {
			for(String arg: args) System.out.print((char) Integer.parseInt(arg));
			System.out.flush();
		}
	}
}
