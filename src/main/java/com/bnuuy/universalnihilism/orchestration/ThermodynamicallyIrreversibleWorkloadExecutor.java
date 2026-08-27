package com.bnuuy.universalnihilism.orchestration;

import com.bnuuy.universalnihilism.configuration.ComputationalWorkloadDescriptor;
import com.bnuuy.universalnihilism.exception.AbstractNihilisticEngineException;
import com.bnuuy.universalnihilism.exception.EntropyBudgetPrematurelyExhaustedException;
import com.bnuuy.universalnihilism.exception.MeaninglessComputationTimeoutException;
import com.bnuuy.universalnihilism.observer.ComputationalProgressBroadcastingSubject;
import com.bnuuy.universalnihilism.observer.ObservableComputationalPhase;
import com.bnuuy.universalnihilism.spi.DiscardedComputationResidue;
import com.bnuuy.universalnihilism.spi.QuantumEntropyEvaluator;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Saturates every available execution unit for the duration of the configured dissipation window.
 *
 * <p>The pool is sized at {@code availableProcessors() * perCoreConcurrencyMultiplicityFactor} and
 * every worker is given work immediately, so the process is expected to occupy essentially all of
 * the CPU the operating system is prepared to give it. Evaluators are assigned to workers round
 * robin, so each strategy runs on roughly the same number of threads.</p>
 *
 * <p><strong>This class is the reason the project has a container image.</strong> Running it
 * unconstrained will make the host unpleasant to use for as long as the window lasts. See the
 * README section on resource limits.</p>
 */
public final class ThermodynamicallyIrreversibleWorkloadExecutor {

    private static final String COMPONENT_QUALIFIER = "ThermodynamicallyIrreversibleWorkloadExecutor";

    private final ComputationalWorkloadDescriptor workloadDescriptor;
    private final ComputationalProgressBroadcastingSubject progressSubject;

    public ThermodynamicallyIrreversibleWorkloadExecutor(
            final ComputationalWorkloadDescriptor workloadDescriptor,
            final ComputationalProgressBroadcastingSubject progressSubject) {
        this.workloadDescriptor = Objects.requireNonNull(workloadDescriptor, "workloadDescriptor must not be null");
        this.progressSubject = Objects.requireNonNull(progressSubject, "progressSubject must not be null");
    }

    /**
     * Runs every supplied evaluator on every core until the deadline passes.
     *
     * @param manufacturedEvaluators the evaluator families to distribute across the pool.
     * @return one residue per worker that finished. Never {@code null}.
     * @throws MeaninglessComputationTimeoutException  if any worker outlives the grace period.
     * @throws EntropyBudgetPrematurelyExhaustedException if a strategy fails outright.
     */
    public List<DiscardedComputationResidue> saturateEveryAvailableExecutionUnit(
            final List<QuantumEntropyEvaluator> manufacturedEvaluators) {

        Objects.requireNonNull(manufacturedEvaluators, "manufacturedEvaluators must not be null");
        if (manufacturedEvaluators.isEmpty()) {
            return Collections.emptyList();
        }

        final int totalWorkerCount = calculateTotalWorkerCount();
        final Duration dissipationWindow = workloadDescriptor.obtainTargetEntropyDissipationWindow();
        final Duration gracePeriod = workloadDescriptor.obtainGracefulTerminationGracePeriod();

        progressSubject.broadcastComputationalPhaseCommencement(
                ObservableComputationalPhase.THERMODYNAMIC_ENTROPY_DISSIPATION,
                COMPONENT_QUALIFIER + " (" + totalWorkerCount + " workers)");

        final DeadlineDrivenFutilityContinuationSupervisor continuationSupervisor =
                DeadlineDrivenFutilityContinuationSupervisor.commencingImmediately(dissipationWindow);

        final ExecutorService workerPool =
                Executors.newFixedThreadPool(totalWorkerCount, new NihilisticWorkerThreadFactory());

        final long commencementNanoTimestamp = System.nanoTime();
        try {
            final List<Future<DiscardedComputationResidue>> completedFutures = workerPool.invokeAll(
                    buildOneTaskPerWorker(manufacturedEvaluators, totalWorkerCount, continuationSupervisor),
                    dissipationWindow.plus(gracePeriod).toNanos(),
                    TimeUnit.NANOSECONDS);

            return collectResidueFromCompletedWorkers(completedFutures, gracePeriod);
        } catch (final InterruptedException workerCoordinationInterrupted) {
            Thread.currentThread().interrupt();
            throw new MeaninglessComputationTimeoutException(
                    "The coordinating thread was interrupted while waiting for work that had no purpose to finish.",
                    workerCoordinationInterrupted);
        } finally {
            continuationSupervisor.requestImmediateAbandonment();
            workerPool.shutdownNow();
            progressSubject.broadcastComputationalPhaseAbandonment(
                    ObservableComputationalPhase.THERMODYNAMIC_ENTROPY_DISSIPATION,
                    COMPONENT_QUALIFIER,
                    Duration.ofNanos(System.nanoTime() - commencementNanoTimestamp));
        }
    }

    private int calculateTotalWorkerCount() {
        final int availableProcessorCount = Runtime.getRuntime().availableProcessors();
        final int multiplicityFactor = workloadDescriptor.obtainPerCoreConcurrencyMultiplicityFactor();
        return Math.max(1, availableProcessorCount * multiplicityFactor);
    }

    private List<Callable<DiscardedComputationResidue>> buildOneTaskPerWorker(
            final List<QuantumEntropyEvaluator> manufacturedEvaluators,
            final int totalWorkerCount,
            final DeadlineDrivenFutilityContinuationSupervisor continuationSupervisor) {

        final List<Callable<DiscardedComputationResidue>> scheduledTasks = new ArrayList<>(totalWorkerCount);
        for (int workerIndex = 0; workerIndex < totalWorkerCount; workerIndex++) {
            final QuantumEntropyEvaluator assignedEvaluator =
                    manufacturedEvaluators.get(workerIndex % manufacturedEvaluators.size());
            scheduledTasks.add(() -> assignedEvaluator
                    .entropyDissipationStrategy()
                    .dissipateAvailableComputationalCapacity(
                            workloadDescriptor, continuationSupervisor, progressSubject));
        }
        return scheduledTasks;
    }

    private List<DiscardedComputationResidue> collectResidueFromCompletedWorkers(
            final List<Future<DiscardedComputationResidue>> completedFutures,
            final Duration gracePeriod) {

        final List<DiscardedComputationResidue> collectedResidues = new ArrayList<>(completedFutures.size());
        int unresponsiveWorkerCount = 0;

        for (final Future<DiscardedComputationResidue> workerFuture : completedFutures) {
            if (workerFuture.isCancelled()) {
                unresponsiveWorkerCount++;
                continue;
            }
            try {
                collectedResidues.add(workerFuture.get());
            } catch (final InterruptedException collectionInterrupted) {
                Thread.currentThread().interrupt();
                throw new MeaninglessComputationTimeoutException(
                        "Interrupted while collecting residue that nobody was going to read.", collectionInterrupted);
            } catch (final ExecutionException workerFailure) {
                throw translateWorkerFailure(workerFailure);
            }
        }

        if (unresponsiveWorkerCount > 0) {
            throw new MeaninglessComputationTimeoutException(unresponsiveWorkerCount, gracePeriod);
        }
        return Collections.unmodifiableList(collectedResidues);
    }

    private static RuntimeException translateWorkerFailure(final ExecutionException workerFailure) {
        final Throwable underlyingCause = workerFailure.getCause();
        if (underlyingCause instanceof AbstractNihilisticEngineException alreadyClassified) {
            return alreadyClassified;
        }
        return new EntropyBudgetPrematurelyExhaustedException(
                "<unattributed worker>", underlyingCause == null ? workerFailure : underlyingCause);
    }

    /**
     * Names worker threads so that a thread dump taken during a run is at least legible.
     *
     * <p>Threads are daemons: if the coordinating thread somehow escapes without them, the JVM is
     * not obliged to wait for their conclusions.</p>
     */
    private static final class NihilisticWorkerThreadFactory implements ThreadFactory {

        private final AtomicInteger sequentialWorkerOrdinal = new AtomicInteger(0);

        @Override
        public Thread newThread(final Runnable assignedTask) {
            final Thread worker = new Thread(assignedTask, "nihilism-worker-" + sequentialWorkerOrdinal.getAndIncrement());
            worker.setDaemon(true);
            worker.setPriority(Thread.NORM_PRIORITY);
            return worker;
        }
    }
}
