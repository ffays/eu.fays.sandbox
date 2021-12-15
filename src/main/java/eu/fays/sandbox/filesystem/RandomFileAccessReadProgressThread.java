package eu.fays.sandbox.filesystem;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.Thread.UncaughtExceptionHandler;
import java.nio.channels.FileChannel;

class RandomFileAccessReadProgressThread extends Thread implements UncaughtExceptionHandler {
	/** Input stream */ private final RandomAccessFile randomAccessFile;
	/** File size */ private final long length;
	/** Read progress in percents */ private double progress = 0d;
	/** Exception from thread */ private Throwable exception = null;

	RandomFileAccessReadProgressThread(final RandomAccessFile raf, final long l) {
		randomAccessFile = raf;
		length = l;
		setUncaughtExceptionHandler(this);
		setName(getClass().getSimpleName());
	}

	double getProgress() { return progress; }
	Throwable getException() { return exception; }
	@Override public void uncaughtException(final Thread t, final Throwable e) { exception = e; }
	
	@Override
	public void run() {
		try {
			long filePointer = -1, previous = -1L;
			final FileChannel channel = randomAccessFile.getChannel();
			while (!isInterrupted() && channel.isOpen() && filePointer < length) {
				filePointer = randomAccessFile.getFilePointer();
				progress = ((double)filePointer) * 100d / ((double)length);
				/* business logic here */ if(previous != ((long)progress)) System.out.println((previous = (long) progress) + "%");
				sleep(100L);
			} 
		} catch (final IOException e) {
			exception = e;
		} catch (final InterruptedException e) {
			// Do nothing
		}
	}
}
