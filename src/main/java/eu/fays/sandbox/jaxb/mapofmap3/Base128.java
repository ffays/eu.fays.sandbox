package eu.fays.sandbox.jaxb.mapofmap3;

import static java.text.MessageFormat.format;
import static java.util.Arrays.stream;
import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.IntStream.rangeClosed;

import java.nio.ByteBuffer;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class Base128 {

	// @formatter:off
	public static final char[][] RANGES = {{'-'},{'.'},{'0','9'},{'A','Z'},{'_'},{'a','z'},{'\u00b7'},{'\u00c0','\u00d6'},{'\u00d8','\u00f6'},{'\u00f8','\u00ff'}};
	public static final int[] MAPPING = stream(RANGES)
		.flatMapToInt(cs -> rangeClosed(cs[0], cs[cs.length-1]))
		.toArray();
	// @formatter:on
	public static void main(String[] args) {
		// @formatter:off
		final TreeMap<Character, Character> map = stream(RANGES).map(r -> new SimpleImmutableEntry<>(r[0], r[r.length-1])).collect(toMap(Entry::getKey,Entry::getValue, (k0, k1) -> null, TreeMap::new));
		// @formatter:on
		final StringBuilder builder = new StringBuilder();
		builder.append('{');
		for (final Entry<Character, Character> entry : map.entrySet()) {
			final char a = entry.getKey();
			final char b = entry.getValue();
			if (a != '-') {
				builder.append(',');
			}
			final char[] cs = a == b ? new char[] { a } : new char[] { a, b };
			builder.append('{');
			for (final char c : cs) {
				final int i = c;
				if (c != a) {
					builder.append(',');
				}
				builder.append('\'');
				if (i < 128) {
					builder.append(c);
				} else {
					builder.append("\\u00");
					builder.append(Integer.toHexString(i));
				}
				builder.append('\'');
			}
			builder.append('}');
		}
		builder.append('}');
		System.out.println(builder.toString());
		{
			System.out.println(MAPPING.length);
			final AtomicInteger i = new AtomicInteger();
			stream(MAPPING).forEach(c -> System.out
					.println(format("{0,number,000}:''{1}''[{2,number,000}]", i.getAndIncrement(), (char) c, c)));
		}
	}

	public String buildId() {
		final UUID uuid = randomUUID();
		final ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
		bb.putLong(uuid.getMostSignificantBits());
		bb.putLong(uuid.getLeastSignificantBits());
		byte[] source = bb.array();
		byte[] target = new byte[19];
		// TODO
		return null;
	}
}
