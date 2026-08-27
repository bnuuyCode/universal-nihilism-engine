package com.bnuuy.universalnihilism.exception;

import java.io.Serial;

/**
 * Raised when the dependency injection container is asked for a collaborator that nobody
 * remembered to register.
 *
 * <p>The container does not attempt classpath scanning, reflective construction, or any other act
 * of optimism. If the binding is absent, the binding is absent.</p>
 */
public final class UnresolvableDependencyDespairException extends AbstractNihilisticEngineException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final transient Class<?> unsatisfiedContract;

    public UnresolvableDependencyDespairException(final Class<?> unsatisfiedContract) {
        super(String.format(
                        "No provider is registered for contract '%s'. The container has searched its registry "
                                + "thoroughly and found only itself.",
                        unsatisfiedContract == null ? "<null>" : unsatisfiedContract.getName()),
                ExistentialSeverityClassification.COSMICALLY_INSIGNIFICANT);
        this.unsatisfiedContract = unsatisfiedContract;
    }

    public Class<?> obtainUnsatisfiedContract() {
        return unsatisfiedContract;
    }
}
