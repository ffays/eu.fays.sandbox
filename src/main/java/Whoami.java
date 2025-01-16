// java -cp . -Djava.util.logging.config.file=jul.properties Whoami

/* jul.properties

handlers=java.util.logging.ConsoleHandler
.level=FINE
java.util.logging.ConsoleHandler.level=FINE
java.util.logging.SimpleFormatter.format=%1$tF %1$tT\t%4$s\t%3$s\t%5$s%6$s%n

*/

public class Whoami {
	public static void main(String[] args) throws Exception {
		ProcessBuilder processBuilder = new ProcessBuilder("whoami");
		Process process = processBuilder.start();
		java.io.InputStream in = process.getInputStream();
		java.io.ByteArrayOutputStream os = new java.io.ByteArrayOutputStream();
		for (int c = in.read(); c != -1; c = in.read()) os.write(c);
		java.util.logging.Logger logger = java.util.logging.Logger.getLogger("com.example.whoami");
		logger.fine(os.toString());
	}
}
