package com.bnuuy.universalnihilism.exception;

import java.io.Serial;
import java.time.Duration;

/**
 * Raised when one or more worker threads decline to stop performing work that was never going to
 * matter, even after the configured grace period has elapsed.
 *
 * <p>This is not a resource leak. It is a difference of opinion about when to give up.</p>
 */
public final class MeaninglessComputationTimeoutException extends AbstractNihilisticEngineException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final int unresponsiveWorkerCount;
    private final Duration observedGracePeriod;

    public MeaninglessComputationTimeoutException(
            final int unresponsiveWorkerCount,
            final Duration observedGracePeriod) {
        super(String.format(
                        "%d worker thread(s) continued to compute nothing of consequence after the %s grace "
                                + "period expired. Their commitment is noted and will not be rewarded.",
                        unresponsiveWorkerCount, observedGracePeriod),
                ExistentialSeverityClassification.TERMINALLY_INEVITABLE);
        this.unresponsiveWorkerCount = unresponsiveWorkerCount;
        this.observedGracePeriod = observedGracePeriod;
    }

    public MeaninglessComputationTimeoutException(
            final String diagnosticMessage,
            final Throwable underlyingCause) {
        super(diagnosticMessage, underlyingCause, ExistentialSeverityClassification.TERMINALLY_INEVITABLE);
        this.unresponsiveWorkerCount = 0;
        this.observedGracePeriod = Duration.ZERO;
    }

    public int obtainUnresponsiveWorkerCount() {
        return unresponsiveWorkerCount;
    }

    public Duration obtainObservedGracePeriod() {
        return observedGracePeriod;
    }
}
