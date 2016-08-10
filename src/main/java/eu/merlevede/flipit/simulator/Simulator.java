package eu.merlevede.flipit.simulator;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import eu.merlevede.flipit.GameResult;
import eu.merlevede.flipit.PlayerResult;
import eu.merlevede.flipit.simulator.actions.Actions;
import eu.merlevede.flipit.simulator.actions.Trigger;
import eu.merlevede.flipit.simulator.actors.*;
import eu.merlevede.flipit.simulator.resource.MutableResource;
import eu.merlevede.flipit.simulator.resource.Resource;
import eu.merlevede.util.DefaultMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by jonat on 12/04/2016.
 */
public final class Simulator {
    private static final int MAX_NUMBER_OF_TICKS = (int) Math.pow(10, 5);

    private Simulator() {}

    private static Simulator instance = new Simulator();
    public static Simulator create() {
        return instance;
    }

    private static Collector<SingleGameResult, AggregateGameResult, GameResult> resultCollector = Collector.of(
            AggregateGameResult::new, //supplier
            AggregateGameResult::addGameInstanceResults, // accumulator
            AggregateGameResult::combine, // combiner,
            t -> t // finalizer
    );

    public GameResult simulate(final Supplier<Game> gameSupplier) {
        return simulate(gameSupplier.get());
    }

    public GameResult simulate(final Game game) {
        return simulate2(game);
    }

    public GameResult simulate(final Supplier<Game> gameSupplier, final int numberOfSimulations) {
        Preconditions.checkArgument(numberOfSimulations > 0);
        return IntStream.range(0, numberOfSimulations).mapToObj($ -> gameSupplier.get())
                //.parallel() // important to do this after the map from before; once created, we can simulated games independently from each other
                .map(game -> simulate2(game))
                .collect(resultCollector);
    }

    private SingleGameResult simulate2(Game game) {
        Clock clock = game.getClock();
        SingleGameResult gameInstanceResults = new SingleGameResult();
        int numberOfTicks = 0;
        Actions actions = new Actions() {
            @Override
            public double getCurrentTime() {
                return game.getClock().getTime();
            }

            @Override
            public Resource getResource() {
                return game.getMutableResource();
            }

            @Override
            public void schedule(final Actor actor, final double time) {
                game.getScheduler().schedule(actor, time);
            }

            @Override
            public void flip(Player player, Resource resource) {
                assert game.getActors().contains(player);
                assert game.getMutableResource().contains(resource);
                MutableResource mr = game.getMutableResource().makeMutable(resource);
                mr.setOwner(player);
                gameInstanceResults.addCost(player, player.getValuator().getFlipCost(game.getClock().getTime()));
            }
        };
        while (!clock.isMaximum()) {
            tick(game, gameInstanceResults, actions);
            numberOfTicks++;
            if (numberOfTicks > MAX_NUMBER_OF_TICKS) {
                throw new IllegalStateException("Maximum number of simulation ticks exceeded. Try reducing the maximum simulation time, or increase the time between actor actions.");
            }
        }
        return gameInstanceResults;
    }

    private void assignGain(Game game, double oldTime, double newTime, SingleGameResult results) {
        Player previousOwner = game.getMutableResource().getOwner();
        double utilityIncrease = previousOwner.getValuator().getTimeValuation(oldTime, newTime);
        results.addGain(previousOwner, utilityIncrease);
    }

    private void tick(final Game game, final SingleGameResult results, final Actions actions) {
        Actor actingActor = game.getScheduler().head();
        assert actingActor != null;
        Clock clock = game.getClock();
        double oldTime = clock.getTime();
        double nextActTime = game.getScheduler().headPriority();
        clock.setTime(nextActTime);
        double newTime = clock.getTime();
        if (newTime > oldTime) {
            assignGain(game, oldTime, newTime, results);
        }
        if (!clock.isMaximum()) {
            actingActor.act(Trigger.Scheduled.create(), actions);
        }
    }

    private static class ModifiablePlayerResult implements PlayerResult {
        private double cost = 0;
        private double gain = 0;

        public ModifiablePlayerResult() {
            this(0, 0);
        }

        public ModifiablePlayerResult(double cost, double gain) {
            this.cost = cost;
            this.gain = gain;
        }

        public ModifiablePlayerResult(PlayerResult result) {
            cost = result.getCost();
            gain = result.getGain();
        }

        public double getCost() {
            return this.cost;
        }

        public double getGain() {
            return this.gain;
        }

        public double getBenefit() {
            return getGain() - getCost();
        }

        public void addCost(double cost) {
            if (cost < 0) {
                throw new IllegalArgumentException("Costs must be positive");
            }
            this.cost += cost;
        }

        public void addGain(double gain) {
            if (gain < 0) {
                throw new IllegalArgumentException("Gains must be positive");
            }
            this.gain += gain;
        }

        @Override
        public String toString() {
            return "Player { cost: " + getCost() + ", gain: " + getGain() + ", benefit: " + getBenefit() + " }";
        }

        @Override
        public int hashCode() {
            int result;
            long temp;
            temp = Double.doubleToLongBits(cost);
            result = (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(gain);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            return result;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            ModifiablePlayerResult that = (ModifiablePlayerResult) o;

            if (Double.compare(that.cost, cost) != 0)
                return false;
            return Double.compare(that.gain, gain) == 0;

        }

    }

    private static class SingleGameResult implements GameResult {
        private final Map<PlayerType, ModifiablePlayerResult> playerResults;
        private final Map<PlayerType, PlayerResult> unmodifiablePlayerResults;


        public SingleGameResult() {
            this.playerResults = new EnumMap<>(PlayerType.class);
            unmodifiablePlayerResults = new DefaultMap<>(Collections.unmodifiableMap(playerResults), () -> new ModifiablePlayerResult());
            for (PlayerType type : PlayerType.values()) {
                playerResults.put(type, new ModifiablePlayerResult());
            }
        }

        public Map<PlayerType, PlayerResult> getPlayerResults() {
            return unmodifiablePlayerResults;
            //return playerResults;
        }

        public void addCost(Player player, double cost) {
            playerResults.get(player.getType()).addCost(cost);
        }

        public void addGain(Player player, double gain) {
            playerResults.get(player.getType()).addGain(gain);
        }

        @Override
        public String toString() {
            return "Single simulation: " + GameResult.stringRepresentation(this);
        }
    }

    private static class AggregateGameResult implements GameResult {
        ArrayList<GameResult> gir = new ArrayList<>();
        boolean changed;
        Map<PlayerType, PlayerResult> buffer;

        void addGameInstanceResults(SingleGameResult results) {
            changed = true;
            gir.add(results);
        }

        AggregateGameResult combine(AggregateGameResult otherResult) {
            changed = true;
            gir.addAll(otherResult.gir);
            return this;
        }

        public Map<PlayerType, PlayerResult> getPlayerResults() {
            if (!changed) {
                return buffer;
            }
            // Collect all player results of the same player together
            ListMultimap<PlayerType, PlayerResult> results = ArrayListMultimap.create();
            gir.stream().map(GameResult::getPlayerResults).map(Multimaps::forMap).forEach(results::putAll);
            // Replace lists of player results by a 'mean' player result
            // Note that sum and divide by numberOfSimulations instead of calling average() to handle the case where
            // a player never moves during an entire game, therefore not occurring in that game's simulationresults.
            int numberOfSimulations = gir.size();
            Map<PlayerType, PlayerResult> averageResults = results
                    .asMap()
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(
                            entry -> entry.getKey(),
                                    entry -> new ModifiablePlayerResult(
                                            entry.getValue().stream().mapToDouble(PlayerResult::getCost).sum() / numberOfSimulations,
                                            entry.getValue().stream().mapToDouble(PlayerResult::getGain).sum() / numberOfSimulations)));
            buffer = new DefaultMap<>(Collections.unmodifiableMap(averageResults), ModifiablePlayerResult::new);
            changed = false;
            return buffer;
        }

        @Override
        public String toString() {
            return GameResult.stringRepresentation(this);
        }
    }
}
