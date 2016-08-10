package eu.merlevede.flipit.simulator;

/**
 * Mutable clock.
 *
 * I must govern the clock, not be governed by it.           -- Golda Meir
 *
 * @see eu.merlevede.flipit.simulator.ClockReader
 *
 * Created by jonat on 12/04/2016.
 */
public class Clock implements ClockReader {
    private double currentTime;
    private final double maximumTime;

    public Clock() {
        this.currentTime = 0;
        this.maximumTime = Double.POSITIVE_INFINITY;
    }

    public Clock(double maximumTime) {
        this.currentTime = 0;
        this.maximumTime = maximumTime;
    }

    @Override
    public double getTime() {
        return currentTime;
    }

    /**
     * Increments this clock's time by the given increment.
     * @param increment The amount of time to increment this clock's current time with.
     */
    public void incrementTime(double increment) {
        setTime(getTime() + increment);
    }

    @Override
    public boolean isLimited() {
        return maximumTime != Double.POSITIVE_INFINITY;
    }

    @Override
    public double getMaximumTime() {
        if (!isLimited()) {
            throw new UnsupportedOperationException();
        }
        return maximumTime;
    }

    /**
     * Sets the time of this clock to the given new time,
     * or the minimum of the given new time and this clock's maximum time if this clock is {@link #isLimited() limited}.
     *
     * @param newTime The new time.
     */
    public void setTime(double newTime) {
        assert newTime >= currentTime : "Lost time is never found again. --Benjamin Franklin";
        assert !isMaximum() : "For a moment, nothing happened. Then, after a second or so, nothing continued to happen. -- Douglas Adams";
        currentTime = maximumTime > newTime ? newTime : maximumTime;
    }
}
