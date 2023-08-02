package eu.fays.sandbox.collections;

import static java.util.Collections.unmodifiableList;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class UnmodifiableListTest {

	@Test
	public void unmodifiableListTest() {
		final List<String> arrayList = new ArrayList<>();
		final List<String> linkedList = new LinkedList<>();
		final List<String> unmodifiableArrayList = unmodifiableList(arrayList);
		final List<String> unmodifiableLinkedList = unmodifiableList(linkedList);
		final List<String> unmodifiableArrayList2 = unmodifiableList(unmodifiableArrayList);
		final List<String> unmodifiableLinkedList2 = unmodifiableList(unmodifiableLinkedList);

		assertFalse(arrayList == unmodifiableList(arrayList));
		assertFalse(linkedList == unmodifiableList(linkedList));
//		assertTrue(unmodifiableArrayList == unmodifiableArrayList2); // only true with Java 17
//		assertTrue(unmodifiableLinkedList == unmodifiableLinkedList2); // only true with Java 17
	}

}
