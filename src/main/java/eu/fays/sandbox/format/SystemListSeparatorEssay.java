package eu.fays.sandbox.format;

import static eu.fays.sandbox.process.ExecuteCommandTrait.executeCommand;

public class SystemListSeparatorEssay {

	public static void main(String[] args) {
		System.out.println(detectSystemListSeparator());
	}

	@SuppressWarnings("nls")
	public static char detectSystemListSeparator() {
		char result = ',';
		// reg query "HKCU\Control Panel\International" /v sList
		if (System.getProperty("os.name").startsWith("Windows")) {
			final String stdout = executeCommand("reg", "query", "\"HKCU\\Control Panel\\International\"", "/v", "sList").trim();
			result = stdout.charAt(stdout.length() - 1);
		} else if ("Mac OS X".equals(System.getProperty("os.name"))) {
			// TODO
		}
		return result;
	}
}
