package eu.fays.sandbox.text;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.StringTokenizer;

public class StringTokenizerEssay {
	public static void main(String[] args) {
		for(String str : new String[] { "a\tb\tc", "a\t\tb", "a\t\t", "\t\t"})
			System.out.println(MessageFormat.format("split(limit=-1): {0} split: {1} tokenizer: {2} \"{3}\"", 
				str.split("\t", -1).length,
				str.split("\t").length, 
				Collections.list(new StringTokenizer(str, "\t")).size(),
				str));
	}
}
