package eu.fays.sandbox.security.rsa;

import static java.util.Arrays.stream;

import java.io.File;
import java.io.PrintWriter;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

/**
 * RSA Key Pair generator
 * @author Frederic Fays
 */
public class GenerateRSAKeyPair {

	/**
	 * Generates a RSA Key Pair.<br>
	 * <br>
	 * The following input parameters are provided as system properties<br>
	 * <ul>
	 * <li>out: output filename, optional, default value : id_rsa (public key will be recorded with the the extension ".pub" added)
	 * <li>bits: strength of the key expressed in # bits, optional, default value : 2048
	 * </ul>
	 * e.g. java -Dout=id_rsa -Dbits=2048 GenerateRSAKeyPair.class
	 * @param args unused
	 * @throws Exception in case of unexpected error
	 */
	public static void main(String[] args) throws Exception {
		final String privateKeyFileName = System.getProperty("out", "id_rsa");
		final String publicKeyFileName = privateKeyFileName + ".pub";
		final int keysize = Integer.parseInt(System.getProperty("bits", "2048"));

		final File privateKeyFile = new File(privateKeyFileName);
		final File publicKeyFile = new File(publicKeyFileName);

		final KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");
		keyGenerator.initialize(keysize);
		final KeyPair keyPair = keyGenerator.genKeyPair();

		// record private key
		try (final PrintWriter pw = new PrintWriter(privateKeyFile)) {
			final String privateKeyBase64 = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
			pw.println("-----BEGIN RSA PRIVATE KEY-----");
			stream(privateKeyBase64.split("(?<=\\G.{64})")).forEach(l -> pw.println(l));
			pw.println("-----END RSA PRIVATE KEY-----");
		}

		// record public key
		try (final PrintWriter pw = new PrintWriter(publicKeyFile)) {
			final String publicKeyBase64 = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
			pw.println("-----BEGIN RSA PUBLIC KEY-----");
			stream(publicKeyBase64.split("(?<=\\G.{64})")).forEach(l -> pw.println(l));
			pw.println("-----END RSA PUBLIC KEY-----");
		}
	}
}
