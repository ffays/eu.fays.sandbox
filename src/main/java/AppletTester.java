/**
 * Java Applet proof of concept that provides some system properties in a text area.<br>
 * <br>
 * &lt;applet code="AppletTester.class" width="640" height="240"&gt;
 */
public class AppletTester extends java.applet.Applet {

	/**
	 * Initialization!
	 */
	public void init() {
		java.awt.TextArea textArea = new java.awt.TextArea(9, 50);
		java.awt.Font font = new java.awt.Font("Dialog", java.awt.Font.PLAIN, 20);
		textArea.setFont(font);
		add(textArea);

		StringBuffer builder = new StringBuffer();
		String[] keys = { "java.class.version", "java.vendor", "java.vendor.url", "java.version", "os.name", "os.arch", "os.version" };
	
		for(int i=0; i<keys.length; i++) {
			builder.append(keys[i]);
			builder.append('=');
			builder.append(System.getProperty(keys[i]));
			builder.append('\n');
		}

		textArea.setText(builder.toString());
	}

	/** Default serial number */
	private static final long serialVersionUID = 1L;
}
