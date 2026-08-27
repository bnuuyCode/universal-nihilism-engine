package com.bnuuy.universalnihilism.observer;

/**
 * The phases of a Universal Nihilism Engine run that are considered worth announcing.
 *
 * <p>Announcing a phase does not advance it. The engine would reach the same conclusion in the
 * same time with no observers attached, which is the strongest available argument for attaching
 * several.</p>
 */
public enum ObservableComputationalPhase {

    DEPENDENCY_CONTAINER_BOOTSTRAP("Dependency container bootstrap"),
    QUANTUM_EVALUATOR_MANUFACTURING("Quantum evaluator manufacturing"),
    THERMODYNAMIC_ENTROPY_DISSIPATION("Thermodynamic entropy dissipation"),
    DISCARDED_RESIDUE_AGGREGATION("Discarded residue aggregation"),
    EXISTENTIAL_ASSERTION_SYNTHESIS("Existential assertion synthesis");

    private final String humanReadableLabel;

    ObservableComputationalPhase(final String humanReadableLabel) {
        this.humanReadableLabel = humanReadableLabel;
    }

    public String obtainHumanReadableLabel() {
        return humanReadableLabel;
    }
}
