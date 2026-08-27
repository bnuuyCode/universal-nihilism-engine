package com.bnuuy.universalnihilism.api;

import java.time.Duration;
import java.util.Objects;

/**
 * The sole value object returned by the Universal Nihilism Engine.
 *
 * <p>Of the four components declared here, exactly one is load bearing:
 * {@link #isSomethingOnScreen()}. The remaining three describe the cost of obtaining it and are
 * retained for audit purposes, where "audit" means "so that the number can be shown to someone".</p>
 *
 * @param somethingOnScreen               the verdict. Always {@code true}. See
 *                                        {@code docs/adr/0002-single-boolean-return-contract.md}.
 * @param totalDiscardedFutilityCycles    the number of computation cycles whose results were
 *                                        deliberately thrown away.
 * @param totalElapsedWallClockDuration   the wall clock time surrendered to reach the verdict.
 * @param participatingEvaluatorCount     how many quantum entropy evaluators contributed nothing.
 */
public record ExistentialObservationResult(
        boolean somethingOnScreen,
        long totalDiscardedFutilityCycles,
        Duration totalElapsedWallClockDuration,
        int participatingEvaluatorCount) {

    public ExistentialObservationResult {
        Objects.requireNonNull(totalElapsedWallClockDuration, "totalElapsedWallClockDuration must not be null");
        if (totalDiscardedFutilityCycles < 0L) {
            throw new IllegalArgumentException("totalDiscardedFutilityCycles must not be negative");
        }
        if (participatingEvaluatorCount < 0) {
            throw new IllegalArgumentException("participatingEvaluatorCount must not be negative");
        }
    }

    /**
     * The flagship accessor of this platform.
     *
     * @return whether something is, at the moment of observation, on screen.
     */
    public boolean isSomethingOnScreen() {
        return somethingOnScreen;
    }

    /**
     * @return the average number of discarded cycles per participating evaluator, or zero when no
     *         evaluator participated. This figure has no interpretation.
     */
    public double calculateMeanFutilityCyclesPerEvaluator() {
        return participatingEvaluatorCount == 0
                ? 0.0d
                : (double) totalDiscardedFutilityCycles / (double) participatingEvaluatorCount;
    }
}
