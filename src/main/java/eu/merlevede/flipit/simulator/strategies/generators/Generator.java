package eu.merlevede.flipit.simulator.strategies.generators;

/**
 * Generator for an (effectively) infinite sequence of double values.
 *
 * Created by Jonathan on 10/05/2016.
 */
public interface Generator {
    double getAndNext();
}
