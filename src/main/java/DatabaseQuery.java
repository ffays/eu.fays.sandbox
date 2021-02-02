import java.io.PrintStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
	 * <li>autoCommit: perform commit after UPDATE/INSERT/DELETE (optional, relies on driver default value)
	 * <li>commit: perform commit after UPDATE/INSERT/DELETE (optional, relies on driver default value)
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
		final String url = System.getProperty("url");
		final String user = System.getProperty("user");
		final String password = System.getProperty("password");
		final String autoCommit = System.getProperty("autoCommit");
		final String commit = System.getProperty("commit");
		final String fileNameScheme = System.getProperty("fileNameScheme");
		final String fileNameExtension = System.getProperty("fileNameExtension", "csv");
		final String separator;
		{
			final String separatorPropertyName = "separator";
			if (System.getProperty(separatorPropertyName) == null) {
				separator = "\t";
			} else {
				final Properties properties = new Properties();
				final StringReader stringReader = new StringReader(separatorPropertyName + "=" + System.getProperty(separatorPropertyName) + "\n");
				properties.load(stringReader);
				separator = properties.getProperty(separatorPropertyName);
			}
		}

		final String querySeparator;
		{
			final String querySeparatorPropertyName = "querySeparator";
			if (System.getProperty(querySeparatorPropertyName) == null) {
				querySeparator = System.getProperty("line.separator", "\n");
			} else {
				final Properties properties = new Properties();
				final StringReader stringReader = new StringReader(querySeparatorPropertyName + "=" + System.getProperty(querySeparatorPropertyName) + "\n");
				properties.load(stringReader);
				querySeparator = properties.getProperty(querySeparatorPropertyName);
			}
		}

		if (url == null || url.isEmpty() || user == null || password == null || args.length < 1) {
			System.out.println("Usage: java -Durl=<jdbcConnectionString> -Duser=<user> -Dpassword=<password> " + DatabaseQuery.class.getSimpleName() + " <sql>");
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

}
