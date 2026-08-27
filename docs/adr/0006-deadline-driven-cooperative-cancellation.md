# ADR-0006: Cooperative deadline cancellation, not thread interruption

- **Status:** Accepted
- **Date:** 2026-08-27
- **Deciders:** bnuuy

## Context

The engine must stop after a configured window. Workers are pure CPU loops: no blocking I/O, no
`sleep`, no `wait`, no queue operations. That detail decides the mechanism.

`Thread.interrupt()` sets a flag and unblocks threads parked in interruptible operations. A thread
executing arithmetic is in no such operation, so interruption does nothing to it until it
voluntarily checks `Thread.interrupted()`. Interruption of a pure compute loop is therefore already
cooperative; it is cooperative cancellation wearing a mechanism's clothes.

## Decision

`FutilityContinuationSupervisor` is an explicit SPI contract with two methods,
`isFurtherFutilityPermitted()` and `obtainRemainingFutilityAllowance()`. Every strategy consults it
at a documented granularity and returns when permission is withdrawn.

`DeadlineDrivenFutilityContinuationSupervisor` captures an absolute deadline from
`System.nanoTime()` at construction. `nanoTime` is monotonic, so an NTP correction, a daylight
saving transition, or an administrator with opinions about the system clock cannot shorten or
extend a run. Comparisons are written `System.nanoTime() - deadline < 0` rather than
`nanoTime() < deadline`, which is the documented overflow safe idiom for comparing `nanoTime`
values.

Consultation granularity is per strategy and is documented in each:

| Strategy | Consulted | Worst case overshoot |
| --- | --- | --- |
| Dense matrix | once per output row, and once per full product | one row |
| Cryptographic digest | once per quota batch | one batch, ~250 000 digests |
| Naive Fibonacci | once per whole evaluation | one full evaluation of `fib(38)` |

The overshoot is bounded and small relative to the grace period, which exists to absorb it.

The executor retains a hard backstop independent of cooperation. `invokeAll` is called with a
timeout of `window + grace`; a worker that has not returned by then is cancelled by the pool, its
future reports `isCancelled()`, and `MeaninglessComputationTimeoutException` is raised naming the
count. The supervisor is additionally told to withdraw permission in a `finally` block, and the pool
is shut down with `shutdownNow()` on every exit path.

## Consequences

- Cancellation points are explicit, visible in the source, and documented per strategy with a
  stated worst case. Nothing depends on a strategy remembering to check an ambient flag.
- The mechanism is testable without threads: a supervisor is an interface with two methods, and a
  fake that returns `false` on the third call exercises every strategy's exit path.
- Workers can overshoot by up to one unit of work. The grace period is configured for this and the
  backstop covers the rest.
- Two mechanisms exist for stopping: cooperative (primary) and pool cancellation (backstop). Two
  mechanisms are more surface than one. Accepted, because the backstop's only job is to turn a hung
  worker into a named exception instead of a hung process.

## Alternatives considered

**`Thread.interrupt()` alone.** Rejected: it does not stop a compute loop. Adopting it would mean
every strategy calling `Thread.interrupted()` in its inner loop, which is cooperative cancellation
with a worse contract and no `obtainRemainingFutilityAllowance()`.

**A shared `volatile boolean` flag.** Nearly the same thing, and it would work. Rejected because a
naked flag has no interface, cannot be faked in a test, and cannot answer "how long is left" for
telemetry. The supervisor is that flag with a contract around it.

**A `ScheduledExecutorService` that calls `shutdownNow()` at the deadline.** Rejected: it makes the
deadline invisible from inside the strategies, and a strategy that cannot see its own deadline
cannot report progress against it.

**Checking `nanoTime()` in the innermost loop.** Rejected: at 512 iterations of a single multiply
and add, the clock read costs more than the arithmetic it guards, and the platform would then be
measuring `nanoTime` throughput.
