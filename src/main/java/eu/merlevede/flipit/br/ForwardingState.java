package eu.merlevede.flipit.br;

import java.util.Random;

/**
 * Helper class that can be used for decorating state.
 *
 * Created by jonat on 8/06/2016.
 */
public interface ForwardingState<T extends State<T>> extends State<T> {
    @Override
    default T randomNeighbour(Random random, double distance) {
        return getThis().randomNeighbour(random, distance);
    }

    @Override
    default double calculateValue() {
        return getThis().calculateValue();
    }
}
