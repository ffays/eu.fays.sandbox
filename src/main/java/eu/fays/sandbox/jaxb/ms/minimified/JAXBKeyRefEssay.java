package eu.fays.sandbox.jaxb.ms.minimified;

import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.rangeClosed;

import java.io.File;

public class JAXBKeyRefEssay {

	public static void main(String[] args) throws Exception {
		Root root = new Root();
		root._parts = rangeClosed(1, 3).mapToObj(i -> new Part(i)).collect(toList());
		root._a = new A[] { new A(root._parts.get(0)), new A(root._parts.get(1)) };
		File file = new File("key.xml");
		root.marshal(file);
//		Root.unmarshal(file);
	}
}
