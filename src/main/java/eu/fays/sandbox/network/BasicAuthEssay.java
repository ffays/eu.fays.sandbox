package eu.fays.sandbox.network;

import static java.net.URLEncoder.encode;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.text.MessageFormat.format;
import static java.util.stream.Collectors.joining;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.Map;
import java.util.function.Supplier;
import java.util.logging.Logger;

/*
htpasswd -c .htpasswd sesame
cat << EOF | tee .htaccess
AuthName "Authentication required"
AuthType Basic
AuthBasicProvider file
AuthUserFile $(pwd)/.htpasswd
Require valid-user
EOF
*/

public class BasicAuthEssay {

	/** Standard Logger */
	private static final Logger LOGGER = Logger.getLogger(BasicAuthEssay.class.getName());

	public static void main(String[] args) {
		final Map<String, String> map = Map.of("p1", "alpha", "p2", "bravo", "p3", "charlie");
		final String query = map.entrySet().stream().map(e -> format("{0}={1}", e.getKey(), encode(e.getValue(), UTF_8))).collect(joining("&"));
		
		final String username = "sesame";
		final String password = "changeit";
		final String authorization = username + ":" + password;
		final String encodedAuthorization = Base64.getEncoder().encodeToString(authorization.getBytes());
		final String authorizationHeader = "Basic " + encodedAuthorization;
		
		// @formatter:off
		final URL url = ((Supplier<URL>) ()-> {try {return new URL("https://fays.eu/?" + query);} catch (MalformedURLException e) {throw new RuntimeException(e);}}).get();
		// @formatter:on

		try {
			final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(5000); // 5 seconds time-out to connect
			connection.setReadTimeout(5000); // 5 seconds time-out to read connect
			connection.setRequestProperty("Authorization", authorizationHeader);
			final int rc = connection.getResponseCode();
			if(rc != 200) {
				LOGGER.warning(format("Cannot connect to {0} ({1,number,0} - {2})!", url, rc, connection.getResponseMessage())); //$NON-NLS-1$
			} else {
				LOGGER.info(format("Connected to {0} ({1,number,0} - {2})!", url, rc, connection.getResponseMessage())); //$NON-NLS-1$
			}
		} catch (final IOException e) {
			LOGGER.warning(format("Cannot connect to {0} ({1} - {2})!", url, e.getClass().getSimpleName(), e.getMessage())); //$NON-NLS-1$
		}
    }

}
