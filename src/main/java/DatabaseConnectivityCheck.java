import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Usage: java -Durl=&lt;jdbcConnectionString&gt; -Duser=&lt;user&gt; -Dpassword=&lt;password&gt; DatabaseConnectivityCheck
 */
public class DatabaseConnectivityCheck {
	@SuppressWarnings("nls")
	public static void main(String[] args) throws Exception {
		final String url = System.getProperty("url");
		final String user = System.getProperty("user");
		final String password = System.getProperty("password");
		try (final Connection connection = DriverManager.getConnection(url, user, password)) {
			try (final Statement statement = connection.createStatement(); final ResultSet rs = statement.executeQuery("SELECT 1")) {
				if(rs.next()) {
					System.out.println("SUCCESS");
				}
			}
		}
	}
}
