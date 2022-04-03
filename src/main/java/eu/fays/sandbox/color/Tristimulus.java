package eu.fays.sandbox.color;

import static java.text.MessageFormat.format;
/**
 * <a href="https://www.easyrgb.com/en/math.php#m_xyz_ref">XYZ (Tristimulus) Reference values of a perfect reflecting diffuser</a> 
 */
public enum Tristimulus {

	/** "A" Observer Illuminant */
	A(109.850	, 100.000	, 35.585	, 111.144	, 100.000	, 35.200	, "Incandescent/tungsten"),
	/** "B" Observer Illuminant */
	B(99.0927	, 100.000	, 85.313	, 99.178	, 100.000	, 84.3493	, "Old direct sunlight at noon"),
	/** "C" Observer Illuminant */
	C(98.074	, 100.000	, 118.232	, 97.285	, 100.000	, 116.145	, "Old daylight"),
	/** "D50" Observer Illuminant */
	D50(96.422	, 100.000	, 82.521	, 96.720	, 100.000	, 81.427	, "ICC profile PCS"),
	/** "D55" Observer Illuminant */
	D55(95.682	, 100.000	, 92.149	, 95.799	, 100.000	, 90.926	, "Mid-morning daylight"),
	/** "D65" Observer Illuminant */
	D65(95.047	, 100.000	, 108.883	, 94.811	, 100.000	, 107.304	, "Daylight, sRGB, Adobe-RGB"),
	/** "D75" Observer Illuminant */
	D75(94.972	, 100.000	, 122.638	, 94.416	, 100.000	, 120.641	, "North sky daylight"),
	/** "E" Observer Illuminant */
	E(100.000	, 100.000	, 100.000	, 100.000	, 100.000	, 100.000	, "Equal energy"),
	/** "F1" Observer Illuminant */
	F1(92.834	, 100.000	, 103.665	, 94.791	, 100.000	, 103.191	, "Daylight Fluorescent"),
	/** "F2" Observer Illuminant */
	F2(99.187	, 100.000	, 67.395	, 103.280	, 100.000	, 69.026	, "Cool fluorescent"),
	/** "F3" Observer Illuminant */
	F3(103.754	, 100.000	, 49.861	, 108.968	, 100.000	, 51.965	, "White Fluorescent"),
	/** "F4" Observer Illuminant */
	F4(109.147	, 100.000	, 38.813	, 114.961	, 100.000	, 40.963	, "Warm White Fluorescent"),
	/** "F5" Observer Illuminant */
	F5(90.872	, 100.000	, 98.723	, 93.369	, 100.000	, 98.636	, "Daylight Fluorescent"),
	/** "F6" Observer Illuminant */
	F6(97.309	, 100.000	, 60.191	, 102.148	, 100.000	, 62.074	, "Lite White Fluorescent"),
	/** "F7" Observer Illuminant */
	F7(95.044	, 100.000	, 108.755	, 95.792	, 100.000	, 107.687	, "Daylight fluorescent, D65 simulator"),
	/** "F8" Observer Illuminant */
	F8(96.413	, 100.000	, 82.333	, 97.115	, 100.000	, 81.135	, "Sylvania F40, D50 simulator"),
	/** "F9" Observer Illuminant */
	F9(100.365	, 100.000	, 67.868	, 102.116	, 100.000	, 67.826	, "Cool White Fluorescent"),
	/** "F10" Observer Illuminant */
	F10(96.174	, 100.000	, 81.712	, 99.001	, 100.000	, 83.134	, "Ultralume 50, Philips TL85"),
	/** "F11" Observer Illuminant */
	F11(100.966	, 100.000	, 64.370	, 103.866	, 100.000	, 65.627	, "Ultralume 40, Philips TL84"),
	/** "F12" Observer Illuminant */
	F12(108.046	, 100.000	, 39.228	, 111.428	, 100.000	, 40.353	, "Ultralume 30, Philips TL83");

	/** X 2° (CIE 1931) */
	public final double x2;
	/** Y 2° (CIE 1931) */
	public final double y2;
	/** Z 2° (CIE 1931) */
	public final double z2;
	/** X 10° (CIE 1964) */
	public final double x10;
	/** Y 10° (CIE 1964) */
	public final double y10;
	/** Z 10° (CIE 1964) */
	public final double z10;
	/** Note */
	public final String note;

	/**
	 * Returns X at either 2° or 10°
	 * 
	 * @param degrees either 2° or 10°
	 * @return X at either 2° or 10°
	 * @throws IllegalArgumentException in case degrees is neither 2 nor 10
	 */
	public double x(final int degrees) {
		if (degrees == 2) {
			return x2;
		} else if (degrees == 10) {
			return x10;
		} else {
			throw new IllegalArgumentException(format("Argumment degrees {0,number,0} is neither 2 nor 10!", degrees));
		}
	}

	/**
	 * Returns Y at either 2° or 10°
	 * 
	 * @param degrees either 2° or 10°
	 * @return Y at either 2° or 10°
	 * @throws IllegalArgumentException in case degrees is neither 2 nor 10
	 */
	public double y(final int degrees) {
		if (degrees == 2) {
			return y2;
		} else if (degrees == 10) {
			return y10;
		} else {
			throw new IllegalArgumentException(format("Argumment degrees {0,number,0} is neither 2 nor 10!", degrees));
		}
	}

	/**
	 * Returns Z at either 2° or 10°
	 * 
	 * @param degrees either 2° or 10°
	 * @return Z at either 2° or 10°
	 * @throws IllegalArgumentException in case degrees is neither 2 nor 10
	 */
	public double z(final int degrees) {
		if (degrees == 2) {
			return z2;
		} else if (degrees == 10) {
			return z10;
		} else {
			throw new IllegalArgumentException(format("Argumment degrees {0,number,0} is neither 2 nor 10!", degrees));
		}
	}
	
	/**
	 * Constructor
	 * @param x2 X 2° (CIE 1931)
	 * @param y2 Y 2° (CIE 1931)
	 * @param z2 Z 2° (CIE 1931)
	 * @param x10 X 10° (CIE 1964)
	 * @param y10 Y 10° (CIE 1964)
	 * @param z10 Z 10° (CIE 1964)
	 * @param note Note
	 */
	private Tristimulus(final double x2, final double y2, final double z2, final double x10, final double y10, final double z10, final String note) {
		this.x2=x2;
		this.y2=y2;
		this.z2=z2;
		this.x10=x10;
		this.y10=y10;
		this.z10=z10;
		this.note=note;
	}
}
