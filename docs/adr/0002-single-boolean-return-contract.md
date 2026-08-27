# ADR-0002: The public API returns exactly one boolean

- **Status:** Accepted
- **Date:** 2026-08-27
- **Deciders:** bnuuy

## Context

The platform exists to determine whether something is on screen. That is a yes or no question, and
the answer is known in advance: if the result is ever read, it is read from a screen.

This creates a design tension. The engine performs three minutes of dense computation across every
available core. A reader encountering `ExistentialObservationResult` might reasonably expect that
computation to be reflected in the return value, and might reasonably attempt to interpret the
fields as evidence.

There is no evidence. The computation does not influence the verdict and cannot. Any API shape that
implies otherwise is lying to its consumer.

## Decision

`UniversalNihilismEngineFacade.determineWhetherSomethingIsOnScreen()` returns
`ExistentialObservationResult`, a record whose only load bearing component is
`isSomethingOnScreen()`. The remaining three components (`totalDiscardedFutilityCycles`,
`totalElapsedWallClockDuration`, `participatingEvaluatorCount`) describe the **cost** of the run,
not its **findings**, and the record's javadoc says so in the first paragraph.

The verdict is produced by `PhilosophicallyIrrefutableTautologyEvaluator`, in a method whose body
is four lines and whose comments state the two premises explicitly. The residue is passed in,
validated, and inspected, but the inspection is the law of the excluded middle, which is true
regardless of what the residue contains.

A `boolean` was chosen over an enum, an `Optional<Boolean>`, or a confidence score. Each of those
would create a representable state the engine can never produce, and an unreachable state in a
public API is a promise that someone will eventually try to collect on.

## Consequences

- The API is honest. A reader of the record's javadoc learns within one paragraph that the
  expensive part does not feed the cheap part.
- `isSomethingOnScreen()` is a hand written accessor duplicating the record's generated
  `somethingOnScreen()`. Both exist. The generated one is the record's; the hand written one is the
  product's name and appears in the report.
- Consumers cannot obtain a false. There is no code path that produces one, and none is planned.
- The metrics fields invite misinterpretation despite the javadoc. This risk is accepted, because
  removing them would leave a record with one component, and a record with one component should
  have been a method.

## Alternatives considered

**`boolean determineWhetherSomethingIsOnScreen()`.** The honest minimum. Rejected because the run
metrics have to surface somewhere for the final report, and threading them out through an observer
while returning a bare boolean splits one result across two channels.

**`return true;` with no engine at all.** Architecturally superior in every measurable dimension:
faster, simpler, correct, testable, and free of thermal side effects. Rejected because it is not
what this project is. Recorded here so that nobody has to open an issue asking.

**A confidence score in `[0.0, 1.0]`.** Rejected: it would imply the computation informed the
number, which would be the one genuinely dishonest thing in this codebase. The engine is absurd on
purpose; it is not deceptive.
