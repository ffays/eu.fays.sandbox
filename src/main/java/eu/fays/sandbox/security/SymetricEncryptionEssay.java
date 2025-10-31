package eu.fays.sandbox.security;

import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.crypto.Cipher.DECRYPT_MODE;
import static javax.crypto.Cipher.ENCRYPT_MODE;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class SymetricEncryptionEssay {

	/**
	 * Main<br>
	 * <br>
	 * Parameters:
	 * <ul>
	 * <li>message: a message to be signed
	 * </ul>
	 * Note: the parameters must be provided to the jvm as runtime properties, e.g. -Dmessage="The quick brown fox jumps over the lazy dog"
	 * 
	 * @param args unused
	 * @throws Exception in case of unexpected error
	 */
	public static void main(String[] args) throws Exception {
		// https://docs.oracle.com/en/java/javase/21/docs/specs/security/standard-names.html#keygenerator-algorithms
		final String transformation = "AES/CBC/PKCS5Padding";

		final String encodedSecretKeyBase64;
		final String initializationVectorBase64;
		final String encryptedMessageBase64;

		// Encrypt
		{
			final Encoder base64Encoder = Base64.getEncoder();
			final String message = System.getProperty("message", "Hello World!");
			final SecretKey secretKey = KeyGenerator.getInstance("AES").generateKey();
			final byte[] encodedSecretKey = secretKey.getEncoded();
			encodedSecretKeyBase64 = base64Encoder.encodeToString(encodedSecretKey);

			final Cipher cipher = Cipher.getInstance(transformation);
			cipher.init(ENCRYPT_MODE, secretKey);
			final byte[] initializationVector = cipher.getIV();
			initializationVectorBase64 = base64Encoder.encodeToString(initializationVector);

			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try (final CipherOutputStream cos = new CipherOutputStream(baos, cipher)) {
				cos.write(message.getBytes());
			}

			final byte[] encryptedMessage = baos.toByteArray();
			encryptedMessageBase64 = base64Encoder.encodeToString(encryptedMessage);

			final Map<String, String> properties = new LinkedHashMap<>();
			properties.put("secretKey", encodedSecretKeyBase64);
			properties.put("initializationVector", initializationVectorBase64);
			properties.put("encryptedMessage", encryptedMessageBase64);
			properties.forEach((k, v) -> System.out.println(k + "=" + v));
		}

		// Decrypt
		{
			final Decoder base64Decoder = Base64.getDecoder();
			final byte[] encodedSecretKey = base64Decoder.decode(encodedSecretKeyBase64);
			final SecretKey secretKey = new SecretKeySpec(encodedSecretKey, 0, encodedSecretKey.length, "AES");
			final byte[] initializationVector = base64Decoder.decode(initializationVectorBase64);
			final byte[] encryptedMessage = base64Decoder.decode(encryptedMessageBase64);

			final Cipher cipher = Cipher.getInstance(transformation);
			cipher.init(DECRYPT_MODE, secretKey, new IvParameterSpec(initializationVector));
			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try (final ByteArrayInputStream bais = new ByteArrayInputStream(encryptedMessage); final CipherInputStream cis = new CipherInputStream(bais, cipher)) {
				int b;
				while ((b = cis.read()) != -1) {
					baos.write(b);
				}
			}
			final String decodedMessage = new String(baos.toByteArray(), UTF_8);
			System.out.println("decodedMessage=" + decodedMessage);
		}
	}

}
