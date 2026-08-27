package com.bnuuy.universalnihilism.container;

import com.bnuuy.universalnihilism.exception.IrreconcilableOntologicalStateException;

import java.util.Objects;

/**
 * The application wide singleton holding the one and only dependency injection container.
 *
 * <p>Implemented as a single element enum, which is the only formulation of the singleton pattern
 * that the Java language itself defends: the JVM guarantees exactly one instance, initialisation is
 * thread safe without a lock, and neither reflection nor deserialisation can produce a second.</p>
 *
 * <p>Installation is permitted exactly once per JVM. A second installation is treated as a
 * contradiction rather than an update, because two containers would mean two answers, and the
 * platform is committed to there being one.</p>
 */
public enum GlobalNihilismContextHolder {

    /** The instance. There is not another. */
    INSTANCE;

    private volatile MinimalistDependencyInjectionContainer installedApplicationContainer;

    /**
     * Installs the application container.
     *
     * @param applicationContainer the fully wired container.
     * @throws IrreconcilableOntologicalStateException if a container is already installed.
     */
    public synchronized void installApplicationContainer(
            final MinimalistDependencyInjectionContainer applicationContainer) {
        Objects.requireNonNull(applicationContainer, "applicationContainer must not be null");
        if (installedApplicationContainer != null) {
            throw new IrreconcilableOntologicalStateException(
                    "An application container is already installed. Installing a second one would give this "
                            + "process two incompatible accounts of its own composition.");
        }
        installedApplicationContainer = applicationContainer;
    }

    /**
     * @return the installed container.
     * @throws IrreconcilableOntologicalStateException if nothing has been installed yet.
     */
    public MinimalistDependencyInjectionContainer obtainApplicationContainer() {
        final MinimalistDependencyInjectionContainer currentContainer = installedApplicationContainer;
        if (currentContainer == null) {
            throw new IrreconcilableOntologicalStateException(
                    "No application container has been installed. The engine has been asked to consult a "
                            + "composition root that does not exist.");
        }
        return currentContainer;
    }

    /**
     * @return whether a container has been installed.
     */
    public boolean isApplicationContainerInstalled() {
        return installedApplicationContainer != null;
    }

    /**
     * Discards the installed container.
     *
     * <p>Exists so that a test harness can start from nothing. Production code has no reason to
     * call it, and calling it in production will not be diagnosed as an error, merely regretted.</p>
     */
    public synchronized void relinquishApplicationContainer() {
        installedApplicationContainer = null;
    }
}
