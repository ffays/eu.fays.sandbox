/**
 * Print System Properties
 */
public class PrintSystemProperties {

	/**
	 * Print System Properties
	 * @param args unused
	 */
	public static void main(String[] args) {
		System.getProperties().keySet().stream().map(k -> (String) k).sorted().forEach(k -> System.out.println(k+"="+System.getProperty(k)));
	}
}
