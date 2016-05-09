package eu.fays.sandbox.security;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.text.MessageFormat;
import java.util.Base64;
import java.util.logging.Logger;

/**
 * An essay on java security
 * 
 * @author Fr&eacute;d&eacute;ric Fays
 */
@SuppressWarnings("nls")
public class SecurityEssay {

	/**
	 * Main
	 * 
	 * @param args unused
	 * @throws Exception in case of unexpected error
	 */
	public static void main(String[] args) throws Exception {
		final String message = "Open Sesame";
		final String signatureInBase64 = sign(message);

		// Read the public key from a key store
		try (final InputStream is = SecurityEssay.class.getResourceAsStream("lumberjack-public.keystore")) {
			// Read the keys
			final KeyStore keystore = KeyStore.getInstance("JKS");
			keystore.load(is, "changeit".toCharArray());
			final Certificate certificate = keystore.getCertificate("sesame");
			final PublicKey publicKey = certificate.getPublicKey();
			boolean result = verify(message, signatureInBase64, publicKey);
			LOGGER.info(MessageFormat.format("Verify #1: {0}", result));
		}

		// Read the public key directly
		try (final InputStream is = SecurityEssay.class.getResourceAsStream("sesame.cer")) {
			final CertificateFactory cf = CertificateFactory.getInstance("X.509");
			final Certificate certificate = cf.generateCertificate(is);
			final PublicKey publicKey = certificate.getPublicKey();
			boolean result = verify(message, signatureInBase64, publicKey);
			LOGGER.info(MessageFormat.format("Verify #2: {0}", result));
		}
	}

	/**
	 * Sign given message with the given private key
	 * @return the signature coded in base64
	 * @throws Exception in case of unexpected error
	 */
	public static String sign(final String message) throws Exception {
		try (final InputStream is = SecurityEssay.class.getResourceAsStream("lumberjack-private.keystore")) {
			final KeyStore keystore = KeyStore.getInstance("JKS");
			// Read the keys
			keystore.load(is, "changeit".toCharArray());
			final PrivateKey privateKey = (PrivateKey) keystore.getKey("sesame", "changeit".toCharArray());
			// Sign
			final Signature signatory = Signature.getInstance("SHA256withDSA", "SUN");
			signatory.initSign(privateKey);
			//
			final byte[] text = message.getBytes();
			signatory.update(text);
			final byte[] signature = signatory.sign();
			final String signatureInBase64 = new String(Base64.getEncoder().encode(signature));
			return signatureInBase64;
		}
	}

	/**
	 * Verify the given message with the given public key
	 * @param message the message to be verified
	 * @param signatureInBase64 the signature of the message
	 * @param publicKey the public key to verify with
	 * @return either true: authentic or false: counterfeit
	 * @throws Exception in case of unexpected error
	 */
	public static boolean verify(final String message, final String signatureInBase64, final PublicKey publicKey) throws Exception {
		final byte[] signature = Base64.getDecoder().decode(signatureInBase64.getBytes());
		final Signature signatory = Signature.getInstance("SHA256withDSA", "SUN");
		signatory.initVerify(publicKey);
		signatory.update(message.getBytes());
		final boolean result = signatory.verify(signature);
		return result;
	}

	/** Standard logger */
	private static final Logger LOGGER = Logger.getLogger(SecurityEssay.class.getName());

}
