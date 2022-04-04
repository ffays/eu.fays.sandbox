package eu.fays.sandbox.color;

import static eu.fays.sandbox.color.Tristimulus.D65;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 * W3C Colors<br>
 * <br>
 * Cf.
 * <ul>
 * <li><a href="https://www.w3.org/wiki/CSS/Properties/color/keywords">CSS/Properties/color/keywords</a>
 * <li><a href="https://www.easyrgb.com/en/math.php">Color math and programming code examples</a>
 * </ul>
 */
public enum Color {
	/** Aliceblue <div style="background-color: aliceblue; padding: 10px; border: 1px solid black"> */
	ALICEBLUE(0xF0F8FF),
	/** Antiquewhite <div style="background-color: antiquewhite; padding: 10px; border: 1px solid black"> */
	ANTIQUEWHITE(0xFAEBD7),
	/** Aqua <div style="background-color: aqua; padding: 10px; border: 1px solid black"> */
	AQUA(0x00FFFF),
	/** Aquamarine <div style="background-color: aquamarine; padding: 10px; border: 1px solid black"> */
	AQUAMARINE(0x7FFFD4),
	/** Azure <div style="background-color: azure; padding: 10px; border: 1px solid black"> */
	AZURE(0xF0FFFF),
	/** Beige <div style="background-color: beige; padding: 10px; border: 1px solid black"> */
	BEIGE(0xF5F5DC),
	/** Bisque <div style="background-color: bisque; padding: 10px; border: 1px solid black"> */
	BISQUE(0xFFE4C4),
	/** Black <div style="background-color: black; padding: 10px; border: 1px solid black"> */
	BLACK(0x000000),
	/** Blanchedalmond <div style="background-color: blanchedalmond; padding: 10px; border: 1px solid black"> */
	BLANCHEDALMOND(0xFFEBCD),
	/** Blue <div style="background-color: blue; padding: 10px; border: 1px solid black"> */
	BLUE(0x0000FF),
	/** Blueviolet <div style="background-color: blueviolet; padding: 10px; border: 1px solid black"> */
	BLUEVIOLET(0x8A2BE2),
	/** Brown <div style="background-color: brown; padding: 10px; border: 1px solid black"> */
	BROWN(0xA52A2A),
	/** Burlywood <div style="background-color: burlywood; padding: 10px; border: 1px solid black"> */
	BURLYWOOD(0xDEB887),
	/** Cadetblue <div style="background-color: cadetblue; padding: 10px; border: 1px solid black"> */
	CADETBLUE(0x5F9EA0),
	/** Chartreuse <div style="background-color: chartreuse; padding: 10px; border: 1px solid black"> */
	CHARTREUSE(0x7FFF00),
	/** Chocolate <div style="background-color: chocolate; padding: 10px; border: 1px solid black"> */
	CHOCOLATE(0xD2691E),
	/** Coral <div style="background-color: coral; padding: 10px; border: 1px solid black"> */
	CORAL(0xFF7F50),
	/** Cornflowerblue <div style="background-color: cornflowerblue; padding: 10px; border: 1px solid black"> */
	CORNFLOWERBLUE(0x6495ED),
	/** Cornsilk <div style="background-color: cornsilk; padding: 10px; border: 1px solid black"> */
	CORNSILK(0xFFF8DC),
	/** Crimson <div style="background-color: crimson; padding: 10px; border: 1px solid black"> */
	CRIMSON(0xDC143C),
	/** Cyan <div style="background-color: cyan; padding: 10px; border: 1px solid black"> */
	CYAN(0x00FFFF),
	/** Darkblue <div style="background-color: darkblue; padding: 10px; border: 1px solid black"> */
	DARKBLUE(0x00008B),
	/** Darkcyan <div style="background-color: darkcyan; padding: 10px; border: 1px solid black"> */
	DARKCYAN(0x008B8B),
	/** Darkgoldenrod <div style="background-color: darkgoldenrod; padding: 10px; border: 1px solid black"> */
	DARKGOLDENROD(0xB8860B),
	/** Darkgray <div style="background-color: darkgray; padding: 10px; border: 1px solid black"> */
	DARKGRAY(0xA9A9A9),
	/** Darkgreen <div style="background-color: darkgreen; padding: 10px; border: 1px solid black"> */
	DARKGREEN(0x006400),
	/** Darkgrey <div style="background-color: darkgrey; padding: 10px; border: 1px solid black"> */
	DARKGREY(0xA9A9A9),
	/** Darkkhaki <div style="background-color: darkkhaki; padding: 10px; border: 1px solid black"> */
	DARKKHAKI(0xBDB76B),
	/** Darkmagenta <div style="background-color: darkmagenta; padding: 10px; border: 1px solid black"> */
	DARKMAGENTA(0x8B008B),
	/** Darkolivegreen <div style="background-color: darkolivegreen; padding: 10px; border: 1px solid black"> */
	DARKOLIVEGREEN(0x556B2F),
	/** Darkorange <div style="background-color: darkorange; padding: 10px; border: 1px solid black"> */
	DARKORANGE(0xFF8C00),
	/** Darkorchid <div style="background-color: darkorchid; padding: 10px; border: 1px solid black"> */
	DARKORCHID(0x9932CC),
	/** Darkred <div style="background-color: darkred; padding: 10px; border: 1px solid black"> */
	DARKRED(0x8B0000),
	/** Darksalmon <div style="background-color: darksalmon; padding: 10px; border: 1px solid black"> */
	DARKSALMON(0xE9967A),
	/** Darkseagreen <div style="background-color: darkseagreen; padding: 10px; border: 1px solid black"> */
	DARKSEAGREEN(0x8FBC8F),
	/** Darkslateblue <div style="background-color: darkslateblue; padding: 10px; border: 1px solid black"> */
	DARKSLATEBLUE(0x483D8B),
	/** Darkslategray <div style="background-color: darkslategray; padding: 10px; border: 1px solid black"> */
	DARKSLATEGRAY(0x2F4F4F),
	/** Darkslategrey <div style="background-color: darkslategrey; padding: 10px; border: 1px solid black"> */
	DARKSLATEGREY(0x2F4F4F),
	/** Darkturquoise <div style="background-color: darkturquoise; padding: 10px; border: 1px solid black"> */
	DARKTURQUOISE(0x00CED1),
	/** Darkviolet <div style="background-color: darkviolet; padding: 10px; border: 1px solid black"> */
	DARKVIOLET(0x9400D3),
	/** Deeppink <div style="background-color: deeppink; padding: 10px; border: 1px solid black"> */
	DEEPPINK(0xFF1493),
	/** Deepskyblue <div style="background-color: deepskyblue; padding: 10px; border: 1px solid black"> */
	DEEPSKYBLUE(0x00BFFF),
	/** Dimgray <div style="background-color: dimgray; padding: 10px; border: 1px solid black"> */
	DIMGRAY(0x696969),
	/** Dimgrey <div style="background-color: dimgrey; padding: 10px; border: 1px solid black"> */
	DIMGREY(0x696969),
	/** Dodgerblue <div style="background-color: dodgerblue; padding: 10px; border: 1px solid black"> */
	DODGERBLUE(0x1E90FF),
	/** Firebrick <div style="background-color: firebrick; padding: 10px; border: 1px solid black"> */
	FIREBRICK(0xB22222),
	/** Floralwhite <div style="background-color: floralwhite; padding: 10px; border: 1px solid black"> */
	FLORALWHITE(0xFFFAF0),
	/** Forestgreen <div style="background-color: forestgreen; padding: 10px; border: 1px solid black"> */
	FORESTGREEN(0x228B22),
	/** Fuchsia <div style="background-color: fuchsia; padding: 10px; border: 1px solid black"> */
	FUCHSIA(0xFF00FF),
	/** Gainsboro <div style="background-color: gainsboro; padding: 10px; border: 1px solid black"> */
	GAINSBORO(0xDCDCDC),
	/** Ghostwhite <div style="background-color: ghostwhite; padding: 10px; border: 1px solid black"> */
	GHOSTWHITE(0xF8F8FF),
	/** Gold <div style="background-color: gold; padding: 10px; border: 1px solid black"> */
	GOLD(0xFFD700),
	/** Goldenrod <div style="background-color: goldenrod; padding: 10px; border: 1px solid black"> */
	GOLDENROD(0xDAA520),
	/** Gray <div style="background-color: gray; padding: 10px; border: 1px solid black"> */
	GRAY(0x808080),
	/** Green <div style="background-color: green; padding: 10px; border: 1px solid black"> */
	GREEN(0x008000),
	/** Greenyellow <div style="background-color: greenyellow; padding: 10px; border: 1px solid black"> */
	GREENYELLOW(0xADFF2F),
	/** Grey <div style="background-color: grey; padding: 10px; border: 1px solid black"> */
	GREY(0x808080),
	/** Honeydew <div style="background-color: honeydew; padding: 10px; border: 1px solid black"> */
	HONEYDEW(0xF0FFF0),
	/** Hotpink <div style="background-color: hotpink; padding: 10px; border: 1px solid black"> */
	HOTPINK(0xFF69B4),
	/** Indianred <div style="background-color: indianred; padding: 10px; border: 1px solid black"> */
	INDIANRED(0xCD5C5C),
	/** Indigo <div style="background-color: indigo; padding: 10px; border: 1px solid black"> */
	INDIGO(0x4B0082),
	/** Ivory <div style="background-color: ivory; padding: 10px; border: 1px solid black"> */
	IVORY(0xFFFFF0),
	/** Khaki <div style="background-color: khaki; padding: 10px; border: 1px solid black"> */
	KHAKI(0xF0E68C),
	/** Lavender <div style="background-color: lavender; padding: 10px; border: 1px solid black"> */
	LAVENDER(0xE6E6FA),
	/** Lavenderblush <div style="background-color: lavenderblush; padding: 10px; border: 1px solid black"> */
	LAVENDERBLUSH(0xFFF0F5),
	/** Lawngreen <div style="background-color: lawngreen; padding: 10px; border: 1px solid black"> */
	LAWNGREEN(0x7CFC00),
	/** Lemonchiffon <div style="background-color: lemonchiffon; padding: 10px; border: 1px solid black"> */
	LEMONCHIFFON(0xFFFACD),
	/** Lightblue <div style="background-color: lightblue; padding: 10px; border: 1px solid black"> */
	LIGHTBLUE(0xADD8E6),
	/** Lightcoral <div style="background-color: lightcoral; padding: 10px; border: 1px solid black"> */
	LIGHTCORAL(0xF08080),
	/** Lightcyan <div style="background-color: lightcyan; padding: 10px; border: 1px solid black"> */
	LIGHTCYAN(0xE0FFFF),
	/** Lightgoldenrodyellow <div style="background-color: lightgoldenrodyellow; padding: 10px; border: 1px solid black"> */
	LIGHTGOLDENRODYELLOW(0xFAFAD2),
	/** Lightgray <div style="background-color: lightgray; padding: 10px; border: 1px solid black"> */
	LIGHTGRAY(0xD3D3D3),
	/** Lightgreen <div style="background-color: lightgreen; padding: 10px; border: 1px solid black"> */
	LIGHTGREEN(0x90EE90),
	/** Lightgrey <div style="background-color: lightgrey; padding: 10px; border: 1px solid black"> */
	LIGHTGREY(0xD3D3D3),
	/** Lightpink <div style="background-color: lightpink; padding: 10px; border: 1px solid black"> */
	LIGHTPINK(0xFFB6C1),
	/** Lightsalmon <div style="background-color: lightsalmon; padding: 10px; border: 1px solid black"> */
	LIGHTSALMON(0xFFA07A),
	/** Lightseagreen <div style="background-color: lightseagreen; padding: 10px; border: 1px solid black"> */
	LIGHTSEAGREEN(0x20B2AA),
	/** Lightskyblue <div style="background-color: lightskyblue; padding: 10px; border: 1px solid black"> */
	LIGHTSKYBLUE(0x87CEFA),
	/** Lightslategray <div style="background-color: lightslategray; padding: 10px; border: 1px solid black"> */
	LIGHTSLATEGRAY(0x778899),
	/** Lightslategrey <div style="background-color: lightslategrey; padding: 10px; border: 1px solid black"> */
	LIGHTSLATEGREY(0x778899),
	/** Lightsteelblue <div style="background-color: lightsteelblue; padding: 10px; border: 1px solid black"> */
	LIGHTSTEELBLUE(0xB0C4DE),
	/** Lightyellow <div style="background-color: lightyellow; padding: 10px; border: 1px solid black"> */
	LIGHTYELLOW(0xFFFFE0),
	/** Lime <div style="background-color: lime; padding: 10px; border: 1px solid black"> */
	LIME(0x00FF00),
	/** Limegreen <div style="background-color: limegreen; padding: 10px; border: 1px solid black"> */
	LIMEGREEN(0x32CD32),
	/** Linen <div style="background-color: linen; padding: 10px; border: 1px solid black"> */
	LINEN(0xFAF0E6),
	/** Magenta <div style="background-color: magenta; padding: 10px; border: 1px solid black"> */
	MAGENTA(0xFF00FF),
	/** Maroon <div style="background-color: maroon; padding: 10px; border: 1px solid black"> */
	MAROON(0x800000),
	/** Mediumaquamarine <div style="background-color: mediumaquamarine; padding: 10px; border: 1px solid black"> */
	MEDIUMAQUAMARINE(0x66CDAA),
	/** Mediumblue <div style="background-color: mediumblue; padding: 10px; border: 1px solid black"> */
	MEDIUMBLUE(0x0000CD),
	/** Mediumorchid <div style="background-color: mediumorchid; padding: 10px; border: 1px solid black"> */
	MEDIUMORCHID(0xBA55D3),
	/** Mediumpurple <div style="background-color: mediumpurple; padding: 10px; border: 1px solid black"> */
	MEDIUMPURPLE(0x9370DB),
	/** Mediumseagreen <div style="background-color: mediumseagreen; padding: 10px; border: 1px solid black"> */
	MEDIUMSEAGREEN(0x3CB371),
	/** Mediumslateblue <div style="background-color: mediumslateblue; padding: 10px; border: 1px solid black"> */
	MEDIUMSLATEBLUE(0x7B68EE),
	/** Mediumspringgreen <div style="background-color: mediumspringgreen; padding: 10px; border: 1px solid black"> */
	MEDIUMSPRINGGREEN(0x00FA9A),
	/** Mediumturquoise <div style="background-color: mediumturquoise; padding: 10px; border: 1px solid black"> */
	MEDIUMTURQUOISE(0x48D1CC),
	/** Mediumvioletred <div style="background-color: mediumvioletred; padding: 10px; border: 1px solid black"> */
	MEDIUMVIOLETRED(0xC71585),
	/** Midnightblue <div style="background-color: midnightblue; padding: 10px; border: 1px solid black"> */
	MIDNIGHTBLUE(0x191970),
	/** Mintcream <div style="background-color: mintcream; padding: 10px; border: 1px solid black"> */
	MINTCREAM(0xF5FFFA),
	/** Mistyrose <div style="background-color: mistyrose; padding: 10px; border: 1px solid black"> */
	MISTYROSE(0xFFE4E1),
	/** Moccasin <div style="background-color: moccasin; padding: 10px; border: 1px solid black"> */
	MOCCASIN(0xFFE4B5),
	/** Navajowhite <div style="background-color: navajowhite; padding: 10px; border: 1px solid black"> */
	NAVAJOWHITE(0xFFDEAD),
	/** Navy <div style="background-color: navy; padding: 10px; border: 1px solid black"> */
	NAVY(0x000080),
	/** Oldlace <div style="background-color: oldlace; padding: 10px; border: 1px solid black"> */
	OLDLACE(0xFDF5E6),
	/** Olive <div style="background-color: olive; padding: 10px; border: 1px solid black"> */
	OLIVE(0x808000),
	/** Olivedrab <div style="background-color: olivedrab; padding: 10px; border: 1px solid black"> */
	OLIVEDRAB(0x6B8E23),
	/** Orange <div style="background-color: orange; padding: 10px; border: 1px solid black"> */
	ORANGE(0xFFA500),
	/** Orangered <div style="background-color: orangered; padding: 10px; border: 1px solid black"> */
	ORANGERED(0xFF4500),
	/** Orchid <div style="background-color: orchid; padding: 10px; border: 1px solid black"> */
	ORCHID(0xDA70D6),
	/** Palegoldenrod <div style="background-color: palegoldenrod; padding: 10px; border: 1px solid black"> */
	PALEGOLDENROD(0xEEE8AA),
	/** Palegreen <div style="background-color: palegreen; padding: 10px; border: 1px solid black"> */
	PALEGREEN(0x98FB98),
	/** Paleturquoise <div style="background-color: paleturquoise; padding: 10px; border: 1px solid black"> */
	PALETURQUOISE(0xAFEEEE),
	/** Palevioletred <div style="background-color: palevioletred; padding: 10px; border: 1px solid black"> */
	PALEVIOLETRED(0xDB7093),
	/** Papayawhip <div style="background-color: papayawhip; padding: 10px; border: 1px solid black"> */
	PAPAYAWHIP(0xFFEFD5),
	/** Peachpuff <div style="background-color: peachpuff; padding: 10px; border: 1px solid black"> */
	PEACHPUFF(0xFFDAB9),
	/** Peru <div style="background-color: peru; padding: 10px; border: 1px solid black"> */
	PERU(0xCD853F),
	/** Pink <div style="background-color: pink; padding: 10px; border: 1px solid black"> */
	PINK(0xFFC0CB),
	/** Plum <div style="background-color: plum; padding: 10px; border: 1px solid black"> */
	PLUM(0xDDA0DD),
	/** Powderblue <div style="background-color: powderblue; padding: 10px; border: 1px solid black"> */
	POWDERBLUE(0xB0E0E6),
	/** Purple <div style="background-color: purple; padding: 10px; border: 1px solid black"> */
	PURPLE(0x800080),
	/** Red <div style="background-color: red; padding: 10px; border: 1px solid black"> */
	RED(0xFF0000),
	/** Rosybrown <div style="background-color: rosybrown; padding: 10px; border: 1px solid black"> */
	ROSYBROWN(0xBC8F8F),
	/** Royalblue <div style="background-color: royalblue; padding: 10px; border: 1px solid black"> */
	ROYALBLUE(0x4169E1),
	/** Saddlebrown <div style="background-color: saddlebrown; padding: 10px; border: 1px solid black"> */
	SADDLEBROWN(0x8B4513),
	/** Salmon <div style="background-color: salmon; padding: 10px; border: 1px solid black"> */
	SALMON(0xFA8072),
	/** Sandybrown <div style="background-color: sandybrown; padding: 10px; border: 1px solid black"> */
	SANDYBROWN(0xF4A460),
	/** Seagreen <div style="background-color: seagreen; padding: 10px; border: 1px solid black"> */
	SEAGREEN(0x2E8B57),
	/** Seashell <div style="background-color: seashell; padding: 10px; border: 1px solid black"> */
	SEASHELL(0xFFF5EE),
	/** Sienna <div style="background-color: sienna; padding: 10px; border: 1px solid black"> */
	SIENNA(0xA0522D),
	/** Silver <div style="background-color: silver; padding: 10px; border: 1px solid black"> */
	SILVER(0xC0C0C0),
	/** Skyblue <div style="background-color: skyblue; padding: 10px; border: 1px solid black"> */
	SKYBLUE(0x87CEEB),
	/** Slateblue <div style="background-color: slateblue; padding: 10px; border: 1px solid black"> */
	SLATEBLUE(0x6A5ACD),
	/** Slategray <div style="background-color: slategray; padding: 10px; border: 1px solid black"> */
	SLATEGRAY(0x708090),
	/** Slategrey <div style="background-color: slategrey; padding: 10px; border: 1px solid black"> */
	SLATEGREY(0x708090),
	/** Snow <div style="background-color: snow; padding: 10px; border: 1px solid black"> */
	SNOW(0xFFFAFA),
	/** Springgreen <div style="background-color: springgreen; padding: 10px; border: 1px solid black"> */
	SPRINGGREEN(0x00FF7F),
	/** Steelblue <div style="background-color: steelblue; padding: 10px; border: 1px solid black"> */
	STEELBLUE(0x4682B4),
	/** Tan <div style="background-color: tan; padding: 10px; border: 1px solid black"> */
	TAN(0xD2B48C),
	/** Teal <div style="background-color: teal; padding: 10px; border: 1px solid black"> */
	TEAL(0x008080),
	/** Thistle <div style="background-color: thistle; padding: 10px; border: 1px solid black"> */
	THISTLE(0xD8BFD8),
	/** Tomato <div style="background-color: tomato; padding: 10px; border: 1px solid black"> */
	TOMATO(0xFF6347),
	/** Turquoise <div style="background-color: turquoise; padding: 10px; border: 1px solid black"> */
	TURQUOISE(0x40E0D0),
	/** Violet <div style="background-color: violet; padding: 10px; border: 1px solid black"> */
	VIOLET(0xEE82EE),
	/** Wheat <div style="background-color: wheat; padding: 10px; border: 1px solid black"> */
	WHEAT(0xF5DEB3),
	/** White <div style="background-color: white; padding: 10px; border: 1px solid black"> */
	WHITE(0xFFFFFF),
	/** Whitesmoke <div style="background-color: whitesmoke; padding: 10px; border: 1px solid black"> */
	WHITESMOKE(0xF5F5F5),
	/** Yellow <div style="background-color: yellow; padding: 10px; border: 1px solid black"> */
	YELLOW(0xFFFF00),
	/** Yellowgreen <div style="background-color: yellowgreen; padding: 10px; border: 1px solid black"> */
	YELLOWGREEN(0x9ACD32);

	public final int rgb;

	/**
	 * Constructor
	 * @param value RGB input
	 */
	private Color(final int rgb) {
		this.rgb = rgb;
	}
	
	/**
	 * Returns the red component
	 * @return the red component
	 */
	public int getRed() {
		return (rgb >> 16) & 0xFF;
	}

	/**
	 * Returns the green component
	 * @return the green component
	 */
	public int getGreen() {
		return (rgb >> 8) & 0xFF;
	}

	/**
	 * Returns the blue component
	 * @return the blue component
	 */
	public int getBlue() {
		return rgb & 0xFF;
	}
	
	/**
	 * Returns red (bits 16..23), green (bits 8..15) and blue (bits 0..7) components as an unique integer
	 * @return red, green and blue components as an unique integer
	 */
	public int getRGB() {
		return rgb;
	}

	/**
	 * Returns hue (bits 16..23), saturation (bits 8..15) and value (bits 0..7) as an unique integer
	 * @return hue, saturation and value as an unique integer
	 */
	public int getHSV() {
		return hsv(getHueSaturationValueInts());
	}

	/**
	 * Returns red, green and blue components
	 * @return red, green and blue components
	 */
	public int[] getRedGreenBlue() {
		return rgb(rgb);
	}



	/**
	 * Returns the hue (degrees), saturation (ratio between 0 and 1) and value (ratio between 0 and 1)
	 * @return the hue (degrees), saturation (ratio between 0 and 1) and value (ratio between 0 and 1)
	 */
	public double[] getHueSaturationValue() {
		return rgb2hsv(rgb(rgb));
	}

	/**
	 * Returns the hue (degrees), saturation (ratio between 0 and 1) and value (ratio between 0 and 1)
	 * @return the hue (degrees), saturation (ratio between 0 and 1) and value (ratio between 0 and 1)
	 */
	public float[] getHueSaturationValueFloats() {
		return rgb2hsvFloats(rgb(rgb));
	}
	
	/**
	 * Returns the hue (8 bits), saturation (8 bits) and value (8 bits)
	 * @return the hue (8 bits), saturation (8 bits) and value (8 bits)
	 */
	public int[] getHueSaturationValueInts() {
		return hsv(getHueSaturationValueFloats());		
	}

	/**
	 * Returns CIE-L*ab (D65/2° standard illuminant)
	 * @return CIE-L*ab (D65/2° standard illuminant)
	 */
	public double[] getLab() {
		return xyz2Lab(D65, 2, rgb2xyz(rgb(rgb)));
	}

	/**
	 * Returns CIE-L*uv (D65/2° standard illuminant)
	 * @return CIE-L*uv (D65/2° standard illuminant)
	 */
	public double[] getLuv() {
		return xyz2Luv(D65, 2, rgb2xyz(rgb(rgb)));
	}
	
	/**
	 * Returns the hue in degrees
	 * @return the hue in degrees 
	 */
	public double getHue() {
		return getHueSaturationValue()[0];
	}
	
	/**
	 * Returns the saturation [0..1] 
	 * @return the saturation [0..1] 
	 */
	public double getSaturation() {
		return getHueSaturationValue()[1];
	}
	
	/**
	 * Returns the value [0..1] 
	 * @return the value [0..1]
	 */
	public double getValue() {
		return getHueSaturationValue()[2];
	}

	/**
	 * Find the closest color by RGB distance
	 * @param rgb red (bits 16..23), green (bits 8..15) and blue (bits 0..7) components as an unique integer
	 * @return the closest color
	 */
	public static Color findClosestColorByRedGreenBlueDistance(final int rgb, Color[] values) {
		final int[] redGreenBlue = rgb(rgb);
		double shortest = Double.MAX_VALUE;
		Color result = BLACK;
		for(final Color color : values) {
			if(color.rgb == rgb) {
				// Exact match
				return color;
			}
		
			final double distance = distance(color.getRedGreenBlue(), redGreenBlue);
			if(distance < shortest) {
				shortest = distance;
				result = color;
			}
		}

		return result;
	}

	/**
	 * Find the closest color by RGB distance #2
	 * @param rgb red (bits 16..23), green (bits 8..15) and blue (bits 0..7) components as an unique integer
	 * @return the closest color
	 */
	public static Color findClosestColorByRedGreenBlueDistanceWeighted(final int rgb, Color[] values) {
		final int[] rgb0 = rgb(rgb);
		double shortest = Double.MAX_VALUE;
		Color result = BLACK;
		for(final Color color : values) {
			if(color.rgb == rgb) {
				// Exact match
				return color;
			}
			
			int[] rgb1 = color.getRedGreenBlue();
			final int meanR = (rgb0[0] + rgb1[0]) >> 1;
			final int[] w = (meanR < 128) ? new int[] { 2, 4, 3 } : new int[] { 3, 4, 2 }; // color weights
			final int weightedDeltaSquarasSum = (w[0] * (rgb0[0] - rgb1[0])*(rgb0[0] - rgb1[0]))+(w[1] * (rgb0[1] - rgb1[1])*(rgb0[1] - rgb1[1]))+(w[2] * (rgb0[2] - rgb1[2])*(rgb0[2] - rgb1[2])); 
			final double distance = sqrt((double)weightedDeltaSquarasSum);

			if(distance < shortest) {
				shortest = distance;
				result = color;
			}
		}
		
		return result;
	}
	
	/**
	 * Find the closest color by HSV distance
	 * @param rgb red (bits 16..23), green (bits 8..15) and blue (bits 0..7) components as an unique integer
	 * @return the closest color
	 */
	public static Color findClosestColorByHueSaturationValueDistance(final int rgb, Color[] values) {
		double[] hsv0 = rgb2hsv(rgb(rgb));
		double hue0 = hsv0[0];

		double shortest = Double.MAX_VALUE;
		Color result = BLACK;
		for(final Color color : values) {
			if(rgb == color.rgb) {
				// Exact match
				return color;
			}
		
			double[] hsv1 = color.getHueSaturationValue();
			double hue1 = hsv1[0];
			
			double deltaH;
			if(hue1 > hue0) {
				deltaH = hue1 - hue0;
			} else {
				deltaH = hue0 - hue1;
			}
			
			if(deltaH > 180d) {
				deltaH = 360d - deltaH;
			}
			
			if(deltaH > 0d) {
				deltaH /= 360d;
			}

			hsv0[0] = 0d;
			hsv1[0] = deltaH;
			
			final double distance = distance(hsv0, hsv1);
			if(distance < shortest) {
				shortest = distance;
				result = color;
			}
		}

		return result;
	}
	
	/**
	 * Find the closest color by HSV weighted distance
	 * @param rgb red (bits 16..23), green (bits 8..15) and blue (bits 0..7) components as an unique integer
	 * @return the closest color
	 */
	public static Color findClosestColorByHueSaturationValueDistanceWeighted(final int rgb, Color[] values) {
		double[] hsv0 = rgb2hsv(rgb(rgb));
		double hue0 = hsv0[0];

		double shortest = Double.MAX_VALUE;
		Color result = BLACK;
		for(final Color color : values) {
			if(rgb == color.rgb) {
				// Exact match
				return color;
			}
			
			double[] hsv1 = color.getHueSaturationValue();
			double hue1 = hsv1[0];
			
			double deltaH;
			if(hue1 > hue0) {
				deltaH = hue1 - hue0;
			} else {
				deltaH = hue0 - hue1;
			}
			
			if(deltaH > 180d) {
				deltaH = 360d - deltaH;
			}
			
			if(deltaH > 0d) {
				deltaH /= 360d;
			}
			
			hsv0[0] = 0d;
			hsv1[0] = deltaH;
			
			// cf. https://stackoverflow.com/questions/1720528/what-is-the-best-algorithm-for-finding-the-closest-color-in-an-array-to-another#comment-25927083
			final double[] w = {0.475d, 0.2875d, 0.2375d};
			final double weightedDeltaSquarasSum = (w[0] * (hsv0[0] - hsv1[0])*(hsv0[0] - hsv1[0]))+(w[1] * (hsv0[1] - hsv1[1])*(hsv0[1] - hsv1[1]))+(w[2] * (hsv0[2] - hsv1[2])*(hsv0[2] - hsv1[2])); 
			final double distance = sqrt(weightedDeltaSquarasSum);

			if(distance < shortest) {
				shortest = distance;
				result = color;
			}
		}
		
		return result;
	}

	/**
	 * Find the closest color by CIE-L*ab distance
	 * @param rgb red (bits 16..23), green (bits 8..15) and blue (bits 0..7) components as an unique integer
	 * @return the closest color
	 */
	public static Color findClosestColorByLabDistance(final int rgb, Color[] values) {
		final double[] lab = xyz2Lab(D65, 2, rgb2xyz(rgb(rgb)));
		
		double shortest = Double.MAX_VALUE;
		Color result = BLACK;
		for(final Color color : values) {
			if(rgb == color.rgb) {
				// Exact match
				return color;
			}
		
			final double distance = distance(lab, color.getLab());
			if(distance < shortest) {
				shortest = distance;
				result = color;
			}
		}

		return result;
	}

	/**
	 * Find the closest color by CIE-L*uv distance
	 * @param rgb red (bits 16..23), green (bits 8..15) and blue (bits 0..7) components as an unique integer
	 * @return the closest color
	 */
	public static Color findClosestColorByLuvDistance(final int rgb, Color[] values) {
		final double[] luv = xyz2Luv(D65, 2, rgb2xyz(rgb(rgb)));
		
		double shortest = Double.MAX_VALUE;
		Color result = BLACK;
		for(final Color color : values) {
			if(rgb == color.rgb) {
				// Exact match
				return color;
			}
			
			final double distance = distance(luv, color.getLuv());
			if(distance < shortest) {
				shortest = distance;
				result = color;
			}
		}
		
		return result;
	}

	/**
	 * Computes the distance between two 3-dimension points
	 * @param a first point coordinates
	 * @param b second point coordinates
	 * @return the distance
	 */
	private static double distance(int[] a, int[] b) {
		final int deltaSquarasSum = ((a[0] - b[0])*(a[0] - b[0]))+((a[1] - b[1])*(a[1] - b[1]))+((a[2] - b[2])*(a[2] - b[2])); 
		final double result = sqrt((double)deltaSquarasSum);
		return result;
	}

	/**
	 * Computes the distance between two 3-dimension points
	 * @param a first point coordinates
	 * @param b second point coordinates
	 * @return the distance
	 */
	private static double distance(double[] a, double[] b) {
		final double deltaSquarasSum = ((a[0] - b[0])*(a[0] - b[0]))+((a[1] - b[1])*(a[1] - b[1]))+((a[2] - b[2])*(a[2] - b[2])); 
		final double result = sqrt((double)deltaSquarasSum);
		return result;
	}
	
	/**
	 * Returns red, green and blue components
	 * @param rgb red, green and blue components as an unique integer
	 * @return red, green and blue components
	 */
	public static int[] rgb(final int rgb) {
		return new int [] { (rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF } ; 		
	}

	/**
	 * Returns red, green and blue components
	 * @param rgb red, green and blue components as an unique integer
	 * @return red, green and blue components
	 */
	public static int rgb(final int[] rgb) {
		return (rgb[0] << 16) | (rgb[1] << 8) |rgb[2] ; 		
	}
	
	/**
	 * Returns the hue (8 bits), saturation (8 bits) and value (8 bits)
	 * @param hueSaturationValue hue (degrees), saturation (ratio between 0 and 1) and value (ratio between 0 and 1)
	 * @return the hue (8 bits), saturation (8 bits) and value (8 bits)
	 */
	private static int[] hsv(final float[] hueSaturationValue) {
		return new int [] { (int)(hueSaturationValue[0] * 255F / 360F), (int)(hueSaturationValue[1] * 255F), (int)(hueSaturationValue[2] * 255F) };
	}
	
	/**
	 * Returns hue (bits 16..23), saturation (bits 8..15) and value (bits 0..7) as an unique integer
	 * @param hueSaturationValue hue (8 bits), saturation (8 bits) and value (8 bits)
	 * @return hue, saturation and value as an unique integer
	 */
	private static final int hsv(final int [] hueSaturationValue) {
		return (hueSaturationValue[0] << 16) | (hueSaturationValue[1] << 8) | hueSaturationValue[2];
	}

	/**
	 * Returns the hue (degrees), saturation (ratio between 0 and 1) and value (ratio between 0 and 1)<br>
	 * <a href="https://en.wikipedia.org/wiki/HSL_and_HSV">HSL and HSV</a>
	 * @param redGreenBlue red, green and blue components
	 * @return the hue (degrees), saturation (ratio between 0 and 1) and value (ratio between 0 and 1)
	 */
	public static double[] rgb2hsv(final int[] redGreenBlue) {
		final int red = redGreenBlue[0];
		final int green = redGreenBlue[1];
		final int blue = redGreenBlue[2];
		final double r = ((double) red) / 255d;
		final double g = ((double) green) / 255d;
		final double b = ((double) blue) / 255d;
		
		final int max;
		if(red >= green) {
			if(red >= blue) {
				max = red;
			} else {
				max = blue;
			}
		} else if (green >= blue) {
			max = green;
		} else {
			max = blue;
		}
	
		final int min;
		if(red <= green) {
			if(red <= blue) {
				min = red;
			} else {
				min = blue;
			}
		} else if (green <= blue) {
			min = green;
		} else {
			min = blue;
		}
	
		final int delta = max - min;
		final double d = ((double) delta) / 255d;
		
		double hue;
		if(delta == 0) {
			hue = 0d;
		} else if (max == red) {
			hue = 60d * ((g - b) / d);
		} else if (max == green) {
			hue = 60d * (((b - r) / d) + 2d);
		} else /* if (max == blue) */ {
			hue = 60d * (((r - g) / d) + 4d);
		}
		if(hue < 0d) {
			hue += 360d;
		}
		
		final double saturation;
		if(max == 0) {
			saturation = 0d;
		} else {
			saturation = (double)delta / (double)max;
		}

		final double value = ((double)max) / 255d;

		return new double [] {hue, saturation, value}  ;
	}

	/**
	 * Returns the hue (degrees), saturation (ratio between 0 and 1) and value (ratio between 0 and 1)<br>
	 * <a href="https://en.wikipedia.org/wiki/HSL_and_HSV">HSL and HSV</a>
	 * @param redGreenBlue red, green and blue components
	 * @return the hue (degrees), saturation (ratio between 0 and 1) and value (ratio between 0 and 1)
	 */
	public static float[] rgb2hsvFloats(final int[] redGreenBlue) {
		final int red = redGreenBlue[0];
		final int green = redGreenBlue[1];
		final int blue = redGreenBlue[2];
		final float r = ((float) red) / 255F;
		final float g = ((float) green) / 255F;
		final float b = ((float) blue) / 255F;
		
		final int max;
		if(red >= green) {
			if(red >= blue) {
				max = red;
			} else {
				max = blue;
			}
		} else if (green >= blue) {
			max = green;
		} else {
			max = blue;
		}
		
		final int min;
		if(red <= green) {
			if(red <= blue) {
				min = red;
			} else {
				min = blue;
			}
		} else if (green <= blue) {
			min = green;
		} else {
			min = blue;
		}
		
		final int delta = max - min;
		final float d = ((float) delta) / 255F;
		
		float hue;
		if(delta == 0) {
			hue = 0F;
		} else if (max == red) {
			hue = 60F * ((g - b) / d);
		} else if (max == green) {
			hue = 60F * (((b - r) / d) + 2F);
		} else /* if (max == blue) */ {
			hue = 60F * (((r - g) / d) + 4F);
		}
		if(hue < 0d) {
			hue += 360F;
		}
		
		final float saturation;
		if(max == 0) {
			saturation = 0F;
		} else {
			saturation = (float)delta / (float)max;
		}
		
		final float value = ((float)max) / 255F;
		
		return new float [] {hue, saturation, value}  ;
	}
	
	/**
	 * Returns X, Y and Z referring to a D65/2° standard illuminant.
	 * @param redGreenBlue red, green and blue components
	 * @return X, Y and Z referring to a D65/2° standard illuminant.
	 */
	public static double[] rgb2xyz(final int[] redGreenBlue) {
		final int red = redGreenBlue[0];
		final int green = redGreenBlue[1];
		final int blue = redGreenBlue[2];
		double r = ((double) red) / 255d;
		double g = ((double) green) / 255d;
		double b = ((double) blue) / 255d;

		if (r > 0.04045d) {
			r = pow((r + 0.055d) / 1.055d, 2.4d);
		} else {
			r = r / 12.92d;
		}

		if (g > 0.04045d) {
			g = pow((g + 0.055d) / 1.055d, 2.4d);
		} else {
			g = g / 12.92d;
		}

		if (b > 0.04045d) {
			b = pow((b + 0.055d) / 1.055d, 2.4d);
		} else {
			b = b / 12.92d;
		}

		r *= 100d;
		g *= 100d;
		b *= 100d;

		double X = r * 0.4124d + g * 0.3576d + b * 0.1805d;
		double Y = r * 0.2126d + g * 0.7152d + b * 0.0722d;
		double Z = r * 0.0193d + g * 0.1192d + b * 0.9505d;

		return new double[] { X, Y, Z };
	}

	/**
	 * X, Y, Z to CIE-L*ab
	 * @param illuminant illuminant
	 * @param degrees either 2° or 10°
	 * @param xyz X, Y, Z
	 * @return CIE-L*ab
	 */
	public static double[] xyz2Lab(final Tristimulus illuminant, final int degrees, final double[] xyz) {
		double varX = xyz[0] / illuminant.x(degrees);
		double varY = xyz[1] / illuminant.y(degrees);
		double varZ = xyz[2] / illuminant.z(degrees);

		if (varX > 0.008856d) {
			varX = pow(varX, 1d / 3d);
		} else {
			varX = (7.787d * varX) + (16d / 116d);
		}
		if (varY > 0.008856d) {
			varY = pow(varY, 1d / 3d);
		} else {
			varY = (7.787 * varY) + (16d / 116d);
		}
		if (varZ > 0.008856d) {
			varZ = pow(varZ, 1d / 3d);
		} else {
			varZ = (7.787d * varZ) + (16d / 116d);
		}

		final double l = (116d * varY) - 16d;
		final double a = 500d * (varX - varY);
		final double b = 200d * (varY - varZ);

		return new double[] { l, a, b };
	}
	
	/**
	 * X, Y, Z to CIE-L*uv
	 * @param illuminant illuminant
	 * @param degrees either 2° or 10°
	 * @param xyz X, Y, Z
	 * @return CIE-L*uv
	 */
	public static double[] xyz2Luv(final Tristimulus illuminant, final int degrees, final double[] xyz) {
		final double x = xyz[0];
		final double y = xyz[1];
		final double z = xyz[2];
		
		final double varU = (4d * x) / (x + (15d * y) + (3d * z));
		final double varV = (9d * y) / (x + (15d * y) + (3d * z));

		double varY = y / 100d;
		if (varY > 0.008856d) {
			varY = pow(varY, (1d / 3d));
		} else {
			varY = (7.787d * varY) + (16d / 116d);
		}
		
		final double refX = illuminant.x(degrees);
		final double refY = illuminant.y(degrees);
		final double refz = illuminant.z(degrees);
		
		final double refU = ( 4d * refX ) / ( refX + ( 15d * refY ) + ( 3d * refz ) );
		final double refV = ( 9d * refY ) / ( refX + ( 15d * refY ) + ( 3d * refz ) );
		
		final double l = ( 116d * varY ) - 16d;
		final double u = 13d * l * ( varU - refU );
		final double v = 13d * l * ( varV - refV );
		
		return new double[] { l, u, v };
	}
	
	/**
	 * Converts Hue Saturation Value components to Red Green Blue components
	 * @param hueSaturationValue Hue Saturation Value components
	 * @return Red Green Blue components
	 */
	public static int[] hsv2rgb(final float[] hueSaturationValue) {
		final float h = hueSaturationValue[0];
		final float s = hueSaturationValue[0];
		final float l = hueSaturationValue[0];

		final int r, g, b;
		if (s == 0F) {
			r = (int) (l * 255F);
			g = (int) (l * 255F);
			b = (int) (l * 255F);
		} else {

			final float v2;
			if (l < 0.5F) {
				v2 = l * (1 + s);
			} else {
				v2 = (l + s) - (s * l);
			}

			final float v1 = 2 * l - v2;

			r = (int) (255F * hue2rgb(v1, v2, h + (1F / 3F)));
			g = (int) (255F * hue2rgb(v1, v2, h));
			b = (int) (255F * hue2rgb(v1, v2, h - (1F / 3F)));
		}

		return new int[] { r, b, g };
	}
	
	/**
	 * Hue to RGB conversion factor computation
	 * @param v1 variable #1
	 * @param v2 variable #2
	 * @param vH variable Hue
	 * @return conversion factor
	 */
	private static float hue2rgb(float v1, float v2, float vH) {
		// @formatter:off
		if ( vH < 0F ) vH += 1F;
		if ( vH > 1F ) vH -= 1F;
		if ( ( 6F * vH ) < 1F ) return ( v1 + ( v2 - v1 ) * 6F * vH );
		if ( ( 2F * vH ) < 1F ) return v2 ;
		if ( ( 3F * vH ) < 2F ) return ( v1 + ( v2 - v1 ) * ( ( 2F / 3F ) - vH ) * 6F );
		return v1; 
		// @formatter:on
	}
}
