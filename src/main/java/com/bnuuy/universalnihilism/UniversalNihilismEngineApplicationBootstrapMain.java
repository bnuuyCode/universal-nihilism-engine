package com.bnuuy.universalnihilism;

import com.bnuuy.universalnihilism.api.ExistentialObservationResult;
import com.bnuuy.universalnihilism.api.UniversalNihilismEngineFacade;
import com.bnuuy.universalnihilism.configuration.ComputationalWorkloadDescriptor;
import com.bnuuy.universalnihilism.configuration.EngineConfigurationSourceResolver;
import com.bnuuy.universalnihilism.container.GlobalNihilismContextHolder;
import com.bnuuy.universalnihilism.container.MinimalistDependencyInjectionContainer;
import com.bnuuy.universalnihilism.exception.AbstractNihilisticEngineException;
import com.bnuuy.universalnihilism.factory.QuantumEntropyEvaluatorFactoryRegistrar;
import com.bnuuy.universalnihilism.observer.ComputationalProgressBroadcastingSubject;
import com.bnuuy.universalnihilism.observer.ObservableComputationalPhase;
import com.bnuuy.universalnihilism.observer.StatisticalFutilityAccumulatingProgressObserver;
import com.bnuuy.universalnihilism.observer.VerboseConsoleTelemetryEmittingProgressObserver;
import com.bnuuy.universalnihilism.orchestration.NihilismEngineOrchestrator;
import com.bnuuy.universalnihilism.orchestration.ThermodynamicallyIrreversibleWorkloadExecutor;
import com.bnuuy.universalnihilism.spi.ExistentialAssertionSynthesizer;
import com.bnuuy.universalnihilism.strategy.JitCompilerDeceptionBlackhole;
import com.bnuuy.universalnihilism.synthesis.DefaultExistentialAssertionSynthesizer;
import com.bnuuy.universalnihilism.synthesis.PhilosophicallyIrrefutableTautologyEvaluator;

import java.time.Duration;

/**
 * The composition root and process entry point.
 *
 * <p>This class does three things: it reads the configuration, it declares every binding in the
 * application, and it calls one method on one interface. The wiring is written out longhand and in
 * one place, so that the entire object graph of this platform can be understood without a debugger,
 * an annotation index, or faith.</p>
 *
 * <p><strong>Operational warning.</strong> Invoking {@link #main(String[])} will occupy every
 * available processor for the configured window, which defaults to three minutes. Run it inside a
 * container with an explicit CPU quota.</p>
 */
public final class UniversalNihilismEngineApplicationBootstrapMain {

    private static final int EXIT_CODE_THE_UNIVERSE_COOPERATED = 0;
    private static final int EXIT_CODE_INTERNAL_NIHILISTIC_FAILURE = 70;

    private static final String APPLICATION_BANNER = """
            ================================================================================
              UNIVERSAL NIHILISM ENGINE
              An enterprise platform for the exhaustive verification of a single boolean.
            ================================================================================""";

    private UniversalNihilismEngineApplicationBootstrapMain() {
        throw new AssertionError("Composition roots are not instantiated. They are merely endured.");
    }

    /**
     * @param commandLineArgumentsThatWillBeIgnoredEntirely accepted for compliance with the JVM
     *                                                      entry point contract, and for no other
     *                                                      reason. The engine is not configurable
     *                                                      by argument; see
     *                                                      {@link EngineConfigurationSourceResolver}.
     */
    public static void main(final String[] commandLineArgumentsThatWillBeIgnoredEntirely) {
        System.out.println(APPLICATION_BANNER);

        final ComputationalWorkloadDescriptor workloadDescriptor =
                new EngineConfigurationSourceResolver().resolveEffectiveWorkloadDescriptor();
        System.out.println("  Effective configuration: " + workloadDescriptor);
        System.out.println("  Available processors:    " + Runtime.getRuntime().availableProcessors());
        System.out.println();

        final MinimalistDependencyInjectionContainer applicationContainer =
                assembleApplicationContainer(workloadDescriptor);
        GlobalNihilismContextHolder.INSTANCE.installApplicationContainer(applicationContainer);

        final ComputationalProgressBroadcastingSubject progressSubject =
                applicationContainer.resolveMandatoryDependency(ComputationalProgressBroadcastingSubject.class);
        progressSubject.broadcastComputationalPhaseCommencement(
                ObservableComputationalPhase.DEPENDENCY_CONTAINER_BOOTSTRAP,
                "UniversalNihilismEngineApplicationBootstrapMain");

        final UniversalNihilismEngineFacade engineFacade =
                applicationContainer.resolveMandatoryDependency(UniversalNihilismEngineFacade.class);
        final StatisticalFutilityAccumulatingProgressObserver statisticalObserver =
                applicationContainer.resolveMandatoryDependency(
                        StatisticalFutilityAccumulatingProgressObserver.class);
        final PhilosophicallyIrrefutableTautologyEvaluator tautologyEvaluator =
                applicationContainer.resolveMandatoryDependency(PhilosophicallyIrrefutableTautologyEvaluator.class);

        progressSubject.broadcastComputationalPhaseAbandonment(
                ObservableComputationalPhase.DEPENDENCY_CONTAINER_BOOTSTRAP,
                "UniversalNihilismEngineApplicationBootstrapMain",
                Duration.ZERO);

        try {
            final ExistentialObservationResult observationResult = engineFacade.determineWhetherSomethingIsOnScreen();
            emitFinalReport(observationResult, statisticalObserver, tautologyEvaluator);
            System.exit(EXIT_CODE_THE_UNIVERSE_COOPERATED);
        } catch (final AbstractNihilisticEngineException nihilisticFailure) {
            System.err.println();
            System.err.println("  RUN ABANDONED [" + nihilisticFailure.obtainSeverityClassification() + "]");
            System.err.println("  " + nihilisticFailure.getMessage());
            System.err.println("  " + nihilisticFailure.obtainOperatorFacingConsolationMessage());
            System.exit(EXIT_CODE_INTERNAL_NIHILISTIC_FAILURE);
        }
    }

    /**
     * Declares every binding in the application.
     *
     * <p>Each provider resolves its own collaborators from the container, so the declaration order
     * below is documentation rather than a constraint.</p>
     *
     * @param workloadDescriptor the already resolved configuration.
     * @return a container that has declared everything and instantiated nothing.
     */
    private static MinimalistDependencyInjectionContainer assembleApplicationContainer(
            final ComputationalWorkloadDescriptor workloadDescriptor) {

        final MinimalistDependencyInjectionContainer applicationContainer =
                new MinimalistDependencyInjectionContainer();

        applicationContainer.registerSingletonProvider(
                ComputationalWorkloadDescriptor.class,
                () -> workloadDescriptor);

        applicationContainer.registerSingletonProvider(
                JitCompilerDeceptionBlackhole.class,
                JitCompilerDeceptionBlackhole::new);

        applicationContainer.registerSingletonProvider(
                StatisticalFutilityAccumulatingProgressObserver.class,
                StatisticalFutilityAccumulatingProgressObserver::new);

        applicationContainer.registerSingletonProvider(
                PhilosophicallyIrrefutableTautologyEvaluator.class,
                PhilosophicallyIrrefutableTautologyEvaluator::new);

        applicationContainer.registerSingletonProvider(
                ComputationalProgressBroadcastingSubject.class,
                () -> {
                    final ComputationalProgressBroadcastingSubject subject =
                            new ComputationalProgressBroadcastingSubject();
                    subject.registerProgressObserver(applicationContainer.resolveMandatoryDependency(
                            StatisticalFutilityAccumulatingProgressObserver.class));
                    if (workloadDescriptor.isVerboseTelemetryEmissionEnabled()) {
                        subject.registerProgressObserver(new VerboseConsoleTelemetryEmittingProgressObserver(
                                Duration.ofMillis(750L)));
                    }
                    return subject;
                });

        applicationContainer.registerSingletonProvider(
                ExistentialAssertionSynthesizer.class,
                () -> new DefaultExistentialAssertionSynthesizer(
                        applicationContainer.resolveMandatoryDependency(
                                PhilosophicallyIrrefutableTautologyEvaluator.class),
                        applicationContainer.resolveMandatoryDependency(
                                ComputationalProgressBroadcastingSubject.class),
                        workloadDescriptor.deriveMinimumCredibleComputationDuration()));

        applicationContainer.registerSingletonProvider(
                QuantumEntropyEvaluatorFactoryRegistrar.class,
                () -> new QuantumEntropyEvaluatorFactoryRegistrar(
                        workloadDescriptor,
                        applicationContainer.resolveMandatoryDependency(JitCompilerDeceptionBlackhole.class),
                        applicationContainer.resolveMandatoryDependency(ExistentialAssertionSynthesizer.class)));

        applicationContainer.registerSingletonProvider(
                ThermodynamicallyIrreversibleWorkloadExecutor.class,
                () -> new ThermodynamicallyIrreversibleWorkloadExecutor(
                        workloadDescriptor,
                        applicationContainer.resolveMandatoryDependency(
                                ComputationalProgressBroadcastingSubject.class)));

        applicationContainer.registerSingletonProvider(
                UniversalNihilismEngineFacade.class,
                () -> NihilismEngineOrchestrator.builder()
                        .withFactoryRegistrar(applicationContainer.resolveMandatoryDependency(
                                QuantumEntropyEvaluatorFactoryRegistrar.class))
                        .withWorkloadExecutor(applicationContainer.resolveMandatoryDependency(
                                ThermodynamicallyIrreversibleWorkloadExecutor.class))
                        .withProgressSubject(applicationContainer.resolveMandatoryDependency(
                                ComputationalProgressBroadcastingSubject.class))
                        .build());

        return applicationContainer;
    }

    private static void emitFinalReport(
            final ExistentialObservationResult observationResult,
            final StatisticalFutilityAccumulatingProgressObserver statisticalObserver,
            final PhilosophicallyIrrefutableTautologyEvaluator tautologyEvaluator) {

        System.out.println();
        System.out.println("================================================================================");
        System.out.println("  EXISTENTIAL OBSERVATION RESULT");
        System.out.println("================================================================================");
        System.out.printf("  isSomethingOnScreen .............. %b%n", observationResult.isSomethingOnScreen());
        System.out.println("  --------------------------------------------------------------------------");
        System.out.printf("  Participating evaluators ......... %d%n",
                observationResult.participatingEvaluatorCount());
        System.out.printf("  Discarded futility cycles ........ %,d%n",
                observationResult.totalDiscardedFutilityCycles());
        System.out.printf("  Mean cycles per evaluator ........ %,.2f%n",
                observationResult.calculateMeanFutilityCyclesPerEvaluator());
        System.out.printf("  Peak cycle count on one worker ... %,d%n",
                statisticalObserver.obtainPeakReportedCycleCount());
        System.out.printf("  Progress reports emitted ......... %,d%n",
                statisticalObserver.obtainObservedFutilityReportCount());
        System.out.printf("  Phases commenced / abandoned ..... %,d / %,d%n",
                statisticalObserver.obtainCommencedPhaseCount(),
                statisticalObserver.obtainAbandonedPhaseCount());
        System.out.printf("  Wall clock consumed .............. %s%n",
                observationResult.totalElapsedWallClockDuration());
        System.out.println("  --------------------------------------------------------------------------");
        System.out.printf("  Reasoning ........................ %s%n", tautologyEvaluator.describeJustification());
        System.out.printf("  Information gained ............... %s%n", "none");
        System.out.println("================================================================================");
    }
}
