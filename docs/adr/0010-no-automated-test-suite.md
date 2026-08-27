# ADR-0010: No automated test suite

- **Status:** Accepted
- **Date:** 2026-08-27
- **Deciders:** bnuuy

## Context

This project has no `src/test/java`. That absence needs a record, because the alternative reading is
that it was forgotten, and it was not.

There is a joke available here: "the system does nothing, so there is nothing to test." The joke is
wrong, and recording why it is wrong is more useful than recording the joke. There are real,
testable invariants in this codebase:

- `ComputationalWorkloadDescriptor.Builder` rejects six distinct invalid configurations.
- `EngineConfigurationSourceResolver` translates four kinds of parse failure.
- `MinimalistDependencyInjectionContainer` caches singletons, resolves reentrantly, and raises on a
  missing binding.
- `DeadlineDrivenFutilityContinuationSupervisor` withdraws permission at its deadline and stays
  withdrawn.
- Every strategy exits promptly when a supervisor returns `false`, which is trivially testable with
  a fake supervisor and no threads at all.
- `ThermodynamicallyIrreversibleWorkloadExecutor` raises `MeaninglessComputationTimeoutException`
  when a future is cancelled.

All of those are worth testing in a project that anyone depends on.

## Decision

No test suite ships, on the following grounds, in order of weight:

1. **Nothing depends on this.** The cost of a defect is that a joke program produces its correct
   output slightly differently. There is no consumer, no data, and no downstream.
2. **The one invariant that actually matters is not unit testable.** The property this platform
   lives or dies by is "the JIT compiler did not delete the workload"
   ([ADR-0005](0005-jit-defeating-volatile-blackhole.md)). That is a property of a specific JVM's
   optimiser under sustained load, not of a method. Verifying it means running the engine for
   minutes and watching a CPU meter, which is a benchmark, not a test. A green unit suite would
   provide false assurance about the only thing that can silently break.
3. **`-Xlint:all` is enabled and the build is warning free.** For a project of this size and stakes,
   that is a proportionate level of assurance.

What is *not* claimed: that the code is too simple to break, or that untestable means unimportant.
The verification story here is deliberately weak and this record says so plainly rather than
dressing it up.

## Consequences

- A regression in configuration validation or container resolution would be caught by running the
  program, or not at all.
- Refactoring is riskier than it would be with a suite. Accepted at this project's stakes.
- This ADR is superseded the moment anything depends on this code, or the moment a second person
  contributes to it. Both conditions are stated so the reversal is mechanical rather than a
  judgement call.
- Adding tests later requires a test scoped dependency, which
  [ADR-0009](0009-zero-third-party-runtime-dependencies.md) explicitly permits: that ADR constrains
  the runtime classpath only.

## Alternatives considered

**JUnit 5 covering the invariants listed above.** Straightforwardly correct for a real project, and
roughly a day of work. Rejected on stakes, not on difficulty. This is the alternative to revisit
first.

**A single smoke test running the engine with `PT1S`.** Rejected as actively misleading: it would
pass whether or not the workload was eliminated, which means it would be green in exactly the
failure mode that matters.

**A JMH benchmark asserting a floor on cycles per second.** This is the *right* verification for
the property in ADR-0005, and it is the honest answer to the gap identified above. Rejected for now
because it needs a JMH dependency and a stable machine to produce a meaningful threshold, and a
flaky performance gate is worse than a documented absence. Worth doing if this project is ever taken
further.
