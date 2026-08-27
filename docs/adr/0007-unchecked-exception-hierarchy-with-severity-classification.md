# ADR-0007: Unchecked exception hierarchy with severity classification

- **Status:** Accepted
- **Date:** 2026-08-27
- **Deciders:** bnuuy

## Context

The engine has five distinguishable failure modes. None of them is recoverable in the sense that
matters: there is no alternative strategy to try, no degraded mode to fall back to, and no retry
that would produce a different outcome. Every one of them ends the run.

Java offers checked exceptions for conditions a caller can reasonably handle, and unchecked ones for
programming errors and unrecoverable conditions. On the plain reading, five unrecoverable failures
belong in the unchecked branch.

There is a second constraint. `EntropyDissipationStrategy` implementations run inside
`Callable` lambdas submitted to an `ExecutorService`, and providers registered in the DI container
are `Supplier` lambdas. Checked exceptions do not pass through the standard functional interfaces.
Adopting them would force either a wrapper interface per functional type or a `try`/`catch` around
every lambda body, which pushes handling code into the composition root for conditions the
composition root cannot handle.

## Decision

One abstract root, `AbstractNihilisticEngineException extends RuntimeException`, with five concrete
subclasses:

| Exception | Raised when | Severity |
| --- | --- | --- |
| `MeaninglessComputationTimeoutException` | Workers outlive the grace period | `TERMINALLY_INEVITABLE` |
| `PrematureExistentialCertaintyException` | The run was shorter than the credibility floor | `PROFOUNDLY_UNSURPRISING` |
| `EntropyBudgetPrematurelyExhaustedException` | A strategy stopped for an unrequested reason | `MILDLY_DISAPPOINTING` |
| `UnresolvableDependencyDespairException` | A binding was never declared | `COSMICALLY_INSIGNIFICANT` |
| `IrreconcilableOntologicalStateException` | The engine holds two incompatible beliefs about itself | `PROFOUNDLY_UNSURPRISING` |

Every instance carries an `ExistentialSeverityClassification`, an enum of four tiers ordered by
increasing indifference, each carrying an operator facing consolation message. The composition root
catches the root type once, prints the message, the severity, and the consolation, and exits 70
(`EX_SOFTWARE`).

Each exception carries the structured data that produced it (worker counts, durations, the
offending strategy identifier, the unsatisfied contract) as typed fields, not only as interpolated
text. A caller that wants to react programmatically can, even though none currently does.

Interfaces declare these types in `throws` clauses despite being unchecked. This is legal Java and
it is deliberate: the clause is documentation that javadoc renders and that an implementer reads,
without imposing a handling burden.

## Consequences

- The strategy SPI has a clean signature that composes with `Callable` and `Supplier` without
  wrapper types.
- One `catch` block in the composition root handles every failure mode the engine defines.
- The compiler no longer enforces that callers acknowledge these conditions. Accepted: there is no
  handling to enforce, and a mandatory `catch` block containing a rethrow is ceremony without
  safety.
- Severity is presentational. It changes what is printed, not what happens. This is stated in the
  enum's own javadoc so that nobody builds alerting on the assumption that it gates behaviour.
- `UnresolvableDependencyDespairException` is classified `COSMICALLY_INSIGNIFICANT` while being the
  most actionable failure in the list. The classification describes cosmic significance, not
  operational urgency, and the tiers were named before that tension was noticed. It is left as is,
  per [ADR-0001](0001-record-architecture-decisions.md).

## Alternatives considered

**Checked exceptions throughout.** The textbook choice for a library API. Rejected on the
functional interface constraint above: the workaround costs a wrapper type per functional interface
and puts `catch` blocks where no handling is possible.

**A single exception type with an enum discriminator.** Fewer classes, and the severity enum is
already carrying similar weight. Rejected because it forces callers to `switch` on a field to
distinguish cases the type system could have distinguished, and because each subclass carries
different structured data.

**Standard JDK exceptions.** `TimeoutException`, `IllegalStateException`,
`NoSuchElementException` cover four of the five adequately. Rejected: the names are the deliverable
here, and a project whose stated purpose is verbose nomenclature does not import
`IllegalStateException`.
