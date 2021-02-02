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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.UUID;

/**
 * Query a database thru a JDBC connection
 */
@SuppressWarnings("nls")
public class DatabaseQuery {

	private final static String URL_PARAMETER_NAME = "url";
	private final static String USER_PARAMETER_NAME = "user";
	private final static String PASSWORD_PARAMETER_NAME = "password";
	private final static String SEPARATOR_PROPERTY_NAME = "separator";
	private final static String QUERY_SEPARATOR_PROPERTY_NAME = "querySeparator";
	private final static String AUTO_COMMIT_PARAMETER_NAME = "autoCommit";
	private final static String COMMIT_PARAMETER_NAME = "commit";
	private final static String FILE_NAME_SCHEME_PARAMETER_NAME = "fileNameScheme";
	private final static String FILE_NAME_EXTENSION_PARAMETER_NAME = "fileNameExtension";

	/**
	 * Usage: java -Durl=&lt;jdbcConnectionString&gt; -Duser=&lt;user&gt; -Dpassword=&lt;password&gt; DatabaseQuery &lt;sql&gt;<br>
	 * <br>
	 * System parameters:
	 * <ul>
	 * <li>url: JDBC connection string
	 * <li>user: database user
	 * <li>password: database password
	 * <li>autoCommit: enable/disable auto-commit mode (optional, true or false, relies on driver default value)
	 * <li>commit: perform commit after UPDATE/INSERT/DELETE (optional, true or false, relies on driver default value)
	 * <li>separator: field separator (optional, default value: tab)
	 * <li>querySeparator: query separator (optional, relies on system line separator value)
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
		final String separator = getSystemProperty(SEPARATOR_PROPERTY_NAME, "\t");
		final String lineSeparator = System.getProperty("line.separator", "\n");
		final String querySeparator = getSystemProperty(QUERY_SEPARATOR_PROPERTY_NAME, lineSeparator);
		final String autoCommit = System.getProperty(AUTO_COMMIT_PARAMETER_NAME);
		final String commit = System.getProperty(COMMIT_PARAMETER_NAME);
		final String fileNameScheme = System.getProperty(FILE_NAME_SCHEME_PARAMETER_NAME);
		final String fileNameExtension = System.getProperty(FILE_NAME_EXTENSION_PARAMETER_NAME, "csv");
		boolean success = true;

		final List<String> queries = Arrays.asList(args);
		try (final InputStreamReader isr = new InputStreamReader(System.in); final BufferedReader br = new BufferedReader(isr)) {
			final StringBuilder builder = new StringBuilder();
			while (br.ready()) {
				String line = br.readLine();
				if (line != null) {
					line = line.trim();
					if (!line.startsWith("--")) {
						if (builder.length() > 0) {
							builder.append(lineSeparator);
						}
						builder.append(line);
						if (line.endsWith(";")) {
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

		if (url == null || url.isEmpty() || user == null || queries.isEmpty()) {
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
							// Header
							final ResultSetMetaData metaData = rs.getMetaData();
							final int n = metaData.getColumnCount();
							for (int c = 1; c <= n; c++) {
								if (c > 1) {
									out.print(separator);
								}
								final String columnName = metaData.getColumnName(c);
								out.print(columnName);
							}
							out.println();
							while (rs.next()) {
								for (int c = 1; c <= n; c++) {
									if (c > 1) {
										out.print(separator);
									}
									final Object value = rs.getObject(c);
									if (value != null) {
										out.print(value.toString());
									}
								}
								out.println();
							}
						} catch (final SQLException e) {
							e.printStackTrace();
							success = false;
						}
					} else {
						try (final Statement statement = connection.createStatement()) {
							final int updateCount = statement.executeUpdate(sql);
							out.println(updateCount);
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
		System.out.println(MessageFormat.format("Usage: java -D{0}=<jdbcConnectionString> -D{1}=<{1}> -D{2}=<{2}> {3} <sql> ...", URL_PARAMETER_NAME, USER_PARAMETER_NAME, PASSWORD_PARAMETER_NAME,
				DatabaseQuery.class.getSimpleName()));
		System.out.println();
		System.out.println("List of named parameters:");
		final Map<String, String> parametersDescriptions = new LinkedHashMap<>();
		parametersDescriptions.put(URL_PARAMETER_NAME, "JDBC connection string (mandatory)");
		parametersDescriptions.put(USER_PARAMETER_NAME, "database user (mandatory)");
		parametersDescriptions.put(PASSWORD_PARAMETER_NAME, "database password (optional)");
		parametersDescriptions.put(SEPARATOR_PROPERTY_NAME, "field separator (optional, default value: tab)");
		parametersDescriptions.put(QUERY_SEPARATOR_PROPERTY_NAME, "query separator (optional, relies on system line separator value)");
		parametersDescriptions.put(AUTO_COMMIT_PARAMETER_NAME, "enable/disable auto-commit mode (optional, true or false, relies on driver default value)");
		parametersDescriptions.put(COMMIT_PARAMETER_NAME, "perform commit after UPDATE/INSERT/DELETE (optional, true or false, relies on driver default value)");
		parametersDescriptions.put(FILE_NAME_SCHEME_PARAMETER_NAME, "file name scheme (optional, 1 => query ordinal, 2 => timestamp, 3 => universally unique identifier, print to standard output by default)");
		parametersDescriptions.put(FILE_NAME_EXTENSION_PARAMETER_NAME, "file name extension (optional, default: csv)");
		for (final Entry<String, String> entry : parametersDescriptions.entrySet()) {
			System.out.print(MessageFormat.format("\t-D{0}", entry.getKey()));
			final int n = 3 - ((entry.getKey().length() + 2) / 8);
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
			final StringReader stringReader = new StringReader(propertyName + "=" + value + "\n");
			properties.load(stringReader);
			result = properties.getProperty(propertyName);
		}

		return result;
	}
}
