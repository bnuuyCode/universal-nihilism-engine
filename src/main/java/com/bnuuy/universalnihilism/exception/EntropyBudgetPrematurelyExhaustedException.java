package com.bnuuy.universalnihilism.exception;

import java.io.Serial;

/**
 * Raised when an entropy dissipation strategy stops wasting resources for a reason other than
 * being politely asked to.
 *
 * <p>The strategy identifier is retained so that the responsible component can be named in a
 * post-mortem that will never be written.</p>
 */
public final class EntropyBudgetPrematurelyExhaustedException extends AbstractNihilisticEngineException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String offendingStrategyIdentifier;

    public EntropyBudgetPrematurelyExhaustedException(
            final String offendingStrategyIdentifier,
            final Throwable underlyingCause) {
        super(String.format(
                        "Strategy '%s' ceased dissipating computational capacity before it was permitted to. "
                                + "Its remaining futility budget has been returned, unused, to nobody.",
                        offendingStrategyIdentifier),
                underlyingCause,
                ExistentialSeverityClassification.MILDLY_DISAPPOINTING);
        this.offendingStrategyIdentifier = offendingStrategyIdentifier;
    }

    public String obtainOffendingStrategyIdentifier() {
        return offendingStrategyIdentifier;
    }
}
