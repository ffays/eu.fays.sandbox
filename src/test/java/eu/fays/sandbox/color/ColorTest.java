package eu.fays.sandbox.color;

import java.util.Arrays;
import java.util.Collection;
import static eu.fays.sandbox.color.Color.*;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ColorTest {

	final Color _color;
	final double _hue;
	final double _saturation;
	final double _value;
	
	@Parameters(name = "{0}")
	public static Collection<Object[]> data() {
		// @formatter:off
		return Arrays.asList(new Object[][] {
			{ BLACK, 0d, 0d, 0d },
			{ WHITE, 0d, 0d, 1d },
			{ RED, 0d, 1d, 1d },
			{ LIME, 120d, 1d, 1d },
			{ BLUE, 240d, 1d, 1d },
			{ YELLOW, 60d, 1d, 1d },
			{ CYAN, 180d, 1d, 1d },
			{ MAGENTA, 300d, 1d, 1d },
			{ SILVER, 0d, 0d, 0.75d },
			{ GRAY, 0d, 0d, 0.5d },
			{ MAROON, 0d, 1d, 0.5d },
			{ OLIVE, 60d, 1d, 0.5d },
			{ GREEN, 120d, 1d, 0.5d },
			{ PURPLE, 300d, 1d, 0.5d },
			{ TEAL, 180d, 1d, 0.5d },
			{ NAVY, 240d, 1d, 0.5d }
		});
		// @formatter:on
	}

	public ColorTest(final Color color, final double hue, final double saturation, final double value) {
		_color = color;
		_hue = hue;
		_saturation = saturation;
		_value = value;
	}
	
	@Test
	public void getHue() {
		assertEquals(_hue, _color.getHue(), 0.003d);
	}

	@Test
	public void getSaturation() {
		assertEquals(_saturation, _color.getSaturation(), 0.003d);
	}

	@Test
	public void getValue() {
		assertEquals(_value, _color.getValue(), 0.003d);
	}
 }
