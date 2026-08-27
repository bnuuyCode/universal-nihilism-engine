package com.bnuuy.universalnihilism.configuration;

import java.time.Duration;
import java.util.Objects;

/**
 * Immutable description of how much of the host machine the engine is authorised to consume, and
 * for how long.
 *
 * <p>Instances are obtained exclusively through {@link Builder}. The constructor is private, the
 * fields are final, and the class is final, so that the configuration cannot change its mind
 * halfway through a run the way everything else does.</p>
 */
public final class ComputationalWorkloadDescriptor {

    /**
     * Java's {@code long} representation of Fibonacci numbers overflows above ordinal 92. The
     * engine has no use for the value, but it declines to compute a wrong one.
     */
    public static final int MAXIMUM_REPRESENTABLE_FIBONACCI_ORDINAL = 92;

    private final Duration targetEntropyDissipationWindow;
    private final Duration gracefulTerminationGracePeriod;
    private final int denseMatrixDimensionalCardinality;
    private final long cryptographicDigestRepetitionQuotaPerCycle;
    private final int naivelyRecursiveFibonacciOrdinalDepth;
    private final int perCoreConcurrencyMultiplicityFactor;
    private final boolean verboseTelemetryEmissionEnabled;

    private ComputationalWorkloadDescriptor(final Builder builder) {
        this.targetEntropyDissipationWindow = builder.targetEntropyDissipationWindow;
        this.gracefulTerminationGracePeriod = builder.gracefulTerminationGracePeriod;
        this.denseMatrixDimensionalCardinality = builder.denseMatrixDimensionalCardinality;
        this.cryptographicDigestRepetitionQuotaPerCycle = builder.cryptographicDigestRepetitionQuotaPerCycle;
        this.naivelyRecursiveFibonacciOrdinalDepth = builder.naivelyRecursiveFibonacciOrdinalDepth;
        this.perCoreConcurrencyMultiplicityFactor = builder.perCoreConcurrencyMultiplicityFactor;
        this.verboseTelemetryEmissionEnabled = builder.verboseTelemetryEmissionEnabled;
    }

    public Duration obtainTargetEntropyDissipationWindow() {
        return targetEntropyDissipationWindow;
    }

    public Duration obtainGracefulTerminationGracePeriod() {
        return gracefulTerminationGracePeriod;
    }

    public int obtainDenseMatrixDimensionalCardinality() {
        return denseMatrixDimensionalCardinality;
    }

    public long obtainCryptographicDigestRepetitionQuotaPerCycle() {
        return cryptographicDigestRepetitionQuotaPerCycle;
    }

    public int obtainNaivelyRecursiveFibonacciOrdinalDepth() {
        return naivelyRecursiveFibonacciOrdinalDepth;
    }

    public int obtainPerCoreConcurrencyMultiplicityFactor() {
        return perCoreConcurrencyMultiplicityFactor;
    }

    public boolean isVerboseTelemetryEmissionEnabled() {
        return verboseTelemetryEmissionEnabled;
    }

    /**
     * The shortest run the engine is willing to describe as conclusive. Anything faster triggers
     * {@code PrematureExistentialCertaintyException}.
     *
     * @return half of the target dissipation window.
     */
    public Duration deriveMinimumCredibleComputationDuration() {
        return targetEntropyDissipationWindow.dividedBy(2L);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "ComputationalWorkloadDescriptor{"
                + "targetEntropyDissipationWindow=" + targetEntropyDissipationWindow
                + ", gracefulTerminationGracePeriod=" + gracefulTerminationGracePeriod
                + ", denseMatrixDimensionalCardinality=" + denseMatrixDimensionalCardinality
                + ", cryptographicDigestRepetitionQuotaPerCycle=" + cryptographicDigestRepetitionQuotaPerCycle
                + ", naivelyRecursiveFibonacciOrdinalDepth=" + naivelyRecursiveFibonacciOrdinalDepth
                + ", perCoreConcurrencyMultiplicityFactor=" + perCoreConcurrencyMultiplicityFactor
                + ", verboseTelemetryEmissionEnabled=" + verboseTelemetryEmissionEnabled
                + '}';
    }

    /**
     * Fluent builder for {@link ComputationalWorkloadDescriptor}.
     *
     * <p>Defaults are calibrated to make a developer laptop audible within roughly ten seconds and
     * regrettable within three minutes.</p>
     */
    public static final class Builder {

        private Duration targetEntropyDissipationWindow = Duration.ofMinutes(3L);
        private Duration gracefulTerminationGracePeriod = Duration.ofSeconds(30L);
        private int denseMatrixDimensionalCardinality = 512;
        private long cryptographicDigestRepetitionQuotaPerCycle = 250_000L;
        private int naivelyRecursiveFibonacciOrdinalDepth = 38;
        private int perCoreConcurrencyMultiplicityFactor = 1;
        private boolean verboseTelemetryEmissionEnabled = true;

        private Builder() {
        }

        public Builder withTargetEntropyDissipationWindow(final Duration value) {
            this.targetEntropyDissipationWindow =
                    Objects.requireNonNull(value, "targetEntropyDissipationWindow must not be null");
            return this;
        }

        public Builder withGracefulTerminationGracePeriod(final Duration value) {
            this.gracefulTerminationGracePeriod =
                    Objects.requireNonNull(value, "gracefulTerminationGracePeriod must not be null");
            return this;
        }

        public Builder withDenseMatrixDimensionalCardinality(final int value) {
            this.denseMatrixDimensionalCardinality = value;
            return this;
        }

        public Builder withCryptographicDigestRepetitionQuotaPerCycle(final long value) {
            this.cryptographicDigestRepetitionQuotaPerCycle = value;
            return this;
        }

        public Builder withNaivelyRecursiveFibonacciOrdinalDepth(final int value) {
            this.naivelyRecursiveFibonacciOrdinalDepth = value;
            return this;
        }

        public Builder withPerCoreConcurrencyMultiplicityFactor(final int value) {
            this.perCoreConcurrencyMultiplicityFactor = value;
            return this;
        }

        public Builder withVerboseTelemetryEmissionEnabled(final boolean value) {
            this.verboseTelemetryEmissionEnabled = value;
            return this;
        }

        /**
         * @return a validated, immutable descriptor.
         * @throws IllegalStateException if any value would make the engine finish early, which is
         *                               the only failure mode this class considers serious.
         */
        public ComputationalWorkloadDescriptor build() {
            if (targetEntropyDissipationWindow.isZero() || targetEntropyDissipationWindow.isNegative()) {
                throw new IllegalStateException(
                        "targetEntropyDissipationWindow must be strictly positive; "
                                + "a zero length window would deliver the answer immediately and honestly");
            }
            if (gracefulTerminationGracePeriod.isNegative()) {
                throw new IllegalStateException("gracefulTerminationGracePeriod must not be negative");
            }
            if (denseMatrixDimensionalCardinality < 2) {
                throw new IllegalStateException(
                        "denseMatrixDimensionalCardinality must be at least 2; "
                                + "a 1x1 matrix multiplication is not a hardship");
            }
            if (cryptographicDigestRepetitionQuotaPerCycle < 1L) {
                throw new IllegalStateException("cryptographicDigestRepetitionQuotaPerCycle must be at least 1");
            }
            if (naivelyRecursiveFibonacciOrdinalDepth < 1
                    || naivelyRecursiveFibonacciOrdinalDepth > MAXIMUM_REPRESENTABLE_FIBONACCI_ORDINAL) {
                throw new IllegalStateException(
                        "naivelyRecursiveFibonacciOrdinalDepth must be within [1, "
                                + MAXIMUM_REPRESENTABLE_FIBONACCI_ORDINAL + "]");
            }
            if (perCoreConcurrencyMultiplicityFactor < 1) {
                throw new IllegalStateException("perCoreConcurrencyMultiplicityFactor must be at least 1");
            }
            return new ComputationalWorkloadDescriptor(this);
        }
    }
}
