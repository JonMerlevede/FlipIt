package eu.merlevede.util;

import java.util.function.Supplier;

/**
 * Created by jonat on 9/06/2016.
 */
public class Timer {
    public static <T> T time(Supplier<T> runnable) {
        long startTime = System.nanoTime();
        T result = runnable.get();
        long endTime = System.nanoTime();
        long millisec = (endTime - startTime)/1000000;
        System.out.println("Execution took " + millisec + "ms");
        return result;
    }

    public static <T> T time(String msg, Supplier<T> runnable) {
        long startTime = System.nanoTime();
        T result = runnable.get();
        long endTime = System.nanoTime();
        long millisec = (endTime - startTime)/1000000;
        System.out.println("Execution of " + msg + " took " + millisec + "ms");
        return result;
    }
}
