import static java.net.NetworkInterface.getNetworkInterfaces;

import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Enumeration;
import java.util.List;

public class LocalIpAddress {

	public static void main(String[] args) throws SocketException {
		System.out.println(localIpAdress().getHostAddress());
	}
	
	public static InetAddress localIpAdress() {
		InetAddress result = null;
		try (final DatagramSocket socket = new DatagramSocket()) {
//		 socket.connect(InetAddress.getByName("1.1.1.1"), 53);
		 socket.connect(InetAddress.getByName("google.com"), 443);
		 result = socket.getLocalAddress();
		} catch (final SocketException | UnknownHostException e) {
			// Do nothing
		}

		// fallback
		if (result == null) {
			try {
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
										result = inetAddress;
										break; // consider only the 1st one
									}
								}
							}
						}
					}
				}
			} catch (final SocketException e) {
				// Do nothing
			}
		}

		return result;
	}
}
