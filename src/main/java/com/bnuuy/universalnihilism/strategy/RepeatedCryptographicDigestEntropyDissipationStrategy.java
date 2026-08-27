package com.bnuuy.universalnihilism.strategy;

import com.bnuuy.universalnihilism.configuration.ComputationalWorkloadDescriptor;
import com.bnuuy.universalnihilism.exception.EntropyBudgetPrematurelyExhaustedException;
import com.bnuuy.universalnihilism.observer.ComputationalProgressBroadcastingSubject;
import com.bnuuy.universalnihilism.observer.ObservableComputationalPhase;
import com.bnuuy.universalnihilism.spi.DiscardedComputationResidue;
import com.bnuuy.universalnihilism.spi.EntropyDissipationStrategy;
import com.bnuuy.universalnihilism.spi.FutilityContinuationSupervisor;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Objects;

/**
 * Repeatedly hashes a buffer with SHA-512, feeding each digest back in as the input to the next.
 *
 * <p>This is, structurally, a key derivation function with the key thrown away. The chain is
 * strictly sequential by construction: digest N cannot begin before digest N minus one has
 * finished, which denies the CPU any opportunity to be clever and denies the platform any
 * opportunity to finish early.</p>
 *
 * <p>Instances are immutable and safe for concurrent invocation. {@link MessageDigest} is not
 * thread safe, so a fresh instance is obtained inside the dissipation method on every call rather
 * than cached in a field.</p>
 */
public final class RepeatedCryptographicDigestEntropyDissipationStrategy implements EntropyDissipationStrategy {

    private static final String CANONICAL_IDENTIFIER = "REPEATED_CRYPTOGRAPHIC_DIGEST";

    /** Guaranteed present on every conforming Java platform, unlike meaning. */
    private static final String MANDATED_DIGEST_ALGORITHM = "SHA-512";

    private static final byte[] IMMUTABLE_SEED_TEMPLATE =
            "universal-nihilism-engine/initial-preimage".getBytes(StandardCharsets.UTF_8);

    private final JitCompilerDeceptionBlackhole jitCompilerDeceptionBlackhole;

    public RepeatedCryptographicDigestEntropyDissipationStrategy(
            final JitCompilerDeceptionBlackhole jitCompilerDeceptionBlackhole) {
        this.jitCompilerDeceptionBlackhole =
                Objects.requireNonNull(jitCompilerDeceptionBlackhole, "jitCompilerDeceptionBlackhole must not be null");
    }

    @Override
    public String obtainCanonicalStrategyIdentifier() {
        return CANONICAL_IDENTIFIER;
    }

    @Override
    public String obtainHumanReadableFutilityDescription() {
        return "Iterated SHA-512 digest chaining whose final preimage protects nothing";
    }

    @Override
    public DiscardedComputationResidue dissipateAvailableComputationalCapacity(
            final ComputationalWorkloadDescriptor workloadDescriptor,
            final FutilityContinuationSupervisor continuationSupervisor,
            final ComputationalProgressBroadcastingSubject progressSubject) {

        final MessageDigest digestEngine = instantiateMandatedDigestEngine();
        final long repetitionQuotaPerCycle = workloadDescriptor.obtainCryptographicDigestRepetitionQuotaPerCycle();

        progressSubject.broadcastComputationalPhaseCommencement(
                ObservableComputationalPhase.THERMODYNAMIC_ENTROPY_DISSIPATION, CANONICAL_IDENTIFIER);

        final long commencementNanoTimestamp = System.nanoTime();
        long completedCycleCount = 0L;
        double runningMeanOfDiscardedLeadingBytes = 0.0d;
        byte[] currentPreimage = IMMUTABLE_SEED_TEMPLATE.clone();

        try {
            while (continuationSupervisor.isFurtherFutilityPermitted()) {
                for (long repetitionIndex = 0L; repetitionIndex < repetitionQuotaPerCycle; repetitionIndex++) {
                    currentPreimage = digestEngine.digest(currentPreimage);
                }

                final byte discardedLeadingByte = currentPreimage[0];
                jitCompilerDeceptionBlackhole.consumeAndImmediatelyDiscard(discardedLeadingByte);

                completedCycleCount++;
                runningMeanOfDiscardedLeadingBytes +=
                        ((double) discardedLeadingByte - runningMeanOfDiscardedLeadingBytes)
                                / (double) completedCycleCount;

                progressSubject.broadcastIncrementalFutility(
                        CANONICAL_IDENTIFIER, completedCycleCount, discardedLeadingByte);
            }
        } catch (final RuntimeException unexpectedInterruptionOfTheInevitable) {
            throw new EntropyBudgetPrematurelyExhaustedException(
                    CANONICAL_IDENTIFIER, unexpectedInterruptionOfTheInevitable);
        }

        final long elapsedNanoseconds = System.nanoTime() - commencementNanoTimestamp;
        progressSubject.broadcastComputationalPhaseAbandonment(
                ObservableComputationalPhase.THERMODYNAMIC_ENTROPY_DISSIPATION,
                CANONICAL_IDENTIFIER,
                Duration.ofNanos(elapsedNanoseconds));

        return new DiscardedComputationResidue(
                CANONICAL_IDENTIFIER, completedCycleCount, elapsedNanoseconds, runningMeanOfDiscardedLeadingBytes);
    }

    private MessageDigest instantiateMandatedDigestEngine() {
        try {
            return MessageDigest.getInstance(MANDATED_DIGEST_ALGORITHM);
        } catch (final NoSuchAlgorithmException absentFromASpecificationThatRequiresIt) {
            throw new EntropyBudgetPrematurelyExhaustedException(
                    CANONICAL_IDENTIFIER, absentFromASpecificationThatRequiresIt);
        }
    }
}
