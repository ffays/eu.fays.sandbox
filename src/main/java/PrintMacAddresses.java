
import static java.net.NetworkInterface.getNetworkInterfaces;
import static java.nio.ByteBuffer.wrap;
import static java.util.Collections.list;
import static java.util.stream.Collectors.toCollection;

import java.util.TreeSet;


public class PrintMacAddresses {

	@SuppressWarnings("nls")
	public static void main(String[] args) throws Exception {
		// @formatter:off
		list(getNetworkInterfaces())
			.stream()
			.map(ni -> {try { return ni != null?ni.getHardwareAddress():null; } catch (Exception e) { return null; }} )
			.filter(ha -> ha != null)
			.map(ha -> {byte[] ha8 = new byte[8]; System.arraycopy(ha, 0, ha8, 2, ha.length); return ha8; })
			.map(ha -> wrap(ha).getLong())
			.collect(toCollection(TreeSet::new)).stream()
			.map(Long::toHexString)
			.forEach(s -> System.out.println("000000000000".substring(s.length()) + s.toUpperCase()));
//			.forEach(s -> System.out.println(s));
		// @formatter:on
		
		// StreamSupport.stream(Spliterators.spliteratorUnknownSize(NetworkInterface.getNetworkInterfaces().asIterator(), Spliterator.ORDERED), false);
			
//		var networkInterfaces = getNetworkInterfaces();
//		while(networkInterfaces.hasMoreElements()) {
//			final byte[] hardwareAddress = networkInterfaces.nextElement().getHardwareAddress();
//			if(hardwareAddress == null) {
//				continue;
//			}
//			final StringBuilder sb = new StringBuilder();
//			for (int i = 0; i < hardwareAddress.length; i++) {
//				sb.append(String.format("%02X", hardwareAddress[i]));
//			}
//			System.out.println(sb.toString());
//		}
	}
}
