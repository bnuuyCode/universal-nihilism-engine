# Architecture Decision Records

This directory records the decisions that shaped the Universal Nihilism Engine, including the ones
that were obviously wrong and were taken anyway for reasons stated in writing.

The format is a lightweight MADR variant: **Status**, **Context**, **Decision**,
**Consequences**, **Alternatives considered**. Records are immutable once accepted. A decision that
is later reversed gets a new record that supersedes the old one; the old one stays where it is.

| ADR | Title | Status |
| --- | --- | --- |
| [0001](0001-record-architecture-decisions.md) | Record architecture decisions | Accepted |
| [0002](0002-single-boolean-return-contract.md) | The public API returns exactly one boolean | Accepted |
| [0003](0003-abstract-factory-for-evaluator-construction.md) | Abstract Factory is the only route to an evaluator | Accepted |
| [0004](0004-hand-rolled-dependency-injection-container.md) | Hand rolled DI container instead of Spring | Accepted |
| [0005](0005-jit-defeating-volatile-blackhole.md) | A volatile blackhole prevents the JIT from deleting the product | Accepted |
| [0006](0006-deadline-driven-cooperative-cancellation.md) | Cooperative deadline cancellation, not thread interruption | Accepted |
| [0007](0007-unchecked-exception-hierarchy-with-severity-classification.md) | Unchecked exception hierarchy with severity classification | Accepted |
| [0008](0008-thread-pool-sized-to-available-processors.md) | Thread pool sized to available processors | Accepted |
| [0009](0009-zero-third-party-runtime-dependencies.md) | Zero third party runtime dependencies | Accepted |
| [0010](0010-no-automated-test-suite.md) | No automated test suite | Accepted |
