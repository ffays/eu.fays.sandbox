import static java.net.NetworkInterface.getNetworkInterfaces;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Enumeration;
import java.util.List;

public class LocalIpAddress {

	public static void main(String[] args) throws SocketException {
		final Enumeration<NetworkInterface> e = getNetworkInterfaces();
		while (e.hasMoreElements()) {
			final NetworkInterface networkInterface = e.nextElement();
			if (networkInterface.isUp() && !networkInterface.isLoopback() && !networkInterface.isVirtual()) {
				final List<InterfaceAddress> interfaceAddresses = networkInterface.getInterfaceAddresses();
				for (final InterfaceAddress interfaceAddress : interfaceAddresses) {
					final InetAddress broadcastAddress = interfaceAddress.getBroadcast();
					if (broadcastAddress != null) {
						final InetAddress inetAddress = interfaceAddress.getAddress();
						if (inetAddress.isSiteLocalAddress() && !inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
							final boolean isNetworkAddress;
							if (inetAddress instanceof Inet4Address) {
								final int ip = ByteBuffer.wrap(inetAddress.getAddress()).getInt();
								final int prefixLength = interfaceAddress.getNetworkPrefixLength();
								final int mask = (prefixLength == 0) ? 0 : 0xFFFFFFFF << (32 - prefixLength);
								isNetworkAddress = (ip & ~mask) == 0;
							} else {
								final long ip = ByteBuffer.wrap(inetAddress.getAddress()).getLong();
								final long prefixLength = interfaceAddress.getNetworkPrefixLength();
								final long mask = (prefixLength == 0) ? 0 : 0xFFFFFFFFFFFFFFFFL << (64L - prefixLength);
								isNetworkAddress = (ip & ~mask) == 0;
							}
							if (!isNetworkAddress) {
								System.out.println(inetAddress.getHostAddress());

							}
						}
					}
				}
			}
		}
	}
}
