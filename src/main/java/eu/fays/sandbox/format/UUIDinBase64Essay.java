package eu.fays.sandbox.format;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;

public class UUIDinBase64Essay {

	public static void main(String[] args) {
		for (int i = 0; i < 100; i++) {
			System.out.println(base64UUID());
		}
	}

	/**
	 * Return a Base 64 encoded UUID
	 * @return the UUID in base 64
	 */
	public static String base64UUID() {
		// c.f. https://stackoverflow.com/questions/17893609/convert-uuid-to-byte-that-works-when-using-uuid-nameuuidfrombytesb
		final UUID uuid = UUID.randomUUID();
		final ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
		bb.putLong(uuid.getMostSignificantBits());
		bb.putLong(uuid.getLeastSignificantBits());
		final String result = Base64.getEncoder().encodeToString(bb.array());
		// 22 == Math.ceil(16 [bytes] * 8 [bit] / 6 [bits per byte])
		// Base64 : each character (i.e. byte), encodes 6 bits (c.f. 2ˆ6 == 64).
		return result.substring(0, 22);
	}
}
