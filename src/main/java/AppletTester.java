/**
 * Java Applet proof of concept that provides the system properties in a text area.<br>
 * <br>
 * Note: requires Java 1.2 or higher to compile (due do the use of {@link java.util.TreeSet TreeSet} to sort the properties)<br>
 * <br>
 * &lt;applet code="AppletTester.class" height="1280" width="720"&gt;
 */
public class AppletTester extends java.applet.Applet {

	// Suppress the annotations to compile with old java versions
	/**
	 * Initialization!
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void init() {
		java.awt.TextArea textArea = new java.awt.TextArea(28, 110);
		java.awt.Font font = new java.awt.Font("Dialog", java.awt.Font.PLAIN, 20);
		textArea.setFont(font);
		add(textArea);

		java.util.Set sortedKeySet = new java.util.TreeSet();
		{
			java.util.Set keySet = System.getProperties().keySet();
			java.util.Iterator keySetIterator = keySet.iterator();
			while (keySetIterator.hasNext()) {
				final String key = (String) keySetIterator.next();
				sortedKeySet.add(key);
			}
		}

		StringBuffer builder = new StringBuffer();
		{
			java.util.Iterator<String> sortedKeySetIterator = sortedKeySet.iterator();
			while (sortedKeySetIterator.hasNext()) {
				String key = (String) sortedKeySetIterator.next();
				builder.append(key);
				builder.append('=');
				builder.append(System.getProperty(key));
				builder.append('\n');
			}
		}

		textArea.setText(builder.toString());
	}

	/** Default serial number */
	private static final long serialVersionUID = 1L;
}
