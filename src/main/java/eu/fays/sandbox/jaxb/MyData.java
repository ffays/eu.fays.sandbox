package eu.fays.sandbox.jaxb;

import java.io.File;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.util.ValidationEventCollector;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import eu.fays.sandbox.iterators.Fruit;

/**
 * A class to play around with JAXB concepts.<br>
 * Articles:
 * <ul>
 * <li><a href="http://lists.xml.org/archives/xml-dev/200902/msg00060.html">A bad idea to use the XML Schema list type?</a>
 * <li><a href="http://stackoverflow.com/questions/13568543/how-do-you-specify-the-date-format-used-when-jaxb-marshals-xsddatetime">How do you specify the date format used when JAXB marshals xsd:dateTime?</a>
 * </ul>
 * 
 * @author Frederic Fays
 */
@SuppressWarnings("nls")
@XmlRootElement
@XmlType // (propOrder = { "myDate", "myTimeStamp", "myBoolean", "myInteger", "myFruit", "myNumberList" })
public class MyData {
	/**
	 * Constructor with default values
	 */
	public MyData() {
		this(true, 0, defaultGregorianCalendar(), defaultGregorianCalendar(), Fruit.DEFAULT);
	}

	/**
	 * Constructor
	 * @param myBoolean a boolean
	 * @param myInteger an integer
	 * @param myDate a date
	 * @param myFruit a fruit
	 */
	public MyData(final boolean myBoolean, final int myInteger, final GregorianCalendar myTimeStamp, final GregorianCalendar myDate, final Fruit myFruit) {
		setMyBoolean(myBoolean);
		setMyInteger(myInteger);
		setMyTimeStamp(myTimeStamp);
		setMyDate(myDate);
		setMyFruit(myFruit);
		_myNumberList = new ArrayList<>();
	}

	/**
	 * Provides a default Gregorian calendar, with UTC as time-zone and the Unix EPOCH as time-stamp.
	 * @return the default Gregorian calendar.
	 */
	private static final GregorianCalendar defaultGregorianCalendar() {
		GregorianCalendar result = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		result.setTimeInMillis(0L);
		return result;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final Set<Object> set = new HashSet<>();
		set.add(Boolean.valueOf(isMyBoolean()));
		set.add(new Integer(getMyInteger()));
		set.add(getMyDate());
		set.add(getMyFruit());
		return set.hashCode();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof MyData)) {
			return false;
		}
		final MyData o = (MyData) obj;

		boolean result = true;
		try {
			result &= o.isMyBoolean() == isMyBoolean();
			result &= o.getMyInteger() == getMyInteger();
			result &= o.getMyTimeStamp().compareTo(getMyTimeStamp()) == 0;
			result &= o.getMyDate().compareTo(getMyDate()) == 0;
			result &= o.getMyFruit() == getMyFruit();
		} catch (NullPointerException e) {
			result = false;
		}

		return result;
	}

	public boolean isMyBoolean() {
		return _myBoolean;
	}

	public void setMyBoolean(boolean myBoolean) {
		_myBoolean = myBoolean;
	}

	public int getMyInteger() {
		return _myInteger;
	}

	public void setMyInteger(int myInteger) {
		_myInteger = myInteger;
	}

	public GregorianCalendar getMyTimeStamp() {
		return _myTimeStamp;
	}

	public void setMyTimeStamp(GregorianCalendar myTimeStamp) {
		_myTimeStamp = myTimeStamp;
		if (!_myTimeStamp.getTimeZone().equals(TimeZone.getTimeZone("UTC"))) {
			_myTimeStamp.setTimeZone(TimeZone.getTimeZone("UTC"));
		}
	}

	
	@XmlElement(name = "myDate") 
	@XmlJavaTypeAdapter(XMLDateAdapter.class)
	public GregorianCalendar getMyDate()  {
		return _myDate;
	}

	public void setMyDate(GregorianCalendar myDate) {
		_myDate = myDate;
		if (!_myDate.getTimeZone().equals(TimeZone.getTimeZone("UTC"))) {
			_myDate.setTimeZone(TimeZone.getTimeZone("UTC"));
		}
	}

	public Fruit getMyFruit() {
		return _myFruit;
	}

	public void setMyFruit(Fruit myFruit) {
		_myFruit = myFruit;
	}

	@XmlElementWrapper
	@XmlElement(name = "li")
	public List<ListItemOfDouble> getMyNumberList() {
		return _myNumberList;
	}

	public void setMyNumberList(List<ListItemOfDouble> myNumberList) {
		_myNumberList = myNumberList;
	}

	public void addNumber(final double myDouble) {
		_myNumberList.add(new ListItemOfDouble(myDouble));
	}

	/**
	 * Marshal to XML this instance into the given output file
	 * @param file the output file
	 * @throws JAXBException in case of unexpected error
	 */
	public void marshal(final File file) throws JAXBException {
		//
		assert file != null;
		//

		final JAXBContext context = JAXBContext.newInstance(getClass());
		final Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);
		marshaller.setProperty("jaxb.noNamespaceSchemaLocation", "../" + XML_SCHEMA_FILE.getPath());
		marshaller.marshal(this, file);
	}

	/**
	 * Unmarshal from XML the given input file
	 * @param file the input file
	 * @return a new instance
	 * @throws JAXBException in case of unexpected error
	 * @throws SAXException in case of unexpected error
	 */
	public static MyData unmarshal(final File file) throws JAXBException, SAXException {
		//
		assert file != null;
		assert file.isFile();
		assert file.canRead();
		//

		final JAXBContext context = JAXBContext.newInstance(MyData.class);
		final SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		final Schema schema = factory.newSchema(XML_SCHEMA_FILE);

		final Unmarshaller unmarshaller = context.createUnmarshaller();
		unmarshaller.setSchema(schema);

		final ValidationEventCollector handler = new ValidationEventCollector();
		unmarshaller.setEventHandler(handler);
		final MyData result = (MyData) unmarshaller.unmarshal(file);

		//
		assert result != null;
		//
		return result;

	}

	/** The XML schema file to validate this class */
	private static final File XML_SCHEMA_FILE = new File("xml/MyData.xsd");

	/** A date */
	private GregorianCalendar _myDate;

	/** A time stamp */
	private GregorianCalendar _myTimeStamp;

	/** A boolean */
	private boolean _myBoolean;

	/** An integer */
	private int _myInteger;

	/** A fruit */
	private Fruit _myFruit;

	/** A list of numbers */
	private List<ListItemOfDouble> _myNumberList;
}
