package eu.fays.sandbox.filesystem;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.util.logging.Level.SEVERE;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

public class HiddenFileEssay {

	/** Standard logger */
	private static final Logger LOGGER = Logger.getLogger(HiddenFileEssay.class.getName());

	/**
	 * Store a hidden file in the Home folder, i.e.<br>
	 * <code>$HOME/.timestamp</code>
	 * <br>
	 * VM args:
	 * <code>-Djava.util.logging.SimpleFormatter.format="%5$s%6$s%n"</code>
	 * 
	 * @param args unused
	 * @throws Exception in case of unexpected error
	 */
	public static void main(String[] args) {
		final String hiddenFilename = ".timestamp";
		final Path hiddenFile = Path.of(System.getProperty("user.home"), hiddenFilename);
		
		final boolean hiddenFileAlreadyExists = Files.exists(hiddenFile, NOFOLLOW_LINKS);
		final Properties properties = new Properties();
		properties.put("timestamp", LocalDateTime.now().toString());
		try(final FileOutputStream fos = new FileOutputStream(hiddenFile.toFile())) {
			properties.store(fos, hiddenFilename);
			LOGGER.info(hiddenFile.toString());
		} catch (final IOException e) {
			LOGGER.log(SEVERE, e.getMessage(), e);
		}

		if(!hiddenFileAlreadyExists && Files.exists(hiddenFile, NOFOLLOW_LINKS)) {
			// Hide the file
			try {
				final FileStore fileStore = Files.getFileStore(hiddenFile);
				final String fstype = fileStore.type();
				if(fileStore.supportsFileAttributeView(DosFileAttributeView.class)) {
					Files.setAttribute(hiddenFile, "dos:hidden", true, NOFOLLOW_LINKS);
				} else if ("hfs".equals(fstype) || "apfs".equals(fstype)) {
					// macOS file system types are BSD and therefore support chflags  
					final Process process = Runtime.getRuntime().exec(new String[] { "/usr/bin/chflags", "hidden", hiddenFile.toString()});
					process.getOutputStream().close();
				}
			} catch (final IOException e) {
				LOGGER.log(SEVERE, e.getMessage(), e);
			}			
		}
		
		
		{
			final List<Class<? extends FileAttributeView>> fileAttributeViewClasses = List.of(
					AclFileAttributeView.class,
					DosFileAttributeView.class,
					PosixFileAttributeView.class,
					UserDefinedFileAttributeView.class
			);

			final FileStore fileStore = FileSystems.getDefault().getFileStores().iterator().next();
			fileAttributeViewClasses.stream().forEach(vc -> LOGGER.info(MessageFormat.format("{0}: {1}", vc.getSimpleName(), fileStore.supportsFileAttributeView(vc))));
		}
	}

}
