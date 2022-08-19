package eu.fays.sandbox.network;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * UDP Multicast receiver essay<br>
 * Cf. <a href="https://www.baeldung.com/java-broadcast-multicast">Broadcasting and Multicasting in Java</a>
 */
public class MulticastReceiverEssay extends Thread implements UncaughtExceptionHandler {

	/** Standard logger */
	private static final Logger LOGGER = Logger.getLogger(MulticastReceiverEssay.class.getName());
	
	/**
	 * VM Args
	 * <pre>-ea -Djava.util.logging.SimpleFormatter.format="%5$s%6$s%n"</pre>
	 * @param args unused
	 */
	public static void main(String[] args) {
		final MulticastReceiverEssay receiver = new MulticastReceiverEssay();
		receiver.start();
	}
	
	public MulticastReceiverEssay() {
		setName(MulticastReceiverEssay.class.getSimpleName());
		setUncaughtExceptionHandler(this);
	}

	public void run() {
		try {
			final MulticastSocket socket = new MulticastSocket(4446);
			final byte[] buffer = new byte[256];
			final InetAddress group = InetAddress.getByName("230.0.0.0");
			socket.joinGroup(group);
			while (!isInterrupted()) {
				final DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
				final String received = new String(packet.getData(), 0, packet.getLength());
				if ("exit".equals(received)) {
					break;
				} else {
					LOGGER.info(received);
				}
			}
			socket.leaveGroup(group);
			socket.close();
		} catch (final IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	/**
	 * @see java.lang.Thread.UncaughtExceptionHandler#uncaughtException(java.lang.Thread, java.lang.Throwable)
	 */
	@Override
	public void uncaughtException(final Thread t, final Throwable e) {
		LOGGER.log(Level.SEVERE, e.getMessage(), e);
	}
}
