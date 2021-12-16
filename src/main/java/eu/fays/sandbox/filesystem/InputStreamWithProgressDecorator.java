package eu.fays.sandbox.filesystem;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.DoubleConsumer;

public class InputStreamWithProgressDecorator extends InputStream {
	/** Input stream to be decorated */ private final InputStream inputStream;
	/** Amount of byte read */ private long position = 0L;
	/** File size */ private final long length;
	/** Mark */ private int mark = 0;
	/** Consumer of the progress */ private final DoubleConsumer callBack;
	
	public InputStreamWithProgressDecorator(final InputStream is, final long l, final DoubleConsumer cb) {
		inputStream = is;
		length = l;
		callBack = cb;
	}

	private void setPosition(final long fp) {
		position = fp;
		callBack.accept(getProgress());
	}

	public double getProgress() {
		return length == 0L ? 100d : ((double) position) * 100d / ((double) length);
	}

	public long getPosition() {
		return position;
	}

	@Override
	public int read(byte[] b) throws IOException {
		final int rc = inputStream.read(b);
		setPosition(position + rc);
		return rc;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		final int rc = inputStream.read(b, off, len);
		setPosition(position + rc);
		return rc;
	}

	@Override
	public byte[] readAllBytes() throws IOException {
		final byte[] result = inputStream.readAllBytes();
		setPosition(position + result.length);
		return result;
	}

	@Override
	public byte[] readNBytes(int len) throws IOException {
		final byte[] result = inputStream.readNBytes(len);
		setPosition(position + result.length);
		return result;
	}

	@Override
	public int readNBytes(byte[] b, int off, int len) throws IOException {
		final int rc = inputStream.readNBytes(b, off, len);
		setPosition(position + rc);
		return rc;
	}

	@Override
	public long skip(long n) throws IOException {
		final long rc = inputStream.skip(n);
		setPosition(position + rc);
		return rc;
	}

	@Override
	public int available() throws IOException {
		return inputStream.available();
	}

	@Override
	public void close() throws IOException {
		inputStream.close();
	}

	@Override
	public synchronized void mark(int readlimit) {
		inputStream.mark(readlimit);
		mark = readlimit;
	}

	@Override
	public synchronized void reset() throws IOException {
		inputStream.reset();
		setPosition(mark);
	}

	@Override
	public boolean markSupported() {
		return inputStream.markSupported();
	}

	@Override
	public int read() throws IOException {
		final int c = inputStream.read();
		setPosition(position + 1);
		return c;
	}
}
