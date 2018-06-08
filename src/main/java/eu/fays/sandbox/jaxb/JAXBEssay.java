package eu.fays.sandbox.jaxb;

import static java.lang.Math.E;
import static java.lang.Math.PI;
import static java.lang.Math.sqrt;

import java.io.File;
import java.time.LocalDateTime;
import java.util.GregorianCalendar;

import eu.fays.sandbox.iterators.Fruit;

/**
 * An essay on JAXB unmarshalling and unmarshalling
 * @author Frederic Fays
 */
@SuppressWarnings("nls")
public class JAXBEssay {

	/**
	 * Main
	 * @param args unused
	 * @throws Exception in case of unexpected error
	 */
	public static void main(String[] args) throws Exception {
		{
			final File file = new File("output/my_data_empty.xml");
			final MyData myOutputData = new MyData();
			myOutputData.marshal(file);
			final MyData myInputData = MyData.unmarshal(file);
			assert myInputData.equals(myOutputData);
		}

		{
			final File file = new File("output/my_data_with_values.xml");
			final MyData myOutputData = new MyData(false, 1, new GregorianCalendar(), new GregorianCalendar(), LocalDateTime.now(), Fruit.ORANGE);
			myOutputData.addNumber(PHI);
			myOutputData.addNumber(E);
			myOutputData.addNumber(PI);
			myOutputData.addSubData(new MySubData(City.BRUSSELS));
			myOutputData.addSubData(new MySubData(City.BERLIN));
			myOutputData.addSubData(new MySubData(City.PARIS));
			myOutputData.addSubDataRef(myOutputData.getMySubDataList().get(0));
			myOutputData.addSubDataRef(myOutputData.getMySubDataList().get(1));
			myOutputData.marshal(file);
			final MyData myInputData = MyData.unmarshal(file);
			assert myInputData.equals(myOutputData);
		}

	}

	/** The golden ratio */
	public static final double PHI = (1d + sqrt(5d)) / 2d;

}
