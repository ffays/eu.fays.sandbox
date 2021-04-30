package eu.fays.sandbox.security.rsa;

import static java.util.stream.Collectors.joining;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.text.MessageFormat;
import java.util.Base64;

/**
 * A utility class to read RSA Key and diplay it information
 * @author Frederic Fays
 */
@SuppressWarnings("nls")
public class ReadRSAKey {

	/**
	 * Reads a RSA Private key.<br>
	 * <br>
	 * The following input parameters are provided as system properties<br>
	 * <ul>
	 * <li>in: input filename, optional, default value : id_rsa (public key will be recorded with the the extension ".pub" added)
	 * </ul>
	 * e.g. java -Din=id_rsa -Dbits=2048 ReadRSAKey.class
	 * @param args unused
	 * @throws Exception in case of unexpected error
	 */

	public static void main(String[] args) throws Exception {
		final String privateKeyFileName = System.getProperty("in");
		final File privateKeyFile;
		if(privateKeyFileName == null) {
			final File homeFolder = new File(System.getProperty("user.home"));
			final File sshFolder = new File(homeFolder, ".ssh");
			privateKeyFile = new File(sshFolder, "id_rsa");
		} else {
			privateKeyFile = new File(privateKeyFileName);
		}
		final KeyPair keyPair = readPrivateKey(privateKeyFile);
		final RSAPrivateCrtKey privateKey = (RSAPrivateCrtKey) keyPair.getPrivate();
		final RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		printPrivateKeyInfo(privateKey);
		System.out.println();
		printPublicKeyInfo(publicKey);
	}

	/**
	 * Read the private key form the file name
	 * @param file the private key file
	 *
	 * @return the key pair
	 * @throws IOException in case of unexpected error
	 * @throws GeneralSecurityException in case of unexpected error
	 */
	public static KeyPair readPrivateKey(final File file) throws IOException, GeneralSecurityException {
		// In case the key would have been provided as a resource:
		// try (final InputStream in = ReadRSAKey.class.getResourceAsStream(fileName); final InputStreamReader isr = new InputStreamReader(in); final BufferedReader reader = new BufferedReader(isr);) {
		try (final FileReader fr = new FileReader(file); final BufferedReader reader = new BufferedReader(fr);) {
			final String encodedKeyBase64 = reader.lines().filter(l -> !l.startsWith("-----")).collect(joining());
			final byte[] encodedKeyBytes = Base64.getDecoder().decode(encodedKeyBase64);
			final PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encodedKeyBytes);
			final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			final RSAPrivateCrtKey privateKey = (RSAPrivateCrtKey) keyFactory.generatePrivate(keySpec);

			// Regenerate Public Key from Private Key
			final RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(privateKey.getModulus(), privateKey.getPublicExponent());
			final PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

			final KeyPair result = new KeyPair(publicKey, privateKey);
			return result;
		}
	}

	/**
	 * Prints the given private key information
	 * @param privateKey the private key
	 */
	public static void printPrivateKeyInfo(final RSAPrivateCrtKey privateKey) {
		System.out.println("RSA Public Key");
		System.out.println("\t         modulus: " + printHexBinary(privateKey.getModulus().toByteArray()).toLowerCase());
		System.out.println("\t public exponent: " + printHexBinary(privateKey.getPublicExponent().toByteArray()).toLowerCase());
		System.out.println("\tprivate exponent: " + printHexBinary(privateKey.getPrivateExponent().toByteArray()).toLowerCase());
		System.out.println("\t          primeP: " + printHexBinary(privateKey.getPrimeP().toByteArray()).toLowerCase());
		System.out.println("\t          primeQ: " + printHexBinary(privateKey.getPrimeQ().toByteArray()).toLowerCase());
		System.out.println("\t  primeExponentP: " + printHexBinary(privateKey.getPrimeExponentP().toByteArray()).toLowerCase());
		System.out.println("\t  primeExponentQ: " + printHexBinary(privateKey.getPrimeExponentQ().toByteArray()).toLowerCase());
		System.out.println("\t  crtCoefficient: " + printHexBinary(privateKey.getCrtCoefficient().toByteArray()).toLowerCase());

	}

	/**
	 * Prints the given public key information
	 * @param publicKey the public key
	 */
	public static void printPublicKeyInfo(final RSAPublicKey publicKey) {
		System.out.println("RSA Private CRT Key");
		System.out.println("\t         modulus: " + printHexBinary(publicKey.getModulus().toByteArray()).toLowerCase());
		System.out.println("\t public exponent: " + printHexBinary(publicKey.getPublicExponent().toByteArray()).toLowerCase());
	}
	
	private static String printHexBinary(byte[] data) {
		final BigInteger i = new BigInteger(1, data);
		final String format = MessageFormat.format("%1$0{0,number,0}X", data.length << 3);
		final String result = String.format(format, i);
		return result;
	}
}
