package com.bnuuy.universalnihilism.observer;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * The observable subject through which every component reports progress it has no reason to be
 * proud of.
 *
 * <p>Registration is unbounded and idempotent only in the sense that registering the same observer
 * twice will notify it twice. Broadcasts are performed on the calling thread, so an observer that
 * blocks will block a worker.</p>
 *
 * <p>Observer failures are swallowed. An exception thrown while reporting the progress of
 * pointless work is itself pointless work, and the engine declines to escalate it.</p>
 */
public final class ComputationalProgressBroadcastingSubject {

    private final List<ComputationalProgressObserver> registeredObservers = new CopyOnWriteArrayList<>();

    public void registerProgressObserver(final ComputationalProgressObserver observerToRegister) {
        registeredObservers.add(Objects.requireNonNull(observerToRegister, "observerToRegister must not be null"));
    }

    public void deregisterProgressObserver(final ComputationalProgressObserver observerToRemove) {
        registeredObservers.remove(observerToRemove);
    }

    public int countRegisteredObservers() {
        return registeredObservers.size();
    }

    public void broadcastComputationalPhaseCommencement(
            final ObservableComputationalPhase commencedPhase,
            final String originatingQualifier) {
        dispatchToEveryObserverQuietly(
                observer -> observer.onComputationalPhaseCommenced(commencedPhase, originatingQualifier));
    }

    public void broadcastIncrementalFutility(
            final String strategyIdentifier,
            final long cumulativeCompletedCycleCount,
            final double mostRecentlyDiscardedValue) {
        dispatchToEveryObserverQuietly(observer -> observer.onIncrementalFutilityReported(
                strategyIdentifier, cumulativeCompletedCycleCount, mostRecentlyDiscardedValue));
    }

    public void broadcastComputationalPhaseAbandonment(
            final ObservableComputationalPhase abandonedPhase,
            final String originatingQualifier,
            final Duration elapsedPhaseDuration) {
        dispatchToEveryObserverQuietly(observer -> observer.onComputationalPhaseAbandoned(
                abandonedPhase, originatingQualifier, elapsedPhaseDuration));
    }

    private void dispatchToEveryObserverQuietly(
            final Consumer<ComputationalProgressObserver> notificationAction) {
        for (final ComputationalProgressObserver observer : registeredObservers) {
            try {
                notificationAction.accept(observer);
            } catch (final RuntimeException observerFailure) {
                System.err.printf(
                        "[UNE] Observer '%s' failed while being told about work that did not matter: %s%n",
                        observer.obtainObserverDisplayName(), observerFailure);
            }
        }
    }
}
