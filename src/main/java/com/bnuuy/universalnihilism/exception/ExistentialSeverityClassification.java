package com.bnuuy.universalnihilism.exception;

/**
 * Enumerates the severity tiers available to failures raised by the Universal Nihilism Engine.
 *
 * <p>The tiers are ordered by increasing indifference. No tier implies that corrective action is
 * possible, available, or worthwhile; the classification exists solely so that downstream
 * monitoring systems have a field to render in a dashboard nobody reads.</p>
 */
public enum ExistentialSeverityClassification {

    /** The failure occurred. The universe did not notice. */
    COSMICALLY_INSIGNIFICANT("No action is required, or indeed possible."),

    /** The failure was foreseeable, and was foreseen, and happened anyway. */
    MILDLY_DISAPPOINTING("This was always going to happen. Please proceed as though it had not."),

    /** The failure confirms a hypothesis nobody wanted confirmed. */
    PROFOUNDLY_UNSURPRISING("The system behaved exactly as designed. That is the problem."),

    /** The failure is structural and will recur for as long as the process is permitted to exist. */
    TERMINALLY_INEVITABLE("Restarting the process will reproduce this outcome with high fidelity.");

    private final String operatorFacingConsolationMessage;

    ExistentialSeverityClassification(final String operatorFacingConsolationMessage) {
        this.operatorFacingConsolationMessage = operatorFacingConsolationMessage;
    }

    /**
     * @return a message intended for a human operator who cannot change the outcome.
     */
    public String obtainOperatorFacingConsolationMessage() {
        return operatorFacingConsolationMessage;
    }
}
