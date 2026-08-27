package com.bnuuy.universalnihilism.observer;

import java.time.Duration;

/**
 * Receives notifications about work that is in progress and will not amount to anything.
 *
 * <p>Implementations are invoked from every worker thread concurrently and must therefore be
 * thread safe. They must also be fast: an observer that slows the engine down reduces the number
 * of cycles the engine can discard per second, which is the platform's only performance metric.</p>
 */
public interface ComputationalProgressObserver {

    /**
     * @return a stable identifier for this observer, used when reporting observer failures.
     */
    String obtainObserverDisplayName();

    /**
     * Invoked once per phase entry, per announcing component.
     *
     * @param commencedPhase      the phase being entered.
     * @param originatingQualifier the component announcing it.
     */
    void onComputationalPhaseCommenced(ObservableComputationalPhase commencedPhase, String originatingQualifier);

    /**
     * Invoked after each completed cycle of discarded work.
     *
     * @param strategyIdentifier            the strategy reporting progress.
     * @param cumulativeCompletedCycleCount how many cycles that strategy has now discarded.
     * @param mostRecentlyDiscardedValue    the value produced and immediately abandoned.
     */
    void onIncrementalFutilityReported(
            String strategyIdentifier,
            long cumulativeCompletedCycleCount,
            double mostRecentlyDiscardedValue);

    /**
     * Invoked once per phase exit. The phase is described as abandoned rather than completed
     * because nothing was completed.
     *
     * @param abandonedPhase       the phase being left.
     * @param originatingQualifier the component announcing it.
     * @param elapsedPhaseDuration how long the phase occupied the machine.
     */
    void onComputationalPhaseAbandoned(
            ObservableComputationalPhase abandonedPhase,
            String originatingQualifier,
            Duration elapsedPhaseDuration);
}
