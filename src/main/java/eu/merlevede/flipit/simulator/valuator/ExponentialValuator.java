package eu.merlevede.flipit.simulator.valuator;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by jonat on 16/04/2016.
 */
public class ExponentialValuator implements Valuator {
    public final double discountFactor;
    public final double flipCost;
    public final double inverseLogDiscountFactor;

    @Inject
    public ExponentialValuator(@Named("discount factor") double discountFactor, @Named("flip cost") double flipCost) {
        this.discountFactor = discountFactor;
        this.flipCost = flipCost;
        this.inverseLogDiscountFactor = 1.0/Math.log(discountFactor);
    }

    @Override
    public double getFlipCost(double time) {
        return flipCost*Math.pow(discountFactor, time);
    }

    @Override
    public double getTimeValuation(double start, double end) {
        // Total simulation time is irrelevant for discounted valuation
        return (Math.pow(discountFactor,start) - Math.pow(discountFactor,end))*inverseLogDiscountFactor;
    }
}
