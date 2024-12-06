import static java.text.MessageFormat.format;
import static java.util.Arrays.asList;
import static java.util.logging.Level.SEVERE;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;

import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

// -Durl=https://github.com -ea -Djava.util.logging.SimpleFormatter.format="%5$s%6$s%n"

// -Djava.util.logging.SimpleFormatter.format="%1$tFT%1$tT,%1$tL	%4$s	%3$s	%5$s%6$s%n"
@SuppressWarnings("nls")
public class DisplayWebCertificate {
	
	/** Standard logger */
	private static final Logger LOGGER = Logger.getLogger(DisplayWebCertificate.class.getName());

	public static void main(String[] args) throws MalformedURLException {
		final String url = System.getProperty("url");
		//
		assert url != null;
		assert !url.isEmpty();
		//
		final List<Map<String, String>> maps = certificatesInfo(new URL(url), LOGGER);
		boolean flag = false;
		for (final Map<String, String> map : maps) {
			if(flag) {
				LOGGER.info(range(0, 72).mapToObj(n -> "-").collect(joining()));
			}
			map.entrySet().forEach(e -> LOGGER.info(format("{0}={1}", e.getKey(), e.getValue())));
			flag = true;
		}
	}
	
	/**
	 * Obtain the certificates information
	 * @param url URL of the Web site
	 * @param logger logger, optional
	 * @return the certificates information
	 */
	final static List<Map<String, String>> certificatesInfo(final URL url, final Logger logger) {
		final List<Map<String, String>> result = new ArrayList<>();
		try {
			// Trust all
			final SSLContext sslContext = SSLContext.getInstance("TLS");
			final X509TrustManager x509TrustManager = new X509TrustManager() {
				@Override public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[] {}; }
				@Override public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException { }
				@Override public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException { }
			};
			final HostnameVerifier hostnameVerifier = new HostnameVerifier() {
				@Override public boolean verify(String hostname, SSLSession session) { return true; }
			};
			sslContext.init(null, new TrustManager[] { x509TrustManager }, new java.security.SecureRandom());
			final SSLSocketFactory sslFactory = sslContext.getSocketFactory();
			
			final HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
			connection.setConnectTimeout(120_000);
			connection.setSSLSocketFactory(sslFactory);
			connection.setHostnameVerifier(hostnameVerifier);
			connection.setRequestMethod("HEAD");
			connection.connect();
			final Certificate[] certs = connection.getServerCertificates();
			connection.disconnect();
			
			for (final Certificate certificate : certs) {
				if (certificate instanceof X509Certificate) {
					final Map<String, String> map = certificateInfo(certificate);
					result.add(map);
				}
			}
		} catch (final IOException | CertificateException | NoSuchAlgorithmException | KeyManagementException e) {
			if(logger != null) {
				logger.log(SEVERE, e.getMessage(), e);
			}
		}
		return result;
	}
			
	/**
	 * Obtain the certificate information
	 * @param certificate the certificate
	 * @return the certificate information
	 * @throws CertificateEncodingException in case of unexpected error
	 * @throws CertificateParsingException in case of unexpected error
	 * @throws NoSuchAlgorithmException in case of unexpected error
	 */
	final static Map<String, String> certificateInfo(final Certificate certificate) throws CertificateEncodingException, CertificateParsingException, NoSuchAlgorithmException {
		final Map<String, String> result = new LinkedHashMap<String, String>();
		final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss z");

		if (certificate instanceof X509Certificate) {
			final X509Certificate cert = (X509Certificate) certificate;

			final String subjectX500PrincipalName = cert.getSubjectX500Principal().getName("RFC1779");
			final String issuerX500PrincipalName = cert.getIssuerX500Principal().getName("RFC1779");
			final String[] principalNames = { subjectX500PrincipalName, issuerX500PrincipalName };

			// retrieving keys
			final String[] prefixes = { "Subject", "Issuer" };
			for (int i = 0; i < prefixes.length; i++) {
				final String[] pairs = principalNames[i].replaceAll("(\\\".*)(,)(.*\\\")", "$1\u00A0$3").replace('\"', '`').split(",");
				for (final String pair : pairs) {
					final String[] kv = pair.replace('`', '\"').replace('\u00A0', ',').split("=");
					if (kv.length == 2) {
						String key = kv[0].trim();
						if ("CN".equals(key)) {
							key = "Common Name";
						} else if ("OU".equals(key)) {
							key = "Organizational Unit";
						} else if ("O".equals(key)) {
							key = "Organization";
						} else if ("L".equals(key)) {
							key = "Location";
						} else if ("ST".equals(key)) {
							key = "State";
						} else if ("C".equals(key)) {
							key = "Country";
						}
						result.put(prefixes[i] + " " + key, kv[1]);
					}
				}
				if (i == 0 && cert.getSubjectAlternativeNames() != null) {
					final List<String> subjectAlternativeNames = new ArrayList<>();
					for (final List<?> altName : cert.getSubjectAlternativeNames()) {
						if (altName.size() >= 2 && Integer.valueOf(2 /* Cf. sun.security.x509.GeneralName */).equals(altName.get(0))) {
							subjectAlternativeNames.add(altName.get(1).toString());
						}
					}
					if(!subjectAlternativeNames.isEmpty()) {
						result.put("Subject Alternative Names", subjectAlternativeNames.stream().collect(Collectors.joining(", ")));
					}
				}
			}

			// exporting keys
			result.put("Version", Integer.toString(cert.getVersion()));
			result.put("Serial Number", cert.getSerialNumber().toString());
			result.put("Issued on", dateFormat.format(cert.getNotBefore()));
			result.put("Expires on", dateFormat.format(cert.getNotAfter()));
			
			// Checksums
			final String[] algorithms = {"MD5", "SHA1", "SHA256"};
			for(final String algorithm : algorithms){
				final byte[] e = cert.getEncoded();
				final MessageDigest md = MessageDigest.getInstance(algorithm);
				md.update(e, 0, e.length);
				final BigInteger bi = new BigInteger(1, md.digest());
				final String v = asList(String.format(format("%1$0{0,number,0}X", md.digest().length << 1), bi).split("(?<=\\G..)")).toString().replaceAll("[\\[\\] ]", "").replace(',', ':');
				result.put(algorithm + " Fingerprint",  v);
			}

			// modulus
			{
				final Matcher matcher = Pattern.compile("modulus: (\\p{XDigit}+)").matcher(cert.toString());
				if (matcher.find()) {
					final String modulus = matcher.group(1);
					result.put("Modulus", modulus);
				}
			}
		}
		
		return result;
	}
}
