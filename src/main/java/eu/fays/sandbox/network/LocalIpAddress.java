package eu.fays.sandbox.network;

import java.net.DatagramSocket;
import java.net.InetAddress;

public class LocalIpAddress {
	public static void main(String[] args) throws Exception {
		try (final DatagramSocket socket = new DatagramSocket()) {
//			 socket.connect(InetAddress.getByName("1.1.1.1"), 53);
			 socket.connect(InetAddress.getByName("google.com"), 443);
			 InetAddress localAddress = socket.getLocalAddress();
			 System.out.println(localAddress.getHostAddress());
		}
	}
}
