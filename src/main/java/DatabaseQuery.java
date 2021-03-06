import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Query a database via a JDBC connection
 */
@SuppressWarnings("nls")
public class DatabaseQuery {

	private final static String URL_PARAMETER_NAME = "url";
	private final static String USER_PARAMETER_NAME = "user";
	private final static String PASSWORD_PARAMETER_NAME = "password";
	private final static String SEPARATOR_PARAMETER_NAME = "separator";
	private final static String QUOTE_CHAR_PARAMETER_NAME = "quoteChar";
	private final static String ESCAPE_CHAR_PARAMETER_NAME = "escapeChar";
	private final static String ROW_SEPARATOR_PARAMETER_NAME = "rowSeparator";
	private final static String QUERY_SEPARATOR_PARAMETER_NAME = "querySeparator";
	private final static String PRINT_HEADER_PARAMETER_NAME = "printHeader";
	private final static String PRINT_NULL_PARAMETER_NAME = "printNull";
	private final static String NULL_VALUE_PARAMETER_NAME = "nullValue";
	private final static String PRINT_EXCEL_DATE_PARAMETER_NAME = "printExcelDate";
	private final static String AUTO_COMMIT_PARAMETER_NAME = "autoCommit";
	private final static String COMMIT_PARAMETER_NAME = "commit";
	private final static String FILE_NAME_SCHEME_PARAMETER_NAME = "fileNameScheme";
	private final static String FILE_NAME_EXTENSION_PARAMETER_NAME = "fileNameExtension";

	/** The Excel Epoch (i.e. 1/1/1900) */
	private static final LocalDate EXCEL_EPOCH = LocalDate.of(1900, 1, 1);

	/** The Unix Epoch (i.e. 1/1/1970) */
	private static final LocalDate UNIX_EPOCH = LocalDate.of(1970, 1, 1);

	/** Milliseconds between the Excel Epoch (i.e. 1/1/1900), and the Unix Epoch (i.e. 1/1/1970) */
	private static final long MILLISECONDS_BETWEEN_EXCEL_EPOCH_AND_UNIX_EPOCH = (ChronoUnit.DAYS.between(EXCEL_EPOCH, UNIX_EPOCH) + 2L /* correction for Excel */) * 86_400_000L;

	/**
	 * Usage: java -Durl=&lt;jdbcConnectionString&gt; -Duser=&lt;user&gt; -Dpassword=&lt;password&gt; DatabaseQuery &lt;sql&gt;<br>
	 * <br>
	 * System parameters:
	 * <ul>
	 * <li>url: JDBC connection string
	 * <li>user: database user
	 * <li>password: database password
	 * <li>separator: field separator (optional, default value: tab)
	 * <li>quoteChar: quoting character for String, Date and Timestamp values (optional, default value: none)
	 * <li>escapeChar: escape character for the quoting parameter of a String, a Date or a Timestamp value (optional, default value: none)
	 * <li>rowSeparator: row separator (optional, relies on system line separator value)
	 * <li>querySeparator: query separator (optional, relies on system line separator value)
	 * <li>printHeader: print-out the header (optional, true or false, default: true)
	 * <li>printNull: print-out null values (optional, true or false, default: false)
	 * <li>nullValue: null replacement value (optional, relies on system null representation)
	 * <li>printExcelDate: print-out both dates and timestamps as Excel dates, i.e. fractional days since January 1st 1900 (optional, true or false, default: false)
	 * <li>autoCommit: enable/disable auto-commit mode (optional, true or false, relies on driver default value)
	 * <li>commit: perform commit after UPDATE/INSERT/DELETE (optional, true or false, relies on driver default value)
	 * <li>fileNameScheme: file name scheme (optional, outputs to standard out by default)
	 * <ol>
	 * <li>query ordinal
	 * <li>timestamp
	 * <li>universally unique identifier
	 * </ol>
	 * <li>fileNameExtension: file name extension (optional, default: csv)
	 * </ul>
	 * @param args SQL queries
	 * @throws Exception in case of unexpected error
	 */
	public static void main(String[] args) throws Exception {
		final String url = System.getProperty(URL_PARAMETER_NAME);
		final String user = System.getProperty(USER_PARAMETER_NAME);
		final String password = System.getProperty(PASSWORD_PARAMETER_NAME);
		final String separator = getSystemProperty(SEPARATOR_PARAMETER_NAME, "\t");
		final String quoteChar = getSystemProperty(QUOTE_CHAR_PARAMETER_NAME, null);
		final String escapeChar = getSystemProperty(ESCAPE_CHAR_PARAMETER_NAME, null);
		final Pattern escapePattern = escapeChar != null && quoteChar != null ? Pattern.compile("[" + quoteChar + "]", Pattern.MULTILINE) : null;
		final String escapedQuoteChar = escapePattern != null ? escapeChar + quoteChar : null;
		final String lineSeparator = System.getProperty("line.separator", "\n");
		final String rowSeparator = getSystemProperty(ROW_SEPARATOR_PARAMETER_NAME, lineSeparator);
		final String querySeparator = getSystemProperty(QUERY_SEPARATOR_PARAMETER_NAME, lineSeparator);
		final boolean printHeader = Boolean.valueOf(System.getProperty(PRINT_HEADER_PARAMETER_NAME, Boolean.TRUE.toString()));
		final boolean printNull = Boolean.valueOf(System.getProperty(PRINT_NULL_PARAMETER_NAME, Boolean.FALSE.toString()));
		final boolean printExcelDate = Boolean.valueOf(System.getProperty(PRINT_EXCEL_DATE_PARAMETER_NAME, Boolean.FALSE.toString()));
		final String nullValue = getSystemProperty(NULL_VALUE_PARAMETER_NAME, String.valueOf((Object) null));
		final String autoCommit = System.getProperty(AUTO_COMMIT_PARAMETER_NAME);
		final String commit = System.getProperty(COMMIT_PARAMETER_NAME);
		final String fileNameScheme = System.getProperty(FILE_NAME_SCHEME_PARAMETER_NAME);
		final String fileNameExtension = System.getProperty(FILE_NAME_EXTENSION_PARAMETER_NAME, "csv");
		final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		boolean success = true;

		// Command line arguments
		final List<String> queries = new ArrayList<>();
		for (final String sql : args) {
			queries.add(sql);
		}
		// Standard Input handling
		try (final InputStreamReader isr = new InputStreamReader(System.in); final BufferedReader br = new BufferedReader(isr)) {
			final StringBuilder builder = new StringBuilder();
			while (br.ready()) {
				String line = br.readLine();
				if (line != null) {
					line = line.trim();
					if (!line.isEmpty() && !line.startsWith("--")) {
						if (builder.length() > 0) {
							builder.append(lineSeparator);
						}

						boolean endsWithSemicolon = false;
						final int semicolonOffset = line.lastIndexOf(';');
						if (semicolonOffset != -1) {
							endsWithSemicolon = semicolonOffset == (line.length() - 1);
							if (!endsWithSemicolon) {
								final int doubleMinusOffset = line.indexOf("--");
								if (doubleMinusOffset > semicolonOffset) {
									final String whitespaces = line.substring(semicolonOffset + 1, doubleMinusOffset);
									if (whitespaces.matches("\\s*")) {
										line = line.substring(0, semicolonOffset);
										endsWithSemicolon = true;
									}
								}
							}
						}

						builder.append(line);
						if (endsWithSemicolon) {
							queries.add(builder.toString());
							builder.setLength(0);
						}
					}
				}
			}
			if (builder.length() > 0) {
				queries.add(builder.toString());
			}
		}

		if (url == null || url.isEmpty() || queries.isEmpty()) {
			printUsage();
			return;
		}

		try (final Connection connection = DriverManager.getConnection(url, user, password)) {
			if (autoCommit != null) {
				connection.setAutoCommit(Boolean.valueOf(autoCommit));
			}
			for (int i = 0; i < queries.size(); i++) {
				final String filename;
				{
					final String basename;
					if ("1".equals(fileNameScheme)) {
						basename = Integer.toString(i);
					} else if ("2".equals(fileNameScheme)) {
						basename = DateTimeFormatter.ofPattern("yyyy-MM-dd\u00A0HH\u00F7mm\u00F7ss\u2027SSS").format(LocalDateTime.now());
					} else if ("3".equals(fileNameScheme)) {
						basename = UUID.randomUUID().toString();
					} else {
						basename = null;
					}

					if (basename != null) {
						filename = basename + "." + fileNameExtension;
					} else {
						filename = null;
					}
				}

				if (i > 0 && filename == null) {
					System.out.print(querySeparator);
					System.out.flush();
				}

				try (final PrintStream ps = filename != null ? new PrintStream(filename) : null) {
					final PrintStream out = ps != null ? ps : System.out;
					final String sql = queries.get(i);
					if (sql.startsWith("SELECT")) {
						try (final Statement statement = connection.createStatement(); final ResultSet rs = statement.executeQuery(sql)) {
							final ResultSetMetaData metaData = rs.getMetaData();
							final int n = metaData.getColumnCount();
							// Header
							if (printHeader) {
								for (int c = 1; c <= n; c++) {
									if (c > 1) {
										out.print(separator);
									}
									final String columnName = metaData.getColumnName(c);
									if (quoteChar != null) {
										out.print(quoteChar);
									}
									if (escapePattern != null) {
										final Matcher matcher = escapePattern.matcher(columnName);
										out.print(matcher.replaceAll(escapedQuoteChar));
									} else {
										out.print(columnName);
									}
									if (quoteChar != null) {
										out.print(quoteChar);
									}
								}
								out.print(rowSeparator);
							}
							while (rs.next()) {
								for (int c = 1; c <= n; c++) {
									if (c > 1) {
										out.print(separator);
									}
									final Object value = rs.getObject(c);
									if (value != null) {
										if (printExcelDate && value instanceof Date) {
											final double excelDate = Long.valueOf(rs.getTimestamp(c, calendar).getTime() + MILLISECONDS_BETWEEN_EXCEL_EPOCH_AND_UNIX_EPOCH).doubleValue() / 86_400_000d;
											out.print(excelDate);
										} else if (quoteChar != null && (value instanceof String || value instanceof Date)) {
											out.print(quoteChar);
											if (escapePattern != null) {
												final Matcher matcher = escapePattern.matcher(value.toString());
												out.print(matcher.replaceAll(escapedQuoteChar));
											} else {
												out.print(value.toString());
											}
											out.print(quoteChar);
										} else {
											out.print(value.toString());
										}
									} else if (printNull) {
										out.print(nullValue);
									}
								}
								out.print(rowSeparator);
							}
						} catch (final SQLException e) {
							e.printStackTrace();
							success = false;
						}
					} else {
						try (final Statement statement = connection.createStatement()) {
							final int updateCount = statement.executeUpdate(sql);
							out.print(updateCount);
							out.print(rowSeparator);
							if (!connection.getAutoCommit() && commit != null) {
								if (Boolean.valueOf(commit)) {
									connection.commit();
								} else {
									connection.rollback();
								}
							}
						} catch (final SQLException e) {
							e.printStackTrace();
							success = false;
						}
					}
				}
				if (filename != null) {
					System.out.println(filename);
				}
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
		final String sql1 = "SELECT 'John' AS firstname";
		final String sql2 = "SELECT 'Doe' AS lastname";
		final String sql3 = sql1 + ", 'Doe' AS lastname";
		final String sql4 = sql3 + ", 'Hello \\\"John\\\"!' AS greeting";
		// @formatter:off
		System.out.println(MessageFormat.format("Usage: java -D{0}=<jdbcConnectionString> -D{1}=<{1}> -D{2}=<{2}> {3} <sql> ...", URL_PARAMETER_NAME, USER_PARAMETER_NAME, PASSWORD_PARAMETER_NAME, DatabaseQuery.class.getSimpleName()));
		System.out.println();
		System.out.println("Examples");
		System.out.println();
		System.out.println(MessageFormat.format("java -cp h2-1.4.200.jar{0}. -D{1}=\"jdbc:h2:mem:mydb\" {2} \"{3}\" \"{4}\"", pathSeparator, URL_PARAMETER_NAME, className, sql1, sql2));
		System.out.println(MessageFormat.format("echo {0,choice,0#\"|1#|2#}{1}{0,choice,0#\"|1#|2#} | java -cp h2-1.4.200.jar{2}. -D{3}=\"jdbc:h2:mem:mydb\" {4}", windowsWordOffset, sql3, pathSeparator, URL_PARAMETER_NAME, className));
		System.out.println(MessageFormat.format("java -cp h2-1.4.200.jar{0}. -D{1}=\"jdbc:h2:mem:mydb\" -Dseparator=\",\" -DquoteChar=\"\\\"\" -DescapeChar=\"\\\"\" {2} \"{3}\"", pathSeparator, URL_PARAMETER_NAME, className, sql4));
		System.out.println(MessageFormat.format("java -cp mssql-jdbc-9.2.0.jre11.jar{0}. -D{1}=\"jdbc:sqlserver://my-sqlserver.lan:1433;databaseName=master\" -D{2}=\"{3}\" -D{4}=\"changeit\" {5} \"{6}\"", pathSeparator, URL_PARAMETER_NAME, USER_PARAMETER_NAME, user, PASSWORD_PARAMETER_NAME, className, sql3));
		System.out.println();
		System.out.println("Parameters:");
		final Map<String, String> parametersDescriptions = new LinkedHashMap<>();
		parametersDescriptions.put(URL_PARAMETER_NAME, "JDBC connection string (mandatory)");
		parametersDescriptions.put(USER_PARAMETER_NAME, "database user (optional)");
		parametersDescriptions.put(PASSWORD_PARAMETER_NAME, "database password (optional)");
		parametersDescriptions.put(SEPARATOR_PARAMETER_NAME, "field separator (optional, default value: tab)");
		parametersDescriptions.put(QUOTE_CHAR_PARAMETER_NAME, "quoting character for String, Date and Timestamp values (optional, default value: none)");
		parametersDescriptions.put(ESCAPE_CHAR_PARAMETER_NAME, "escape character for the quoting parameter of a String, a Date or a Timestamp value (optional, default value: none)");
		parametersDescriptions.put(ROW_SEPARATOR_PARAMETER_NAME, "row separator (optional, relies on system line separator value)");
		parametersDescriptions.put(QUERY_SEPARATOR_PARAMETER_NAME, "query separator (optional, relies on system line separator value)");
		parametersDescriptions.put(PRINT_HEADER_PARAMETER_NAME, "print-out the header (optional, true or false, default: true)");
		parametersDescriptions.put(PRINT_NULL_PARAMETER_NAME, "print-out null values (optional, true or false, default: false)");
		parametersDescriptions.put(NULL_VALUE_PARAMETER_NAME, "null replacement value (optional, relies on system null representation)");
		parametersDescriptions.put(PRINT_EXCEL_DATE_PARAMETER_NAME, "print-out both dates and timestamps as Excel dates, i.e. fractional days since January 1st 1900 (optional, true or false, default: false)");
		parametersDescriptions.put(AUTO_COMMIT_PARAMETER_NAME, "enable/disable auto-commit mode (optional, true or false, relies on driver default value)");
		parametersDescriptions.put(COMMIT_PARAMETER_NAME, "perform commit after UPDATE/INSERT/DELETE (optional, true or false, relies on driver default value)");
		parametersDescriptions.put(FILE_NAME_SCHEME_PARAMETER_NAME, "file name scheme (optional, 1 => query ordinal, 2 => timestamp, 3 => universally unique identifier, print to standard output by default)");
		parametersDescriptions.put(FILE_NAME_EXTENSION_PARAMETER_NAME, "file name extension (optional, default: csv)");
		// @formatter:on
		for (final Entry<String, String> entry : parametersDescriptions.entrySet()) {
			System.out.print(MessageFormat.format("  -D{0}", entry.getKey()));
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
}
