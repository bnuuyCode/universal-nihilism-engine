package com.bnuuy.universalnihilism.exception;

import java.io.Serial;
import java.time.Duration;

/**
 * Raised when the engine arrives at its conclusion faster than the conclusion deserves.
 *
 * <p>The verdict produced by this system is correct regardless of how long it takes to reach. It
 * is, however, only <em>credible</em> if the machine suffered adequately on the way there. An
 * answer obtained cheaply invites the question of why the answer was computed at all, and that
 * question is out of scope.</p>
 */
public final class PrematureExistentialCertaintyException extends AbstractNihilisticEngineException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Duration observedComputationDuration;
    private final Duration minimumCredibleComputationDuration;

    public PrematureExistentialCertaintyException(
            final Duration observedComputationDuration,
            final Duration minimumCredibleComputationDuration) {
        super(String.format(
                        "Certainty was reached after only %s, which falls short of the %s required for that "
                                + "certainty to be taken seriously. The result is discarded on aesthetic grounds.",
                        observedComputationDuration, minimumCredibleComputationDuration),
                ExistentialSeverityClassification.PROFOUNDLY_UNSURPRISING);
        this.observedComputationDuration = observedComputationDuration;
        this.minimumCredibleComputationDuration = minimumCredibleComputationDuration;
    }

    public Duration obtainObservedComputationDuration() {
        return observedComputationDuration;
    }

    public Duration obtainMinimumCredibleComputationDuration() {
        return minimumCredibleComputationDuration;
    }
}
