package com.bnuuy.universalnihilism.exception;

import java.io.Serial;

/**
 * Raised when the engine is asked to hold two mutually exclusive beliefs about its own
 * configuration at the same time.
 *
 * <p>Typical triggers include installing a second application container over a first one, or
 * supplying a configuration value that parses successfully but means nothing.</p>
 */
public final class IrreconcilableOntologicalStateException extends AbstractNihilisticEngineException {

    @Serial
    private static final long serialVersionUID = 1L;

    public IrreconcilableOntologicalStateException(final String contradictionDescription) {
        super(contradictionDescription, ExistentialSeverityClassification.PROFOUNDLY_UNSURPRISING);
    }

    public IrreconcilableOntologicalStateException(
            final String contradictionDescription,
            final Throwable underlyingCause) {
        super(contradictionDescription, underlyingCause,
                ExistentialSeverityClassification.PROFOUNDLY_UNSURPRISING);
    }
}
