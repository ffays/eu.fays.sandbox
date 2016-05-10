package eu.fays.sandbox.xml;

import static java.text.MessageFormat.format;
import static org.w3c.dom.Node.ATTRIBUTE_NODE;
import static org.w3c.dom.Node.ELEMENT_NODE;
import static org.w3c.dom.Node.TEXT_NODE;

import java.io.File;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Stack;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * An essay on DOM use.<br>
 * Input file, see <a href="https://msdn.microsoft.com/en-us/library/ms762271(v=vs.85).aspx">Sample XML File (books.xml)</a>
 */
@SuppressWarnings("nls")
public class XmlDomEssay {

	/**
	 * Main<br>
	 * <br>
	 * Parameters:
	 * <ul>
	 * <li>file: an XML File
	 * </ul>
	 * Note: the parameters must be provided to the jvm as runtime properties, e.g. -Dfile="books.xml"
	 * @param args unused
	 * @throws Exception in case of unexpected error.
	 */
	public static void main(String[] args) throws Exception {
		final String fileName = System.getProperty("file", "books.xml");
		final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		final DocumentBuilder db = dbf.newDocumentBuilder();
		final Document dom = db.parse(new File(fileName));

		final Stack<Node> stack = new Stack<>();

		final Element rootNode = dom.getDocumentElement();
		stack.push(rootNode);
		while (!stack.isEmpty()) {
			final Node node = stack.pop();

			if (node.getNodeType() == ELEMENT_NODE) {
				final Element element = (Element) node;
				final String elementPath = computePath(element);
				LOGGER.info(elementPath);

				if (element.hasAttributes()) {
					final NamedNodeMap attributes = element.getAttributes();
					for (int i = 0; i < attributes.getLength(); i++) {
						assert attributes.item(i).getNodeType() == ATTRIBUTE_NODE;
						final Attr attribute = (Attr) attributes.item(i);
						final String attributeName = attribute.getNodeName();
						final String attributeValue = attribute.getNodeValue();
						LOGGER.info(format("{0}[@{1}=''{2}'']", elementPath, attributeName, attributeValue));
					}
				}
				if (element.hasChildNodes()) {
					for (Node childElement = element.getFirstChild(); childElement != null; childElement = childElement.getNextSibling()) {
						stack.push(childElement);
					}
				}
			} else if (node.getNodeType() == TEXT_NODE) {
				final String text = node.getNodeValue().trim();
				if (!text.isEmpty()) {
					final String elementPath = computePath((Element) node.getParentNode());
					LOGGER.info(format("{0}={1}", elementPath, text));
				}
			}

		}
	}

	/**
	 * Computes the path of the given element
	 * @param element the given element
	 * @return the path
	 */
	public static final String computePath(final Element element) {
		//
		assert element != null;
		//

		final Deque<String> path = new LinkedList<>();
		for (Node node = element; node != null; node = node.getParentNode()) {
			path.addFirst(node.getNodeName());
		}

		final String result = String.join("/", path);

		return result;
	}

	/** Standard logger */
	private static final Logger LOGGER = Logger.getLogger(XmlDomEssay.class.getName());
}
