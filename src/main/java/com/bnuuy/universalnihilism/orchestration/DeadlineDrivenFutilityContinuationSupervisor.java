package com.bnuuy.universalnihilism.orchestration;

import com.bnuuy.universalnihilism.spi.FutilityContinuationSupervisor;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Grants permission to waste resources until a fixed monotonic deadline passes.
 *
 * <p>The deadline is captured from {@link System#nanoTime()} rather than wall clock time, so that a
 * daylight saving transition, an NTP correction, or an administrator with a strong opinion about
 * the system clock cannot shorten or extend the run.</p>
 *
 * <p>Comparisons are written as {@code current - deadline < 0} rather than
 * {@code current < deadline}, which is the documented overflow safe idiom for {@code nanoTime}
 * values.</p>
 */
public final class DeadlineDrivenFutilityContinuationSupervisor implements FutilityContinuationSupervisor {

    private final long absoluteDeadlineNanoTimestamp;
    private final AtomicBoolean immediateAbandonmentRequested = new AtomicBoolean(false);

    private DeadlineDrivenFutilityContinuationSupervisor(final long absoluteDeadlineNanoTimestamp) {
        this.absoluteDeadlineNanoTimestamp = absoluteDeadlineNanoTimestamp;
    }

    /**
     * @param permittedDissipationWindow how long futility remains authorised, starting now.
     * @return a supervisor whose deadline is already running.
     */
    public static DeadlineDrivenFutilityContinuationSupervisor commencingImmediately(
            final Duration permittedDissipationWindow) {
        Objects.requireNonNull(permittedDissipationWindow, "permittedDissipationWindow must not be null");
        return new DeadlineDrivenFutilityContinuationSupervisor(
                System.nanoTime() + permittedDissipationWindow.toNanos());
    }

    @Override
    public boolean isFurtherFutilityPermitted() {
        if (immediateAbandonmentRequested.get()) {
            return false;
        }
        return System.nanoTime() - absoluteDeadlineNanoTimestamp < 0L;
    }

    @Override
    public Duration obtainRemainingFutilityAllowance() {
        if (immediateAbandonmentRequested.get()) {
            return Duration.ZERO;
        }
        final long remainingNanoseconds = absoluteDeadlineNanoTimestamp - System.nanoTime();
        return remainingNanoseconds <= 0L ? Duration.ZERO : Duration.ofNanos(remainingNanoseconds);
    }

    /**
     * Withdraws permission ahead of the deadline. Idempotent, and irreversible by design: nothing
     * in this platform is entitled to change its mind twice.
     */
    public void requestImmediateAbandonment() {
        immediateAbandonmentRequested.set(true);
    }
}
