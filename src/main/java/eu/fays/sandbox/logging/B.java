package eu.fays.sandbox.logging;

import java.util.function.BooleanSupplier;

/**
 * Boolean Supplier "short-name" functional interface
 * @see BooleanSupplier
 */
@FunctionalInterface
public interface B {
    /**
     * Cf. {@link BooleanSupplier#getAsBoolean()}
     * @return either true or false
     */
    boolean b();
}
