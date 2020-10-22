import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class ExportWebCertificate implements HostnameVerifier, X509TrustManager {

	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.out.println("Usage: " + ExportWebCertificate.class.getSimpleName() + " <url> <zip>");
			return;
		}
		final URL url = new URL(args[0]);
		System.out.println(url);
		final File file = new File(args[1]);
		final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss z");

		// Trust all
		final SSLContext sslContext = SSLContext.getInstance("TLS");
		final ExportWebCertificate singleton = new ExportWebCertificate();
		final X509TrustManager x509TrustManager = singleton;
		final HostnameVerifier hostnameVerifier = singleton;
		sslContext.init(null, new TrustManager[] { x509TrustManager }, new java.security.SecureRandom());
		final SSLSocketFactory sslFactory = sslContext.getSocketFactory();

		// Global Trust all
		// HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
		// HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);

		try (final FileOutputStream fos = new FileOutputStream(file); final ZipOutputStream zos = new ZipOutputStream(fos);) {

			// Prepare the connection
			final HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
			connection.setSSLSocketFactory(sslFactory);
			connection.setHostnameVerifier(hostnameVerifier);
			connection.setRequestMethod("HEAD");
			connection.connect();

			final Certificate[] certs = connection.getServerCertificates();
			connection.disconnect();

			final LinkedHashMap<String, Map<String, String>> infoMapMap = new LinkedHashMap<String, Map<String, String>>();
			final LinkedHashMap<String, X509Certificate> certMap = new LinkedHashMap<String, X509Certificate>();
			final String[] prefixes = { "Subject", "Issuer" };
			for (final Certificate certificate : certs) {
				if (certificate instanceof X509Certificate) {
					final X509Certificate cert = (X509Certificate) certificate;
					final String md5 = buildMD5(cert.getEncoded());

					final String subjectX500PrincipalName = cert.getSubjectX500Principal().getName("RFC1779");
					final String issuerX500PrincipalName = cert.getIssuerX500Principal().getName("RFC1779");
					final String[] principalNames = { subjectX500PrincipalName, issuerX500PrincipalName };

					// retrieving keys
					final Map<String, String> infoMap = new LinkedHashMap<String, String>();
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
								infoMap.put(prefixes[i] + " " + key, kv[1]);
							}
						}
					}

					// exporting keys
					infoMap.put("Version", Integer.toString(cert.getVersion()));
					infoMap.put("Serial Number", cert.getSerialNumber().toString());
					infoMap.put("Issued on", dateFormat.format(cert.getNotBefore()));
					infoMap.put("Expires on", dateFormat.format(cert.getNotAfter()));
					infoMap.put("SHA1 Fingerprint", hexToHexWithColon(buildSHA1(cert.getEncoded())));
					infoMap.put("MD5 Fingerprint", hexToHexWithColon(md5));

					// modulus
					{
						final Matcher matcher = Pattern.compile("modulus: (\\p{XDigit}+)").matcher(cert.toString());
						if (matcher.find()) {
							final String modulus = matcher.group(1);
							infoMap.put("Modulus", modulus);
						}
					}

					infoMapMap.put(md5, infoMap);
					certMap.put(md5, cert);
				}
			}
			assert infoMapMap != null;

			// Archive
			for (final String md5 : certMap.keySet()) {
				final X509Certificate cert = certMap.get(md5);
				final Map<String, String> infoMap = infoMapMap.get(md5);
				final String basename = infoMap.containsKey("Subject Common Name") ? substitueNTFSReservedCharacters(infoMap.get("Subject Common Name")) : md5;

				// the certificate
				{
					String text = exportCertificate(cert);
					final ZipEntry zipEntry = new ZipEntry(basename + ".cer");
					zipEntry.setSize(text.getBytes().length);
					zos.putNextEntry(zipEntry);
					zos.write(text.getBytes());
					zos.closeEntry();
				}

				// the description
				{
					final StringBuilder text = new StringBuilder();
					for (Map.Entry<String, String> entry : infoMap.entrySet()) {
						final String line = MessageFormat.format("{0}: {1}{2}", entry.getKey(), entry.getValue(), System.lineSeparator());
						text.append(line);
					}

					final ZipEntry zipEntry = new ZipEntry(basename + ".txt");
					zipEntry.setSize(text.toString().getBytes().length);
					zos.putNextEntry(zipEntry);
					zos.write(text.toString().getBytes());
					zos.closeEntry();
				}
			}

			zos.finish();
			zos.flush();
		}
		System.out.println(file);
	}

	/**
	 * Exports the certificate
	 * @param cert the certificate
	 * @return the certificate
	 * @throws CertificateEncodingException
	 */
	protected static String exportCertificate(final X509Certificate cert) throws CertificateEncodingException {
		//
		assert cert != null;
		//
		final StringBuilder result = new StringBuilder();

		result.append("-----BEGIN CERTIFICATE-----");
		result.append(System.lineSeparator());
		result.append(new String(Base64.getEncoder().encode(cert.getEncoded())));
		result.append("-----END CERTIFICATE-----");
		result.append(System.lineSeparator());

		//
		assert result != null;
		assert result.length() > 0;
		//
		return result.toString();
	}

	/**
	 * Produce the MD5 digest from the given data
	 * @param data the data from which the checksum has to be computed
	 * @return a MD5
	 */
	protected static String buildMD5(final byte[] data) throws NoSuchAlgorithmException {
		//
		assert data != null;
		assert data.length > 0;
		//

		final String result;
		final MessageDigest m = MessageDigest.getInstance("MD5");
		m.update(data, 0, data.length);
		final BigInteger i = new BigInteger(1, m.digest());
		result = String.format("%1$032X", i);
		//
		assert result != null;
		//
		return result;
	}

	/**
	 * Produce the SHA-1 digest from the given data
	 * @param data the data from which the checksum has to be computed
	 * @return a SHA-1
	 */
	protected static String buildSHA1(byte[] data) throws NoSuchAlgorithmException {
		//
		assert data != null;
		assert data.length > 0;
		//
		final String result;
		final MessageDigest m = MessageDigest.getInstance("SHA1");
		m.update(data, 0, data.length);
		final BigInteger i = new BigInteger(1, m.digest());
		result = String.format("%1$040X", i);
		//
		assert result != null;
		//
		return result;
	}

	/**
	 * Separate each hexadecimal digit pair with a colon sign
	 * @param data the hexadecimal digits
	 * @return a string of hexadecimal digit pairs separated with a colon sign
	 */
	protected static String hexToHexWithColon(final String data) {
		//
		assert data != null;
		assert !data.isEmpty();
		//
		return Arrays.asList(data.split("(?<=\\G..)")).toString().replaceAll("[\\[\\] ]", "").replace(',', ':');
	}

	/**
	 * Replace the NTFS reserved characters of the given input string as follow:
	 * <ul>
	 * <li>'"' to '¨' - DIAERESIS
	 * <li>'*' to '¤' - CURRENCY SIGN
	 * <li>'/' to 'ø' - LATIN SMALL LETTER O WITH STROKE
	 * <li>':' to '÷' - DIVISION SIGN
	 * <li>'<' to '«' - LEFT-POINTING DOUBLE ANGLE QUOTATION MARK
	 * <li>'>' to '»' - RIGHT-POINTING DOUBLE ANGLE QUOTATION MARK
	 * <li>'?' to '¿' - INVERTED QUESTION MARK
	 * <li>'\' to 'ÿ' - LATIN SMALL LETTER Y WITH DIAERESIS
	 * <li>'|' to '¦' - BROKEN BAR
	 * <li>All control characters, i.e. ASCII values below 32, to ' ' - NON-BREAKING SPACE
	 * </ul>
	 * @param input the input string
	 * @return the input string having the NTFS reserved characters replaced
	 */
	protected static String substitueNTFSReservedCharacters(final String input) {
		//
		assert input != null;
		//
		String result = input;

		final Pattern reservedCharactersPattern = Pattern.compile("[\\x00-\\x1F\\x22\\x2A\\x3A\\x3C\\x3E\\x3F\\x5C\\x7C]");

		if (reservedCharactersPattern.matcher(input).find()) {
			result = result.replace('"', (char) 0xa8 /* DIAERESIS */);
			result = result.replace('*', (char) 0xa4 /* CURRENCY SIGN */);
			result = result.replace('/', (char) 0xf8 /* LATIN SMALL LETTER O WITH STROKE */);
			result = result.replace(':', (char) 0xf7 /* DIVISION SIGN */);
			result = result.replace('<', (char) 0xab /* LEFT-POINTING DOUBLE ANGLE QUOTATION MARK */);
			result = result.replace('>', (char) 0xbb /* RIGHT-POINTING DOUBLE ANGLE QUOTATION MARK */);
			result = result.replace('?', (char) 0xbf /* INVERTED QUESTION MARK */);
			result = result.replace('\\', (char) 0xff /* LATIN SMALL LETTER Y WITH DIAERESIS */);
			result = result.replace('|', (char) 0xa6 /* BROKEN BAR */);
			if (reservedCharactersPattern.matcher(input).find()) {
				// This can happen only if there are control characters, e.g '\t', '\f' ... this case is unlikely
				result = reservedCharactersPattern.matcher(result).replaceAll("\u00A0");
			}
		}

		//
		assert !reservedCharactersPattern.matcher(result).find();
		//
		return result;
	}

	@Override
	public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
	}

	@Override
	public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		return new X509Certificate[] {};
	}

	public boolean verify(String hostname, SSLSession session) {
		return true;
	}
}
