package eu.fays.sandbox.clone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CloneEssay {

	// https://www.artima.com/intv/bloch13.html
	public static void main(String[] args) throws Exception {
		B b0 = new B(1, "one", Math.E, List.of("alpha", "beta", "gamma"));
		B b1 = b0.copy();
		System.out.println("b0: " + b0);
		System.out.println("b1: " + b1);
		b1.setNumber(2);
		b1.setText("two");
		b1.setDecimal(Math.PI);
		b1.add("delta");
		System.out.println("b0: " + b0);
		System.out.println("b1: " + b1);
	}

}
