package eu.fays.sandbox.record;

import java.util.List;

/**
 * @param firstname Firs tName
 * @param lastname Last Name
 */
public record User(String firstname, String lastname) {
	/** User: John Doe */
	public static final User JOHN_DOE = new User("John", "Doe");
	/** User: Juan Perez */
	public static final User JUAN_PEREZ = new User("Juan ", "Perez");

	/** Pre-canned placeholder users */
	public static final List<User> USERS = List.of(JOHN_DOE, JUAN_PEREZ);
}
