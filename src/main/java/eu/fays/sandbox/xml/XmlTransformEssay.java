package eu.fays.sandbox.xml;

import static java.lang.System.getProperty;
import static java.lang.System.lineSeparator;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.readAllLines;
import static java.util.logging.Level.INFO;
import static java.util.stream.Collectors.joining;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class XmlTransformEssay {

	/** Standard logger */
	private static final Logger LOGGER = Logger.getLogger(XmlTransformEssay.class.getName());
	
	/**
	 * System properties
	 * <ul>
	 * <li>schema : XML Transformation Schema file path
	 * <li>source : XML source file path
	 * <li>target : XML target file name
	 * </ul>
	 * @param args unused
	 * @throws Exception in case of unexpected error
	 */
	public static void main(String[] args) throws Exception {
		// https://stackoverflow.com/questions/5977846/how-to-apply-xsl-to-xml-in-java
		// https://jakarta.ee/specifications/xml-binding/3.0/apidocs/jakarta.xml.bind/jakarta/xml/bind/util/jaxbsource

		final File schemaFile = new File(getProperty("schema"));
		final File sourceFile = new File(getProperty("source"));
		final File targetFile = new File(sourceFile.getParentFile(), getProperty("target"));

		try (final FileInputStream schemaInputStream = new FileInputStream(schemaFile); final FileInputStream sourceInputStream = new FileInputStream(sourceFile); final FileOutputStream targetOutputStream = new FileOutputStream(targetFile)) {
			final TransformerFactory factory = TransformerFactory.newInstance();
			final Source schema = new StreamSource(schemaInputStream);
			final Templates template = factory.newTemplates(schema);
			final Transformer transformer = template.newTransformer();

			// Cf. [No newline emitted after XML decl in XSLT output](https://bugs.openjdk.org/browse/JDK-7150637)
			transformer.setOutputProperty("http://www.oracle.com/xml/is-standalone", "yes");

			final Source source = new StreamSource(sourceInputStream);
			final Result result = new StreamResult(targetOutputStream);
			// Apply the XML Transformation Schema to the source file and write the result to the target file
			transformer.transform(source, result);
		}

		final Level level = INFO;
		if (LOGGER.isLoggable(level)) {
			final List<String> lines = readAllLines(targetFile.toPath(), UTF_8);
			LOGGER.log(level, lines.stream().collect(joining(lineSeparator())));
		}
	}
}
