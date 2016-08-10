package eu.merlevede.flipit.simulator.actors;

import gnu.trove.map.hash.TObjectDoubleHashMap;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by jonat on 19/05/2016.
 */
public final class Schedulers {
    private Schedulers() {}

    public static final Scheduler create(int capacity) {
        // We can give optimized implementations for different capacities
        if (capacity <= 2) {
            return new BinaryScheduler();
        } else if (capacity == 3) {
            return new TernaryScheduler();
        } else if (capacity <= 5) {
            return new NaryScheduler2();
        } else {
            return new NaryScheduler();
        }
    }

    private static final class BinaryScheduler implements Scheduler {
        private Actor leftActor;
        private Actor rightActor;
        private double leftPriority;
        private double rightPriority;
        private Actor nextActor;
        private double nextPriority;

        public BinaryScheduler() {
            leftPriority = Double.MAX_VALUE;
            rightPriority = Double.MAX_VALUE;
        }

        private void updateNextActor() {
            if (leftPriority <= rightPriority) {
                nextActor = leftActor;
                nextPriority = leftPriority;
            } else {
                nextActor = rightActor;
                nextPriority = rightPriority;
            }
        }

        @Override
        public void schedule(final Actor actor, final double time) {
            if (actor == leftActor) {
                leftPriority = time;
            } else if (actor == rightActor) {
                rightPriority = time;
            } else if (leftActor == null) {
                leftActor = actor;
                leftPriority = time;
            } else if (rightActor == null) {
                rightActor = actor;
                rightPriority = time;
            } else {
                throw new IllegalArgumentException("Binary scheduler can only be used for two actors.");
            }
            updateNextActor();
        }

        @Override
        public Actor head() {
            return nextActor;
        }

        @Override
        public double headPriority() {
            return nextPriority;
        }
    }

    private static final class TernaryScheduler implements Scheduler {
        private Actor actor1;
        private Actor actor2;
        private Actor actor3;
        private Actor nextActor;
        private double time1;
        private double time2;
        private double time3;
        private double nextTime;


        private void updateNextActor() {
            if (time1 < time2) {
                if (time1 < time3) {
                    nextActor = actor1;
                    nextTime = time1;
                } else {
                    nextActor = actor3;
                    nextTime = time3;
                }
            } else {
                if (time2 < time3) {
                    nextActor = actor2;
                    nextTime = time2;
                } else {
                    nextActor = actor3;
                    nextTime = time3;
                }
            }
        }

        @Override
        public void schedule(final Actor actor, final double time) {
            if (actor == actor1) {
                time1 = time;
            } else if (actor == actor2) {
                time2 = time;
            } else if (actor == actor3) {
                time3 = time;
            } else if (actor1 == null) {
                actor1 = actor;
                time1 = time;
            } else if (actor2 == null) {
                actor2 = actor;
                time2 = time;
            } else if (actor3 == null) {
                actor3 = actor;
                time3 = time;
            } else {
                throw new IllegalArgumentException();
            }
            updateNextActor();
        }

        @Override
        public Actor head() {
            return nextActor;
        }

        @Override
        public double headPriority() {
            return nextTime;
        }
    }

    /**
     * O(n) but faster than {@link NaryScheduler} for small number of actors (< 10?).
     */
    private static final class NaryScheduler2 implements Scheduler {
        final TObjectDoubleHashMap<Actor> scheduledTimes;
        public NaryScheduler2() {
            this.scheduledTimes = new TObjectDoubleHashMap<>(2);
        }

        Actor nextActor;
        double nextPriority;
        public void setNextActor() {
            nextPriority = Double.MAX_VALUE;
            scheduledTimes.forEachEntry((actor, priority) -> {
                if (priority < nextPriority) {
                    nextPriority = priority;
                    nextActor = actor;
                }
                return true;
            });
        }

        public void schedule(final Actor actor, final double time) {
            assert time >= scheduledTimes.get(actor);
            scheduledTimes.put(actor, time);
            setNextActor();
        }

        @Override
        public Actor head() {
            return nextActor;
        }

        @Override
        public double headPriority() {
            return nextPriority;
        }
    }

    /**
     * O(n log n) time complexity but very slow for small numbers of actors.
     * Execution of periodic simulation takes ~30s instead of ~9s for the binary scheduler.
     */
    private static final class NaryScheduler implements  Scheduler {
        private final TObjectDoubleHashMap<Actor> scheduledTimes;
        private final SortedSet<Actor> actors;

        public NaryScheduler() {
            scheduledTimes = new TObjectDoubleHashMap<>(2);
            Comparator<Actor> c = (o1, o2) -> {
                final double t1 = scheduledTimes.get(o1);
                final double t2 = scheduledTimes.get(o2);
                if (t1 < t2) {
                    return -1;
                } else if (t1 == t2) {
                    return 0;
                } else {
                    return 1;
                }
            };
            this.actors = new TreeSet<>(c);
        }

        @Override
        public void schedule(final Actor actor, final double time) {
            assert time >= (scheduledTimes.containsKey(actor) ? scheduledTimes.get(actor) : time) : time + " is not greater than " + scheduledTimes.get(actor);
            actors.remove(actor);
            scheduledTimes.put(actor, time);
            actors.add(actor);
        }

        @Override
        public Actor head() {
            return actors.first();
        }

        @Override
        public double headPriority() {
            return scheduledTimes.get(actors.first());
        }
    }
}
