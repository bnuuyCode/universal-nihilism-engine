package com.bnuuy.universalnihilism.orchestration;

import com.bnuuy.universalnihilism.api.ExistentialObservationResult;
import com.bnuuy.universalnihilism.api.UniversalNihilismEngineFacade;
import com.bnuuy.universalnihilism.exception.IrreconcilableOntologicalStateException;
import com.bnuuy.universalnihilism.factory.AbstractQuantumEntropyEvaluatorFactory;
import com.bnuuy.universalnihilism.factory.QuantumEntropyEvaluatorFactoryRegistrar;
import com.bnuuy.universalnihilism.observer.ComputationalProgressBroadcastingSubject;
import com.bnuuy.universalnihilism.observer.ObservableComputationalPhase;
import com.bnuuy.universalnihilism.spi.DiscardedComputationResidue;
import com.bnuuy.universalnihilism.spi.QuantumEntropyEvaluator;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Coordinates a complete run: manufacture the evaluators, saturate the machine, synthesize the
 * boolean.
 *
 * <p>The orchestrator performs no computation of its own. It performs no analysis of the residue.
 * It exists to hold four collaborators in a field each and to call them in a fixed order, which is
 * the highest form of enterprise architecture.</p>
 */
public final class NihilismEngineOrchestrator implements UniversalNihilismEngineFacade {

    private static final String COMPONENT_QUALIFIER = "NihilismEngineOrchestrator";

    private final QuantumEntropyEvaluatorFactoryRegistrar factoryRegistrar;
    private final ThermodynamicallyIrreversibleWorkloadExecutor workloadExecutor;
    private final ComputationalProgressBroadcastingSubject progressSubject;

    private NihilismEngineOrchestrator(final Builder builder) {
        this.factoryRegistrar = builder.factoryRegistrar;
        this.workloadExecutor = builder.workloadExecutor;
        this.progressSubject = builder.progressSubject;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String obtainImplementationDisplayName() {
        return COMPONENT_QUALIFIER;
    }

    @Override
    public ExistentialObservationResult determineWhetherSomethingIsOnScreen() {
        final List<QuantumEntropyEvaluator> manufacturedEvaluators = manufactureEveryRegisteredEvaluator();

        final long commencementNanoTimestamp = System.nanoTime();
        final List<DiscardedComputationResidue> accumulatedResidues =
                workloadExecutor.saturateEveryAvailableExecutionUnit(manufacturedEvaluators);
        final Duration totalElapsedWallClockDuration = Duration.ofNanos(System.nanoTime() - commencementNanoTimestamp);

        progressSubject.broadcastComputationalPhaseCommencement(
                ObservableComputationalPhase.DISCARDED_RESIDUE_AGGREGATION, COMPONENT_QUALIFIER);
        progressSubject.broadcastComputationalPhaseAbandonment(
                ObservableComputationalPhase.DISCARDED_RESIDUE_AGGREGATION,
                COMPONENT_QUALIFIER,
                totalElapsedWallClockDuration);

        // Every registered factory produces the same synthesizer, so the choice of which one to
        // consult is arbitrary. It is made here, once, in the open, rather than being hidden in a
        // strategy for selecting strategies.
        return manufacturedEvaluators
                .get(0)
                .existentialAssertionSynthesizer()
                .synthesizeIrrefutableExistentialAssertion(accumulatedResidues, totalElapsedWallClockDuration);
    }

    private List<QuantumEntropyEvaluator> manufactureEveryRegisteredEvaluator() {
        progressSubject.broadcastComputationalPhaseCommencement(
                ObservableComputationalPhase.QUANTUM_EVALUATOR_MANUFACTURING, COMPONENT_QUALIFIER);

        final long commencementNanoTimestamp = System.nanoTime();
        final List<AbstractQuantumEntropyEvaluatorFactory> registeredFactories =
                factoryRegistrar.obtainRegisteredFactories();

        if (registeredFactories.isEmpty()) {
            throw new IrreconcilableOntologicalStateException(
                    "No evaluator factories are registered. The engine has been asked to exhaustively verify "
                            + "a proposition using no means whatsoever, which it would rather not do quickly.");
        }

        final List<QuantumEntropyEvaluator> manufacturedEvaluators = new ArrayList<>(registeredFactories.size());
        for (final AbstractQuantumEntropyEvaluatorFactory registeredFactory : registeredFactories) {
            manufacturedEvaluators.add(registeredFactory.manufactureFullyConfiguredQuantumEntropyEvaluator());
        }

        progressSubject.broadcastComputationalPhaseAbandonment(
                ObservableComputationalPhase.QUANTUM_EVALUATOR_MANUFACTURING,
                COMPONENT_QUALIFIER,
                Duration.ofNanos(System.nanoTime() - commencementNanoTimestamp));

        return List.copyOf(manufacturedEvaluators);
    }

    /**
     * Fluent builder for {@link NihilismEngineOrchestrator}.
     *
     * <p>The orchestrator has three mandatory collaborators and no optional ones, so a constructor
     * would have sufficed. A builder was chosen anyway, on the grounds that a fourth collaborator
     * may one day exist and will then be welcomed without a source incompatible change.</p>
     */
    public static final class Builder {

        private QuantumEntropyEvaluatorFactoryRegistrar factoryRegistrar;
        private ThermodynamicallyIrreversibleWorkloadExecutor workloadExecutor;
        private ComputationalProgressBroadcastingSubject progressSubject;

        private Builder() {
        }

        public Builder withFactoryRegistrar(final QuantumEntropyEvaluatorFactoryRegistrar value) {
            this.factoryRegistrar = value;
            return this;
        }

        public Builder withWorkloadExecutor(final ThermodynamicallyIrreversibleWorkloadExecutor value) {
            this.workloadExecutor = value;
            return this;
        }

        public Builder withProgressSubject(final ComputationalProgressBroadcastingSubject value) {
            this.progressSubject = value;
            return this;
        }

        /**
         * @return a fully wired orchestrator.
         * @throws NullPointerException if any mandatory collaborator was omitted.
         */
        public NihilismEngineOrchestrator build() {
            Objects.requireNonNull(factoryRegistrar, "factoryRegistrar must be supplied before build()");
            Objects.requireNonNull(workloadExecutor, "workloadExecutor must be supplied before build()");
            Objects.requireNonNull(progressSubject, "progressSubject must be supplied before build()");
            return new NihilismEngineOrchestrator(this);
        }
    }
}
