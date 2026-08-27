package com.bnuuy.universalnihilism.observer;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Writes progress to standard output in a format that closely resembles a real application log.
 *
 * <p>Incremental futility reports are rate limited, because emitting one line per discarded cycle
 * would make the console the bottleneck and the CPU would get a rest it has not earned.</p>
 */
public final class VerboseConsoleTelemetryEmittingProgressObserver implements ComputationalProgressObserver {

    private static final DateTimeFormatter WALL_CLOCK_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    private final long minimumEmissionIntervalNanoseconds;
    private final AtomicLong mostRecentEmissionNanoTimestamp;

    public VerboseConsoleTelemetryEmittingProgressObserver(final Duration minimumEmissionInterval) {
        this.minimumEmissionIntervalNanoseconds = Math.max(1L, minimumEmissionInterval.toNanos());
        this.mostRecentEmissionNanoTimestamp =
                new AtomicLong(System.nanoTime() - this.minimumEmissionIntervalNanoseconds);
    }

    @Override
    public String obtainObserverDisplayName() {
        return "VerboseConsoleTelemetryEmittingProgressObserver";
    }

    @Override
    public void onComputationalPhaseCommenced(
            final ObservableComputationalPhase commencedPhase,
            final String originatingQualifier) {
        emitFormattedLine("INFO ", "PHASE  BEGIN  %-34s %s",
                commencedPhase.obtainHumanReadableLabel(), originatingQualifier);
    }

    @Override
    public void onIncrementalFutilityReported(
            final String strategyIdentifier,
            final long cumulativeCompletedCycleCount,
            final double mostRecentlyDiscardedValue) {
        if (!attemptToClaimEmissionSlot()) {
            return;
        }
        emitFormattedLine("DEBUG", "CYCLE  %-38s n=%-10d discarded=%+.6e",
                strategyIdentifier, cumulativeCompletedCycleCount, mostRecentlyDiscardedValue);
    }

    @Override
    public void onComputationalPhaseAbandoned(
            final ObservableComputationalPhase abandonedPhase,
            final String originatingQualifier,
            final Duration elapsedPhaseDuration) {
        emitFormattedLine("INFO ", "PHASE  END    %-34s %s after %s",
                abandonedPhase.obtainHumanReadableLabel(), originatingQualifier, elapsedPhaseDuration);
    }

    /**
     * Rate limits emission without locking.
     *
     * @return {@code true} if the calling thread won the right to print.
     */
    private boolean attemptToClaimEmissionSlot() {
        final long currentNanoTimestamp = System.nanoTime();
        final long previousNanoTimestamp = mostRecentEmissionNanoTimestamp.get();
        if (currentNanoTimestamp - previousNanoTimestamp < minimumEmissionIntervalNanoseconds) {
            return false;
        }
        return mostRecentEmissionNanoTimestamp.compareAndSet(previousNanoTimestamp, currentNanoTimestamp);
    }

    private void emitFormattedLine(final String severityLabel, final String messageTemplate, final Object... arguments) {
        System.out.printf(
                "%s %s [%s] %s%n",
                LocalTime.now().format(WALL_CLOCK_FORMATTER),
                severityLabel,
                Thread.currentThread().getName(),
                String.format(messageTemplate, arguments));
    }
}
