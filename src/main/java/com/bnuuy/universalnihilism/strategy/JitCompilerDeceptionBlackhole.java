package com.bnuuy.universalnihilism.strategy;

/**
 * Absorbs computed values so that the HotSpot JIT compiler cannot prove them unused.
 *
 * <p>This class is the single most important component in the platform, and the only one that is
 * load bearing in the engineering sense rather than the rhetorical one. Every strategy in this
 * codebase computes a result and discards it. A sufficiently aggressive optimising compiler will
 * observe that the result is never read, classify the entire computation as dead code, and delete
 * it. The engine would then complete its three minute run in roughly four milliseconds and report
 * exactly the same boolean.</p>
 *
 * <p>The countermeasure is a {@code volatile} write. The Java memory model requires a volatile
 * write to be visible to other threads, so the compiler is not permitted to elide it, and
 * therefore not permitted to elide the computation that produced the value being written.</p>
 *
 * <p>This is the same technique used by {@code org.openjdk.jmh.infra.Blackhole}, reimplemented here
 * because taking a dependency on JMH would mean this project had a dependency, and see
 * {@code docs/adr/0009-zero-third-party-runtime-dependencies.md}.</p>
 *
 * <p>Instances are safe for concurrent use. Concurrent writers will overwrite each other's values,
 * which is acceptable, because the values are worthless.</p>
 */
public final class JitCompilerDeceptionBlackhole {

    private volatile double mostRecentlyDiscardedDoubleValue;
    private volatile long mostRecentlyDiscardedLongValue;
    private volatile byte mostRecentlyDiscardedByteValue;

    /**
     * @param condemnedValue a value that will never be read again.
     */
    public void consumeAndImmediatelyDiscard(final double condemnedValue) {
        this.mostRecentlyDiscardedDoubleValue = condemnedValue;
    }

    /**
     * @param condemnedValue a value that will never be read again.
     */
    public void consumeAndImmediatelyDiscard(final long condemnedValue) {
        this.mostRecentlyDiscardedLongValue = condemnedValue;
    }

    /**
     * @param condemnedValue a value that will never be read again.
     */
    public void consumeAndImmediatelyDiscard(final byte condemnedValue) {
        this.mostRecentlyDiscardedByteValue = condemnedValue;
    }

    /**
     * Reads back the most recent discarded values.
     *
     * <p>Provided for completeness. Calling it does not recover the information the values once
     * carried, because they never carried any.</p>
     *
     * @return a diagnostic rendering of the void.
     */
    public String describeContentsOfTheVoid() {
        return String.format(
                "JitCompilerDeceptionBlackhole{double=%+.6e, long=%d, byte=%d}",
                mostRecentlyDiscardedDoubleValue,
                mostRecentlyDiscardedLongValue,
                mostRecentlyDiscardedByteValue);
    }
}
