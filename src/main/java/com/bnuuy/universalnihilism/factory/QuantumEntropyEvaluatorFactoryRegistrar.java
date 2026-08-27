package com.bnuuy.universalnihilism.factory;

import com.bnuuy.universalnihilism.configuration.ComputationalWorkloadDescriptor;
import com.bnuuy.universalnihilism.spi.ExistentialAssertionSynthesizer;
import com.bnuuy.universalnihilism.strategy.JitCompilerDeceptionBlackhole;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Holds the ordered set of abstract factories the engine will use.
 *
 * <p>This class exists because {@code MinimalistDependencyInjectionContainer} keys its registry by
 * {@link Class}, and Java erases the type argument of
 * {@code List<AbstractQuantumEntropyEvaluatorFactory>} at compile time, leaving nothing distinctive
 * to key on. Wrapping the list in a named type restores the distinction. The alternative was a type
 * token, and a type token is a generic array of regret.</p>
 */
public final class QuantumEntropyEvaluatorFactoryRegistrar {

    private final List<AbstractQuantumEntropyEvaluatorFactory> registeredFactories;

    public QuantumEntropyEvaluatorFactoryRegistrar(
            final ComputationalWorkloadDescriptor workloadDescriptor,
            final JitCompilerDeceptionBlackhole jitCompilerDeceptionBlackhole,
            final ExistentialAssertionSynthesizer sharedExistentialAssertionSynthesizer) {
        Objects.requireNonNull(workloadDescriptor, "workloadDescriptor must not be null");
        Objects.requireNonNull(jitCompilerDeceptionBlackhole, "jitCompilerDeceptionBlackhole must not be null");
        Objects.requireNonNull(
                sharedExistentialAssertionSynthesizer, "sharedExistentialAssertionSynthesizer must not be null");

        this.registeredFactories = List.of(
                new MatrixAlgebraQuantumEntropyEvaluatorFactory(
                        workloadDescriptor, jitCompilerDeceptionBlackhole, sharedExistentialAssertionSynthesizer),
                new CryptographicDigestQuantumEntropyEvaluatorFactory(
                        workloadDescriptor, jitCompilerDeceptionBlackhole, sharedExistentialAssertionSynthesizer),
                new RecursiveCombinatorialQuantumEntropyEvaluatorFactory(
                        workloadDescriptor, jitCompilerDeceptionBlackhole, sharedExistentialAssertionSynthesizer));
    }

    /**
     * @return every registered factory, in declaration order. Unmodifiable.
     */
    public List<AbstractQuantumEntropyEvaluatorFactory> obtainRegisteredFactories() {
        return Collections.unmodifiableList(registeredFactories);
    }

    public int countRegisteredFactories() {
        return registeredFactories.size();
    }
}
