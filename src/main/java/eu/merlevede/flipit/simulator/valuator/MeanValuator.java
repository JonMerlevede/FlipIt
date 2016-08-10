package eu.merlevede.flipit.simulator.valuator;

/**
 * Created by jonat on 16/04/2016.
 */
public class MeanValuator implements Valuator {
    private final double flipCost;
    private final double totalSimulationTime;

    private MeanValuator(final double flipCost, final double totalSimulationTime) {
        this.flipCost = flipCost;
        this.totalSimulationTime = totalSimulationTime;
    }

    public static Valuator create(final double totalSimulationTime) {
        return new MeanValuator(0, totalSimulationTime);
    }

    public static Valuator create(final double flipCost, final double totalSimulationTime) {
        return new MeanValuator(flipCost, totalSimulationTime);
    }

    @Override
    public double getFlipCost(final double time) {
        assert time >= 0;
        assert totalSimulationTime >= time;
        return this.flipCost/totalSimulationTime;
    }

    @Override
    public double getTimeValuation(final double start, final double end) {
        assert end > start;
        assert totalSimulationTime >= end;
        return (end - start)/totalSimulationTime;
    }
}
