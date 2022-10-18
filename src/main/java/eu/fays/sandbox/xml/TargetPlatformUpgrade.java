package eu.fays.sandbox.xml;

import static java.lang.Math.min;
import static java.net.URLDecoder.decode;
import static java.net.URLEncoder.encode;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.text.MessageFormat.format;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

// -ea -Djava.util.logging.SimpleFormatter.format="%1$tFT%1$tT,%1$tL	%4$s	%3$s	%5$s%6$s%n"
public class TargetPlatformUpgrade {

	/** Standard logger */
	private static final Logger LOGGER = Logger.getLogger(TargetPlatformUpgrade.class.getName());
	
	@SuppressWarnings("nls")
	public static void main(String[] args) throws Exception {
		final String targetPlatformDefinitionFilename = System.getProperty("file");
		final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		final DocumentBuilder db = dbf.newDocumentBuilder();
		final File targetPlatformDefinitionFile = new File(targetPlatformDefinitionFilename);
		final Document dom = db.parse(targetPlatformDefinitionFile);
		final Element rootNode = dom.getDocumentElement();

		final Map<String, File> repositoryContentFileMap = new HashMap<>();
		
		for(final File repositoryContentFile : targetPlatformDefinitionFile.getParentFile().listFiles()) {
			if(repositoryContentFile.getName().endsWith(".txt")) {
				repositoryContentFileMap.put(repositoryContentFile.getName(), repositoryContentFile);
			}
		}
		
		if (!rootNode.hasChildNodes()) return;
		
		for (Node locationsElement = rootNode.getFirstChild(); locationsElement != null; locationsElement = locationsElement.getNextSibling()) {
			if(!locationsElement.hasChildNodes()) continue;
			if(!"locations".equals(locationsElement.getNodeName())) continue;
			
			for (Node locationElement = locationsElement.getFirstChild(); locationElement != null; locationElement = locationElement.getNextSibling()) {
				if(!locationElement.hasChildNodes()) continue;
				String repositoryUrl = null;
				Node repositoryNode = null;
				final LinkedHashMap<String, String> bundleVersionMap = new LinkedHashMap<>();
				final LinkedHashMap<String, Node> bundleNodeMap = new LinkedHashMap<>();
				for (Node childElement = locationElement.getFirstChild(); childElement != null; childElement = childElement.getNextSibling()) {
					if("unit".equals(childElement.getNodeName())) {
						final String bundleSymbolicName = childElement.getAttributes().getNamedItem("id").getNodeValue();
						final String version = childElement.getAttributes().getNamedItem("version").getNodeValue();
						bundleVersionMap.put(bundleSymbolicName, version);
						bundleNodeMap.put(bundleSymbolicName, childElement);
					} else if("repository".equals(childElement.getNodeName())) {
						repositoryUrl = childElement.getAttributes().getNamedItem("location").getNodeValue();
						repositoryNode = childElement;
					}
				}
				final String currentRepositoryContentFilename = encode(repositoryUrl, UTF_8) + ".txt";
				String newRepositoryContentFilename = null;
				if(repositoryContentFileMap.containsKey(currentRepositoryContentFilename)) {
					newRepositoryContentFilename = currentRepositoryContentFilename;
				} else {
					int best = -1;
					for(final String repositoryContentFilename : repositoryContentFileMap.keySet()) {
						int score = findFirstMismatch(currentRepositoryContentFilename, repositoryContentFilename);
						if(score > best) {
							best = score;
							newRepositoryContentFilename = repositoryContentFilename;
						}
					}
				}
				assert newRepositoryContentFilename != null;
				final String newRepositoryUrl = decode(newRepositoryContentFilename.substring(0, newRepositoryContentFilename.length()-4), UTF_8);
				LOGGER.info(format("location {0} \u2794 {1}", repositoryUrl, newRepositoryUrl));
				
				final Properties bundleNewVersionMap = new Properties();
				final File newRepositoryContentFile = repositoryContentFileMap.get(newRepositoryContentFilename);
				try(final FileReader reader = new FileReader(newRepositoryContentFile, UTF_8)) {
					bundleNewVersionMap.load(reader);
					for(final Entry<String, Node> entry : bundleNodeMap.entrySet()) {
						final String bundleSymbolicName = entry.getKey();
						final String oldVersion = bundleVersionMap.get(bundleSymbolicName);
						final String newVersion = bundleNewVersionMap.getProperty(bundleSymbolicName);
						if(newVersion != null) {
							entry.getValue().getAttributes().getNamedItem("version").setNodeValue(newVersion);
							int upgraded = newVersion.equals(oldVersion)?0:newVersion.compareTo(oldVersion)>0?1:-1;
							LOGGER.info(format("bundle{0,choice,0# |1#\u2191|2#?} {1} : {2} \u2794 {3}", upgraded, bundleSymbolicName, oldVersion, newVersion));
						} else {
							LOGGER.warning(format("bundle  {0} : {1} \u2794 {2}", bundleSymbolicName, oldVersion, newVersion));
						}
					}
				}
				repositoryNode.getAttributes().getNamedItem("location").setNodeValue(newRepositoryUrl);
			}
		}
	}
	
	private static int findFirstMismatch(final String a, final String b) {
		final int n = min(a.length(), b.length());
		int result = n;
		for(int i = 0; i < n ; i++) {
			if(a.charAt(i) != b.charAt(i)) {
				result = i;
				break;
			}
		}
	
		return result;
	}

}
