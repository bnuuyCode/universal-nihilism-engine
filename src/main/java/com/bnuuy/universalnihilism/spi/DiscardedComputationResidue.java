package com.bnuuy.universalnihilism.spi;

import java.time.Duration;
import java.util.Objects;

/**
 * What remains after a strategy has finished computing something and throwing it away.
 *
 * <p>The residue records the shape of the work rather than its output. The output was the point of
 * the work; the shape is the point of the platform.</p>
 *
 * @param originatingStrategyIdentifier         which strategy produced this residue.
 * @param completedFutilityCycleCount           how many full cycles were completed and discarded.
 * @param elapsedComputationNanoseconds         how long the strategy occupied its worker thread.
 * @param statisticallyIrrelevantAggregateValue a running mean of discarded values, retained so that
 *                                              the JIT compiler cannot prove the work was dead.
 */
public record DiscardedComputationResidue(
        String originatingStrategyIdentifier,
        long completedFutilityCycleCount,
        long elapsedComputationNanoseconds,
        double statisticallyIrrelevantAggregateValue) {

    public DiscardedComputationResidue {
        Objects.requireNonNull(originatingStrategyIdentifier, "originatingStrategyIdentifier must not be null");
        if (completedFutilityCycleCount < 0L) {
            throw new IllegalArgumentException("completedFutilityCycleCount must not be negative");
        }
        if (elapsedComputationNanoseconds < 0L) {
            throw new IllegalArgumentException("elapsedComputationNanoseconds must not be negative");
        }
    }

    /**
     * @param originatingStrategyIdentifier the strategy that achieved nothing at all.
     * @return a residue describing a cycle count of zero, which is the honest baseline.
     */
    public static DiscardedComputationResidue describingNoWorkWhatsoever(final String originatingStrategyIdentifier) {
        return new DiscardedComputationResidue(originatingStrategyIdentifier, 0L, 0L, 0.0d);
    }

    public Duration obtainElapsedComputationDuration() {
        return Duration.ofNanos(elapsedComputationNanoseconds);
    }
}
