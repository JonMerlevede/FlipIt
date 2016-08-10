package eu.merlevede.flipit.br;

import org.inferred.freebuilder.FreeBuilder;

import java.util.Random;
import java.util.function.Supplier;

/**
 * Stepsize function.
 *
 * Indicates at which distance to look for neighboring states as part of a search process, as a
 * function of {@link Params} such as the current step number.
 *
 * Created by jonat on 3/06/2016.
 */
public interface StepsizeFunction {
    /**
     * Parameters to the step size function.
     * Note that additional parameters may be added in the future.
     */
    @FreeBuilder
    interface Params {
        int getMaxSteps();
        int getNumberOfStepsWithoutImprovement();
        int getCurrentStep();
        Random getRandom();

        default double getProgress() {
            return ((double) getCurrentStep()) / getMaxSteps();
        }

        class Builder extends StepsizeFunction_Params_Builder {}
    }

    double stepSize(Params params);
    default boolean mayStop() { return true; };
}
