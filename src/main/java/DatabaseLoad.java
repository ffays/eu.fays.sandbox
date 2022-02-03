import static java.sql.Types.BIGINT;
import static java.sql.Types.BIT;
import static java.sql.Types.BOOLEAN;
import static java.sql.Types.CHAR;
import static java.sql.Types.DOUBLE;
import static java.sql.Types.INTEGER;
import static java.sql.Types.TIMESTAMP;
import static java.sql.Types.VARCHAR;
import static java.text.MessageFormat.format;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Load a database via a JDBC connection
 */
@SuppressWarnings("nls")
public class DatabaseLoad {

	private final static String URL_PARAMETER_NAME = "url";
	private final static String USER_PARAMETER_NAME = "user";
	private final static String PASSWORD_PARAMETER_NAME = "password";
	private final static String SEPARATOR_PARAMETER_NAME = "separator";
	private final static String QUOTE_CHAR_PARAMETER_NAME = "quoteChar";
	private final static String QUERY_SEPARATOR_PARAMETER_NAME = "querySeparator";
	private final static String EXCEL_DATE_PARAMETER_NAME = "excelDate";
	private final static String AUTO_COMMIT_PARAMETER_NAME = "autoCommit";
	private final static String COMMIT_PARAMETER_NAME = "commit";
	private final static String CSV_PARAMETER_NAME = "csv";
	private final static String ENCODING_PARAMETER_NAME = "encoding";
	private final static String SCHEMA_PARAMETER_NAME = "schema";
	private final static String TABLE_PARAMETER_NAME = "table";
	private final static String FIRST_ROW_HEADER_PARAMETER_NAME = "firstRowHeader";

	/**
	 * Usage: java -Durl=&lt;jdbcConnectionString&gt; -Duser=&lt;user&gt; -Dpassword=&lt;password&gt; DatabaseLoad &ltfile1&gt; &ltfile2&gt; ...<br>
	 * <br>
	 * System parameters:
	 * <ul>
	 * <li>url: JDBC connection string
	 * <li>user: database user
	 * <li>password: database password
	 * <li>separator: field separator (optional, default value: tab)
	 * <li>quoteChar: quoting character for String, Date and Timestamp values (optional, default value: none)
	 * <li>excelDate: dates and timestamps as Excel dates, i.e. fractional days since January 1st 1900 (optional, true or false, default: false)
	 * <li>autoCommit: enable/disable auto-commit mode (optional, true or false, relies on driver default value)
	 * <li>commit: perform commit after load (optional, true (=commit) or false (=rollback), default: true)
	 * <li>csv: equivalent to separator=, and quoteChar=" (optional)
	 * <li>encoding: the file encoding (optional, default={@link StandardCharsets#UTF_8 UTF-8})
	 * <li>schema: name of the schema into which the given data will be inserted (optional)
	 * <li>table: name of the table into which the given data will be inserted (optional, if not given then the name of the file without its extension will be used as table name)
	 * <li>firstRowHeader: first row of data is the header (optional, true or false, default: true)
	 * </ul>
	 * @param args SQL queries
	 * @throws Exception in case of unexpected error
	 */
	public static void main(String[] args) throws Exception {
		final Properties systemProperties = System.getProperties();
		final String url = System.getProperty(URL_PARAMETER_NAME);
		final String user = System.getProperty(USER_PARAMETER_NAME);
		final String password = System.getProperty(PASSWORD_PARAMETER_NAME);
		final boolean csv = systemProperties.containsKey(CSV_PARAMETER_NAME);
		final char separator;
		if(csv && !systemProperties.containsKey(SEPARATOR_PARAMETER_NAME)) {
			final char decimalSeparator = new DecimalFormatSymbols().getDecimalSeparator();
			separator = decimalSeparator == ',' ? ';' : ','; // Infer list separator (e.g. ",") based on the decimal separator (e.g. ".")
		} else {
			separator = getSystemProperty(SEPARATOR_PARAMETER_NAME, "\t").charAt(0);
		}
		final char quoteChar;
		if(csv && !systemProperties.containsKey(QUOTE_CHAR_PARAMETER_NAME)) {
			quoteChar = '"';
		} else if (systemProperties.containsKey(QUOTE_CHAR_PARAMETER_NAME)) {
			quoteChar = getSystemProperty(QUOTE_CHAR_PARAMETER_NAME, null).charAt(0);
		} else {
			quoteChar = (char) -1; // -1 == none
		}

		final boolean excelDate = Boolean.valueOf(System.getProperty(EXCEL_DATE_PARAMETER_NAME, Boolean.FALSE.toString()));
		final String autoCommit = System.getProperty(AUTO_COMMIT_PARAMETER_NAME);
		final boolean commit = Boolean.valueOf(System.getProperty(COMMIT_PARAMETER_NAME, Boolean.TRUE.toString()));
		final Charset encoding;
		if(systemProperties.containsKey(ENCODING_PARAMETER_NAME)) {
			encoding = Charset.forName(System.getProperty(ENCODING_PARAMETER_NAME));
		} else {
			encoding = StandardCharsets.UTF_8;	
		}
		final String schema = System.getProperty(SCHEMA_PARAMETER_NAME);
		final boolean firstRowHeader = Boolean.valueOf(System.getProperty(FIRST_ROW_HEADER_PARAMETER_NAME, Boolean.TRUE.toString()));
		boolean success = true;
		
		
		if (url == null || url.isEmpty()) {
			printUsage();
			return;
		}

		final List<File> files = new ArrayList<>();

		for(final String path: args) {
			files.add(new File(path));
		}

		if(files.isEmpty()) {
			files.add(null); // trick to indicate to read from stdin
		}
		try (final Connection connection = DriverManager.getConnection(url, user, password)) {
			if (autoCommit != null) {
				connection.setAutoCommit(Boolean.valueOf(autoCommit));
			}
			
			for(final File file : files) {
				try (final InputStream is =  file != null ? new FileInputStream(file) : System.in; final PushbackInputStream pis = new PushbackInputStream(is);) {
					removeByteOrderMark(pis);
					

					final String table = System.getProperty(TABLE_PARAMETER_NAME, file != null ? getBaseName(file.getName()): null);
					final String qualifiedTable = (schema != null) ? format("{0}.{1}",schema, table) : table;
					final DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(java.util.Locale.getDefault());
					final DecimalFormat bigDecimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(java.util.Locale.getDefault());
					bigDecimalFormat.setParseBigDecimal(true);
					final StringBuilder selectStatmementBuilder = new StringBuilder("SELECT ");
					final StringBuilder insertIntoStatementBuilder = new StringBuilder("INSERT INTO ");
					final StringBuilder insertValuesStatementBuilder = new StringBuilder(" VALUES (");
					insertIntoStatementBuilder.append(qualifiedTable);

					PreparedStatement pstmt = null;
					ResultSetMetaData metaData = null;
					
					try(final InputStreamReader isr = new InputStreamReader(pis, encoding)) {
						List<String> record = null;
						int r = 0;
						while((record = readRecord(isr, separator, quoteChar)) != null) {
							System.out.println(record.stream().collect(Collectors.joining("|")));
					
							if(pstmt == null) {
								boolean flagComma = false;
								if(firstRowHeader) {
									insertIntoStatementBuilder.append(" (");
								}
								for(final String column : record) {
									if(flagComma) {
										if(firstRowHeader) {
											selectStatmementBuilder.append(", ");
											insertIntoStatementBuilder.append(", ");
										}
										insertValuesStatementBuilder.append(", ");
									} else {
										flagComma = true;
									}
									if(firstRowHeader) {
										selectStatmementBuilder.append(column);
										insertIntoStatementBuilder.append(column);
									}
									insertValuesStatementBuilder.append('?');
								}
								if(firstRowHeader) {
									insertIntoStatementBuilder.append(')');
								} else {
									selectStatmementBuilder.append("*");
								}
								selectStatmementBuilder.append(" FROM ");
								selectStatmementBuilder.append(qualifiedTable);
								selectStatmementBuilder.append(" WHERE 1=0");
								insertIntoStatementBuilder.append(insertValuesStatementBuilder.toString());
								insertIntoStatementBuilder.append(')');
								
								final Statement stmt = connection.createStatement();
								final ResultSet resultSet = stmt.executeQuery(selectStatmementBuilder.toString());
								metaData = resultSet.getMetaData();
								pstmt = connection.prepareStatement(insertIntoStatementBuilder.toString());
							}
							
							if(r==0 && firstRowHeader) {
								// Skip
							} else {
								int c = 1;
								for(final String data : record) {
									final int sqlType = metaData.getColumnType(c);
									if (data.isEmpty()) {
										pstmt.setNull(c, sqlType);
									} else if (sqlType == VARCHAR || sqlType == CHAR) {
										pstmt.setString(c, data);
									} else if (sqlType == DOUBLE) {
										pstmt.setDouble(c, decimalFormat.parse(data).doubleValue());
									} else if (sqlType == INTEGER) {
										pstmt.setInt(c, Integer.parseInt(data));
									} else if (sqlType == BOOLEAN || sqlType == BIT) {
										pstmt.setBoolean(c, "1".equals(data) || Boolean.valueOf(data));
									} else if (sqlType == TIMESTAMP) {
										if(excelDate) {
											final Timestamp timestamp = toTimestamp(decimalFormat.parse(data).doubleValue());	
											pstmt.setTimestamp(c, timestamp);
										} else {
											pstmt.setObject(c, data);
										}
									} else if (sqlType == BIGINT) {
										pstmt.setBigDecimal(c, (BigDecimal) bigDecimalFormat.parse(data));
									} else {
										pstmt.setObject(c, data);
									}								
									c++;
								}
								pstmt.addBatch();
							}
							r++;
						}
						
						pstmt.executeBatch();
					}
				}
			}
			
			if(commit) {
				connection.commit();
			} else {
				connection.rollback();
			}
		}

		if (!success) {
			System.exit(1);
		}
	}

	/**
	 * Print usage
	 */
	private static void printUsage() {
		final String user = System.getProperty("user.name");
		final String className = DatabaseQuery.class.getSimpleName();
		final String pathSeparator = System.getProperty("path.separator");
		final int windowsWordOffset = System.getProperty("os.name").indexOf("Windows") + 1;
		System.out.println(format("Usage: java -D{0}=<jdbcConnectionString> -D{1}=<{1}> -D{2}=<{2}> {3} <file> ...", URL_PARAMETER_NAME, USER_PARAMETER_NAME, PASSWORD_PARAMETER_NAME, DatabaseQuery.class.getSimpleName()));
		System.out.println();
		System.out.println("Examples");
		System.out.println();
		System.out.println(format("java -cp h2-1.4.200.jar{0}. -D{1}=\"jdbc:h2:mem:mydb\" -D{2}=my_table {3} data1.tab data2.tab data3.tab", pathSeparator, URL_PARAMETER_NAME, TABLE_PARAMETER_NAME, className));
		System.out.println(format("echo {0,choice,0#\"|1#|2#}{1}{0,choice,0#\"|1#|2#} | java -cp h2-1.4.200.jar{2}. -D{3}=\"jdbc:h2:mem:mydb\" -D{4}=false {5} my_table.tab", windowsWordOffset, "first,second,third", pathSeparator, URL_PARAMETER_NAME, FIRST_ROW_HEADER_PARAMETER_NAME, className));
		System.out.println(format("java -cp h2-1.4.200.jar{0}. -D{1}=\"jdbc:h2:mem:mydb\" -Dseparator=\",\" -DquoteChar=\"\\\"\" -DescapeChar=\"\\\"\" {2} my_table.csv", pathSeparator, URL_PARAMETER_NAME, className));
		System.out.println(format("java -cp mssql-jdbc-9.4.0.jre11.jar{0}. -D{1}=\"jdbc:sqlserver://my-sqlserver.lan:1433;databaseName=master\" -D{2}=\"{3}\" -D{4}=\"changeit\" {5} my_table.tab", pathSeparator, URL_PARAMETER_NAME, USER_PARAMETER_NAME, user, PASSWORD_PARAMETER_NAME, className));
		System.out.println();
		System.out.println("Parameters:");
		final Map<String, String> parametersDescriptions = new LinkedHashMap<>();
		parametersDescriptions.put(URL_PARAMETER_NAME, "JDBC connection string (mandatory)");
		parametersDescriptions.put(USER_PARAMETER_NAME, "database user (optional)");
		parametersDescriptions.put(PASSWORD_PARAMETER_NAME, "database password (optional)");
		parametersDescriptions.put(SEPARATOR_PARAMETER_NAME, "field separator (optional, default value: tab)");
		parametersDescriptions.put(QUOTE_CHAR_PARAMETER_NAME, "quoting character for String, Date and Timestamp values (optional, default value: none)");
		parametersDescriptions.put(QUERY_SEPARATOR_PARAMETER_NAME, "query separator (optional, relies on system line separator value)");
		parametersDescriptions.put(EXCEL_DATE_PARAMETER_NAME, "dates and timestamps as Excel dates, i.e. fractional days since January 1st 1900 (optional, true or false, default: false)");
		parametersDescriptions.put(AUTO_COMMIT_PARAMETER_NAME, "enable/disable auto-commit mode (optional, true or false, relies on driver default value)");
		parametersDescriptions.put(COMMIT_PARAMETER_NAME, "perform commit after load (optional, true (=commit) or false (=rollback), default: true)");
		parametersDescriptions.put(CSV_PARAMETER_NAME, format("equivalent to [-D{0}=, -D{1}=\"] (optional)", SEPARATOR_PARAMETER_NAME, QUOTE_CHAR_PARAMETER_NAME));
		parametersDescriptions.put(ENCODING_PARAMETER_NAME, "the file encoding (optional, default=UTF-8)");
		parametersDescriptions.put(SCHEMA_PARAMETER_NAME, "the name of the schema into which the given data will be inserted (optional)");
		parametersDescriptions.put(TABLE_PARAMETER_NAME, "the name of the table into which the given data will be inserted (optional, if not given then the name of the file without its extension will be used as table name)");
		parametersDescriptions.put(FIRST_ROW_HEADER_PARAMETER_NAME, "first row of data is the header (optional, true or false, default: true)");
		

		// @formatter:on
		for (final Entry<String, String> entry : parametersDescriptions.entrySet()) {
			System.out.print(format("  -D{0}", entry.getKey()));
			final int n = 3 - ((entry.getKey().length() + 4) / 8);
			for (int i = 0; i < n; i++) {
				System.out.print('\t');
			}
			System.out.println(entry.getValue());
		}
	}

	/**
	 * Returns either the value of the given system property or the given default value.<br>
	 * <br>
	 * Note: this method allows the substitution of escaped control characters, such as '\t', '\r', '\n' and '\f'.<br>
	 * @param propertyName the name of the system property
	 * @param defaultValue the default value in case the system property is not defined
	 * @return the value of the system property
	 * @throws IOException in case of unexpected error
	 */
	private static String getSystemProperty(final String propertyName, final String defaultValue) throws IOException {
		final String result;
		final String value = System.getProperty(propertyName);
		if (value == null) {
			result = defaultValue;
		} else {
			final Properties properties = new Properties();
			final StringReader reader = new StringReader(propertyName + "=" + value + "\n");
			properties.load(reader);
			result = properties.getProperty(propertyName);
		}

		return result;
	}
	
	/**
	 * Reads a "Comma-Separated Values" record.<br>
	 * Records must be compliant to <a href="https://datatracker.ietf.org/doc/html/rfc4180">RFC 4180</a>. 
	 * @param reader the reader
	 * @param separator field separator (e.g. comma, semicolon, tab)
	 * @param quoteChar quoting character (e.g. double quote, simple quote. -1 = none) 
	 * @return the record
	 * @throws IOException in case of unexpected error
	 */
	private static List<String> readRecord(final Reader reader, final char separator, final char quoteChar) throws IOException {
		
		//
		assert reader != null;
		assert separator != quoteChar;
		//
		
		final StringBuilder builder = new StringBuilder(); // builder for the current field
		int p = -1; // previous char
		int c = reader.read(); // current char
		boolean quoteless = true; // quote-less line flag
		boolean quoting = false; // quoting flag
		if(c == -1) {
			return null; // end of file
		}
		final List<String> record = new ArrayList<>();
		while(c != -1) {
			if(c == separator || c == '\r' || c == '\n') {
				if(quoting) {
					// quoting ... may span over multiple lines
					builder.append((char) c);
				} else if (c == separator) {
					// end of field
					record.add(builder.toString());
					quoteless=true;
					builder.setLength(0);
				} else if (c == '\n') {
					// end of record
					break; 
				}
				// Skip '\r'
			} else {
				if(c == quoteChar) {
					if(quoteless) {
						quoting = true;
						quoteless = false;
						builder.setLength(0); // strip everything before the first quote
					} else if(quoting) {
						quoting = false;
					} else {
						quoting = true;
						if(p == quoteChar) {
							// keep escaped quote character
							builder.append((char) c);
						}
					}
				} else if(quoteless || quoting) {
					builder.append((char) c);
				}
			}
			p = c;
			c = reader.read();
		}
		record.add(builder.toString());
		return record;
	}
	
	/**
	 * Removes the Byte Order Mark if present
	 * @param pushbackInputStream the push-back input stream
	 * @throws IOException in case of unexpected error
	 */
	private static void removeByteOrderMark(final PushbackInputStream pushbackInputStream) throws IOException {
		final int n=3;
		byte[] bom = new byte[n];
		if (pushbackInputStream.read(bom) != -1) {
			if (bom[n - 3] == (byte) 0xEF && bom[n - 2] == (byte) 0xBB && bom[n - 1] == (byte) 0xBF) {
				// BOM discarded !
			} else {
				// Not a BOM, send back the data to the stream and move forward
				pushbackInputStream.unread(bom);
			}
		}
	}
	
	
	/**
	 * Returns the filename without its extension
	 * @param filename the filename
	 * @return the filename without its extension
	 */
	private static String getBaseName(final String filename) {
		int offset = filename.lastIndexOf('.');
		if (offset != -1) {
			return filename.substring(0, offset);
		} else {
			return filename;
		}
	}
	
	/**
	 * Converts the given excel time stamp into a java time stamp<br>
	 * <br>
	 * Note the precision is up to the millisecond, and therefore not beyond!
	 * @param timestamp the excel time stamp
	 * @return the java time stamp
	 */
	private static Timestamp toTimestamp(final double timestamp) {
		//
		assert !Double.isNaN(timestamp);
		assert timestamp >= 0d;
		//

		// For DBase and thus Excel, Epoch is "January 0th 1900"
		final LocalDateTime epoch = LocalDateTime.of(1899, 12, 31, 0, 0);

		// For DBase and thus Excel, Epoch + 60 days = "February 29th 1900"
		// ... an non-existing day because 1900 is not a leap year,
		// therefore, starting from the 61st day after the Epoch, this non-existing day has to be skipped.

		// 1 day == 24h * 60m * 60s == 86_400 s
		final double secondsSinceExcelEpoch = (timestamp - (timestamp >= 61d ? 1d : 0d)) * 86_400d;
		final long millisSinceExcelEpoch = Math.round(secondsSinceExcelEpoch) * 1000L;
		final LocalDateTime result = epoch.plus(millisSinceExcelEpoch, ChronoUnit.MILLIS);

		return Timestamp.valueOf(result);
	}
}
