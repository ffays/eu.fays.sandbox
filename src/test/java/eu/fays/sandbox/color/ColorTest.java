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
	final float _hue;
	final float _saturation;
	final float _value;
	
	@Parameters(name = "{0}")
	public static Collection<Object[]> data() {
		// @formatter:off
		return Arrays.asList(new Object[][] {
			{ BLACK, 0F, 0F, 0F },
			{ WHITE, 0F, 0F, 1F },
			{ RED, 0F, 1F, 1F },
			{ LIME, 120F, 1F, 1F },
			{ BLUE, 240F, 1F, 1F },
			{ YELLOW, 60F, 1F, 1F },
			{ CYAN, 180F, 1F, 1F },
			{ MAGENTA, 300F, 1F, 1F },
			{ SILVER, 0F, 0F, 0.75F },
			{ GRAY, 0F, 0F, 0.5F },
			{ MAROON, 0F, 1F, 0.5F },
			{ OLIVE, 60F, 1F, 0.5F },
			{ GREEN, 120F, 1F, 0.5F },
			{ PURPLE, 300F, 1F, 0.5F },
			{ TEAL, 180F, 1F, 0.5F },
			{ NAVY, 240F, 1F, 0.5F }
		});
	}

	public ColorTest(final Color color, final float hue, final float saturation, final float value) {
		_color = color;
		_hue = hue;
		_saturation = saturation;
		_value = value;
	}
	
	@Test
	public void getHue() {
		assertEquals(_hue, _color.getHue(), 0.003F);
	}

	@Test
	public void getSaturation() {
		assertEquals(_saturation, _color.getSaturation(), 0.003F);
	}

	@Test
	public void getValue() {
		assertEquals(_value, _color.getValue(), 0.003F);
	}
 }
