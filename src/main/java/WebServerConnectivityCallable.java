

import static java.lang.System.nanoTime;
import static java.text.MessageFormat.format;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * Test connectivity of multiple web servers
 */
public class WebServerConnectivityCallable implements Callable<WebServerConnectivityCallable> {

	/** Standard logger */
	static final Logger LOGGER = Logger.getLogger(WebServerConnectivityCallable.class.getName());
	
	/** The {@code String} to parse as a URL */
	private final String spec;

	/** The Server's URL */
	private URL url;

	/** The Server's time */
	private ZonedDateTime time;
	
	/** Duration of the connection in nano seconds */
	private long duration;

	/**
	 * Args<br>
	 * <pre>https://google.com https://github.com</pre>
	 * VM Args<br>
	 * <pre>-ea -Djava.util.logging.SimpleFormatter.format="%5$s%6$s%n"</pre>
	 * @param args URLs
	 */
	public static void main(String[] args) throws Exception {
		final List<Callable<WebServerConnectivityCallable>> callableList = stream(args).map(s -> new WebServerConnectivityCallable(s)).collect(toList());
		try(final ExecutorService executorService = Executors.newFixedThreadPool(callableList.size())) {
			final WebServerConnectivityCallable result = executorService.invokeAny(callableList);
			LOGGER.info(format("{0} {1,number,0}ms {2}", result.getTime(), result.getDuration()/1_000_000L,  result.getURL()));
		}
	}
	
	/**
	 * Constructor
	 * @param spec the {@code String} to parse as a URL.
	 */
	public WebServerConnectivityCallable(final String spec) {
		this.spec = spec;
	}

	/**
	 * Connect to the URL, perform an HTTP HEAD method call, retrieve the Server's time and compute connection duration
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public WebServerConnectivityCallable call() throws Exception {
		url = new URI(spec).toURL();
		final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("HEAD");
		connection.setUseCaches(false);
		connection.setRequestProperty("Host", url.getHost());
		final long t0 = nanoTime();
		connection.connect();
		connection.disconnect();
		duration = nanoTime() - t0;
		time = Instant.ofEpochMilli(connection.getDate()).atZone(ZoneId.of("GMT"));
		return this;
	}

	/**
	 * Returns the Server's URL
	 * @return the Server's URL
	 */
	public URL getURL() {
		return url;
	}

	/**
	 * Returns the Server's time
	 * @return the Server's time
	 */
	public ZonedDateTime getTime() {
		return time;
	}
	
	/**
	 * Returns the duration of the HTTP connection in nanoseconds
	 * @return the duration of the HTTP connection in nanoseconds
	 */
	public long getDuration() {
		return duration;
	}
}
