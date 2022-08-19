package eu.fays.sandbox.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * UDP Multicast publisher essay<br>
 * Cf. <a href="https://www.baeldung.com/java-broadcast-multicast">Broadcasting and Multicasting in Java</a>
 */
public class MulticastPublisherEssay {

	/**
	 * @param args unused
	 * @throws IOException in case of unexpected error
	 */
	public static void main(String[] args) throws IOException {
		multicast("Hello world!");
		multicast("Doctor Livingstone I presume?");
		multicast("exit");
	}

	public static void multicast(final String multicastMessage) throws IOException {
		final DatagramSocket socket = new DatagramSocket();
		final InetAddress group = InetAddress.getByName("230.0.0.0");
		final byte[] buffer = multicastMessage.getBytes();

		final DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, 4446);
		socket.send(packet);
		socket.close();
	}
}
