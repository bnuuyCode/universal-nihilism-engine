package com.bnuuy.universalnihilism.api;

import com.bnuuy.universalnihilism.exception.MeaninglessComputationTimeoutException;
import com.bnuuy.universalnihilism.exception.PrematureExistentialCertaintyException;

/**
 * The public entry point of the Universal Nihilism Engine.
 *
 * <p>Consumers are expected to depend on this interface and never on any implementation of it.
 * There is currently one implementation. There has only ever been one implementation. The
 * interface exists so that the second one, should it arrive, will be welcome.</p>
 */
public interface UniversalNihilismEngineFacade {

    /**
     * Determines, exhaustively and at considerable expense, whether something is on screen.
     *
     * <p>Implementations must not short circuit. Implementations must not cache. Implementations
     * must not consult the display subsystem, the window manager, or any other source that would
     * answer this question cheaply and thereby render the platform unnecessary.</p>
     *
     * @return the observation result. Never {@code null}.
     * @throws MeaninglessComputationTimeoutException  if worker threads refuse to stop.
     * @throws PrematureExistentialCertaintyException  if the answer arrives suspiciously early.
     */
    ExistentialObservationResult determineWhetherSomethingIsOnScreen();

    /**
     * @return a short, human readable description of the implementation, for logging.
     */
    String obtainImplementationDisplayName();
}
