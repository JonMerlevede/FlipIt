package eu.merlevede.flipit.br;

import java.util.Random;

/**
 * Decorated state that lazily calculates and saves (buffers) the value returned by {@link #calculateValue()}.
 * In other words, a buffered state is a state decorator that saves its value when it is first calculated, preventing
 * the value from being calculated multiple times.
 *
 * Created by jonat on 8/06/2016.
 */
public class BufferedState<T extends State<T>> implements ForwardingState<T> {
    private final T delegate;
    private double value = Double.NaN;

    private BufferedState(T delegate) {
        this.delegate = delegate;
    }

    public static <S extends State<S>> S buffer(S wrapped) {
        if (wrapped instanceof BufferedState) {
            return wrapped;
        } else {
            return new BufferedState<>(wrapped).getThis();
        }
    }

//    public static <S extends State<S>> S buffer(S wrapped) {
//        if (wrapped instanceof BufferedState) {
//            return wrapped;
//        } else {
//            return new BufferedState<>(wrapped).getThis();
//        }
//    }

    @Override
    public T randomNeighbour(Random random, double distance) {
        return buffer(delegate.randomNeighbour(random, distance)).getThis();
    }

    @Override
    public T getThis() {
        return delegate;
    }

    @Override
    public double calculateValue() {
        if (Double.isNaN(value)) {
            value = delegate.calculateValue();
            if (Double.isNaN(value)) {
                throw new IllegalStateException();
            }
        }
        return value;
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return delegate.equals(obj);
    }
}
