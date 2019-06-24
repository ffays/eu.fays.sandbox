package eu.fays.sandbox.clone;

import java.util.ArrayList;

public class A implements Cloneable {

	public Integer a;
	public String b;
	public ArrayList<String> l;
	
	public A() {}

	public A(int a, String b, ArrayList<String> l) {
		this.a = a;
		this.b = b;
		this.l = l;
	}

	@Override
	public String toString() {
		return "[a: " + a + ",b: " + b + ",l: " + l + "]";
	}
	
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
