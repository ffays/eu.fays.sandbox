package eu.fays.sandbox.jaxb;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
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
 * <li><a href="http://stackoverflow.com/questions/28340772/prevent-writing-default-attribute-values-jaxb">Prevent writing default attribute values JAXB</a>
 * <li><a href="http://stackoverflow.com/questions/8885011/jaxb-avoid-saving-default-values">JAXB Avoid saving default values</a>
 * <li><a href="http://blog.bdoughan.com/2011/06/using-jaxbs-xmlaccessortype-to.html">Using JAXB's @XmlAccessorType to Configure Field or Property Access</a>
 * </ul>
 * 
 * @author Frederic Fays
 */
@SuppressWarnings("nls")
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "myDate", "myTimeStamp", "myBoolean", "myInteger", "myFruit", "myNumberList" })
public class MyData {
	/**
	 * Constructor with default values
	 */
	public MyData() {

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
		this.myNumberList = new ArrayList<>();
	}

	/**
	 * Provides a default Gregorian calendar, with UTC as time-zone and the Unix EPOCH as time-stamp.
	 * @return the default Gregorian calendar.
	 */
	private static final GregorianCalendar defaultGregorianCalendar() {
		GregorianCalendar result = new GregorianCalendar(UTC);
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
	public boolean equals(final Object obj) {
		if (obj == null || !(obj instanceof MyData)) {
			return false;
		}
		final MyData o = (MyData) obj;

		boolean result = true;
		try {
			result &= o.getMyDate().compareTo(getMyDate()) == 0; // equals() does not work for GregorianCalendar
			result &= o.getMyTimeStamp().compareTo(getMyTimeStamp()) == 0;
			result &= o.isMyBoolean() == isMyBoolean();
			result &= o.getMyInteger() == getMyInteger();
			result &= o.getMyFruit() == getMyFruit();
			result &= o.getMyNumberList().equals(getMyNumberList());
		} catch (NullPointerException e) {
			result = false;
		}

		return result;
	}

	public boolean isMyBoolean() {
		if (myBoolean == null) {
			return false;
		}
		return myBoolean;
	}

	public void setMyBoolean(final boolean myBoolean) {
		this.myBoolean = myBoolean;
	}

	public int getMyInteger() {
		if (myInteger == null) {
			return 0;
		}
		return myInteger;
	}

	public void setMyInteger(final int myInteger) {
		this.myInteger = myInteger;
	}

	public GregorianCalendar getMyTimeStamp() {
		if (myTimeStamp == null) {
			return defaultGregorianCalendar();
		}
		return myTimeStamp;
	}

	public void setMyTimeStamp(final GregorianCalendar myTimeStamp) {
		// To prevent side effects, the given GregorianCalendar argument must be cloned first!
		GregorianCalendar c = (GregorianCalendar) myTimeStamp.clone();
		c.setTimeZone(UTC);
		this.myTimeStamp = c;
	}

	public GregorianCalendar getMyDate() {
		if (myDate == null) {
			return defaultGregorianCalendar();
		}
		return myDate;
	}

	public void setMyDate(final GregorianCalendar myDate) {
		//
		assert myDate != null;
		//

		// To prevent side effects, the given GregorianCalendar argument must be cloned first!
		GregorianCalendar c0 = (GregorianCalendar) myDate.clone();
		c0.setTimeZone(UTC);
		GregorianCalendar c = defaultGregorianCalendar();
		// Keep only the date parts.
		Arrays.stream(new int[] { YEAR, MONTH, DAY_OF_MONTH }).forEach(f -> c.set(f, c0.get(f)));
		this.myDate = c;
	}

	public Fruit getMyFruit() {
		if (myFruit == null) {
			return Fruit.DEFAULT;
		}
		return myFruit;
	}

	public void setMyFruit(final Fruit myFruit) {
		this.myFruit = myFruit;
	}

	public List<ListItemOfDouble> getMyNumberList() {
		if (myNumberList == null) {
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(myNumberList);
	}

	public void setMyNumberList(final List<ListItemOfDouble> myNumberList) {
		// To prevent side effects, the given List must be cloned
		this.myNumberList = new ArrayList<>(myNumberList);
	}

	public void addNumber(final double myDouble) {
		if (myNumberList == null) {
			// Lazy initialization
			myNumberList = new ArrayList<>();
		}
		myNumberList.add(new ListItemOfDouble(myDouble));
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

	/** UTC time zone */
	private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

	/** The XML schema file to validate this class */
	private static final File XML_SCHEMA_FILE = new File("xml/MyData.xsd");

	/** A date */
	@XmlElement
	@XmlJavaTypeAdapter(XMLDateAdapter.class)
	private GregorianCalendar myDate = null;

	/** A time stamp */
	@XmlElement
	private GregorianCalendar myTimeStamp = null;

	/** A boolean */
	@XmlElement
	private Boolean myBoolean = null;

	/** An integer */
	@XmlElement
	private Integer myInteger = null;

	/** A fruit */
	@XmlElement
	private Fruit myFruit = null;

	/** A list of numbers */
	@XmlElementWrapper
	@XmlElement(name = "li")
	private List<ListItemOfDouble> myNumberList = null;
}
