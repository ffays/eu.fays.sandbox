import static java.lang.System.getProperty;
import static java.lang.System.out;
import static java.nio.file.Files.newInputStream;
import static java.nio.file.StandardOpenOption.READ;
import static java.text.MessageFormat.format;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.Enumeration;

import javax.crypto.SecretKey;

/**
 * Lists both secret keys and certificates from the given Keystore file<br>
 * <br>
 * Usage: java KeyToolQuery <br>
 * <br>
 * System parameters:
 * <ul>
 * <li>keystore: path to the Keystore file
 * <li>password: Keystore password
 * <li>storetype: Keystore type. E.g. JKS, JCEKS, PKCS11, PKCS12
 * </ul>
 * <br>
 * How to create a Keystore with secret keys, e.g.<br>
 * 
 * <pre>
 * keytool -importpass -alias my_alias -storetype PKCS12 -keystore $USERNAME.keystore
 * </pre>
 * 
 * @param args unused
 * @throws Exception in case of unexpected error
 */
public class KeyToolQuery {
	private final static String KEYSTORE_PARAMETER_NAME = "keystore";
	private final static String PASSWORD_PARAMETER_NAME = "password";
	private final static String STORETYPE_PARAMETER_NAME = "storetype";

	public static void main(String[] args) throws Exception {
		final String password = getProperty(PASSWORD_PARAMETER_NAME, "changeit");
		final String storetype;

		Path keystoreFile = null;
		if (getProperty(KEYSTORE_PARAMETER_NAME) != null) {
			storetype = getProperty(STORETYPE_PARAMETER_NAME, "PKCS12");
			String keystore = getProperty(KEYSTORE_PARAMETER_NAME);
			if (keystore.charAt(0) == '~') {
				keystore = keystore.replaceFirst("~", getProperty("user.home"));
			}
			keystoreFile = Paths.get(keystore);
		} else {
			storetype = "JKS";

			// Infer Java Home
			String javaHome = null;
			if (getProperty("JAVA_HOME") != null) {
				javaHome = getProperty("JAVA_HOME");
			} else {
				final ProcessBuilder processBuilder;
				final String os = getProperty("os.name", "Unknown");
				if (os.startsWith("Windows")) {
					processBuilder = new ProcessBuilder("powershell.exe", "-ExecutionPolicy", "Bypass", "-NoLogo", "-NonInteractive", "-NoProfile", "-Command",
							"Get-ItemPropertyValue -Path Registry::HKEY_LOCAL_MACHINE\\SOFTWARE\\JavaSoft\\JDK\\$(Get-ItemPropertyValue -Path Registry::HKEY_LOCAL_MACHINE\\SOFTWARE\\JavaSoft\\JDK -Name CurrentVersion) -Name JavaHome");
				} else if (os.startsWith("Mac")) {
					processBuilder = new ProcessBuilder("/usr/libexec/java_home");
				} else {
					processBuilder = new ProcessBuilder("/bin/sh", "-c", "dirname $(dirname $(readlink -f $(which javac)))");
				}

				final Process process = processBuilder.start();
				if (process.waitFor(3, SECONDS)) {
					if (process.exitValue() == 0) {
						try (final InputStream is = process.getInputStream(); final InputStreamReader isr = new InputStreamReader(is); final BufferedReader br = new BufferedReader(isr)) {
							javaHome = br.readLine();
						}
					}
				}
			}

			// Keystore File == Trust Store
			if (javaHome != null) {
				keystoreFile = Paths.get(javaHome, "lib", "security", "cacerts");
			}
		}

		if (keystoreFile != null && password != null) {
			final KeyStore ks = KeyStore.getInstance(storetype);
			try (final InputStream fis = newInputStream(keystoreFile, READ)) {
				ks.load(fis, password.toCharArray());
				final Enumeration<String> aliases = ks.aliases();
				while (aliases.hasMoreElements()) {
					final String alias = aliases.nextElement();
					if (ks.isKeyEntry(alias)) {
						final Key key = ks.getKey(alias, password.toCharArray());
						if (key instanceof SecretKey) {
							final SecretKey secretKey = (SecretKey) key;
							final String secret = new String(secretKey.getEncoded(), "UTF-8");
							out.println(format("{0}={1}", alias, secret));
						}
					} else if (ks.isCertificateEntry(alias)) {
						final Certificate cert = ks.getCertificate(alias);
						final String md5 = buildMD5(cert.getEncoded());
						final String sha1 = buildSHA1(cert.getEncoded());
						final String sha256 = buildSHA256(cert.getEncoded());
						out.println(format("{0},MD5={1},SHA1={2},SHA256={3}", alias, hexToHexWithColon(md5), hexToHexWithColon(sha1), hexToHexWithColon(sha256)));
					}
				}
			}
		}
	}

	/**
	 * Produce the MD5 digest from the given data
	 * @param data the data from which the checksum has to be computed
	 * @return a MD5
	 */
	private static String buildMD5(final byte[] data) throws NoSuchAlgorithmException {
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
	private static String buildSHA1(byte[] data) throws NoSuchAlgorithmException {
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
	 * Produce the SHA-256 digest from the given data
	 * @param data the data from which the checksum has to be computed
	 * @return a SHA-256
	 */
	protected static String buildSHA256(byte[] data) throws NoSuchAlgorithmException {
		//
		assert data != null;
		assert data.length > 0;
		//
		final String result;
		final MessageDigest m = MessageDigest.getInstance("SHA-256");
		m.update(data, 0, data.length);
		final BigInteger i = new BigInteger(1, m.digest());
		result = String.format("%1$064X", i);
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
	private static String hexToHexWithColon(final String data) {
		//
		assert data != null;
		assert !data.isEmpty();
		//
		return Arrays.asList(data.split("(?<=\\G..)")).toString().replaceAll("[\\[\\] ]", "").replace(',', ':');
	}
}
