package com.bnuuy.universalnihilism.observer;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * Accumulates run wide counters so that the final report has numbers in it.
 *
 * <p>The counters are accurate. They are also, individually and collectively, without
 * consequence.</p>
 */
public final class StatisticalFutilityAccumulatingProgressObserver implements ComputationalProgressObserver {

    private final LongAdder observedFutilityReportCount = new LongAdder();
    private final LongAdder commencedPhaseCount = new LongAdder();
    private final LongAdder abandonedPhaseCount = new LongAdder();
    private final AtomicLong peakReportedCycleCount = new AtomicLong(0L);

    @Override
    public String obtainObserverDisplayName() {
        return "StatisticalFutilityAccumulatingProgressObserver";
    }

    @Override
    public void onComputationalPhaseCommenced(
            final ObservableComputationalPhase commencedPhase,
            final String originatingQualifier) {
        commencedPhaseCount.increment();
    }

    @Override
    public void onIncrementalFutilityReported(
            final String strategyIdentifier,
            final long cumulativeCompletedCycleCount,
            final double mostRecentlyDiscardedValue) {
        observedFutilityReportCount.increment();
        peakReportedCycleCount.accumulateAndGet(cumulativeCompletedCycleCount, Math::max);
    }

    @Override
    public void onComputationalPhaseAbandoned(
            final ObservableComputationalPhase abandonedPhase,
            final String originatingQualifier,
            final Duration elapsedPhaseDuration) {
        abandonedPhaseCount.increment();
    }

    public long obtainObservedFutilityReportCount() {
        return observedFutilityReportCount.sum();
    }

    public long obtainCommencedPhaseCount() {
        return commencedPhaseCount.sum();
    }

    public long obtainAbandonedPhaseCount() {
        return abandonedPhaseCount.sum();
    }

    /**
     * @return the highest per worker cycle count seen during the run, which is the closest thing
     *         this platform has to a high score.
     */
    public long obtainPeakReportedCycleCount() {
        return peakReportedCycleCount.get();
    }
}
