package com.bnuuy.universalnihilism.strategy;

import com.bnuuy.universalnihilism.configuration.ComputationalWorkloadDescriptor;
import com.bnuuy.universalnihilism.exception.EntropyBudgetPrematurelyExhaustedException;
import com.bnuuy.universalnihilism.observer.ComputationalProgressBroadcastingSubject;
import com.bnuuy.universalnihilism.observer.ObservableComputationalPhase;
import com.bnuuy.universalnihilism.spi.DiscardedComputationResidue;
import com.bnuuy.universalnihilism.spi.EntropyDissipationStrategy;
import com.bnuuy.universalnihilism.spi.FutilityContinuationSupervisor;

import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;
import java.util.SplittableRandom;

/**
 * Repeatedly multiplies two dense square matrices of doubles using the textbook cubic algorithm.
 *
 * <p>The algorithm is deliberately the naive one. Blocked and cache oblivious variants exist, are
 * well documented, and would produce the same numbers faster. Producing the same discarded numbers
 * faster is not an improvement this platform recognises.</p>
 *
 * <p>The operands are generated from a fixed seed, so every worker on every machine multiplies the
 * same two matrices and arrives at the same answer, independently, in parallel, at great expense.</p>
 *
 * <p>Instances are immutable and safe for concurrent invocation. All buffers are allocated inside
 * the dissipation method; at the default cardinality of 512 that is approximately six megabytes of
 * heap per worker thread.</p>
 */
public final class DenseMatrixMultiplicationEntropyDissipationStrategy implements EntropyDissipationStrategy {

    private static final String CANONICAL_IDENTIFIER = "DENSE_MATRIX_MULTIPLICATION";

    /** Chosen so that runs are reproducible. Reproducing them serves no purpose. */
    private static final long DETERMINISTIC_OPERAND_SEED = 0x4E49484C495354L;

    private final JitCompilerDeceptionBlackhole jitCompilerDeceptionBlackhole;

    public DenseMatrixMultiplicationEntropyDissipationStrategy(
            final JitCompilerDeceptionBlackhole jitCompilerDeceptionBlackhole) {
        this.jitCompilerDeceptionBlackhole =
                Objects.requireNonNull(jitCompilerDeceptionBlackhole, "jitCompilerDeceptionBlackhole must not be null");
    }

    @Override
    public String obtainCanonicalStrategyIdentifier() {
        return CANONICAL_IDENTIFIER;
    }

    @Override
    public String obtainHumanReadableFutilityDescription() {
        return "Naive O(n^3) multiplication of two dense square matrices whose product is never inspected";
    }

    @Override
    public DiscardedComputationResidue dissipateAvailableComputationalCapacity(
            final ComputationalWorkloadDescriptor workloadDescriptor,
            final FutilityContinuationSupervisor continuationSupervisor,
            final ComputationalProgressBroadcastingSubject progressSubject) {

        final int dimensionalCardinality = workloadDescriptor.obtainDenseMatrixDimensionalCardinality();
        progressSubject.broadcastComputationalPhaseCommencement(
                ObservableComputationalPhase.THERMODYNAMIC_ENTROPY_DISSIPATION, CANONICAL_IDENTIFIER);

        final long commencementNanoTimestamp = System.nanoTime();
        long completedCycleCount = 0L;
        double runningMeanOfDiscardedTraces = 0.0d;

        try {
            final SplittableRandom deterministicOperandGenerator = new SplittableRandom(DETERMINISTIC_OPERAND_SEED);
            final double[] leftOperandMatrix =
                    allocateAndPopulateOperand(dimensionalCardinality, deterministicOperandGenerator);
            final double[] rightOperandMatrix =
                    allocateAndPopulateOperand(dimensionalCardinality, deterministicOperandGenerator);
            final double[] productAccumulationMatrix = new double[dimensionalCardinality * dimensionalCardinality];

            while (continuationSupervisor.isFurtherFutilityPermitted()) {
                multiplyWithMaximalDisregardForCacheLocality(
                        leftOperandMatrix,
                        rightOperandMatrix,
                        productAccumulationMatrix,
                        dimensionalCardinality,
                        continuationSupervisor);

                final double discardedTrace =
                        computeDiagonalTrace(productAccumulationMatrix, dimensionalCardinality);
                jitCompilerDeceptionBlackhole.consumeAndImmediatelyDiscard(discardedTrace);

                completedCycleCount++;
                runningMeanOfDiscardedTraces +=
                        (discardedTrace - runningMeanOfDiscardedTraces) / (double) completedCycleCount;

                progressSubject.broadcastIncrementalFutility(
                        CANONICAL_IDENTIFIER, completedCycleCount, discardedTrace);
            }
        } catch (final OutOfMemoryError insufficientHeapForNothingness) {
            throw new EntropyBudgetPrematurelyExhaustedException(CANONICAL_IDENTIFIER, insufficientHeapForNothingness);
        } catch (final RuntimeException unexpectedInterruptionOfTheInevitable) {
            throw new EntropyBudgetPrematurelyExhaustedException(
                    CANONICAL_IDENTIFIER, unexpectedInterruptionOfTheInevitable);
        }

        final long elapsedNanoseconds = System.nanoTime() - commencementNanoTimestamp;
        progressSubject.broadcastComputationalPhaseAbandonment(
                ObservableComputationalPhase.THERMODYNAMIC_ENTROPY_DISSIPATION,
                CANONICAL_IDENTIFIER,
                Duration.ofNanos(elapsedNanoseconds));

        return new DiscardedComputationResidue(
                CANONICAL_IDENTIFIER, completedCycleCount, elapsedNanoseconds, runningMeanOfDiscardedTraces);
    }

    private static double[] allocateAndPopulateOperand(
            final int dimensionalCardinality,
            final SplittableRandom deterministicOperandGenerator) {
        final double[] flattenedOperand = new double[dimensionalCardinality * dimensionalCardinality];
        for (int elementIndex = 0; elementIndex < flattenedOperand.length; elementIndex++) {
            flattenedOperand[elementIndex] = deterministicOperandGenerator.nextDouble(-1.0d, 1.0d);
        }
        return flattenedOperand;
    }

    /**
     * Performs one complete matrix product into {@code productAccumulationMatrix}.
     *
     * <p>The supervisor is consulted once per output row rather than once per element. Consulting
     * it per element would make the clock read dominate the arithmetic, and the arithmetic is the
     * product being sold here.</p>
     */
    private static void multiplyWithMaximalDisregardForCacheLocality(
            final double[] leftOperandMatrix,
            final double[] rightOperandMatrix,
            final double[] productAccumulationMatrix,
            final int dimensionalCardinality,
            final FutilityContinuationSupervisor continuationSupervisor) {

        Arrays.fill(productAccumulationMatrix, 0.0d);

        for (int rowIndex = 0; rowIndex < dimensionalCardinality; rowIndex++) {
            if (!continuationSupervisor.isFurtherFutilityPermitted()) {
                return;
            }
            final int rowOffset = rowIndex * dimensionalCardinality;
            for (int innerIndex = 0; innerIndex < dimensionalCardinality; innerIndex++) {
                final double leftElement = leftOperandMatrix[rowOffset + innerIndex];
                final int rightRowOffset = innerIndex * dimensionalCardinality;
                for (int columnIndex = 0; columnIndex < dimensionalCardinality; columnIndex++) {
                    productAccumulationMatrix[rowOffset + columnIndex] +=
                            leftElement * rightOperandMatrix[rightRowOffset + columnIndex];
                }
            }
        }
    }

    private static double computeDiagonalTrace(
            final double[] productAccumulationMatrix,
            final int dimensionalCardinality) {
        double accumulatedTrace = 0.0d;
        for (int diagonalIndex = 0; diagonalIndex < dimensionalCardinality; diagonalIndex++) {
            accumulatedTrace += productAccumulationMatrix[diagonalIndex * dimensionalCardinality + diagonalIndex];
        }
        return accumulatedTrace;
    }
}
