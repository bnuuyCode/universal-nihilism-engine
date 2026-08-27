package com.bnuuy.universalnihilism.strategy;

import com.bnuuy.universalnihilism.configuration.ComputationalWorkloadDescriptor;
import com.bnuuy.universalnihilism.exception.EntropyBudgetPrematurelyExhaustedException;
import com.bnuuy.universalnihilism.observer.ComputationalProgressBroadcastingSubject;
import com.bnuuy.universalnihilism.observer.ObservableComputationalPhase;
import com.bnuuy.universalnihilism.spi.DiscardedComputationResidue;
import com.bnuuy.universalnihilism.spi.EntropyDissipationStrategy;
import com.bnuuy.universalnihilism.spi.FutilityContinuationSupervisor;

import java.time.Duration;
import java.util.Objects;

/**
 * Computes Fibonacci numbers by the tree recursion every textbook uses as its example of what not
 * to do.
 *
 * <p>Memoisation would reduce this to linear time. An iterative accumulator would reduce it to
 * linear time with constant space. A closed form exists. All three have been considered and all
 * three have been rejected, because each of them ends the computation sooner, and ending the
 * computation sooner is the failure mode this platform was built to avoid.</p>
 *
 * <p>The supervisor is consulted between whole evaluations rather than inside the recursion, so
 * this strategy overshoots its deadline by at most the cost of one evaluation. The configured
 * grace period absorbs that overshoot. See
 * {@code docs/adr/0006-deadline-driven-cooperative-cancellation.md}.</p>
 *
 * <p>Instances are immutable and safe for concurrent invocation; the recursion runs entirely on the
 * calling thread's stack.</p>
 */
public final class ExponentiallyNaiveFibonacciEntropyDissipationStrategy implements EntropyDissipationStrategy {

    private static final String CANONICAL_IDENTIFIER = "EXPONENTIALLY_NAIVE_FIBONACCI";

    private final JitCompilerDeceptionBlackhole jitCompilerDeceptionBlackhole;

    public ExponentiallyNaiveFibonacciEntropyDissipationStrategy(
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
        return "Unmemoised tree recursive Fibonacci evaluation, recomputing every subproblem from first principles";
    }

    @Override
    public DiscardedComputationResidue dissipateAvailableComputationalCapacity(
            final ComputationalWorkloadDescriptor workloadDescriptor,
            final FutilityContinuationSupervisor continuationSupervisor,
            final ComputationalProgressBroadcastingSubject progressSubject) {

        final int ordinalDepth = workloadDescriptor.obtainNaivelyRecursiveFibonacciOrdinalDepth();

        progressSubject.broadcastComputationalPhaseCommencement(
                ObservableComputationalPhase.THERMODYNAMIC_ENTROPY_DISSIPATION, CANONICAL_IDENTIFIER);

        final long commencementNanoTimestamp = System.nanoTime();
        long completedCycleCount = 0L;
        double runningMeanOfDiscardedOrdinalValues = 0.0d;

        try {
            while (continuationSupervisor.isFurtherFutilityPermitted()) {
                final long discardedOrdinalValue = computeWithMaximallyNaiveRecursion(ordinalDepth);
                jitCompilerDeceptionBlackhole.consumeAndImmediatelyDiscard(discardedOrdinalValue);

                completedCycleCount++;
                runningMeanOfDiscardedOrdinalValues +=
                        ((double) discardedOrdinalValue - runningMeanOfDiscardedOrdinalValues)
                                / (double) completedCycleCount;

                progressSubject.broadcastIncrementalFutility(
                        CANONICAL_IDENTIFIER, completedCycleCount, (double) discardedOrdinalValue);
            }
        } catch (final StackOverflowError exhaustedStackInPursuitOfNothing) {
            throw new EntropyBudgetPrematurelyExhaustedException(
                    CANONICAL_IDENTIFIER, exhaustedStackInPursuitOfNothing);
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
                CANONICAL_IDENTIFIER, completedCycleCount, elapsedNanoseconds, runningMeanOfDiscardedOrdinalValues);
    }

    /**
     * The canonical worst implementation.
     *
     * @param requestedOrdinal the Fibonacci ordinal to evaluate.
     * @return the Fibonacci number at that ordinal, at a cost exponential in the ordinal.
     */
    private static long computeWithMaximallyNaiveRecursion(final int requestedOrdinal) {
        if (requestedOrdinal < 2) {
            return requestedOrdinal;
        }
        return computeWithMaximallyNaiveRecursion(requestedOrdinal - 1)
                + computeWithMaximallyNaiveRecursion(requestedOrdinal - 2);
    }
}
