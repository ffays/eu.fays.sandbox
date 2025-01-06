package eu.fays.sandbox.uuid;

import java.math.BigInteger;
import java.util.UUID;
import java.util.logging.Logger;

// -ea -Djava.util.logging.SimpleFormatter.format="%1$tFT%1$tT,%1$tL	%4$s	%3$s	%5$s%6$s%n"
@SuppressWarnings("nls")
public class UUIDEssay {

	/** Standard logger */
	private static final Logger LOGGER = Logger.getLogger(UUIDEssay.class.getName());

	public static void main(String[] args) {
		final UUID uuid0 = UUID.randomUUID();
		final String hex0 = uuid0.toString().replace("-", "");
		LOGGER.info(hex0);
		
		final BigInteger bi = new BigInteger(hex0, 16);
		LOGGER.info(bi.toString(16));

		long leastSignificantBits = bi.longValue();
		long mostSignificantBits = bi.shiftRight(64).longValue();

		mostSignificantBits &= 0xfffffffffffff0ffL; // Clear version
		mostSignificantBits |= 0x0000000000004000L; // Set to version 4
		leastSignificantBits &= 0x3fffffffffffffffL; // Clear variant
		leastSignificantBits |= 0x8000000000000000L; // Set to IETF variant

		final UUID uuid1 = new UUID(mostSignificantBits, leastSignificantBits);
		final String hex1 = uuid1.toString().replace("-", "");
		LOGGER.info(hex1);
		assert hex0.equals(hex1);
	}
}
