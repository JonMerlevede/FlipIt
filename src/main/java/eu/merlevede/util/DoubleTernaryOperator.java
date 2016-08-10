package eu.merlevede.util;

/**
 * Created by jonat on 30/05/2016.
 */
@FunctionalInterface
public interface DoubleTernaryOperator {
    double applyAsDouble(double arg1, double arg2, double arg3);
}
