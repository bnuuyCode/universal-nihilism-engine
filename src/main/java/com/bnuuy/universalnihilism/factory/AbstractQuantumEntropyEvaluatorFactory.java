package com.bnuuy.universalnihilism.factory;

import com.bnuuy.universalnihilism.configuration.ComputationalWorkloadDescriptor;
import com.bnuuy.universalnihilism.spi.EntropyDissipationStrategy;
import com.bnuuy.universalnihilism.spi.ExistentialAssertionSynthesizer;
import com.bnuuy.universalnihilism.spi.QuantumEntropyEvaluator;

import java.util.Objects;

/**
 * Abstract factory for families of collaborators that consume resources and then return a boolean.
 *
 * <p>Two products are manufactured per family: an {@link EntropyDissipationStrategy}, which does
 * the wasting, and an {@link ExistentialAssertionSynthesizer}, which does the concluding. Callers
 * never construct either directly.</p>
 *
 * <p>The public manufacturing method is {@code final} and delegates to two protected primitives, so
 * that subclasses may vary the products without varying the ceremony surrounding them. This is the
 * template method pattern, applied to a template with one step.</p>
 */
public abstract class AbstractQuantumEntropyEvaluatorFactory {

    private final ComputationalWorkloadDescriptor workloadDescriptor;

    protected AbstractQuantumEntropyEvaluatorFactory(final ComputationalWorkloadDescriptor workloadDescriptor) {
        this.workloadDescriptor = Objects.requireNonNull(workloadDescriptor, "workloadDescriptor must not be null");
    }

    /**
     * @return the descriptor this factory was configured with, for the benefit of subclasses that
     *         want it and the confusion of those that do not.
     */
    protected final ComputationalWorkloadDescriptor obtainWorkloadDescriptor() {
        return workloadDescriptor;
    }

    /**
     * @return a short name identifying this factory in logs and reports.
     */
    public abstract String obtainFactoryDisplayName();

    /**
     * @return a newly constructed dissipation strategy, or a shared immutable one. The distinction
     *         is invisible to callers and is therefore left to subclasses.
     */
    protected abstract EntropyDissipationStrategy instantiateEntropyDissipationStrategy();

    /**
     * @return the synthesizer that will interpret this family's residue.
     */
    protected abstract ExistentialAssertionSynthesizer instantiateExistentialAssertionSynthesizer();

    /**
     * Assembles the complete product family.
     *
     * @return a fully configured evaluator. Never {@code null}.
     */
    public final QuantumEntropyEvaluator manufactureFullyConfiguredQuantumEntropyEvaluator() {
        return new QuantumEntropyEvaluator(
                obtainFactoryDisplayName(),
                instantiateEntropyDissipationStrategy(),
                instantiateExistentialAssertionSynthesizer());
    }
}
