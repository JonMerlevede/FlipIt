package eu.merlevede.flipit.simulator.strategies.generators;

import java.util.Random;

/**
 * Generator of double values that are separated from each other by iid distances drawn from this generator's exponential
 * distribution defined by {@link #getLambda()}.
 *
 * Created by Jonathan on 10/05/2016.
 */
public class ExponentialGenerator implements Generator {
    private final Random random;
    private final double invLambda;
    private double next;

    public ExponentialGenerator(final Random random, final double lambda) {
        this.random = random;
        this.invLambda = -1/lambda;
        next = Math.log(random.nextDouble())*invLambda;
    }

    public double getLambda() {
        return -1/invLambda;
    }

    @Override
    public double getAndNext() {
        double r = next;
        next += Math.log(random.nextDouble())*invLambda;
        return r;
    }
}
