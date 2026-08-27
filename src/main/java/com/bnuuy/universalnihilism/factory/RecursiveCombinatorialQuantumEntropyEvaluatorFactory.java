package com.bnuuy.universalnihilism.factory;

import com.bnuuy.universalnihilism.configuration.ComputationalWorkloadDescriptor;
import com.bnuuy.universalnihilism.spi.EntropyDissipationStrategy;
import com.bnuuy.universalnihilism.spi.ExistentialAssertionSynthesizer;
import com.bnuuy.universalnihilism.strategy.ExponentiallyNaiveFibonacciEntropyDissipationStrategy;
import com.bnuuy.universalnihilism.strategy.JitCompilerDeceptionBlackhole;

import java.util.Objects;

/**
 * Produces the evaluator family whose futility is expressed through unbounded recursion.
 */
public final class RecursiveCombinatorialQuantumEntropyEvaluatorFactory extends AbstractQuantumEntropyEvaluatorFactory {

    private final JitCompilerDeceptionBlackhole jitCompilerDeceptionBlackhole;
    private final ExistentialAssertionSynthesizer sharedExistentialAssertionSynthesizer;

    public RecursiveCombinatorialQuantumEntropyEvaluatorFactory(
            final ComputationalWorkloadDescriptor workloadDescriptor,
            final JitCompilerDeceptionBlackhole jitCompilerDeceptionBlackhole,
            final ExistentialAssertionSynthesizer sharedExistentialAssertionSynthesizer) {
        super(workloadDescriptor);
        this.jitCompilerDeceptionBlackhole =
                Objects.requireNonNull(jitCompilerDeceptionBlackhole, "jitCompilerDeceptionBlackhole must not be null");
        this.sharedExistentialAssertionSynthesizer = Objects.requireNonNull(
                sharedExistentialAssertionSynthesizer, "sharedExistentialAssertionSynthesizer must not be null");
    }

    @Override
    public String obtainFactoryDisplayName() {
        return "RecursiveCombinatorialQuantumEntropyEvaluatorFactory";
    }

    @Override
    protected EntropyDissipationStrategy instantiateEntropyDissipationStrategy() {
        return new ExponentiallyNaiveFibonacciEntropyDissipationStrategy(jitCompilerDeceptionBlackhole);
    }

    @Override
    protected ExistentialAssertionSynthesizer instantiateExistentialAssertionSynthesizer() {
        return sharedExistentialAssertionSynthesizer;
    }
}
