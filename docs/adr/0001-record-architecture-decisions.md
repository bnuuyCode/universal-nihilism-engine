# ADR-0001: Record architecture decisions

- **Status:** Accepted
- **Date:** 2026-08-27
- **Deciders:** bnuuy

## Context

This project makes a large number of choices that are indefensible on their merits and defensible
only in terms of each other. Without a record, a future reader encountering, for example, a
hand rolled dependency injection container in a project with no dependencies would reasonably
conclude that the author did not know Spring exists.

The author knows Spring exists. That knowledge is the decision, not its absence.

Architecture decision records solve a specific problem: code shows what was built, git history
shows when, and neither shows why. For a codebase whose entire premise is that the "why" is more
interesting than the "what", omitting the ADRs would remove the only part worth reading.

## Decision

Every non obvious structural decision in this repository is recorded as a numbered ADR in
`docs/adr/`, using a lightweight MADR variant with the sections **Status**, **Context**,
**Decision**, **Consequences**, and **Alternatives considered**.

Records are immutable once accepted. Reversing a decision means writing a new record that
supersedes the old one; the old record is not edited and not deleted. An ADR that turned out to be
wrong is more valuable than one that was quietly corrected.

Each record states the alternative that was rejected, and states it fairly. A rejected alternative
written as a strawman is worse than no record, because it looks like reasoning while preventing it.

## Consequences

- The rationale survives the author, who will not remember any of this in six months.
- Every structural change now costs a markdown file. This is a real cost and it is accepted.
- The documentation is longer than the code it documents. For this project that ratio is correct.

## Alternatives considered

**Comments in the source.** Rejected: comments explain the line they sit on, not the shape of the
system. A decision that spans eleven packages has no line to sit on.

**A single DESIGN.md.** Rejected: one document accumulating decisions over time becomes a document
that is edited, and an edited rationale loses the thing that made it useful, which is that it was
written before the outcome was known.

**No records at all.** Rejected. See Context.
