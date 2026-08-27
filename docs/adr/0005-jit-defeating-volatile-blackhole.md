# ADR-0005: A volatile blackhole prevents the JIT from deleting the product

- **Status:** Accepted
- **Date:** 2026-08-27
- **Deciders:** bnuuy

## Context

This is the most important decision in the repository, and the only one that is a genuine
engineering problem rather than an elaborate joke about one.

Every strategy in this codebase computes a value and discards it. That is the product. But the
HotSpot C2 compiler performs dead code elimination: once a computation's result is provably never
read, the compiler is entitled to delete the computation. It does not delete the loop's observable
effects; it deletes the arithmetic, and then the loop, and then the allocation feeding it.

An early prototype confirmed this in the most direct way available. A three minute dissipation
window completed in single digit milliseconds, reported `isSomethingOnScreen = true`, and left the
CPU idle throughout. The output was correct. The platform was not.

The failure is subtle in the wrong direction: the program still works, still terminates, and still
prints the right answer. Nothing looks broken. Only the fans stay quiet.

## Decision

`JitCompilerDeceptionBlackhole` absorbs every discarded value into a `volatile` field, with three
overloads (`double`, `long`, `byte`) covering what the strategies produce.

The mechanism is the Java Memory Model. A volatile write establishes a happens before edge visible
to other threads, so the compiler is not permitted to elide it. Not being permitted to elide the
write means not being permitted to elide the computation that produced the value being written. The
arithmetic survives because its result becomes, formally, observable.

Every strategy calls `consumeAndImmediatelyDiscard(...)` exactly once per cycle, not once per inner
iteration. Once per cycle is sufficient: the cycle's final value transitively depends on all the
work inside it, so preserving the final value preserves the work. Once per inner iteration would
make the volatile write itself the bottleneck and would measure cache coherence traffic rather than
arithmetic.

Each strategy additionally maintains a running mean of its discarded values in a local, returned in
the residue, providing a second data dependency the compiler cannot discharge.

This is the technique used by `org.openjdk.jmh.infra.Blackhole`, reimplemented here rather than
depended upon; see [ADR-0009](0009-zero-third-party-runtime-dependencies.md).

## Consequences

- The engine performs the work it claims to perform. This is verifiable from a host CPU meter,
  which is the platform's only meaningful integration test.
- The blackhole is shared across every worker thread. Concurrent writers overwrite each other's
  values. This is correct: the values are worthless, only the write matters.
- Cache line contention on the shared volatile fields is real but negligible at one write per
  cycle, where a cycle is hundreds of milliseconds of arithmetic.
- A future JVM with a more aggressive escape analysis could, in principle, prove more than current
  HotSpot does. If a run ever completes suspiciously fast, this is the first file to inspect.

## Alternatives considered

**Depend on JMH's `Blackhole`.** The reference implementation, maintained by the people who write
the compiler being defeated. Rejected only on the dependency grounds in ADR-0009. If that ADR is
ever reversed, this is the first thing that should change.

**Print the value.** Guaranteed to defeat elimination, since I/O is observable. Rejected: at
hundreds of cycles per second across a dozen threads, the console becomes the bottleneck and the
CPU gets a rest it has not earned.

**Write to a static non volatile field.** Insufficient. A plain static write to a field nobody
reads is still eliminable; the JMM makes no visibility promise the compiler must honour.

**Accumulate into a returned value only.** The running mean already does this, and it helps. It is
kept as defence in depth but is not relied on alone: the compiler can reason about a local
accumulator far more easily than about a volatile write, and relying on it would make the platform's
correctness a matter of compiler mood.

**`-XX:-DoEscapeAnalysis` or similar JVM flags.** Rejected: correctness that depends on the
operator remembering a flag is not correctness. The countermeasure belongs in the source.
