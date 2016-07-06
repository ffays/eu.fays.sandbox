package eu.fays.sandbox.jaxb.ms;

import java.io.File;

import eu.fays.sandbox.jaxb.MyData;

/**
 * Article: <a href="https://msdn.microsoft.com/en-us/library/ms256101(v=vs.110).aspx">&lt;xsd:key&gt; Element</a>
 */
public class JAXBKeyRefEssay {

	public static void main(String[] args) throws Exception {
		final File file = new File("output/key.xml");
		final Root myOutputData = new Root();
		final Part part1 = new Part(1);
		final Part part2 = new Part(2);
		final Part part3 = new Part(3);
		myOutputData.parts.add(part1);
		myOutputData.parts.add(part2);
		myOutputData.parts.add(part3);
		myOutputData.a = new A[] { new A(part1), new A(part2) };

		myOutputData.marshal(file);
		final Root myInputData = Root.unmarshal(file);
		assert myInputData.equals(myOutputData);
	}

}
