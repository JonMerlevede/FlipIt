package eu.merlevede.flipit.br.sa;

import eu.merlevede.flipit.br.State;
import org.inferred.freebuilder.FreeBuilder;

/**
 * Acceptance function.
 *
 * Created by jonat on 3/06/2016.
 */
@FunctionalInterface
public interface AcceptanceFunction {
    double acceptProbability(Params params);

    /**
     * Default simulated annealing acceptance function.
     */
    AcceptanceFunction DEFAULT =
            (p) -> p.getDeltaEnergy() < 0 ? 1d : Math.exp(-p.getDeltaEnergy()/p.getTemperature());
    /**
     * Boltzman acceptance function.
     */
    AcceptanceFunction BOLTZ = // 50% chance to get accepted when result is worse
            (p) -> p.getDeltaEnergy() < 0 ? 1d : 1d / (1+Math.exp(p.getDeltaEnergy()/p.getTemperature()));

    AcceptanceFunction BOLTZ_DEBUG =
            (p) -> {
                double dE = p.getDeltaEnergy();
                if (dE < 0) {
                    return 1;
                } else {
                    double result = 1.0 / (1+Math.exp(p.getDeltaEnergy()/p.getTemperature()));
                    System.out.println(result);
                    return result;
                }
            };

    @FreeBuilder
    interface Params {
        State<?> getCurrent();
        State<?> getProposed();
        State<?> getBest();
        double getTemperature();
        boolean isMaximizing(); // this parameter determines the sign of getDeltaEnergy()
        double getStartTime();
        // Energy is what we MINIMIZE ==> a NEGATIVE deltaEnergy corresponds to an improvement
        default double getDeltaEnergy() {
            if (isMaximizing()) {
                return getCurrent().calculateValue() - getProposed().calculateValue();
            } else {
                return getProposed().calculateValue() - getCurrent().calculateValue();
            }
        }

        class Builder extends AcceptanceFunction_Params_Builder {}
    }
}