package eu.merlevede.flipit.br.sa;

import org.inferred.freebuilder.FreeBuilder;

/**
 * Simulated annealing cooling schedule.
 *
 * Created by jonat on 3/06/2016.
 */
@FunctionalInterface
public interface CoolingSchedule {
    double temperature(Params params);

    CoolingSchedule EXP =
            (p) -> p.getInitialTemperature() * Math.pow(0.95, p.getCurrentStep());
    CoolingSchedule FAST =
            (p) -> p.getInitialTemperature() / (1+p.getCurrentStep());
    CoolingSchedule BOLTZ =
            (p) -> p.getInitialTemperature() / (1+Math.log(1+p.getCurrentStep()));

    @FreeBuilder
    interface Params {
        double getInitialTemperature();
        int getMaxSteps();

        /**
         * Default: 0
         */
        int getCurrentStep();

        class Builder extends CoolingSchedule_Params_Builder {}
    }
}