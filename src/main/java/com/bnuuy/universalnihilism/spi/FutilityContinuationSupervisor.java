package com.bnuuy.universalnihilism.spi;

import java.time.Duration;

/**
 * Consulted by every entropy dissipation strategy to decide whether it is still permitted to waste
 * the machine's time.
 *
 * <p>Cancellation is cooperative. The engine could interrupt its workers instead, but interruption
 * asks a thread to stop <em>eventually</em>, and this platform prefers its threads to stop for a
 * reason they were told in advance.</p>
 *
 * <p>Implementations are consulted from many threads and must be safe for concurrent use.</p>
 */
public interface FutilityContinuationSupervisor {

    /**
     * @return {@code true} while further pointless computation remains authorised.
     */
    boolean isFurtherFutilityPermitted();

    /**
     * @return how much of the dissipation window remains, or {@link Duration#ZERO} once it has
     *         elapsed. Provided for telemetry; strategies must not use it to plan ahead, because
     *         planning implies a future worth planning for.
     */
    Duration obtainRemainingFutilityAllowance();
}
