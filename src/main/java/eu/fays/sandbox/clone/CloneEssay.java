package eu.fays.sandbox.clone;

import java.util.ArrayList;
import java.util.Arrays;

public class CloneEssay {

	// https://www.artima.com/intv/bloch13.html
	public static void main(String[] args) throws Exception {
		A a0 = new A(1, "one", new ArrayList<String>(Arrays.asList(new String[] { "alpha", "beta", "gamma" })));
		A a1 = a0.copy();
		System.out.println("a0: " + a0);
		System.out.println("a1: " + a1);
		a1.i = Integer.valueOf(2);
		a1.s = "two";
		a1.l.add("delta");
		System.out.println("a0: " + a0);
		System.out.println("a1: " + a1);
	}

}
