package eu.merlevede.flipit.br;

import java.util.function.Supplier;

/**
 * Static factory methods pertaining to {@link StepsizeFunction} instances.
 *
 * Created by jonat on 9/06/2016.
 */
public final class StepsizeFunctions {
    /**
     * There should be no instance of this class!
     */
    private StepsizeFunctions() {}

    public static final Supplier<StepsizeFunction> dynamicExponential(double initialStepSize, int maxNumberOfStepsWithoutImprovement, double minSize) {
        return dynamicExponential(initialStepSize, 0.5, maxNumberOfStepsWithoutImprovement, minSize);
    }

    public static final Supplier<StepsizeFunction> dynamicExponential(double initialStepSize, double factor, int maxNumberOfStepsWithoutImprovement, double minSize) {
        if (factor > 1) {
            throw new IllegalArgumentException("Factor is supposed to be smaller than 1");
        }
        return () -> new StepsizeFunction() {
            private double stepSize = initialStepSize;
            private int lastIncrease = 0;
            private int lastCalled = -1;
            @Override
            public double stepSize(final Params params) {
                if (lastCalled != params.getCurrentStep()-1) { throw new IllegalArgumentException("You are not supposed to reuse this stepsize function"); }
                lastCalled = params.getCurrentStep();
                if ( stepSize > minSize
                        && params.getNumberOfStepsWithoutImprovement() > maxNumberOfStepsWithoutImprovement
                        && lastIncrease + maxNumberOfStepsWithoutImprovement <= lastCalled) {
                    stepSize *= factor;
                    lastIncrease = lastCalled;
                }
                return stepSize;
            }
            @Override
            public boolean mayStop() {
                return Double.isNaN(minSize) || stepSize <= minSize;
            }
        };
    }
    public static final Supplier<StepsizeFunction> constant(double stepSize) {
        return () -> ($) -> stepSize;
    }
    public static final Supplier<StepsizeFunction> linear(double initialStepSize) {
        return () -> (p) -> (1 - p.getProgress()) * initialStepSize;
    }
    public static final Supplier<StepsizeFunction> linearOffset(double initialStepSize, double offSet) {
        return () -> (p) -> offSet + (1 - p.getProgress()) * initialStepSize;
    }
}
