package com.bnuuy.universalnihilism.configuration;

import com.bnuuy.universalnihilism.exception.IrreconcilableOntologicalStateException;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.Properties;

/**
 * Resolves the effective {@link ComputationalWorkloadDescriptor} from JVM system properties.
 *
 * <p>There is no configuration file, no environment variable fallback and no remote configuration
 * server. Every additional configuration source is another place from which the truth might
 * differ, and this platform already has one truth too many.</p>
 */
public final class EngineConfigurationSourceResolver {

    public static final String PROPERTY_DISSIPATION_WINDOW = "une.dissipation.window";
    public static final String PROPERTY_TERMINATION_GRACE = "une.termination.grace";
    public static final String PROPERTY_MATRIX_DIMENSION = "une.matrix.dimension";
    public static final String PROPERTY_DIGEST_QUOTA = "une.digest.quota";
    public static final String PROPERTY_FIBONACCI_DEPTH = "une.fibonacci.depth";
    public static final String PROPERTY_CONCURRENCY_MULTIPLIER = "une.concurrency.multiplier";
    public static final String PROPERTY_VERBOSE_TELEMETRY = "une.telemetry.verbose";

    private final Properties configurationSource;

    /**
     * Creates a resolver backed by {@link System#getProperties()}.
     */
    public EngineConfigurationSourceResolver() {
        this(System.getProperties());
    }

    /**
     * Creates a resolver backed by an explicit property set, primarily so that the class can be
     * exercised without mutating global JVM state.
     *
     * @param configurationSource the properties to read from; never {@code null}.
     */
    public EngineConfigurationSourceResolver(final Properties configurationSource) {
        this.configurationSource = Objects.requireNonNull(configurationSource, "configurationSource must not be null");
    }

    /**
     * @return the descriptor implied by the configured properties, falling back to the builder
     *         defaults for anything unspecified.
     * @throws IrreconcilableOntologicalStateException if a supplied value cannot be interpreted.
     */
    public ComputationalWorkloadDescriptor resolveEffectiveWorkloadDescriptor() {
        final ComputationalWorkloadDescriptor.Builder builder = ComputationalWorkloadDescriptor.builder();

        readOptionalDuration(PROPERTY_DISSIPATION_WINDOW).ifPresentDo(builder::withTargetEntropyDissipationWindow);
        readOptionalDuration(PROPERTY_TERMINATION_GRACE).ifPresentDo(builder::withGracefulTerminationGracePeriod);
        readOptionalInteger(PROPERTY_MATRIX_DIMENSION).ifPresentDo(builder::withDenseMatrixDimensionalCardinality);
        readOptionalLong(PROPERTY_DIGEST_QUOTA).ifPresentDo(builder::withCryptographicDigestRepetitionQuotaPerCycle);
        readOptionalInteger(PROPERTY_FIBONACCI_DEPTH).ifPresentDo(builder::withNaivelyRecursiveFibonacciOrdinalDepth);
        readOptionalInteger(PROPERTY_CONCURRENCY_MULTIPLIER)
                .ifPresentDo(builder::withPerCoreConcurrencyMultiplicityFactor);
        readOptionalBoolean(PROPERTY_VERBOSE_TELEMETRY).ifPresentDo(builder::withVerboseTelemetryEmissionEnabled);

        try {
            return builder.build();
        } catch (final IllegalStateException invalidCombination) {
            throw new IrreconcilableOntologicalStateException(
                    "The supplied configuration describes a run that would not be sufficiently punishing: "
                            + invalidCombination.getMessage(),
                    invalidCombination);
        }
    }

    private OptionalConfigurationValue<Duration> readOptionalDuration(final String propertyKey) {
        final String rawValue = readTrimmedRawValue(propertyKey);
        if (rawValue == null) {
            return OptionalConfigurationValue.absent();
        }
        try {
            return OptionalConfigurationValue.present(Duration.parse(rawValue));
        } catch (final DateTimeParseException malformed) {
            throw new IrreconcilableOntologicalStateException(
                    "Property '" + propertyKey + "' must be an ISO-8601 duration such as PT3M, but was '"
                            + rawValue + "'.",
                    malformed);
        }
    }

    private OptionalConfigurationValue<Integer> readOptionalInteger(final String propertyKey) {
        final String rawValue = readTrimmedRawValue(propertyKey);
        if (rawValue == null) {
            return OptionalConfigurationValue.absent();
        }
        try {
            return OptionalConfigurationValue.present(Integer.valueOf(rawValue));
        } catch (final NumberFormatException malformed) {
            throw new IrreconcilableOntologicalStateException(
                    "Property '" + propertyKey + "' must be a 32 bit integer, but was '" + rawValue + "'.",
                    malformed);
        }
    }

    private OptionalConfigurationValue<Long> readOptionalLong(final String propertyKey) {
        final String rawValue = readTrimmedRawValue(propertyKey);
        if (rawValue == null) {
            return OptionalConfigurationValue.absent();
        }
        try {
            return OptionalConfigurationValue.present(Long.valueOf(rawValue));
        } catch (final NumberFormatException malformed) {
            throw new IrreconcilableOntologicalStateException(
                    "Property '" + propertyKey + "' must be a 64 bit integer, but was '" + rawValue + "'.",
                    malformed);
        }
    }

    private OptionalConfigurationValue<Boolean> readOptionalBoolean(final String propertyKey) {
        final String rawValue = readTrimmedRawValue(propertyKey);
        if (rawValue == null) {
            return OptionalConfigurationValue.absent();
        }
        if ("true".equalsIgnoreCase(rawValue)) {
            return OptionalConfigurationValue.present(Boolean.TRUE);
        }
        if ("false".equalsIgnoreCase(rawValue)) {
            return OptionalConfigurationValue.present(Boolean.FALSE);
        }
        throw new IrreconcilableOntologicalStateException(
                "Property '" + propertyKey + "' must be 'true' or 'false', but was '" + rawValue
                        + "'. The engine declines to guess which of two things you meant.");
    }

    private String readTrimmedRawValue(final String propertyKey) {
        final String rawValue = configurationSource.getProperty(propertyKey);
        if (rawValue == null) {
            return null;
        }
        final String trimmedValue = rawValue.trim();
        return trimmedValue.isEmpty() ? null : trimmedValue;
    }

    /**
     * A deliberately minimal optional type.
     *
     * <p>{@link java.util.Optional} would have sufficed. It would also have been the fourth party
     * involved in a two party transaction.</p>
     *
     * @param <T> the type of the configured value.
     */
    private static final class OptionalConfigurationValue<T> {

        private static final OptionalConfigurationValue<?> ABSENT_SINGLETON = new OptionalConfigurationValue<>(null);

        private final T resolvedValue;

        private OptionalConfigurationValue(final T resolvedValue) {
            this.resolvedValue = resolvedValue;
        }

        @SuppressWarnings("unchecked")
        static <T> OptionalConfigurationValue<T> absent() {
            return (OptionalConfigurationValue<T>) ABSENT_SINGLETON;
        }

        static <T> OptionalConfigurationValue<T> present(final T resolvedValue) {
            return new OptionalConfigurationValue<>(
                    Objects.requireNonNull(resolvedValue, "resolvedValue must not be null"));
        }

        void ifPresentDo(final java.util.function.Consumer<T> configurationApplicator) {
            if (resolvedValue != null) {
                configurationApplicator.accept(resolvedValue);
            }
        }
    }
}
