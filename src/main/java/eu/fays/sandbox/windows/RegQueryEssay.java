package eu.fays.sandbox.windows;

import static java.lang.System.out;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.text.MessageFormat.format;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

/**
 * <a href="https://learn.microsoft.com/en-us/windows-server/administration/windows-commands/reg-query">reg query</a>
 */
public class RegQueryEssay {

	@SuppressWarnings("nls")
	public static void main(String[] args) {
		// @formatter:off
		final List<Entry<String, String>> list = List.of(
			new SimpleEntry<>("HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\User Shell Folders", "{374DE290-123F-4565-9164-39C4925E467B}"),
			new SimpleEntry<>("HKCU\\Control Panel\\International", "sDecimal"),
			new SimpleEntry<>("HKCU\\Control Panel\\International", "sList"),
			new SimpleEntry<>("HKLM\\SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion", "ProductName"),
			new SimpleEntry<>("HKLM\\SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion", "DigitalProductId"),
			new SimpleEntry<>("HKLM\\SYSTEM\\CurrentControlSet\\Control\\FileSystem", "LongPathsEnabled")
		);
		// @formatter:on
		
		list.forEach(e -> out.println(format("{0}\\{1}={2}", e.getKey(), e.getValue(), regQuery(e.getKey(), e.getValue()))));
	}
	
	/**
	 * <a href="https://learn.microsoft.com/en-us/windows-server/administration/windows-commands/reg-query">reg query</a>
	 * @param keyname Specifies the full path of the subkey
	 * @param valuename Specifies the registry value name that is to be queried
	 * @return the registry value
	 */
	@SuppressWarnings("nls")
	public static String regQuery(final String keyname, final String valuename) {
		String result = null;

		try {
			final String[] command = {"reg", "query", keyname, "/v", valuename};
			final Process process = new ProcessBuilder().command(command).start();
			process.waitFor(5L, TimeUnit.SECONDS);

			String lastLine = null;
			try (final InputStream is = process.getInputStream(); final InputStreamReader isr = new InputStreamReader(is, UTF_8); final BufferedReader reader = new BufferedReader(isr)) {
				for (String l = reader.readLine(); l != null; l = reader.readLine()) {
					if(!l.isEmpty()) {
						lastLine = l;
					}
				}
			}
			
			if(lastLine != null) {
				final String[] registryTypes = { "REG_SZ", "REG_MULTI_SZ", "REG_EXPAND_SZ", "REG_DWORD", "REG_BINARY", "REG_NONE"};
				for(final String registryType : registryTypes) {
					final int o = lastLine.indexOf(registryType);
					if(o != -1) {
						result =  lastLine.substring(o+registryType.length()).stripLeading();
						break;
					}
				}
			}
		} catch (final IOException | InterruptedException e) {
			throw new RuntimeException(e.getMessage(), e);
		}

		return result;
	}
}
