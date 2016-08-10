package eu.merlevede.flipit.simulator;

/**
 * Immutable clock.
 *
 * Created by jonat on 12/04/2016.
 */
public interface ClockReader {
    /**
     * This clock's current time.
     */
    double getTime();

    /**
     * Indicates whether the current time of this clock is equal to this clock's maximum time.
     * Should only be called if this clock is {@link #isLimited() limited}.
     *
     * @throws  UnsupportedOperationException This clock is not limited.
     */
    default boolean isMaximum() {
        if (!isLimited()) {
            throw new UnsupportedOperationException();
        }
        return getTime() == getMaximumTime();
    };

    /**
     * This clock's maximum time.
     * Should only be called if this clock is {@link #isLimited() limited}.
     *
     * @throws UnsupportedOperationException This clock is not limited.
     */
    double getMaximumTime();

    /**
     * Whether this clock has a maximum time or not.
     *
     * @see #getMaximumTime()
     */
    boolean isLimited();
}
