package com.bnuuy.universalnihilism.spi;

import com.bnuuy.universalnihilism.configuration.ComputationalWorkloadDescriptor;
import com.bnuuy.universalnihilism.exception.EntropyBudgetPrematurelyExhaustedException;
import com.bnuuy.universalnihilism.observer.ComputationalProgressBroadcastingSubject;

/**
 * A pluggable method of converting electrical power into heat via the arithmetic logic unit.
 *
 * <p><strong>Implementation contract.</strong> Implementations must be immutable and must confine
 * every piece of mutable state to the body of
 * {@link #dissipateAvailableComputationalCapacity}. A single strategy instance is invoked
 * concurrently by every worker thread in the pool, and a shared buffer would produce a data race
 * whose results are wrong. The results are discarded either way, but they will be discarded
 * <em>correctly</em>.</p>
 */
public interface EntropyDissipationStrategy {

    /**
     * @return a stable, machine readable identifier, unique across strategies.
     */
    String obtainCanonicalStrategyIdentifier();

    /**
     * @return a one line description of exactly which arithmetic is being wasted.
     */
    String obtainHumanReadableFutilityDescription();

    /**
     * Occupies the calling thread with expensive computation until the supervisor withdraws its
     * permission.
     *
     * @param workloadDescriptor the sizing parameters for this run.
     * @param continuationSupervisor the authority that decides when the futility ends.
     * @param progressSubject the channel on which progress is announced to nobody in particular.
     * @return the residue of the work performed. Never {@code null}.
     * @throws EntropyBudgetPrematurelyExhaustedException if the strategy stops for any reason other
     *                                                    than being asked to.
     */
    DiscardedComputationResidue dissipateAvailableComputationalCapacity(
            ComputationalWorkloadDescriptor workloadDescriptor,
            FutilityContinuationSupervisor continuationSupervisor,
            ComputationalProgressBroadcastingSubject progressSubject);
}
