package eu.fays.sandbox.windows;

import static java.text.MessageFormat.format;
import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Using WMIC, identify the serial number of the physical disk hosting the boot partition.
 * @author Frederic Fays
 */
@SuppressWarnings("nls")
public class IdentifyBootDiskDriveSerialNumber {

	public static void main(String[] args) throws Exception {
		final String bootPartitionID = getBootPartitionID();
		final SortedMap<String, String> diskMap = getDiskDriveIdAndSerialNumber();
		final String diskDriveSerial = getBootDiskDriveSerial(diskMap, bootPartitionID);

		System.out.println("Boot Partition ID: " + bootPartitionID);
		diskMap.entrySet().forEach(e -> System.out.println(e.getKey() + ":" + e.getValue()));
		System.out.println("Boot Disk Serial: " + diskDriveSerial);
	}

	/**
	 * Execute the given WMIC command and returns its output lines.
	 * @param command the DOS command to be executed
	 * @return the output lines.
	 * @throws IOException in case of unexpected error
	 * @throws InterruptedException in case of unexpected error
	 */
	public static List<String> executeWMIC(final String... command) throws IOException, InterruptedException {
		//
		// There is a little catch with the WMIC command:
		//
		// It seems that the fine Microsoft software engineer that has programmed this command
		// had added a "feature" such as an unexpected "extra" carriage return character is
		// present at every line ending, making all line endings being composed of the sequence
		// "CR CR LF".
		//
		// On the other hand, the very correct implementation of the BufferedReader.readLine() java method states
		// the following: "Reads a line of text. A line is considered to be terminated by any one of a line feed ('\n'),
		// a carriage return ('\r'), or a carriage return followed immediately by a linefeed."
		// So, the readLine() command is considering the "extra" carriage return as valid a line termination,
		// resulting in a extra empty blank line for every WMIC command output lines.
		//
		// Therefore, as a workaround, specifically for the WMIC command, I had to use the Scanner class in this implementation.
		//

		//
		// There is two alternative potential workaround implementations:
		// 1. Using the process builder object, Redirect the output in a temporary file and then read content of the temporary file.
		// 2. Implement a custom InputStream class that will leverage on PushbackInputStream to skip the "extra" carriage return.
		//

		//
		assert command != null;
		assert command.length > 0;
		assert "wmic".equalsIgnoreCase(command[0]);
		//

		final List<String> result = new ArrayList<>();
		final Process process = (new ProcessBuilder(command)).start();
		if (process.waitFor() == 0) {
			try (final InputStream in = process.getInputStream(); final Scanner scanner = new Scanner(in)) {
				scanner.useDelimiter("\r\r\n");
				while (scanner.hasNext()) {
					final String line = scanner.next().replaceAll("\\s+$", ""); // right trim
					if (!line.isEmpty()) {
						result.add(line);
					}
				}
			}
		}
		return Collections.unmodifiableList(result);
	}

	/**
	 * Using the WMIC command, returns the boot partition identifier
	 * @return the boot partition identifier
	 * @throws IOException in case of unexpected error
	 * @throws InterruptedException in case of unexpected error
	 */
	public static String getBootPartitionID() throws IOException, InterruptedException {
		final String[] command = { "wmic", "partition", "where", "BootPartition=TRUE", "get", "DeviceID" };
		final List<String> list = executeWMIC(command);
		assert list.size() == 2;
		final String result = list.get(1);
		//
		assert result != null;
		assert result.startsWith("Disk");
		//
		return result;
	}

	/**
	 * Using the WMIC command, returns the disk drive identifiers along with their serial numbers.
	 * @return a dictionary, the keys being the disk identifiers and the values being the disk serial numbers.
	 * @throws IOException in case of unexpected error
	 * @throws InterruptedException in case of unexpected error
	 */
	public static final SortedMap<String, String> getDiskDriveIdAndSerialNumber() throws IOException, InterruptedException {
		final String[] command = { "wmic", "diskdrive", "get", "DeviceID,SerialNumber" };
		final List<String> list = executeWMIC(command);
		assert list.size() > 1;
		final SortedMap<String, String> result = new TreeMap<>(list.stream().map(l -> new SimpleEntry<String, String>(l.split(" +")[0], l.split(" +")[1])).collect(toMap(Entry::getKey, Entry::getValue)));
		result.remove("DeviceID");
		return result;
	}

	/**
	 * Using the WMIC command, and given both the dictionary of disk drive (key: disk drive identifier, value: disk drive serial number) and the boot partition id, returns the serial number of the disk drive hosting the
	 * boot partition
	 * @param diskMap the dictionary of disk drive (key: disk drive identifier, value: disk drive serial number)
	 * @param bootPartitionID the boot partition identifier
	 * @return the disk drive serial number hosting the boot partition.
	 * @throws IOException in case of unexpected error
	 * @throws InterruptedException in case of unexpected error
	 */
	public static final String getBootDiskDriveSerial(final SortedMap<String, String> diskMap, final String bootPartitionID) throws IOException, InterruptedException {
		String result = null;
		for (Entry<String, String> entry : diskMap.entrySet()) {
			final String[] command = { "wmic", "diskdrive", "where", format("DeviceID=''{0}''", entry.getKey().replace("\\", "\\\\")), "assoc", "/assocclass:Win32_DiskDriveToDiskPartition" };
			final List<String> list = executeWMIC(command);
			final String query = format("Win32_DiskPartition.DeviceID=\"{0}\"", bootPartitionID);
			final boolean bootDisk = list.stream().map(l -> l.indexOf(query) != -1).reduce(false, Boolean::logicalOr);
			if (bootDisk) {
				result = entry.getValue();
				break;
			}
		}
		//
		assert result != null;
		assert !result.isEmpty();
		//
		return result;
	}
}
