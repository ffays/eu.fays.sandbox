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

public class BasicAuthEssay {

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

	public static void main(String[] args) throws IOException {
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

		final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty("Authorization", authorizationHeader);

		// "Authorization"
		System.out.println(url.toString());
        System.out.println(connection.getResponseCode());
        System.out.println(connection.getResponseMessage());
    }

}
