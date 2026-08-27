# ADR-0009: Zero third party runtime dependencies

- **Status:** Accepted
- **Date:** 2026-08-27
- **Deciders:** bnuuy

## Context

Several components in this project reimplement things that exist, are mature, and are maintained by
people better at maintaining them:

- `MinimalistDependencyInjectionContainer` reimplements a subset of Guice.
- `JitCompilerDeceptionBlackhole` reimplements `org.openjdk.jmh.infra.Blackhole`.
- `VerboseConsoleTelemetryEmittingProgressObserver` reimplements a fraction of SLF4J plus an
  appender.
- `OptionalConfigurationValue`, nested inside the configuration resolver, reimplements a fraction of
  `java.util.Optional`, which is not even third party.

Each of those is a real cost. The reimplementations are smaller, less tested, and less capable than
the originals.

## Decision

The `pom.xml` declares no `<dependencies>` element. The runtime classpath is the JDK.

The reasoning is proportional rather than ideological. This project is a joke that ships as a
single jar and will be run, occasionally, by people who found it on GitHub. In that context a
dependency tree costs more than it returns: it is a supply chain, a set of CVE notifications, a
periodic Dependabot pull request, and a version conflict waiting for the day someone adds a second
library. The functionality being replaced amounts to roughly three hundred lines that will not
change again.

The threshold at which this reverses is stated so the decision can be checked rather than inherited:
**if this project ever needs a real logging backend, a serialisation format, an HTTP client, or a
test framework, the dependency is taken and this ADR is superseded.** Reimplementing any of those
would be the actual mistake.

`OptionalConfigurationValue` is the one case that fails its own test, since `java.util.Optional`
carries no supply chain risk at all. It is kept because it is nine lines and private, and because
the joke it makes about not trusting a fourth party in a two party transaction is, on balance, worth
nine lines. This is recorded as a known inconsistency rather than defended as a principle.

## Consequences

- The build has no network dependency beyond the Maven plugins themselves.
- The distributable is a single jar, roughly 30 KB of application code, runnable on any JRE 17.
- No CVE surface outside the JDK.
- Reimplemented components are less capable than what they replace. The DI container has no cycle
  detection, the observer has no log levels or appenders, and the blackhole has fewer overloads than
  JMH's. Each of those limitations is documented in the class that carries it.
- Adding a test framework in future means taking a test scoped dependency. That is explicitly fine:
  this ADR is about the *runtime* classpath. See [ADR-0010](0010-no-automated-test-suite.md) for why
  there is no test suite, which is a separate decision on separate grounds.

## Alternatives considered

**Take SLF4J plus Logback.** Would improve the telemetry observer substantially: levels, appenders,
formatting, MDC. Rejected for now on the proportionality argument, since the entire logging
requirement is "print a line, sometimes". This is the dependency most likely to be taken first if
the threshold above is ever met.

**Take JMH for `Blackhole` alone.** Tempting, because [ADR-0005](0005-jit-defeating-volatile-blackhole.md)
depends on getting that mechanism exactly right and JMH's version is written by the people who write
the compiler. The reimplementation is one volatile write per overload and the mechanism is fully
described in the JMM, so the risk is judged low. If a run ever completes suspiciously fast, taking
this dependency is the first remedy.

**Take Guice.** See [ADR-0004](0004-hand-rolled-dependency-injection-container.md), which rejects it
on readability grounds independent of this ADR. Both reasons apply.
