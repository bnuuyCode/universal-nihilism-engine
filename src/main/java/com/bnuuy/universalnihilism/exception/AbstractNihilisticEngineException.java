package com.bnuuy.universalnihilism.exception;

import java.io.Serial;
import java.util.Objects;

/**
 * Root of the engine's exception taxonomy.
 *
 * <p>Every failure raised by the Universal Nihilism Engine carries an
 * {@link ExistentialSeverityClassification}, so that operators are informed not merely that
 * something went wrong, but precisely how little that fact matters.</p>
 *
 * <p>The hierarchy is deliberately unchecked. See
 * {@code docs/adr/0007-unchecked-exception-hierarchy-with-severity-classification.md}.</p>
 */
public abstract class AbstractNihilisticEngineException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final ExistentialSeverityClassification severityClassification;

    protected AbstractNihilisticEngineException(
            final String diagnosticMessage,
            final ExistentialSeverityClassification severityClassification) {
        super(diagnosticMessage);
        this.severityClassification =
                Objects.requireNonNull(severityClassification, "severityClassification must not be null");
    }

    protected AbstractNihilisticEngineException(
            final String diagnosticMessage,
            final Throwable underlyingCause,
            final ExistentialSeverityClassification severityClassification) {
        super(diagnosticMessage, underlyingCause);
        this.severityClassification =
                Objects.requireNonNull(severityClassification, "severityClassification must not be null");
    }

    public final ExistentialSeverityClassification obtainSeverityClassification() {
        return severityClassification;
    }

    public final String obtainOperatorFacingConsolationMessage() {
        return severityClassification.obtainOperatorFacingConsolationMessage();
    }
}
