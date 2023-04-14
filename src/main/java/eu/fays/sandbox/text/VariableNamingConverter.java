package eu.fays.sandbox.text;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

import java.util.function.BiFunction;
import java.util.function.Function;

// Cf. https://curc.readthedocs.io/en/latest/programming/coding-best-practices.html
public interface VariableNamingConverter {

	/** Generic CamelCase to snake_case */
	@SuppressWarnings("nls")
	public static BiFunction<String, Function<String, String>, String> CAMEL_TO_SNAKE_CASE_FUNCTION = (s, sf) -> stream(s.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])")).map(sf).collect(joining("_"));

	/** CamelCase to snake_case, e.g. "theQuickBrownFoxJumpsOverTheLazyDog" becomes "the_quick_brown_fox_jumps_over_the_lazy_dog" */
	public static Function<String, String> CAMEL_TO_SNAKE_LOWER_CASE_FUNCTION = s -> CAMEL_TO_SNAKE_CASE_FUNCTION.apply(s, String::toLowerCase);

	/** CamelCase to SNAKE_CASE, e.g. "theQuickBrownFoxJumpsOverTheLazyDog" becomes "THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG" */
	public static Function<String, String> CAMEL_TO_SNAKE_UPPER_CASE_FUNCTION = s -> CAMEL_TO_SNAKE_CASE_FUNCTION.apply(s, String::toUpperCase);
}
