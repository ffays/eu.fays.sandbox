import java.io.IOException;

public class PrintManifestAttribute {
	public static void main(String[] args) throws IOException {
		new java.util.jar.Manifest(System.in).getMainAttributes().getValue(System.getProperty("attribute"));
	}
}
