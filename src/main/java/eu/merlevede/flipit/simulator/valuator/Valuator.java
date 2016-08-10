package eu.merlevede.flipit.simulator.valuator;

/**
 * Created by jonat on 12/04/2016.
 */
public interface Valuator {
    double getFlipCost(double time);
    double getTimeValuation(double start, double end);
}
