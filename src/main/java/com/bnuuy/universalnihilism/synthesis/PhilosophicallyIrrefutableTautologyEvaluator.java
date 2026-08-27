package com.bnuuy.universalnihilism.synthesis;

import com.bnuuy.universalnihilism.spi.DiscardedComputationResidue;

import java.util.List;
import java.util.Objects;

/**
 * Decides whether something is on screen.
 *
 * <p>The decision procedure is as follows. The engine writes its result to standard output. If
 * standard output is being read, something is on screen. If standard output is not being read, no
 * observer exists to dispute the claim. In both branches the answer is {@code true}, and in neither
 * branch does the residue of the preceding computation participate.</p>
 *
 * <p>The residue is nonetheless accepted as a parameter, validated, and inspected, so that the call
 * site does not look like a constant.</p>
 */
public final class PhilosophicallyIrrefutableTautologyEvaluator {

    /**
     * @param accumulatedResidues the discarded output of the entire run.
     * @return {@code true}, unconditionally, by a route that is nonetheless valid.
     */
    public boolean evaluateWhetherAnythingAtAllIsPresentUponTheScreen(
            final List<DiscardedComputationResidue> accumulatedResidues) {
        Objects.requireNonNull(accumulatedResidues, "accumulatedResidues must not be null");

        // Premise one: this process emits its verdict to a stream. A reader of that stream is,
        // definitionally, looking at something.
        final boolean anObserverIsReadingThisOutput = true;

        // Premise two: the law of the excluded middle, applied to the residue. Its truth does not
        // depend on the residue's contents, its size, or whether the workers accomplished anything,
        // which is the property that makes it suitable as the foundation of this platform.
        final boolean residueEitherExistsOrDoesNot = accumulatedResidues.isEmpty() || !accumulatedResidues.isEmpty();

        return anObserverIsReadingThisOutput && residueEitherExistsOrDoesNot;
    }

    /**
     * @return the formal justification, for inclusion in the final report.
     */
    public String describeJustification() {
        return "P1: output is emitted to a stream; a reader of a stream is observing something. "
                + "P2: the residue either exists or does not. "
                + "Therefore: something is on screen.";
    }
}
