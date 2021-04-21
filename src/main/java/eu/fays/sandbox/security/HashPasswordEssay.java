package eu.fays.sandbox.security;

import static java.lang.System.arraycopy;
import static java.lang.System.out;

import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class HashPasswordEssay {

	@SuppressWarnings("nls")
	public static void main(String[] args) throws Exception {
		final String password = System.getProperty("password", "changeit");

		// Salt and pepper computation
		final SecureRandom secureRandom = new SecureRandom();
		byte[] salt = new byte[16];
		secureRandom.nextBytes(salt);
		byte[] pepper = new byte[16];
		// TODO : set the pepper
		byte[] saltAndPepper = new byte[salt.length + pepper.length];
		arraycopy(salt, 0, saltAndPepper, 0, salt.length);
		arraycopy(pepper, 0, saltAndPepper, salt.length, pepper.length);

		// Password hash computation
		final KeySpec spec = new PBEKeySpec(password.toCharArray(), saltAndPepper, 65536, 512);
		final SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
		final byte[] hash = factory.generateSecret(spec).getEncoded();

		// Convert to base 64
		final String hashedSaltAndPepperBase64 = Base64.getEncoder().encodeToString(saltAndPepper);
		final String hashedPasswordBase64 = Base64.getEncoder().encodeToString(hash);

		out.println("hashedSaltAndPepperBase64: " + hashedSaltAndPepperBase64);
		out.println("hashedPasswordBase64: " + hashedPasswordBase64);
	}

}
