package com.bnuuy.universalnihilism.factory;

import com.bnuuy.universalnihilism.configuration.ComputationalWorkloadDescriptor;
import com.bnuuy.universalnihilism.spi.EntropyDissipationStrategy;
import com.bnuuy.universalnihilism.spi.ExistentialAssertionSynthesizer;
import com.bnuuy.universalnihilism.strategy.DenseMatrixMultiplicationEntropyDissipationStrategy;
import com.bnuuy.universalnihilism.strategy.JitCompilerDeceptionBlackhole;

import java.util.Objects;

/**
 * Produces the evaluator family whose futility is expressed through linear algebra.
 */
public final class MatrixAlgebraQuantumEntropyEvaluatorFactory extends AbstractQuantumEntropyEvaluatorFactory {

    private final JitCompilerDeceptionBlackhole jitCompilerDeceptionBlackhole;
    private final ExistentialAssertionSynthesizer sharedExistentialAssertionSynthesizer;

    public MatrixAlgebraQuantumEntropyEvaluatorFactory(
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
        return "MatrixAlgebraQuantumEntropyEvaluatorFactory";
    }

    @Override
    protected EntropyDissipationStrategy instantiateEntropyDissipationStrategy() {
        return new DenseMatrixMultiplicationEntropyDissipationStrategy(jitCompilerDeceptionBlackhole);
    }

    @Override
    protected ExistentialAssertionSynthesizer instantiateExistentialAssertionSynthesizer() {
        return sharedExistentialAssertionSynthesizer;
    }
}
