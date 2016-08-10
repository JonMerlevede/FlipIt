package eu.merlevede.util;

import com.google.auto.value.AutoValue;

import java.util.function.Function;

/**
 * Created by jonat on 26/05/2016.
 */
@AutoValue
public abstract class NumberedObj<T> {
    public static <T> NumberedObj<T> create(int number, T entry) {
        return new AutoValue_NumberedObj<>(number, entry);
    }
    public abstract int getNumber();
    public abstract T getObj();
    public <S> NumberedObj<S> map(Function<T,S> mapper) {
        return create(getNumber(), mapper.apply(getObj()));
    }

}
