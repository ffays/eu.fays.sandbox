package eu.fays.sandbox.network;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.function.Supplier;

public class WebServerTimeEssay {
	// @formatter:off
	public static final URL URL = ((Supplier<URL>) ()-> {try {return new URL("http://fays.eu");} catch (MalformedURLException e) {throw new RuntimeException(e);}}).get();
	// @formatter:on
	
	public static void main(String[] args) throws Exception {
		final HttpURLConnection connection = (HttpURLConnection) URL.openConnection();
		connection.setRequestMethod("HEAD");
		connection.connect();
		connection.disconnect();
		System.out.println(connection.getHeaderField("Date"));
		
		final LocalDateTime timestamp = Instant.ofEpochMilli(connection.getDate()).atZone(ZoneId.systemDefault()).toLocalDateTime();
		final DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.SHORT);
		System.out.println(formatter.format(timestamp));
	}
}
