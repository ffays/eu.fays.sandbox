public class EchoSystemProperties {
	@SuppressWarnings({ "rawtypes", "nls" })
	public static void main(String[] args) {
		for (final java.util.Map.Entry e : System.getProperties().entrySet()) {
			System.out.println(e.getKey() + "=" + e.getValue());
		}
	}
}
