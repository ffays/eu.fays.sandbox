package eu.fays.sandbox.filesystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An essay to count all files under a given folder and following the symbolic links<br>
 * <br>
 * For windows users, if you get the following Exception:<br>
 *   java.nio.file.FileSystemException: "A required privilege is not held by the client"<br>
 * <br>
 * perform the following actions:<br>
 * <ol>
 * <li>Run lusrmgr.msc
 * <ol>
 * <li>Go to Groups &rArr; Administrators<br>
 * <li><b>*** BEFORE DOING THIS STEP ENSURE THAT YOU HAVE ANOTHER ADMINISTRATOR ACCOUNT THAT YOU CAN USE!!! ***</b> Remove your user name.<br>
 * </ol>
 * <li>Run secpol.msc
 * <ol>
 * <li>Go to Security Settings &rArr; Local Policies &rArr; User Rights Assignment &rArr; Create symbolic links
 * <li>Add your user name.
 * <li>Restart your session, i.e. log-off and log-on.
 * </ol>
 * </ol>
 * C.f. article: <a href="http://stackoverflow.com/questions/23217460/how-to-create-soft-symbolic-link-using-java-nio-files">How to create Soft symbolic Link using java.nio.Files</a>
 */
public class SymbolicLinkEssay {

	/**
	 * Main method
	 * @param args unused
	 * @throws Exception in case of unexpected error
	 */
	@SuppressWarnings("nls")
	public static void main(String[] args) throws Exception {
		// Setup
		final File tmpDir = new File(System.getProperty("java.io.tmpdir"));
		final File dir0 = new File(tmpDir, "dir0-" + UUID.randomUUID().toString());
		final File dir1 = new File(tmpDir, "dir1-" + UUID.randomUUID().toString());

		dir0.mkdir();
		dir1.mkdir();

		final File file0 = File.createTempFile("file0-", ".txt", dir0);
		final File file1 = File.createTempFile("file1-", ".txt", dir1);
		final Path link0 = Files.createSymbolicLink(new File(dir0, dir1.getName()).toPath(), dir1.toPath());

		dir0.deleteOnExit();
		file0.deleteOnExit();
		dir1.deleteOnExit();
		file1.deleteOnExit();
		link0.toFile().deleteOnExit();

		// Count files
		try (final Stream<Path> stream = Files.walk(dir0.toPath(), FileVisitOption.FOLLOW_LINKS)) {
			final PathMatcher filter = dir0.toPath().getFileSystem().getPathMatcher("glob:**.{txt}");
			final List<File> files = stream.filter(filter::matches).map(p -> p.toFile()).collect(Collectors.toList());
			System.out.println(files.size());
		}
	}
}
