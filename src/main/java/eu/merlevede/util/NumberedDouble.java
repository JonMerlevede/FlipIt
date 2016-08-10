package eu.merlevede.util;

import com.google.auto.value.AutoValue;

import java.util.function.DoubleFunction;
import java.util.function.DoubleUnaryOperator;

/**
 * Created by jonat on 26/05/2016.
 */
@AutoValue
public abstract class NumberedDouble {
    public static NumberedDouble create(int number, double entry) {
        return new AutoValue_NumberedDouble(number, entry);
    }
    public abstract int getNumber();
    public abstract double getEntry();

    public <S> NumberedObj<S> mapToObj(DoubleFunction<S> mapper) {
        return NumberedObj.create(getNumber(), mapper.apply(getEntry()));
    }

    public NumberedDouble map(DoubleUnaryOperator mapper) {
        return create(getNumber(), mapper.applyAsDouble(getEntry()));
    }

}
