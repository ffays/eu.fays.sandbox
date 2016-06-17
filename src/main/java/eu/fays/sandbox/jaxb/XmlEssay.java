package eu.fays.sandbox.jaxb;

import static java.lang.Math.E;
import static java.lang.Math.PI;
import static java.lang.Math.sqrt;

import java.io.File;
import java.util.GregorianCalendar;

import eu.fays.sandbox.iterators.Fruit;

@SuppressWarnings("nls")
public class XmlEssay {

	public static final double PHI = (1d + sqrt(5d)) / 2d;

	public static void main(String[] args) throws Exception {
		{
			final File file = new File("output/data.xml");
			final MyData myOutputData = new MyData();
			myOutputData.marshal(file);
			final MyData myInputData = MyData.unmarshal(file);
			assert myInputData.equals(myOutputData);
		}

		{
			final File file = new File("output/data2.xml");
			final MyData myOutputData = new MyData(false, 1, new GregorianCalendar(), new GregorianCalendar(), Fruit.ORANGE);
			myOutputData.addNumber(PHI);
			myOutputData.addNumber(E);
			myOutputData.addNumber(PI);
			myOutputData.marshal(file);
		}

	}

	
}
