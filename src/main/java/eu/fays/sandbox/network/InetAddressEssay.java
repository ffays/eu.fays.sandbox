package eu.fays.sandbox.network;

import static java.util.Arrays.asList;
import static java.util.Spliterator.ORDERED;
import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toCollection;
import static java.text.MessageFormat.format;

import java.net.InetAddress;
import java.util.ArrayDeque;
import java.util.stream.StreamSupport;

/**
 * Fully Qualified Domain Name lookup using Operating System resolver.<br>
 * <br>
 * <table>
 * <tr><th>Windows</th><td><code>[System.Net.Dns]::GetHostAddresses('example.com').IPAddressToString</code></td></tr>
 * <tr><th>Mac OS</th><td><code>dscacheutil -q host -a name example.com</code></td></tr>
 * <tr><th>Linux</th><td><code>getent hosts example.com</code></td></tr>
 * </table>
 * <br>
 * Implied:
 * <ul>
 * <li><code>-Djdk.net.hosts.file=/etc/hosts</code>
 * <li><code>-Djdk.net.hosts.file=C:\Windows\System32\drivers\etc\hosts</code>
 * </ul>
 */
@SuppressWarnings("nls")
public class InetAddressEssay {

	public static void main(String[] args) throws Exception {
		// @formatter:off
		// reverse the order of the words in the package name
		final String host = StreamSupport.stream(spliteratorUnknownSize(asList(InetAddressEssay.class.getPackageName().split("\\.")).stream().limit(2).collect(toCollection(() -> new ArrayDeque<String>())).descendingIterator(), ORDERED), false).collect(joining(".")); 
		// @formatter:on
		
		for (InetAddress inetAddress : InetAddress.getAllByName(host)) {
			final String hostAddress = inetAddress.getHostAddress();
			System.out.println(format("{0}\t{1}", hostAddress, host));
		}
	}
}
