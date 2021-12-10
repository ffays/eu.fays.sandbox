import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.SortedSet;
import java.util.TreeSet;

public class LinuxListMacAddresses {

	public static void main(String[] args) throws Exception {
		final SortedSet<String> macAddresses = new TreeSet<>();
		final ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", "/usr/bin/head -q /sys/class/net/*/address");
		final Process process = pb.start();
		process.waitFor();
		try (final InputStream is = process.getInputStream(); final InputStreamReader isr = new InputStreamReader(is); final BufferedReader br = new BufferedReader(isr);) {
			String macAddress = br.readLine();
			while (macAddress != null) {
				if (!"00:00:00:00:00:00".equals(macAddress)) {
					macAddresses.add(macAddress);
				}
				macAddress = br.readLine();
			}
			if (!macAddresses.isEmpty()) {
				System.out.println(String.join(",", macAddresses));
			}
		}

		try (final InputStream is = process.getErrorStream(); final InputStreamReader isr = new InputStreamReader(is); final BufferedReader br = new BufferedReader(isr);) {
			String line = br.readLine();
			while (line != null) {
				System.err.println(line);
				line = br.readLine();
			}
		}

		System.exit(process.exitValue());
	}
}
