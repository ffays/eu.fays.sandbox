package eu.fays.sandbox.filesystem;

import static java.nio.file.Files.newOutputStream;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.SYNC;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;

public class AppendEssay {

	public static void main(String[] args) throws IOException {
		final Path path = Path.of("out.txt");
		try (final OutputStream out = newOutputStream(path, CREATE, WRITE, APPEND, SYNC)) {
			for (final String arg : args) {
				out.write(arg.getBytes());
				out.write(System.lineSeparator().getBytes());
				out.flush();
			}
		}
	}

}
