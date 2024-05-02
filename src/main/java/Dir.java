import java.io.File;
import java.io.IOException;

public class Dir {

	public static void main(String[] args) throws IOException {
		for(String arg : args) {
			System.out.println(arg);
		}
		File dir = new File(".");
		if(dir.isDirectory()) {
			for(File f : dir.listFiles()) {
				System.out.println(f.getCanonicalPath());
			}
		}
	}
}
