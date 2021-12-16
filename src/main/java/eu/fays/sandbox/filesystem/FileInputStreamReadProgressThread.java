package eu.fays.sandbox.filesystem;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.nio.channels.FileChannel;
import java.util.function.DoubleConsumer;

public class FileInputStreamReadProgressThread extends Thread implements UncaughtExceptionHandler {
	/** Input stream */ private final FileInputStream fileInputStream;
	/** File size */ private final long length;
	/** Read progress in percents */ private double progress = 0d;
	/** Exception from thread */ private Throwable exception = null;
	/** Consumer of the progress */ private final DoubleConsumer callBack;

	public FileInputStreamReadProgressThread(final FileInputStream fis, final long l, final DoubleConsumer cb) {
		fileInputStream = fis;
		length = l;
		callBack = cb;
		setUncaughtExceptionHandler(this);
		setName(getClass().getSimpleName());
	}

	public double getProgress() { return progress; }
	public Throwable getException() { return exception; }
	@Override public void uncaughtException(final Thread t, final Throwable e) { exception = e; }
	
	@Override
	public void run() {
		try {
			long position = -1L;
			final FileChannel channel = fileInputStream.getChannel();
			while (!isInterrupted() && channel.isOpen() && position < length) {
				position = channel.position();
				progress = length == 0L ? 100d : ((double)position) * 100d / ((double)length);
				callBack.accept(progress);
				sleep(100L);
			} 
		} catch (final IOException e) {
			exception = e;
		} catch (final InterruptedException e) {
			// Do nothing
		}
	}
}
