package eu.merlevede.flipit.simulator.strategies.generators;

import java.util.Random;

/**
 * Generator of double values that are {@link #getPeriod() period} away from each other (i.e. iid), where the first value is drawn
 * uniformly random from the interval [0, period).
 *
 * Created by Jonathan on 10/05/2016.
 */
public final class PeriodicGenerator implements Generator {
    private final double period;
    private double next;

    public PeriodicGenerator(final Random random, final double period) {
        this.period = period;
        this.next = random.nextDouble() * period;
    }

    public double getPeriod() {
        return period;
    }

    @Override
    public double getAndNext() {
        double r = next;
        next += period;
        return r;
    }
}
