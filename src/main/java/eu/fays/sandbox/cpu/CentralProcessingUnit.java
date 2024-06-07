package eu.fays.sandbox.cpu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Central Processing Unit info
 */
@SuppressWarnings("nls")
public enum CentralProcessingUnit {

	/** Unknown  */
	UNKNOWN( 1, "Unknown"),
	/** Apple M3 Max */
	APPLE_M3_MAX ( 5095, "Apple M3 Max"),
	/** Intel i5-4460 */
	INTEL_I5_4460( 1856, "Intel(R) Core(TM) i5-4460 CPU @ 3.20GHz"),
	/** Intel Core i7-3770 */
	INTEL_I7_3770( 1700, "Intel(R) Core(TM) i7-3770 CPU @ 3.40GHz");

	/** 
	 * <a href="https://math.nist.gov/scimark2/">SciMark 2</a> composite score.<br>
	 * <br>
	 * <pre>
	 * curl https://math.nist.gov/scimark2/scimark2lib.jar -o scimark2lib.jar
	 * java -cp scimark2lib.jar jnt.scimark2.commandline
	 * <pre>
	 */
	public final int sciMark2Score;
	
	/** 
	 * Central Processing Unit name.<br>
	 * <br>
	 * Command line commands (1. Windows, 2. Mac OS, 3. Linux):
	 * <ol>
	 * <li>wmic cpu get name
	 * <li>sysctl -n machdep.cpu.brand_string
	 * <li>grep 'model name' /proc/cpuinfo | head -1 | cut -d ':' -f 2 | cut -c 2-
	 * </ol>
	 */
	public final String label;

	/**
	 * Returns the enumerated value matching the given label
	 * @param importFormatLabel the given format label
	 * @return the enumerated value matching the given label
	 */
	public static CentralProcessingUnit safeValueOfLabel(final String label) {
		final String l0 = label.replaceAll("\\s","");
		for(final CentralProcessingUnit cpu : values()) {
			final String l1 = cpu.label.replaceAll("\\s","");
			if(l0.equalsIgnoreCase(l1)) {
				return cpu;
			}
		}
		return UNKNOWN;
	}

	/**
	 * Returns the CPU model name
	 * @return the CPU model name
	 */
	public static String getCentralProcessingUnitName() {
		
		String cpuName = System.getProperty("cpu.name");
		
		if(cpuName == null) {
			final String osName = System.getProperty("os.name", "Unknown");
			
			try {
				if(osName.startsWith("Windows")) {
					final Process process = new ProcessBuilder("wmic", "cpu", "get", "name").start();
					process.waitFor();
					try (final InputStream is = process.getInputStream(); final InputStreamReader isr = new InputStreamReader(is); final BufferedReader br = new BufferedReader(isr);) {
						br.readLine();
						cpuName = br.readLine();
					}
				} else if (osName.startsWith("Linux")) {
					try (final FileReader fr = new FileReader(new File("/proc/cpuinfo")); final BufferedReader br = new BufferedReader(fr);) {
						String line;
						while((line = br.readLine()) != null) {
							if(line.startsWith("model name")) {
								final int o = line.indexOf(':');
								if(o != -1) {
									cpuName = line.substring(o+2);
									break;
								}
							}
						}
					}
				} else if (osName.startsWith("Mac OS X")) {
					final Process process = new ProcessBuilder("sysctl", "-n", "machdep.cpu.brand_string").start();
					process.waitFor();
					try (final InputStream is = process.getInputStream(); final InputStreamReader isr = new InputStreamReader(is); final BufferedReader br = new BufferedReader(isr);) {
						cpuName = br.readLine();
					}
				}
			} catch (final InterruptedException | IOException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		}
		
		if(cpuName != null) {
			System.setProperty("cpu.name", cpuName);
		}		

		return cpuName;
	}
	
	/**
	 * Constructor
	 * @param sciMark2Score <a href="https://math.nist.gov/scimark2/">SciMark 2</a> composite score
	 * @param label Central Processing Unit name
	 */
	private CentralProcessingUnit(final int sciMark2Score, final String label) {
		this.sciMark2Score = sciMark2Score;
		this.label = label;
	}
}
