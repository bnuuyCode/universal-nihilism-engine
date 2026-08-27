package com.bnuuy.universalnihilism.spi;

import com.bnuuy.universalnihilism.api.ExistentialObservationResult;
import com.bnuuy.universalnihilism.exception.PrematureExistentialCertaintyException;

import java.time.Duration;
import java.util.List;

/**
 * Converts a pile of discarded computation residue into the single boolean the platform exists to
 * produce.
 *
 * <p>The conversion is not a calculation. No implementation is required to read the residue, and
 * the reference implementation does not. The residue is passed in so that the signature of this
 * method makes the boolean look earned.</p>
 */
public interface ExistentialAssertionSynthesizer {

    /**
     * @param accumulatedResidues         everything the workers produced and threw away.
     * @param totalElapsedWallClockDuration how long the run took, end to end.
     * @return the observation result.
     * @throws PrematureExistentialCertaintyException if the run was too short to be dignified.
     */
    ExistentialObservationResult synthesizeIrrefutableExistentialAssertion(
            List<DiscardedComputationResidue> accumulatedResidues,
            Duration totalElapsedWallClockDuration);
}
