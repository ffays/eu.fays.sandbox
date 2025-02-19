package eu.fays.sandbox.clone;

import java.util.ArrayList;

public class A implements Cloneable {

	public Integer i;
	public String s;
	public ArrayList<String> l;

	public A() {
	}

	public A(final int i, final String s, final ArrayList<String> l) {
		this.i = i;
		this.s = s;
		this.l = l;
	}

	@Override
	public String toString() {
		return "{a:" + i + ", b:" + s + ", l:" + l + "}";
	}

	/**
	 * Deep clone
	 * @return deep clone
	 */
	@SuppressWarnings("unchecked")
	public A copy() {
		try {
			final A result = (A) clone();
			result.l = (ArrayList<String>) l.clone();
			return result;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}
}
