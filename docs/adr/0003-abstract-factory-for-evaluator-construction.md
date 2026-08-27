# ADR-0003: Abstract Factory is the only route to an evaluator

- **Status:** Accepted
- **Date:** 2026-08-27
- **Deciders:** bnuuy

## Context

The engine needs, per run, a set of paired collaborators: an `EntropyDissipationStrategy` that
consumes the CPU and an `ExistentialAssertionSynthesizer` that produces the verdict. Three
dissipation strategies ship (matrix, digest, recursion) and one synthesizer ships.

Three strategies and one synthesizer is not a matrix that needs a factory. It is three
constructor calls.

## Decision

Evaluators are constructed exclusively through `AbstractQuantumEntropyEvaluatorFactory`. The class
declares two protected primitives, `instantiateEntropyDissipationStrategy()` and
`instantiateExistentialAssertionSynthesizer()`, and one `final` public template method,
`manufactureFullyConfiguredQuantumEntropyEvaluator()`, which combines their output into a
`QuantumEntropyEvaluator` record.

Three concrete subclasses exist, one per strategy. Each shares the injected synthesizer instance.

The `final` on the template method is the substantive part of this decision. It means a subclass can
vary the products but cannot vary the assembly, so every evaluator in the system is guaranteed to
have been built the same way. That guarantee is worth more than the constructor calls it replaces,
which is the entire argument for the pattern and is stated here so that the argument can be
evaluated rather than assumed.

Products are bundled into a record rather than returned separately, so the factory can assert that
the pair was manufactured as a family. No incompatible combination currently exists. The assertion
is available and is therefore made.

## Consequences

- Adding a fourth strategy is: one strategy class, one factory subclass, one line in
  `QuantumEntropyEvaluatorFactoryRegistrar`. No existing file is modified beyond that line.
- The registrar returns `List<AbstractQuantumEntropyEvaluatorFactory>`, so
  `NihilismEngineOrchestrator` iterates a homogeneous collection and never names a concrete type.
- Three classes exist whose entire body is two one line overrides and a constructor. This is the
  cost of the pattern and it is visible in the file listing.
- The abstract base holds a `ComputationalWorkloadDescriptor` that none of the three current
  subclasses reads. It is exposed through a protected accessor for subclasses that might.

## Alternatives considered

**Direct construction in the composition root.** Three `new` expressions in
`assembleApplicationContainer`. Simpler, shorter, and adequate for the current three. Rejected
because it puts strategy selection in the composition root, where a fourth strategy would mean
editing the file that wires the whole application rather than the file that lists strategies.

**A `Supplier<EntropyDissipationStrategy>` per strategy.** A lambda per strategy, held in a list.
Genuinely lighter and would work. Rejected because it produces one product, not a family, and the
synthesizer would then have to be sourced from somewhere else, splitting a matched pair across two
mechanisms.

**`ServiceLoader` discovery.** Strategies discovered from `META-INF/services`. Rejected: it makes
the set of active strategies depend on the classpath, which means the answer to "what will this run
do" requires inspecting a jar. Explicit registration in one file is worth more than extensibility
nobody has asked for.
