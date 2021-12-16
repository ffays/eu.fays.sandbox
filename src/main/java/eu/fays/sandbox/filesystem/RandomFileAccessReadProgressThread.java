package eu.fays.sandbox.filesystem;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.Thread.UncaughtExceptionHandler;
import java.nio.channels.FileChannel;
import java.util.function.DoubleConsumer;

public class RandomFileAccessReadProgressThread extends Thread implements UncaughtExceptionHandler {
	/** Input stream */ private final RandomAccessFile randomAccessFile;
	/** File size */ private final long length;
	/** Read progress in percents */ private double progress = 0d;
	/** Exception from thread */ private Throwable exception = null;
	/** Consumer of the progress */ private final DoubleConsumer callBack;

	public RandomFileAccessReadProgressThread(final RandomAccessFile raf, final long l, final DoubleConsumer cb) {
		randomAccessFile = raf;
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
			long filePointer = -1L;
			final FileChannel channel = randomAccessFile.getChannel();
			while (!isInterrupted() && channel.isOpen() && filePointer < length) {
				filePointer = randomAccessFile.getFilePointer();
				progress = length == 0L ? 100d : ((double)filePointer) * 100d / ((double)length);
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
