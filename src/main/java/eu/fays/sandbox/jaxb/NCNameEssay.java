package eu.fays.sandbox.jaxb;

import static java.lang.System.out;
import static java.text.MessageFormat.format;

import java.util.regex.Pattern;
import java.util.stream.Stream;

public class NCNameEssay {

	public static void main(String[] args) {
		// IDREF is of type NCName
		// NCName definition: https://www.w3.org/TR/1999/WD-xmlschema-2-19990924/#NCName
		// Name definition : https://www.w3.org/TR/REC-xml/#d0e804

		Stream.of("a", ":", "_").forEach(s -> out.println(format("NAME_START_CHAR_PATTERN[{0}]: true == {1}", s, NAME_START_CHAR_PATTERN.matcher(s).matches())));
		Stream.of("0", "$", "|").forEach(s -> out.println(format("NAME_START_CHAR_PATTERN[{0}]: false == {1}", s, NAME_START_CHAR_PATTERN.matcher(s).matches())));
		Stream.of("a", ":", "0").forEach(s -> out.println(format("NAME_CHAR_PATTERN[{0}]: true == {1}", s, NAME_CHAR_PATTERN.matcher(s).matches())));
		Stream.of("+", "/", "=").forEach(s -> out.println(format("NAME_CHAR_PATTERN[{0}]: false == {1}", s, NAME_CHAR_PATTERN.matcher(s).matches())));
		 
	}
	
	public static final String NAME_START_CHAR = ":A-Z_a-z\\u00C0-\\u00D6\\u00D8-\\u00F6\\u00F8-\\u02FF\\u0370-\\u037D\\u037F-\\u1FFF\\u200C-\\u200D\\u2070-\\u218F\\u2C00-\\u2FEF\\u3001-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFFD";
	public static final String NAME_CHAR = NAME_START_CHAR + "0-9\\-.\\u00B7\\u0300-\\u036F\\u203F-\\u2040";
	public static final Pattern NCNAME_PATTERN = Pattern.compile("["+NAME_START_CHAR+"]"+"["+NAME_CHAR+"]*");

	public static final Pattern NAME_START_CHAR_PATTERN = Pattern.compile( "["+NAME_START_CHAR+"]");
	public static final Pattern NAME_CHAR_PATTERN = Pattern.compile( "["+NAME_CHAR+"]");
	public static final Pattern NAME_START_CHAR_ANTIPATTERN = Pattern.compile( "[^"+NAME_START_CHAR+"]");
	public static final Pattern NAME_CHAR_ANTIPATTERN = Pattern.compile( "[^"+NAME_CHAR+"]");
}
