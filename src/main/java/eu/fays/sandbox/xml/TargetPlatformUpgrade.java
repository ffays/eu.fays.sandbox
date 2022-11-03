package eu.fays.sandbox.xml;

import static java.lang.Math.min;
import static java.net.URLDecoder.decode;
import static java.net.URLEncoder.encode;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.text.MessageFormat.format;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

// -ea -Djava.util.logging.SimpleFormatter.format="%1$tFT%1$tT,%1$tL	%4$s	%3$s	%5$s%6$s%n"
public class TargetPlatformUpgrade {

	/** Standard logger */
	private static final Logger LOGGER = Logger.getLogger(TargetPlatformUpgrade.class.getName());
	
	@SuppressWarnings("nls")
	public static void main(String[] args) throws Exception {
		final String targetPlatformDefinitionFileLocation = System.getProperty("file");
		final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		final File targetPlatformDefinitionFile = new File(targetPlatformDefinitionFileLocation);
		final Document dom = documentBuilder.parse(targetPlatformDefinitionFile);
		final Element rootNode = dom.getDocumentElement();
		
		// Bundle synonyms dictionary
		final Map<String,String> synonymsMap = new HashMap<>();
		synonymsMap.put("org.apiguardian", "org.apiguardian.api");
		
		// Missing bundles 
		final Map<String,Element> missingMap = new LinkedHashMap<>();

		// Repository content file dictionary
		final Map<String, File> repositoryContentFileMap = new HashMap<>();
		for(final File repositoryContentFile : targetPlatformDefinitionFile.getParentFile().listFiles()) {
			if(repositoryContentFile.getName().endsWith(".txt")) {
				repositoryContentFileMap.put(repositoryContentFile.getName(), repositoryContentFile);
			}
		}
		
		if (!rootNode.hasChildNodes()) return;
		
		for(int pass=1; pass<=2; pass++) {
			for (Node locationsElement = rootNode.getFirstChild(); locationsElement != null; locationsElement = locationsElement.getNextSibling()) {
				if(!locationsElement.hasChildNodes()) continue;
				if(!"locations".equals(locationsElement.getNodeName())) continue;
				
				for (Node locationElement = locationsElement.getFirstChild(); locationElement != null; locationElement = locationElement.getNextSibling()) {
					if(!locationElement.hasChildNodes()) continue;
					String repositoryUrl = null;
					Node repositoryNode = null;
					final LinkedHashMap<String, String> bundleVersionMap = new LinkedHashMap<>();
					final LinkedHashMap<String, Element> unitElementMap = new LinkedHashMap<>();
					for (Node childElement = locationElement.getFirstChild(); childElement != null; childElement = childElement.getNextSibling()) {
						if("unit".equals(childElement.getNodeName())) {
							final String bundleSymbolicName = childElement.getAttributes().getNamedItem("id").getNodeValue();
							final String version = childElement.getAttributes().getNamedItem("version").getNodeValue();
							bundleVersionMap.put(bundleSymbolicName, version);
							unitElementMap.put(bundleSymbolicName, (Element) childElement);
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
					
					final Properties bundleNewVersionProperties = new Properties();
					final File newRepositoryContentFile = repositoryContentFileMap.get(newRepositoryContentFilename);
					try(final FileReader reader = new FileReader(newRepositoryContentFile, UTF_8)) {
						bundleNewVersionProperties.load(reader);
						
						if(pass == 1) {
							// 1st pass : identify both upgraded bundles and missing bundles
							for(final Entry<String, Element> entry : unitElementMap.entrySet()) {
								final String bundleSymbolicName = entry.getKey();
								final Element unitElement = entry.getValue();
								final String oldVersion = bundleVersionMap.get(bundleSymbolicName);
								if(bundleNewVersionProperties.containsKey(bundleSymbolicName)) {
									final String newVersion = bundleNewVersionProperties.getProperty(bundleSymbolicName);
									unitElement.getAttributes().getNamedItem("version").setNodeValue(newVersion);
									int upgraded = newVersion.equals(oldVersion)?0:newVersion.compareTo(oldVersion)>0?1:-1;
									LOGGER.info(format("bundle{0,choice,0# |1#\u2191|2#?} {1} : {2} \u2794 {3}", upgraded, bundleSymbolicName, oldVersion, newVersion));
								} else if (synonymsMap.containsKey(bundleSymbolicName) && bundleNewVersionProperties.containsKey(synonymsMap.get(bundleSymbolicName))) {
									final String newBundleSymbolicName = synonymsMap.get(bundleSymbolicName);
									final String newVersion = bundleNewVersionProperties.getProperty(newBundleSymbolicName);
									unitElement.getAttributes().getNamedItem("id").setNodeValue(newBundleSymbolicName);
									unitElement.getAttributes().getNamedItem("version").setNodeValue(newVersion);
									int upgraded = newVersion.equals(oldVersion)?0:newVersion.compareTo(oldVersion)>0?1:-1;
									LOGGER.info(format("bundle{0,choice,0# |1#\u2191|2#?} {1} \u2794 {2} : {3} \u2794 {4}", upgraded, bundleSymbolicName, newBundleSymbolicName, oldVersion, newVersion));
								} else {
									missingMap.put(bundleSymbolicName, unitElement);
									unitElement.getParentNode().removeChild(unitElement);
								}
							}
						} else if(pass == 2){
							// 2nd pass : lookup missing bundles in other locations and possibly move them to the new location
							final Iterator<Entry<String, Element>> iterator = missingMap.entrySet().iterator();
							while(iterator.hasNext()) {
								final Entry<String, Element> entry = iterator.next();
								final String bundleSymbolicName = entry.getKey();
								if(bundleNewVersionProperties.containsKey(bundleSymbolicName)) {
									final String newVersion = bundleNewVersionProperties.getProperty(bundleSymbolicName);
									final Element unitElement = entry.getValue();
									final String oldVersion = unitElement.getAttributes().getNamedItem("version").getNodeValue();
									unitElement.getAttributes().getNamedItem("version").setNodeValue(newVersion);
									int upgraded = newVersion.equals(oldVersion)?0:newVersion.compareTo(oldVersion)>0?1:-1;
									
									// move unit to new location
									final Text newText = (Text) locationElement.getPreviousSibling().cloneNode(true);
									newText.appendData("\t");
									locationElement.insertBefore(unitElement, repositoryNode);
									locationElement.insertBefore(newText, repositoryNode);
									//
									LOGGER.info(format("bundle{0,choice,0# |1#\u2191|2#?} {1} : {2} \u2794 {3}", upgraded, bundleSymbolicName, oldVersion, newVersion));
									iterator.remove();
								}
							}
						}
					}

					repositoryNode.getAttributes().getNamedItem("location").setNodeValue(newRepositoryUrl);
				}
			}
		}
		
		for(final Entry<String, Element> entry : missingMap.entrySet()) {
			LOGGER.warning(format("bundle  {0} : {1} \u2794 {2}", entry.getKey(), entry.getValue().getAttributes().getNamedItem("version"), null));
		}
		
		// Write to output
		final DOMSource domSource = new DOMSource(dom);
		final String targetPlatformDefinitionFilename = targetPlatformDefinitionFile.getName();
		final String targetPlatformDefinitionBasename = targetPlatformDefinitionFilename.substring(0,targetPlatformDefinitionFilename.lastIndexOf('.'));
		final File newTargetPlatformDefinitionFile = new File(targetPlatformDefinitionFile.getParentFile(), targetPlatformDefinitionBasename + ".txt");
		try(final FileWriter writer = new FileWriter(newTargetPlatformDefinitionFile)) {
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.transform(domSource, result);
		}
		LOGGER.info(newTargetPlatformDefinitionFile.toString());
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
