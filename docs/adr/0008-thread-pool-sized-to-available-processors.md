# ADR-0008: Thread pool sized to available processors

- **Status:** Accepted
- **Date:** 2026-08-27
- **Deciders:** bnuuy

## Context

The platform's stated behaviour is that it consumes the CPU. Consuming *some* of the CPU is not the
product. If the engine ran one worker on a sixteen core host it would occupy six percent of the
machine and the claim in the README would be false.

The work is entirely CPU bound. There is no I/O, no blocking, and no memory stall pattern that
would benefit from oversubscription. For CPU bound work, the throughput optimal pool size is the
number of available cores: fewer leaves cores idle, more adds context switching without adding
arithmetic.

## Decision

`ThermodynamicallyIrreversibleWorkloadExecutor` sizes a fixed pool at
`Runtime.getRuntime().availableProcessors() * une.concurrency.multiplier`, with the multiplier
defaulting to 1 and floored at one worker.

`availableProcessors()` is deliberately the source rather than a configured constant. Modern JVMs
are container aware: under a cgroup CPU quota the method reports the quota, not the host's core
count. The engine therefore respects a quota without being told about it, which makes an externally
imposed limit the supported way to bound its appetite.

Evaluators are assigned to workers round robin (`evaluators.get(workerIndex % evaluators.size())`),
so with three strategies and twelve workers each strategy runs on four threads. Round robin rather
than one thread per strategy: the latter would cap parallelism at three regardless of host size.

Workers are daemon threads named `nihilism-worker-N` via an explicit `ThreadFactory`, so a thread
dump taken mid run is legible and so a stranded pool cannot keep the JVM alive.

The multiplier is retained as configuration despite 1 being optimal, because oversubscription is
the only available lever for someone who wants the machine to be *unpleasant* rather than merely
*busy*, and that is a legitimate use of this software.

## Consequences

- The engine occupies essentially all CPU the OS will give it, which is the documented behaviour.
- CPU quotas are respected automatically. This is the recommended way to run it and the README says
  so.
- Heap scales with the pool: each matrix worker allocates three `double[dimension^2]` buffers,
  roughly 6 MiB per worker at the default cardinality of 512. Twelve workers is about 72 MiB, which
  is why a `-Xmx512m` ceiling is worth setting and worth stating.
- On a host where the JVM sees many cores and the operator sets no quota, the machine becomes
  unpleasant to use for the duration of the window. This is the intended behaviour and is stated
  plainly in the README's resource warning, which is the only mitigation the project ships.
- Because the pool is fixed and every task is submitted at once, all workers start immediately.
  There is no queueing, so a cancelled future genuinely means an unresponsive worker rather than one
  that never started, which is what makes the `isCancelled()` check in
  [ADR-0006](0006-deadline-driven-cooperative-cancellation.md) meaningful.

## Alternatives considered

**A fixed count, e.g. 4.** Rejected: wrong on both a 2 core and a 64 core host, and it would make
the platform's behaviour depend on a number chosen by someone who had never seen the target
machine.

**`ForkJoinPool.commonPool()`.** Sized to `availableProcessors() - 1` and shared with parallel
streams. Rejected: leaving one core free is a courtesy this program does not extend, and occupying
the common pool for three minutes would be rude to any other code in the JVM.

**Virtual threads.** Available on the target platform. Rejected: virtual threads multiplex blocking
tasks onto carrier threads, and there is nothing here to multiplex. Every task is CPU bound and
never yields, so virtual threads would add a scheduler and change nothing.

**`availableProcessors() - 1`, to keep the host usable.** Rejected as a default: it makes the
headline behaviour a near miss. The correct way to keep the host usable is a CPU quota, which the
engine already respects.
