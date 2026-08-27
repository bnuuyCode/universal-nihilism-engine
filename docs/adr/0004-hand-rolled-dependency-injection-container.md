# ADR-0004: Hand rolled DI container instead of Spring

- **Status:** Accepted
- **Date:** 2026-08-27
- **Deciders:** bnuuy

## Context

The application has eight bindings. All eight are singletons. None has a lifecycle callback, a
scope other than singleton, a conditional activation, or a profile.

A mature framework (Spring, Guice, Dagger, CDI) would handle this correctly, is battle tested, and
would require roughly no code. It would also introduce a runtime dependency, which conflicts with
[ADR-0009](0009-zero-third-party-runtime-dependencies.md), and would move the object graph from
source code into annotations resolved at startup.

## Decision

`MinimalistDependencyInjectionContainer` implements exactly two operations:

```java
<T> Container registerSingletonProvider(Class<T> contract, Supplier<? extends T> provider);
<T> T resolveMandatoryDependency(Class<T> contract);
```

Every binding is a singleton, instantiated lazily on first resolution and cached. Resolution is
reentrant, so a provider may resolve its own collaborators from the same container; the object
graph is therefore built depth first from the root, in dependency order, without the container ever
needing to compute that order.

All bindings are declared in one method, `assembleApplicationContainer`, in the composition root.
The whole object graph is readable top to bottom in about a minute.

There is no annotation processing, no classpath scanning, no proxying, no lifecycle management, and
no configuration file. A missing binding raises `UnresolvableDependencyDespairException` naming the
contract.

## Consequences

- The wiring of this application can be understood by reading one method. No debugger, no
  annotation index, no startup log.
- Zero runtime dependencies preserved.
- A binding cycle overflows the stack rather than producing a diagnostic. This is a real regression
  against every mature container and is accepted: eight bindings in one visible method make a cycle
  a reading error, not a debugging session.
- The container keys on `Class`, so it cannot hold a `List<AbstractQuantumEntropyEvaluatorFactory>`
  distinctly from any other `List`. `QuantumEntropyEvaluatorFactoryRegistrar` exists solely to give
  that list a nameable type. This is erasure leaking into the design and it is documented in that
  class.
- All public methods are `synchronized`. Contention is irrelevant: resolution happens once, at
  startup, on one thread.

## Alternatives considered

**Spring Framework.** The obvious choice, and correct for almost any real application. Rejected:
one runtime dependency and roughly 6 MB of jars to manage eight singletons, plus a startup path
that resolves the graph by scanning rather than by reading.

**Google Guice.** Lighter than Spring and a better fit. Still a runtime dependency, and its value
appears at a binding count this project will never reach.

**Dagger.** Compile time injection, no runtime dependency in the finished artifact, and it would
have satisfied ADR-0009. Rejected on a narrower ground: the generated graph is not readable in the
source tree, and for this project the readability of the composition root is the point.

**No container at all.** Eight `new` expressions in `main`, nested by hand. This would work. It was
rejected because the platform's stated position is that nothing should do directly what it can
delegate to an interface, and a composition root that constructs its own graph is the one place
that position would have been abandoned.
