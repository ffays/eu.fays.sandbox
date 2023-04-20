package eu.fays.sandbox.logging;

/**
 * Side-effect "short-name" functional interface
 */
@FunctionalInterface
public interface S {
	
	/**
	 * Apply the side-effect and return true
	 * @param sideEffect side-effect
	 * @return true always
	 */
	public static boolean t(final S sideEffect) {
		sideEffect.sideEffect();
		return true;
	}

	/**
	 * Side-effect
	 */
	void sideEffect();
}
