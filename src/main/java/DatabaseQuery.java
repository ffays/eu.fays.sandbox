import java.io.IOException;
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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.UUID;

/**
 * Query a database thru a JDBC connection
 */
public class DatabaseQuery {

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
		final String urlParameterName = "url";
		final String userParameterName = "user";
		final String passwordParameterName = "password";
		final String separatorPropertyName = "separator";
		final String querySeparatorPropertyName = "querySeparator";
		final String autoCommitParameterName = "autoCommit";
		final String commitParameterName = "commit";
		final String fileNameSchemeParameterName = "fileNameScheme";
		final String fileNameExtensionParameterName = "fileNameExtension";
		final Map<String, String> parametersDescriptions = new LinkedHashMap<>();
		parametersDescriptions.put(urlParameterName, "JDBC connection string (mandatory)");
		parametersDescriptions.put(userParameterName, "database user (mandatory)");
		parametersDescriptions.put(passwordParameterName, "database password (optional)");
		parametersDescriptions.put(separatorPropertyName, "field separator (optional, default value: tab)");
		parametersDescriptions.put(querySeparatorPropertyName, "query separator (optional, relies on system line separator value)");
		parametersDescriptions.put(autoCommitParameterName, "enable/disable auto-commit mode (optional, true or false, relies on driver default value)");
		parametersDescriptions.put(commitParameterName, "perform commit after UPDATE/INSERT/DELETE (optional, true or false, relies on driver default value)");
		parametersDescriptions.put(fileNameSchemeParameterName, "file name scheme (optional, 1 => query ordinal, 2 => timestamp, 3 => universally unique identifier, print to standard output by default)");
		parametersDescriptions.put(fileNameExtensionParameterName, "file name extension (optional, default: csv)");

		final String url = System.getProperty("url");
		final String user = System.getProperty("user");
		final String password = System.getProperty("password");
		final String separator = getSystemProperty(separatorPropertyName, "\t");
		final String querySeparator = getSystemProperty(querySeparatorPropertyName, System.getProperty("line.separator", "\n"));
		final String autoCommit = System.getProperty("autoCommit");
		final String commit = System.getProperty("commit");
		final String fileNameScheme = System.getProperty("fileNameScheme");
		final String fileNameExtension = System.getProperty("fileNameExtension", "csv");

		if (url == null || url.isEmpty() || user == null) {
			System.out.println(MessageFormat.format("Usage: java -D{0}=<jdbcConnectionString> -D{1}=<{1}> -D{2}=<{2}> {3} <sql> ...", urlParameterName, userParameterName, passwordParameterName, DatabaseQuery.class.getSimpleName()));
			System.out.println();
			System.out.println("List of named parameters:");
			for (final Entry<String, String> entry : parametersDescriptions.entrySet()) {
				System.out.print(MessageFormat.format("\t-D{0}", entry.getKey()));
				final int n = 3 - ((entry.getKey().length()+2)/8);
				for(int i=0; i<n; i++) {
					System.out.print('\t');
				}
				System.out.println(entry.getValue());
			}
			return;
		}

		try (final Connection connection = DriverManager.getConnection(url, user, password)) {
			if (autoCommit != null) {
				connection.setAutoCommit(Boolean.valueOf(autoCommit));
			}
			for (int i = 0; i < args.length; i++) {
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
					final String sql = args[i];
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
						}
					}
				}
				if (filename != null) {
					System.out.println(filename);
				}
			}
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
