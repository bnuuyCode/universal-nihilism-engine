package com.bnuuy.universalnihilism.synthesis;

import com.bnuuy.universalnihilism.api.ExistentialObservationResult;
import com.bnuuy.universalnihilism.exception.PrematureExistentialCertaintyException;
import com.bnuuy.universalnihilism.observer.ComputationalProgressBroadcastingSubject;
import com.bnuuy.universalnihilism.observer.ObservableComputationalPhase;
import com.bnuuy.universalnihilism.spi.DiscardedComputationResidue;
import com.bnuuy.universalnihilism.spi.ExistentialAssertionSynthesizer;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

/**
 * The reference synthesizer.
 *
 * <p>Enforces the credibility floor, totals the residue, delegates the actual verdict to
 * {@link PhilosophicallyIrrefutableTautologyEvaluator}, and assembles the result object.</p>
 */
public final class DefaultExistentialAssertionSynthesizer implements ExistentialAssertionSynthesizer {

    private final PhilosophicallyIrrefutableTautologyEvaluator tautologyEvaluator;
    private final ComputationalProgressBroadcastingSubject progressSubject;
    private final Duration minimumCredibleComputationDuration;

    public DefaultExistentialAssertionSynthesizer(
            final PhilosophicallyIrrefutableTautologyEvaluator tautologyEvaluator,
            final ComputationalProgressBroadcastingSubject progressSubject,
            final Duration minimumCredibleComputationDuration) {
        this.tautologyEvaluator = Objects.requireNonNull(tautologyEvaluator, "tautologyEvaluator must not be null");
        this.progressSubject = Objects.requireNonNull(progressSubject, "progressSubject must not be null");
        this.minimumCredibleComputationDuration = Objects.requireNonNull(
                minimumCredibleComputationDuration, "minimumCredibleComputationDuration must not be null");
    }

    @Override
    public ExistentialObservationResult synthesizeIrrefutableExistentialAssertion(
            final List<DiscardedComputationResidue> accumulatedResidues,
            final Duration totalElapsedWallClockDuration) {

        Objects.requireNonNull(accumulatedResidues, "accumulatedResidues must not be null");
        Objects.requireNonNull(totalElapsedWallClockDuration, "totalElapsedWallClockDuration must not be null");

        progressSubject.broadcastComputationalPhaseCommencement(
                ObservableComputationalPhase.EXISTENTIAL_ASSERTION_SYNTHESIS,
                "DefaultExistentialAssertionSynthesizer");

        if (totalElapsedWallClockDuration.compareTo(minimumCredibleComputationDuration) < 0) {
            throw new PrematureExistentialCertaintyException(
                    totalElapsedWallClockDuration, minimumCredibleComputationDuration);
        }

        long totalDiscardedFutilityCycles = 0L;
        for (final DiscardedComputationResidue residue : accumulatedResidues) {
            totalDiscardedFutilityCycles += residue.completedFutilityCycleCount();
        }

        final boolean somethingIsOnScreen =
                tautologyEvaluator.evaluateWhetherAnythingAtAllIsPresentUponTheScreen(accumulatedResidues);

        progressSubject.broadcastComputationalPhaseAbandonment(
                ObservableComputationalPhase.EXISTENTIAL_ASSERTION_SYNTHESIS,
                "DefaultExistentialAssertionSynthesizer",
                totalElapsedWallClockDuration);

        return new ExistentialObservationResult(
                somethingIsOnScreen,
                totalDiscardedFutilityCycles,
                totalElapsedWallClockDuration,
                accumulatedResidues.size());
    }

    /**
     * @return the tautology evaluator backing this synthesizer, so that the final report can quote
     *         the reasoning verbatim.
     */
    public PhilosophicallyIrrefutableTautologyEvaluator obtainTautologyEvaluator() {
        return tautologyEvaluator;
    }
}
