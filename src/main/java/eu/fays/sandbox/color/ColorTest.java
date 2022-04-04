package eu.fays.sandbox.color;

import static java.awt.Color.RGBtoHSB;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ColorTest {

	private final Color _color;

	@Parameters(name = "{0}")
	public static Collection<Object[]> data() {
		return Arrays.stream(Color.values()).map(c -> new Object[] { c }).collect(toList());
	}

	public ColorTest(final Color color) {
		_color = color;
	}

	@Test
	public void getHueSaturationValueFloats() {
		float[] hsv0 = RGBtoHSB(_color.getRed(), _color.getGreen(), _color.getBlue(), null);
		hsv0[0] *= 360F;
		float[] hsv1 = _color.getHueSaturationValueFloats();

		assertEquals("Hue mismatch", hsv0[0], hsv1[0], 0.001F);
		assertEquals("Saturation mismatch", hsv0[1], hsv1[1], 0.001F);
		assertEquals("Value mismatch", hsv0[2], hsv1[2], 0.001F);
	}
}
