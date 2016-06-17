package eu.fays.sandbox.jaxb;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * An adapter to both marshal and unmarshal a GregorianCalendar to an XML date<br>
 * <br>
 * Article: <a href="http://stackoverflow.com/questions/13568543/how-do-you-specify-the-date-format-used-when-jaxb-marshals-xsddatetime">How do you specify the date format used when JAXB marshals xsd:dateTime?</a><br>
 * @author Frederic Fays
 */
@SuppressWarnings("nls")
public class XMLDateAdapter extends XmlAdapter<String, GregorianCalendar> {

	/** ISO8601 date format */
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	@Override
	public String marshal(final GregorianCalendar v) throws Exception {
		return DATE_FORMAT.format(v.getTime());
	}

	/**
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
	 */
	@Override
	public GregorianCalendar unmarshal(final String v) throws Exception {
		GregorianCalendar result = new GregorianCalendar();
		result.setTimeZone(TimeZone.getTimeZone("UTC"));
		result.setTime(DATE_FORMAT.parse(v));
		return result;
	}

	/**
	 * Another way to convert a GregorianCalendar into an XML date
	 * @param v the input date
	 * @return the output date
	 */
	public static XMLGregorianCalendar toXMLDate(final GregorianCalendar v) {
		try {
			return DatatypeFactory.newInstance().newXMLGregorianCalendar(new SimpleDateFormat("yyyy-MM-dd").format(v.getTime()));
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}
	}
}
