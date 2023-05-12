package eu.fays.sandbox.color;

import static eu.fays.sandbox.color.Color.BLACK;
import static eu.fays.sandbox.color.Color.BLUE;
import static eu.fays.sandbox.color.Color.CYAN;
import static eu.fays.sandbox.color.Color.GRAY;
import static eu.fays.sandbox.color.Color.GREEN;
import static eu.fays.sandbox.color.Color.LIME;
import static eu.fays.sandbox.color.Color.MAGENTA;
import static eu.fays.sandbox.color.Color.MAROON;
import static eu.fays.sandbox.color.Color.NAVY;
import static eu.fays.sandbox.color.Color.OLIVE;
import static eu.fays.sandbox.color.Color.PURPLE;
import static eu.fays.sandbox.color.Color.RED;
import static eu.fays.sandbox.color.Color.SILVER;
import static eu.fays.sandbox.color.Color.TEAL;
import static eu.fays.sandbox.color.Color.WHITE;
import static eu.fays.sandbox.color.Color.YELLOW;
import static java.awt.Color.RGBtoHSB;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@SuppressWarnings("nls")
public class ColorTest {
	
	public static Stream<Arguments> data() {
		// @formatter:off
		return Stream.of(
			Arguments.of(BLACK, 0d, 0d, 0d),
			Arguments.of(WHITE, 0d, 0d, 1d),
			Arguments.of(RED, 0d, 1d, 1d),
			Arguments.of(LIME, 120d, 1d, 1d),
			Arguments.of(BLUE, 240d, 1d, 1d),
			Arguments.of(YELLOW, 60d, 1d, 1d),
			Arguments.of(CYAN, 180d, 1d, 1d),
			Arguments.of(MAGENTA, 300d, 1d, 1d),
			Arguments.of(SILVER, 0d, 0d, 0.75d),
			Arguments.of(GRAY, 0d, 0d, 0.5d),
			Arguments.of(MAROON, 0d, 1d, 0.5d),
			Arguments.of(OLIVE, 60d, 1d, 0.5d),
			Arguments.of(GREEN, 120d, 1d, 0.5d),
			Arguments.of(PURPLE, 300d, 1d, 0.5d),
			Arguments.of(TEAL, 180d, 1d, 0.5d),
			Arguments.of(NAVY, 240d, 1d, 0.5d)
		);
		// @formatter:on		
	}
	
	public static Stream<Color> data2() {
		return Arrays.stream(Color.values());
	}

	@ParameterizedTest(name = "{0}")
	@Tag("color-test")
	@MethodSource("data")
	public void getHue(final Color color, final double hue, final double saturation, final double value) {
		assertEquals(hue, color.getHue(), 0.003d);
	}

	@ParameterizedTest(name = "{0}")
	@Tag("color-test")
	@MethodSource("data")
	public void getSaturation(final Color color, final double hue, final double saturation, final double value) {
		assertEquals(saturation, color.getSaturation(), 0.003d);
	}

	@ParameterizedTest(name = "{0}")
	@Tag("color-test")
	@MethodSource("data")
	public void getValue(final Color color, final double hue, final double saturation, final double value) {
		assertEquals(value, color.getValue(), 0.003d);
	}
	
	@ParameterizedTest(name = "{0}")
	@Tag("color-test")
	@MethodSource("data2")
	public void getHueSaturationValueFloats(final Color color) {
		float[] hsv0 = RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
		hsv0[0] *= 360F;
		float[] hsv1 = color.getHueSaturationValueFloats();

		assertEquals(hsv0[0], hsv1[0], 0.001F, "Hue mismatch");
		assertEquals(hsv0[1], hsv1[1], 0.001F, "Saturation mismatch");
		assertEquals(hsv0[2], hsv1[2], 0.001F, "Value mismatch");
	}
	
	@ParameterizedTest(name = "{0}")
	@Tag("color-test")
	@MethodSource("data2")
	public void hsv2rgb(final Color color) {
		final float[] hsv = color.getHueSaturationValueFloats();
		final int[] rgb = Color.hsv2rgb(hsv);
		assertEquals(color.getRed(), rgb[0], "Red missmatch");
		assertEquals(color.getGreen(), rgb[1], "Green missmatch");
		assertEquals(color.getBlue(), rgb[2], "Blue missmatch");
	}
 }
