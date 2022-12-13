public class PrintManifestAttribute {
	public static void main(String[] args) throws Exception {
		 System.out.println(new java.util.jar.Manifest(System.in).getMainAttributes().getValue(System.getProperty("attribute")));
	}
}
