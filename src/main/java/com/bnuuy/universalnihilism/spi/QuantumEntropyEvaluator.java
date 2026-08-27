package com.bnuuy.universalnihilism.spi;

import java.util.Objects;

/**
 * The product of an abstract factory: a matched pair of collaborators that, together, waste time
 * and then declare a boolean.
 *
 * <p>The two components are bundled rather than injected separately so that the factory can
 * guarantee they were manufactured as a family. No known combination is incompatible with any
 * other, but the guarantee is available and is therefore made.</p>
 *
 * @param originatingFactoryDisplayName    the factory that produced this evaluator.
 * @param entropyDissipationStrategy       the component that performs the waste.
 * @param existentialAssertionSynthesizer  the component that declares the verdict.
 */
public record QuantumEntropyEvaluator(
        String originatingFactoryDisplayName,
        EntropyDissipationStrategy entropyDissipationStrategy,
        ExistentialAssertionSynthesizer existentialAssertionSynthesizer) {

    public QuantumEntropyEvaluator {
        Objects.requireNonNull(originatingFactoryDisplayName, "originatingFactoryDisplayName must not be null");
        Objects.requireNonNull(entropyDissipationStrategy, "entropyDissipationStrategy must not be null");
        Objects.requireNonNull(existentialAssertionSynthesizer, "existentialAssertionSynthesizer must not be null");
    }
}
