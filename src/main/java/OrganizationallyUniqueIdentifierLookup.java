import static java.lang.System.arraycopy;
import static java.net.NetworkInterface.getNetworkInterfaces;
import static java.nio.ByteBuffer.wrap;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.Enumeration;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;

public class OrganizationallyUniqueIdentifierLookup {

	@SuppressWarnings({ "nls" })
	public static void main(String[] args) throws Exception {
		// Wireshark manufacturer database
		final URL url = new URL("https://www.wireshark.org/download/automated/data/manuf.gz");
		final HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
		final TreeMap<Integer,String> lookup = new TreeMap<>();
		try(final InputStream is = connection.getInputStream(); final GZIPInputStream gzis = new GZIPInputStream(is); final InputStreamReader isr = new InputStreamReader(gzis, UTF_8); final BufferedReader reader = new BufferedReader(isr)) {
			for (String l = reader.readLine(); l != null; l = reader.readLine()) {
				if(!l.isEmpty() && l.charAt(0) != '#') {
					int oui = 0;
					for(int i=0; i<6; i++) {
						char c = l.charAt(7 - i - (i>>1));
						long v = c & 0xF;
						if((c & 0x40) > 0) {
							v += 9;
						}
						oui += v << (i << 2);
					}
					final String manufacturer = l.substring(l.lastIndexOf('\t')+1);
					lookup.put(oui, manufacturer);
				}
			}
		}

		final Enumeration<NetworkInterface> e = getNetworkInterfaces();
		final TreeSet<Long> macs = new TreeSet<>();
		while(e.hasMoreElements()) {
			final NetworkInterface ni = e.nextElement();
			final byte[] ha = ni.getHardwareAddress();
			if(ha != null) {
				final byte[] ha8 = new byte[8];
				arraycopy(ha, 0, ha8, 2, ha.length);
				final Long mac = wrap(ha8).getLong();
				macs.add(mac);
			}
		}
		
		for (final Long mac : macs) {
			final String s = Long.toHexString(mac);
			System.out.print("000000000000".substring(s.length()) + s.toUpperCase());
			final int oui = (int)(mac >> 24);
			if(lookup.containsKey(oui)) {
				System.out.print('\t');				
				System.out.print(lookup.get(oui));
			}
			System.out.println();			
		}
		System.out.flush();
	}
}
