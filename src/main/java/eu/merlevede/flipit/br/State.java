package eu.merlevede.flipit.br;


import java.util.Random;

/**
 * Represents a state, i.e. a point in search space.
 * The dimension of the search space can be arbitrarily high.
 *
 * Created by jonat on 8/06/2016.
 */
public interface State<T extends State<T>> {
    /**
     * Returns a neighbor state at approximately the provided distance from this one.
     *
     * The returned state might but does not have to be stochastic. If the returned state is stochastic, all randomness
     * stems from the provided random, i.e. calls to this method with the same arguments always return the same state.
     *
     * @param random The provided random / source of randomness.
     * @param distance The distance from this state at which to search for a neighbor.
     * @return Neighbor state.
     */
    T randomNeighbour(Random random, double distance);

    /**
     * Returns this state as an instance of T.
     */
    T getThis();

    /**
     * Calculates the value of this state.
     * Calculating the value of a state might be an expensive operation. Look into {@link BufferedState} for performance benefits.
     * It is not prohibited for the value of a state to change between calculations.
     *
     *
     * @return The value of this state.
     */
    double calculateValue();
}
