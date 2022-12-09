import static java.sql.Types.BIGINT;
import static java.sql.Types.BIT;
import static java.sql.Types.BOOLEAN;
import static java.sql.Types.CHAR;
import static java.sql.Types.DATE;
import static java.sql.Types.DECIMAL;
import static java.sql.Types.DOUBLE;
import static java.sql.Types.FLOAT;
import static java.sql.Types.INTEGER;
import static java.sql.Types.LONGVARCHAR;
import static java.sql.Types.NUMERIC;
import static java.sql.Types.SMALLINT;
import static java.sql.Types.TIME;
import static java.sql.Types.TIMESTAMP;
import static java.sql.Types.TINYINT;
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
import  java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Load a database via a JDBC connection
 */
@SuppressWarnings("nls")
public class DatabaseLoad {

	private static final String URL_PARAMETER_NAME = "url";
	private static final String USER_PARAMETER_NAME = "user";
	private static final String PASSWORD_PARAMETER_NAME = "password";
	private static final String SEPARATOR_PARAMETER_NAME = "separator";
	private static final String QUOTE_CHAR_PARAMETER_NAME = "quoteChar";
	private static final String QUERY_SEPARATOR_PARAMETER_NAME = "querySeparator";
	private static final String EXCEL_DATE_PARAMETER_NAME = "excelDate";
	private static final String AUTO_COMMIT_PARAMETER_NAME = "autoCommit";
	private static final String ROLLBACK_PARAMETER_NAME = "rollback";
	private static final String CSV_PARAMETER_NAME = "csv";
	private static final String ENCODING_PARAMETER_NAME = "encoding";
	private static final String SCHEMA_PARAMETER_NAME = "schema";
	private static final String TABLE_PARAMETER_NAME = "table";
	private static final String FIRST_ROW_HEADER_PARAMETER_NAME = "firstRowHeader";
	private static final String QUOTE_COLUMNS_PARAMETER_NAME = "quoteColumns";
	private static final String DRY_RUN_PARAMETER_NAME = "dryRun";
	private static final String BATCH_PARAMETER_NAME = "batch";
	private static final String ON_ERROR_CONTINUE_PARAMETER_NAME = "onErrorContinue";

	private static final DecimalFormat DECIMAL_FORMAT = (DecimalFormat) NumberFormat.getNumberInstance(java.util.Locale.getDefault());
	private static final DecimalFormat BIG_DECIMAL_FORMAT = (DecimalFormat) NumberFormat.getNumberInstance(java.util.Locale.getDefault());
	
	static {
		BIG_DECIMAL_FORMAT.setParseBigDecimal(true);
	}

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
	 * <li>rollback: perform rollback after load (optional, default: commit)
	 * <li>csv: equivalent to separator=, and quoteChar=" (optional)
	 * <li>encoding: the file encoding (optional, default={@link StandardCharsets#UTF_8 UTF-8})
	 * <li>schema: name of the schema into which the given data will be inserted (optional)
	 * <li>table: name of the table into which the given data will be inserted (optional, if not given then the name of the file without its extension will be used as table name)
	 * <li>firstRowHeader: first row of data is the header (optional)
	 * <li>quoteColumns: quote the name of the columns in the "INSERT INTO" SQL statement (optional, default: do not quote columns)
	 * <li>dryRun: dry run, i.e. print out the "INSERT INTO" SQL statements and do not execute them (optional) 
	 * <li>batch: execute in batch (optional) 
	 * <li>onErrorContinue: on error print the error message then continue (optional, default: exit the program)  
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

		final boolean excelDate = systemProperties.containsKey(EXCEL_DATE_PARAMETER_NAME);
		final boolean dryRun = systemProperties.containsKey(DRY_RUN_PARAMETER_NAME) || systemProperties.containsKey(DRY_RUN_PARAMETER_NAME.toLowerCase());
		final boolean rollback = systemProperties.containsKey(ROLLBACK_PARAMETER_NAME);
		final Charset encoding;
		if(systemProperties.containsKey(ENCODING_PARAMETER_NAME)) {
			encoding = Charset.forName(System.getProperty(ENCODING_PARAMETER_NAME));
		} else {
			encoding = StandardCharsets.UTF_8;	
		}
		final String schema = System.getProperty(SCHEMA_PARAMETER_NAME);
		final boolean firstRowHeader = systemProperties.containsKey(FIRST_ROW_HEADER_PARAMETER_NAME);
		final boolean quoteColumns = systemProperties.containsKey(QUOTE_COLUMNS_PARAMETER_NAME);
		final boolean batch = systemProperties.containsKey(BATCH_PARAMETER_NAME);
		final boolean onErrorContinue = systemProperties.containsKey(ON_ERROR_CONTINUE_PARAMETER_NAME);
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
			if (systemProperties.containsKey(AUTO_COMMIT_PARAMETER_NAME)) {
				final boolean autoCommit = Boolean.valueOf(System.getProperty(AUTO_COMMIT_PARAMETER_NAME));
				connection.setAutoCommit(autoCommit);
			}
			final boolean autoCommit = connection.getAutoCommit();
			final boolean sqlServer = connection.getMetaData().getDriverName().contains("Microsoft") && connection.getMetaData().getDriverName().contains("SQL Server");
			final char columnLeftQuote = sqlServer?'[':'"';
			final char columnRightQuote = sqlServer?']':'"';
			
			for(final File file : files) {
				try (final InputStream is =  file != null ? new FileInputStream(file) : System.in; final PushbackInputStream pis = new PushbackInputStream(is);) {
					System.out.println(file);
					removeByteOrderMark(pis);

					try(final InputStreamReader isr = new InputStreamReader(pis, encoding)) {
						final AtomicInteger lineNumber = new AtomicInteger();
						List<String> record = readRecord(isr, separator, quoteChar, lineNumber);
						
						if(record != null) {
							final String insertIntoPrefix;
							final String insertIntoParametrized;
							int[] sqlTypes = {};
							{
								final String table = System.getProperty(TABLE_PARAMETER_NAME, file != null ? getBaseName(file.getName()): null);
								final String qualifiedTable = (schema != null) ? format("{0}.{1}",schema, table) : table;
								final StringBuilder selectStatmementBuilder = new StringBuilder("SELECT ");
								final StringBuilder insertIntoStatementBuilder = new StringBuilder("INSERT INTO ");
								final StringBuilder insertValuesStatementBuilder = new StringBuilder(" VALUES (");
								insertIntoStatementBuilder.append(qualifiedTable);

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
										if(quoteColumns) {
											insertIntoStatementBuilder.append(columnLeftQuote);
										}
										insertIntoStatementBuilder.append(column);
										if(quoteColumns) {
											insertIntoStatementBuilder.append(columnRightQuote);
										}
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
								insertIntoPrefix = insertIntoStatementBuilder.toString();
								insertIntoStatementBuilder.append(insertValuesStatementBuilder.toString());
								insertIntoStatementBuilder.append(')');
								insertIntoParametrized = insertIntoStatementBuilder.toString();
								
								
								try(final Statement stmt = connection.createStatement(); final ResultSet resultSet = stmt.executeQuery(selectStatmementBuilder.toString())) {
									final ResultSetMetaData metaData = resultSet.getMetaData();
									sqlTypes = new int[metaData.getColumnCount()];
									for(int c=1; c <= sqlTypes.length; c++) {
										sqlTypes[c-1]  = metaData.getColumnType(c);
									}
								}
							}
							
							try(final PreparedStatement pstmt = connection.prepareStatement(insertIntoParametrized)) {
								if(firstRowHeader) {
									// skip first record
									record = readRecord(isr, separator, quoteChar, lineNumber);
								}

								while(record != null) {
									if(dryRun) {
										final String insertIntoStatement = buildInsertStatement(record, insertIntoPrefix, sqlTypes, excelDate);
										System.out.println(insertIntoStatement);
									} else {
										int c = 1;
										for(final String data : record) {
											final int sqlType = sqlTypes[c-1];
											if (data.isEmpty()) {
												pstmt.setNull(c, sqlType);
											} else if (sqlType == VARCHAR || sqlType == CHAR || sqlType == LONGVARCHAR) {
												pstmt.setString(c, data);
											} else if (sqlType == INTEGER) {
												pstmt.setInt(c, Integer.parseInt(data));
											} else if (sqlType == DOUBLE) {
												pstmt.setDouble(c, DECIMAL_FORMAT.parse(data).doubleValue());
											} else if (sqlType == FLOAT) {
												pstmt.setFloat(c, DECIMAL_FORMAT.parse(data).floatValue());
											} else if (sqlType == SMALLINT) {
												pstmt.setShort(c, Short.parseShort(data));
											} else if (sqlType == BOOLEAN || sqlType == BIT) {
												pstmt.setBoolean(c, "1".equals(data) || Boolean.valueOf(data));
											} else if (sqlType == TIMESTAMP || sqlType == DATE || sqlType == TIME) {
												if(excelDate) {
													final LocalDateTime localDateTime = toLocalDateTime(DECIMAL_FORMAT.parse(data).doubleValue());
													if(sqlType == TIMESTAMP) {
														final Timestamp timestamp = Timestamp.valueOf(localDateTime);
														pstmt.setTimestamp(c, timestamp);
													} else if (sqlType == DATE) {
														final Date date = Date.valueOf(localDateTime.toLocalDate());
														pstmt.setDate(c, date);
													} else if (sqlType == TIME) {
														final Time time = Time.valueOf(localDateTime.toLocalTime());
														pstmt.setTime(c, time);	
													}
												} else {
													pstmt.setObject(c, data);
												}
											} else if (sqlType == BIGINT || sqlType == DECIMAL || sqlType == NUMERIC) {
												pstmt.setBigDecimal(c, (BigDecimal) BIG_DECIMAL_FORMAT.parse(data));
											} else if (sqlType == TINYINT) {
												pstmt.setByte(c, (byte)Integer.parseInt(data));
											} else {
												pstmt.setObject(c, data);
											}								
											c++;
										}
										if(batch) {
											pstmt.addBatch();
										} else if (onErrorContinue) {
											try {
												pstmt.execute();
											} catch (final SQLException e) {
												success = false;
												final String insertIntoStatement = buildInsertStatement(record, insertIntoPrefix, sqlTypes, excelDate);
												String message = e.getMessage();
												if(message != null) {
													message = message.trim().replaceAll(System.lineSeparator(), " ~ ");
												} else {
													message = "";
												}
												System.err.println(format("{0}:{1,number,0}:{2}:{3}", file.getPath(), lineNumber.get(), message, insertIntoStatement));
											}
										} else {
											pstmt.execute();
										}
									}
									record = readRecord(isr, separator, quoteChar, lineNumber);
								}

								if(!dryRun && batch) {
									if (onErrorContinue) {
										try {
											pstmt.executeBatch();
										} catch (final SQLException e) {
											success = false;
											String message = e.getMessage();
											if(message != null) {
												message = message.trim().replaceAll(System.lineSeparator(), " ~ ");
											} else {
												message = "";
											}
											System.err.println(format("{0}:{1}", file.getPath(), message));
										}
									} else {
										pstmt.executeBatch();
									}
								}
							}
						}
					}
				}
			}

			if (!dryRun && !autoCommit) {
				if(rollback) {
					connection.rollback();
				} else {
					connection.commit();
				}
			}
		}

		if (!success) {
			System.exit(1);
		}
	}

	private static String buildInsertStatement(final List<String> record, final String insertIntoPrefix, final int[] sqlTypes, final boolean excelDate) throws ParseException {
		final StringBuilder result = new StringBuilder(insertIntoPrefix);
		result.append(" VALUES (");
		int c = 1;
		for(final String data : record) {
			final int sqlType = sqlTypes[c-1];
				if(c > 1) {
					result.append(", ");
				}
				if (data.isEmpty()) {
					result.append("NULL");
				} else if (sqlType == DOUBLE) {
					final Double value = DECIMAL_FORMAT.parse(data).doubleValue();
					result.append(value);
				} else if (sqlType == FLOAT) {
					final Float value = DECIMAL_FORMAT.parse(data).floatValue();
					result.append(value);
				} else if (sqlType == INTEGER || sqlType == SMALLINT || sqlType == TINYINT) {
					result.append(data);
				} else if (sqlType == BIGINT || sqlType == DECIMAL || sqlType == NUMERIC) {
					final BigDecimal value = (BigDecimal) BIG_DECIMAL_FORMAT.parse(data);
					result.append(value);
				} else if (sqlType == BOOLEAN) {
					final Boolean value = "1".equals(data) || Boolean.valueOf(data);
					result.append(value.toString());
				} else if (sqlType == BIT) {
					final boolean value = "1".equals(data) || Boolean.valueOf(data);
					result.append(value?"1":"0");												
				} else {
					final String value;
					if (sqlType == TIMESTAMP && excelDate) {
						final LocalDateTime localDateTime = toLocalDateTime(DECIMAL_FORMAT.parse(data).doubleValue());
						value = localDateTime.toString();
					} else {
						value = data.replaceAll("'", "''"); 
					}
					result.append('\'');
					result.append(value);
					result.append('\'');
				}
			c++;
		}
		result.append(");");
		return result.toString();
	}
	/**
	 * Print usage
	 */
	private static void printUsage() {
		final String user = System.getProperty("user.name");
		final String className = DatabaseLoad.class.getSimpleName();
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
		parametersDescriptions.put(EXCEL_DATE_PARAMETER_NAME, "dates and timestamps as Excel dates, i.e. fractional days since January 1st 1900 (optional)");
		parametersDescriptions.put(AUTO_COMMIT_PARAMETER_NAME, "enable/disable auto-commit mode (optional, true or false, relies on driver default value)");
		parametersDescriptions.put(ROLLBACK_PARAMETER_NAME, "perform rollback after load (optional, default: commit)");
		parametersDescriptions.put(CSV_PARAMETER_NAME, format("equivalent to [-D{0}=, -D{1}=\"] (optional)", SEPARATOR_PARAMETER_NAME, QUOTE_CHAR_PARAMETER_NAME));
		parametersDescriptions.put(ENCODING_PARAMETER_NAME, "the file encoding (optional, default=UTF-8)");
		parametersDescriptions.put(SCHEMA_PARAMETER_NAME, "the name of the schema into which the given data will be inserted (optional)");
		parametersDescriptions.put(TABLE_PARAMETER_NAME, "the name of the table into which the given data will be inserted (optional, if not given then the name of the file without its extension will be used as table name)");
		parametersDescriptions.put(FIRST_ROW_HEADER_PARAMETER_NAME, "first row of data is the header (optional)");
		parametersDescriptions.put(QUOTE_COLUMNS_PARAMETER_NAME, "quote the name of the columns in the \"INSERT INTO\" SQL statement (optional, default: do not quote columns)");
		parametersDescriptions.put(DRY_RUN_PARAMETER_NAME, "dry run, i.e. print out the \"INSERT INTO\" SQL statements and do not execute them (optional)");
		parametersDescriptions.put(BATCH_PARAMETER_NAME, "execute in batch (optional)");
		parametersDescriptions.put(ON_ERROR_CONTINUE_PARAMETER_NAME, "on error print the error message then continue (optional, default: exit the program)");

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
	 * @param lineNumber line number (out parameter) 
	 * @return the record
	 * @throws IOException in case of unexpected error
	 */
	private static List<String> readRecord(final Reader reader, final char separator, final char quoteChar, final AtomicInteger lineNumber) throws IOException {
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
					if (c == '\n') {
						lineNumber.incrementAndGet();	
					}
				} else if (c == separator) {
					// end of field
					record.add(builder.toString());
					quoteless=true;
					builder.setLength(0);
				} else if (c == '\n') {
					// end of record
					lineNumber.incrementAndGet();
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
		final int b1 = pushbackInputStream.read();
		if(b1 == -1) {
			return;
		}
		
		if(b1 == 0xEF) {
			final int b2 = pushbackInputStream.read();
			if(b2 == -1) {
				pushbackInputStream.unread(b1);
				return;
			}

			if(b1 == 0xBB) {
				final int b3 = pushbackInputStream.read();			
				
				if(b3 == -1) {
					pushbackInputStream.unread(b2);				
					pushbackInputStream.unread(b1);				
					return;
				}
				if(b3 == 0xBF) {
					return; // BOM discarded
				} else {
					pushbackInputStream.unread(b3);				
					pushbackInputStream.unread(b2);				
					pushbackInputStream.unread(b1);				
					return;
				}
			} else {
				pushbackInputStream.unread(b2);				
				pushbackInputStream.unread(b1);				
				return;
			}

		} else {
			pushbackInputStream.unread(b1);
			return;
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
	private static LocalDateTime toLocalDateTime(final double timestamp) {
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

		return result;
	}
}
