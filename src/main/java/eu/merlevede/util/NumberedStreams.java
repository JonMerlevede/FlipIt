package eu.merlevede.util;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static javafx.scene.input.KeyCode.T;

/**
 * Created by jonat on 26/05/2016.
 */
public final class NumberedStreams {
    private NumberedStreams() {}
    public static Stream<NumberedDouble> of(double[] vals) {
        return IntStream.range(0, vals.length).mapToObj(no -> NumberedDouble.create(no, vals[no]));
    }
    public static <T> Stream<NumberedObj<T>> of(T[] vals) {
        return IntStream.range(0, vals.length).mapToObj(no -> NumberedObj.create(no, vals[no]));
    }

    public static <T> Stream<NumberedObj<T>> of(Collection<T> coll) {
        return of(coll.size(), coll);
    }

    public static <T> Stream<NumberedObj<T>> of(int numel, Collection<T> coll) {
        AtomicInteger i = new AtomicInteger(-1);
        Iterator<NumberedObj<T>> elems = coll.stream().map(el -> NumberedObj.create(i.incrementAndGet(), el)).iterator();
        return StreamSupport.stream(Spliterators.spliterator(elems, numel, Spliterator.IMMUTABLE), false);
    }

    public static <T> Stream<NumberedObj<T>> of(int numel, Supplier<T> supplier) {
        //return IntStream.range(0, numel).mapToObj(number -> NumberedObj.buffer(number, supplier.get()));
        //Collection<NumberedObj<T>> elems =
        //return StreamSupport.stream(elems, true);
//        List<NumberedObj<T>> l = IntStream.iterate(0, i -> i+1).limit(numel).mapToObj(number -> NumberedObj.buffer(number, supplier.get())).collect(Collectors.toList());
//        return l.stream();
        Iterator<NumberedObj<T>> elems = IntStream.iterate(0, i -> i+1).limit(numel).mapToObj(number -> NumberedObj.create(number, supplier.get())).iterator();
        return StreamSupport.stream(Spliterators.spliterator(elems, numel, Spliterator.CONCURRENT | Spliterator.IMMUTABLE), false);
    }

    public static <T> Stream<NumberedObj<T>> of(int startIncl, int endExcl, Supplier<T> supplier) {
        return IntStream.range(startIncl, endExcl).mapToObj(
                number -> NumberedObj.create(number, supplier.get())
        );
    }
}
