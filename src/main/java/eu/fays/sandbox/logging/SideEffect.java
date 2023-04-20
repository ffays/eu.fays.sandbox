package eu.fays.sandbox.logging;

/**
 * Side-effect functional interface
 */
@FunctionalInterface
public interface SideEffect {

	/**
	 * Apply the side-effect and return true
	 * @param sideEffect side-effect
	 * @return true always
	 */
	public static boolean t(final SideEffect sideEffect) {
		sideEffect.sideEffect();
		return true;
	}

	/**
	 * Side-effect
	 */
	void sideEffect();
}
