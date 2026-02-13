package eu.fays.sandbox.color;

// Cf. https://stackoverflow.com/a/19429792/1317548
public enum PrimaryColor {
	RED,
	GREEN,
	BLUE,
	
	CYAN,
	MAGENTA,
	YELLOW;
	
	private PrimaryColor color0;
	private PrimaryColor color1;
	
	public PrimaryColor color0() {
		return color0;
	}

	public PrimaryColor color1() {
		return color1;
	}

	static {
		RED.color0 = MAGENTA;
		RED.color1 = YELLOW;
		
		GREEN.color0 = YELLOW;
		GREEN.color1 = CYAN;
		
		BLUE.color0 = CYAN;
		BLUE.color1 = MAGENTA;
		
		CYAN.color0 = GREEN;
		CYAN.color1 = BLUE;

		MAGENTA.color0 = BLUE;
		MAGENTA.color1 = RED;
		
		YELLOW.color0 = RED;
		YELLOW.color1 = GREEN;
	}
}
