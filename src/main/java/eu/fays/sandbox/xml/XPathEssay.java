package eu.fays.sandbox.xml;

import java.io.File;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

/**
 * XPath essay to extract both the Service Name and the Host Name from the Tomcat server configuration
 */
public class XPathEssay {

	/**
	 * <code>-ea -Dcatalina.home=/usr/local/apache/apache-tomcat-11 -Djava.util.logging.SimpleFormatter.format="%1$tF %1$tT	%4$s	%3$s	%5$s%6$s%n"</code>
	 * @param args unused
	 * @throws Exception in case of error
	 */
	@SuppressWarnings("nls")
	public static void main(String[] args) throws Exception {
		final String catalinaHome = System.getProperty("catalina.home",  System.getenv("CATALINA_HOME"));
		assert catalinaHome != null;
		final File serverXmlFile = new File(new File(new File(catalinaHome), "conf"), "server.xml");
		assert serverXmlFile.exists();
		assert serverXmlFile.isFile();
		assert serverXmlFile.canRead();
		
		final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		final DocumentBuilder db = dbf.newDocumentBuilder();
		final Document dom = db.parse(serverXmlFile);
		final XPath xpath = XPathFactory.newInstance().newXPath();
		
		final String serviceName = (String) xpath.evaluate("/Server/Service/@name", dom, XPathConstants.STRING);
		assert serviceName != null;
		assert !serviceName.isEmpty();
		LOGGER.info("serviceName=" + serviceName);

		final String hostName = (String) xpath.evaluate("/Server/Service[@name='"+serviceName+"']/Engine/Host/@name", dom, XPathConstants.STRING);
		assert hostName != null;
		assert !hostName.isEmpty();
		LOGGER.info("hostName=" + hostName);
	}
	
	
	/** Standard logger */
	private static final Logger LOGGER = Logger.getLogger(XPathEssay.class.getName());
}
