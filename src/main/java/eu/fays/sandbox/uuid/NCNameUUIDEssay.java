package eu.fays.sandbox.uuid;

import java.util.Random;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Generate a random UUID and make it Non-Colonized Name compliant
 */
public class NCNameUUIDEssay {
	
	/** Standard logger */
	private static final Logger LOGGER = Logger.getLogger(NCNameUUIDEssay.class.getName());
	
	/** Random number generator */
	private static final Random RANDOM = new Random();

	/** Masks: 0xA000000000000000, 0xB000000000000000, 0xC000000000000000, 0xD000000000000000, 0xE000000000000000 and 0xF000000000000000 */
	private static final long[] MASKS = { -0x6000000000000000L, -0x5000000000000000L, -0x4000000000000000L, -0x3000000000000000L, -0x2000000000000000L, -0x1000000000000000L };
//	static { for (int i = 10; i < 16; i++) LOGGER.info(Long.toString(Long.parseUnsignedLong(String.valueOf(Character.toChars('0'+i+(i>=10?7:0))) + "000000000000000", 16), 16)); }

	/**
	 * Generate a random UUID and make it Non-Colonized Name compliant, i.e. an NCName for XML "id" attribute<br>
	 * <code>-ea -Djava.util.logging.SimpleFormatter.format="%5$s%6$s%n"</code>
	 * @param args not used
	 */
	public static void main(String[] args) {
//		int[] stats = new int[6];
//		final int n = 10000000;
//		for(int i=0; i<n; i++) {
			UUID uuid = UUID.randomUUID();
			long msb = uuid.getMostSignificantBits();
			if((msb  & -0x6000000000000000L) == -0x6000000000000000L /* 0xA000000000000000 1010 */ || (msb  & -0x4000000000000000L) == -0x4000000000000000L /* 0xC000000000000000 1100 */) {
				// do nothing
			} else {
				msb = msb & 0x0FFFFFFFFFFFFFFFL | MASKS[RANDOM.nextInt(6 /* MASKS.length */)]; // [A-F]: 16.666%
//				msb = msb | (RANDOM.nextBoolean()?-0x6000000000000000L:-0x4000000000000000L); // [A-D]: 15.625%, [E-F]: 18.75%
				uuid = new UUID(msb, uuid.getLeastSignificantBits());
			}
			assert Character.isAlphabetic(uuid.toString().charAt(0));
			LOGGER.info(uuid.toString());
//			stats[uuid.toString().charAt(0) - 'a']++;
//		}
//		for(int i=0; i<stats.length; i++) LOGGER.info(java.text.MessageFormat.format("{0}: {1,number,#0.000}%", String.valueOf(Character.toChars('A'+i)), 100F*(float)stats[i]/(float)n));
	}
}
