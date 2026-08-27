package com.bnuuy.universalnihilism.container;

import com.bnuuy.universalnihilism.exception.UnresolvableDependencyDespairException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * A dependency injection container with exactly the features this project uses and no others.
 *
 * <p>Every binding is a singleton. There is no prototype scope, no request scope, no session scope,
 * no annotation processing, no classpath scanning, no proxying, no lifecycle callbacks and no
 * configuration file. Bindings are declared explicitly in code, which means that the wiring of this
 * application can be read from top to bottom by a person who has never seen it before, in about a
 * minute. See {@code docs/adr/0004-hand-rolled-dependency-injection-container.md} for why a mature
 * framework was rejected.</p>
 *
 * <p>Resolution is reentrant: a provider may resolve its own collaborators from the same container.
 * A binding cycle will therefore overflow the stack rather than being diagnosed, which is
 * consistent with the container's general policy of not being helpful.</p>
 */
public final class MinimalistDependencyInjectionContainer {

    private final Map<Class<?>, Supplier<?>> declaredProviderRegistry = new LinkedHashMap<>();
    private final Map<Class<?>, Object> instantiatedSingletonCache = new LinkedHashMap<>();

    /**
     * Declares the sole provider for a contract.
     *
     * @param contractType     the interface or class consumers will ask for.
     * @param instanceProvider the factory that will be invoked at most once.
     * @param <T>              the contract type.
     * @return this container, for chaining.
     */
    public synchronized <T> MinimalistDependencyInjectionContainer registerSingletonProvider(
            final Class<T> contractType,
            final Supplier<? extends T> instanceProvider) {
        Objects.requireNonNull(contractType, "contractType must not be null");
        Objects.requireNonNull(instanceProvider, "instanceProvider must not be null");
        declaredProviderRegistry.put(contractType, instanceProvider);
        return this;
    }

    /**
     * Resolves a contract, instantiating it on first request.
     *
     * @param contractType the contract to resolve.
     * @param <T>          the contract type.
     * @return the singleton instance. Never {@code null}.
     * @throws UnresolvableDependencyDespairException if no provider is registered, or if the
     *                                                registered provider returns {@code null}.
     */
    public synchronized <T> T resolveMandatoryDependency(final Class<T> contractType) {
        Objects.requireNonNull(contractType, "contractType must not be null");

        final Object cachedInstance = instantiatedSingletonCache.get(contractType);
        if (cachedInstance != null) {
            return contractType.cast(cachedInstance);
        }

        final Supplier<?> declaredProvider = declaredProviderRegistry.get(contractType);
        if (declaredProvider == null) {
            throw new UnresolvableDependencyDespairException(contractType);
        }

        final Object freshlyConstructedInstance = declaredProvider.get();
        if (freshlyConstructedInstance == null) {
            throw new UnresolvableDependencyDespairException(contractType);
        }

        instantiatedSingletonCache.put(contractType, freshlyConstructedInstance);
        return contractType.cast(freshlyConstructedInstance);
    }

    /**
     * @return the fully qualified names of every declared contract, in declaration order.
     */
    public synchronized List<String> describeDeclaredContracts() {
        final List<String> declaredContractNames = new ArrayList<>(declaredProviderRegistry.size());
        for (final Class<?> contractType : declaredProviderRegistry.keySet()) {
            declaredContractNames.add(contractType.getName());
        }
        return Collections.unmodifiableList(declaredContractNames);
    }

    /**
     * @return how many contracts have actually been instantiated so far.
     */
    public synchronized int countInstantiatedSingletons() {
        return instantiatedSingletonCache.size();
    }
}
