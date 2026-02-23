package eu.fays.sandbox;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@TestMethodOrder(OrderAnnotation.class)
public class BeforeEachTest {

	static Integer[] oneTwoThreeData() {
		return new Integer[] { 1, 2, 3 };
	}

	static Integer[] fourFiveSixData() {
		return new Integer[] { 4, 5, 6 };
	}

	static final AtomicInteger ai = new AtomicInteger();

	@BeforeEach
	public void beforeEach() {
		ai.incrementAndGet();
	}

	@ParameterizedTest
	@MethodSource("oneTwoThreeData")
	@Order(1)
	public void oneTwoThreeTest(final int i) {
		assertEquals(i, ai.get());
	}

	@ParameterizedTest
	@MethodSource("fourFiveSixData")
	@Order(2)
	public void fourFiveSixTest(final int i) {
		assertEquals(i, ai.get());
	}

	@Test
	@Order(3)
	public void atomicIntegerFinalValueTest() {
		assertEquals(7, ai.get());
	}

}
