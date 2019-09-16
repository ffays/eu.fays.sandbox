package eu.fays.sandbox.jaxb;

import java.time.LocalDateTime;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * An adapter to both marshal and unmarshal a LocalDateTime from/to XML<br>
 * <br>
 * Article: <a href="http://stackoverflow.com/questions/13568543/how-do-you-specify-the-date-format-used-when-jaxb-marshals-xsddatetime">How do you specify the date format used when JAXB marshals xsd:dateTime?</a><br>
 */
public class LocalDateTimeXmlAdapter extends XmlAdapter<String, LocalDateTime> {
	/**
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	@Override
	public String marshal(final LocalDateTime v) throws Exception {
		return v.toString();
	}

	/**
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
	 */
	@Override
	public LocalDateTime unmarshal(final String v) throws Exception {
		return LocalDateTime.parse(v);
	}
}
