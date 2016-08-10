package eu.merlevede.flipit;

import com.google.auto.value.AutoValue;
import com.jmatio.io.MatFileWriter;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLDouble;
import eu.merlevede.flipit.simulator.*;
import eu.merlevede.flipit.simulator.actors.PlayerType;
import eu.merlevede.util.NumberedObj;
import eu.merlevede.util.NumberedStreams;
import eu.merlevede.flipit.theory.Periodic;
import org.inferred.freebuilder.FreeBuilder;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Wrapper around {@link Simulator} that allows you to easily perform different simulations using the same
 * game configuration, except from the (maximum) simulated duration. Really only useful to test the influence
 * of simulated duration on the accuracy of the simulated benefits.
 *
 * Created by jonat on 23/05/2016.
 */
public class DurationsSimulator {
    @FreeBuilder
    public interface Params {
        default void log(String message) {
            if (!isSilent()) { System.out.print(message); }
        }
        /**
         * Whether or not to print output on progress during simulation.
         * Default is to be verbose.
         */
        boolean isSilent();
        /**
         * Whether or not to execute in parallel
         */
        boolean isParallel();
        /**
         * Number of simulations to perform for a single gain evaluation.
         */
        int getNumberOfSimulations();

        /**
         * Number of times to repeat the same
         */
        double[] getSimulationDurations();

        /**
         * Partially completed gameconfiguration builder.
         * Game duration should be unset, all other properties should be set!
         */
        GameConfiguration.Builder getConfigBuilder();

        /**
         * Theoretical result
         */
        @FreeBuilder
        interface Target {
            double getValue();
            String getArrayName();
            class Builder extends DurationsSimulator_Params_Target_Builder {}
        }

        Optional<Target> getTarget();

        @FreeBuilder
        interface ArrayNames {
            String getResults();
            String getDuration();
            class Builder extends DurationsSimulator_Params_ArrayNames_Builder {
                public Builder() {
                    setResults("results");
                    setDuration("duration");
                }
            }
        }

        /**
         * Names for the arrays inside of the Matlab file.
         */
        ArrayNames getArrayNames();

        /**
         * File to write Matlab data to.
         */
        Optional<File> getFile();

        class Builder extends DurationsSimulator_Params_Builder {
            public Builder() {
                setSilent(false);
                setParallel(false);
            }
        }
    }

    //private final GameComponent gc;
    //Supplier<Game> gameSupplier;
    private final Simulator simulator;
    private final Params params;
    private final MLDouble results;

    //private Simulator createSimulator() {
    //    return Simulator.buffer(() -> gc.gameInstanceComponent().game());
    //}

    public DurationsSimulator(Params params) {
        this.simulator = Simulator.create();
        this.params = params;
        int[] resultDimensions = new int[]{params.getNumberOfSimulations(), params.getSimulationDurations().length};
        results = new MLDouble(params.getArrayNames().getResults(), resultDimensions);
    }

    @AutoValue
    static abstract class NumberedGame {
        abstract int gameNo();
        abstract int durationNo();
        abstract double duration();
        abstract Game game();
        static NumberedGame create(final int simNo, final int durationNo, final double duration, final Game game) {
            return new AutoValue_DurationsSimulator_NumberedGame(simNo, durationNo, duration, game);
        }

    }

    //private void setGain(int simulationNo, int durationNo) {
    //    synchronized ()
    //}

    private void calculateAndSetGain(Game game, int simulationNo, int durationNo) {
        double gain = simulator.simulate(game).getPlayerResults().get(PlayerType.ATTACKER).getGain();
        synchronized (results) { // apparently necessary...
            results.set(gain, simulationNo, durationNo);
        }
    }

    MLDouble getResults() {
        return results;
    }


    /**
     * It should be allowed for the configuration stream to be parallel (untested).
     *
     * @param configurationStream
     * @param nSimulations
     * @param isParallel
     * @return
     */
    public Stream<NumberedObj<Stream<NumberedObj<GameResult>>>> compute(Stream<NumberedObj<GameConfiguration>> configurationStream, int nSimulations, boolean isParallel) {
        return configurationStream.map(nConf ->
                nConf.map(conf -> compute(conf, nSimulations, isParallel))
        );
    }

    public Stream<NumberedObj<GameResult>> compute(GameConfiguration conf, int nSimulations, boolean isParallel) {
        Simulator sim = Simulator.create();
        Supplier<Game> gameSupplier = Game.supplier(conf);
        Stream<NumberedObj<Game>> s = NumberedStreams.of(nSimulations,gameSupplier);
        if (isParallel) { s = s.parallel(); } else { s = s.sequential(); }
        return s.map(nGame -> nGame.map(sim::simulate));
    }

    //private Stream<NumberedObj<GameConfiguration>> createNumberedConfStream() {
    //    return IntStream.range(0, params.getSimulationDurations().length)
    //            .mapToObj(no -> {
    //                double simulationDuration = params.getSimulationDurations()[no];
    //                GameConfiguration conf = new GameConfiguration.Builder().mergeFrom(params.getConfigBuilder()).setTotalSimulationDuration(simulationDuration).build();
    //                return NumberedObj.buffer(no, conf);
    //            });
    //}

    public void compute() {
        //Stream<NumberedObj<Double>> moo = NumberedObj.of(params.getSimulationDurations());
        Stream<NumberedObj<GameConfiguration>> confStream = NumberedStreams.of(params.getSimulationDurations()).map(
                nDuration -> nDuration.mapToObj(
                        duration -> (new GameConfiguration.Builder().mergeFrom(params.getConfigBuilder().setTotalSimulationDuration(duration)).build())
                )
        );
        //Stream<NumberedObj<GameConfiguration>> confStream = createNumberedConfStream();
        final int nSimulations = params.getSimulationDurations().length;
        compute(confStream, params.getNumberOfSimulations(), params.isParallel()).forEach(nConfResultStream -> {
            params.log("\rCompleted simulation set " + (nConfResultStream.getNumber() + 1) + "/" + nSimulations);
            nConfResultStream.getObj().forEach(nSimulationResult -> {
                assert nConfResultStream.getNumber() < nSimulations;
                synchronized (results) {
                    results.set(
                            nSimulationResult.getObj().getPlayerResults().get(PlayerType.ATTACKER).getGain(),
                            nSimulationResult.getNumber(),
                            nConfResultStream.getNumber()
                    );
                }
            });
        });
        params.log("\n");
    }

//    public void compute() {
//        int nSimDurations = params.getSimulationDurations().length;
//        final Simulator sim = Simulator.buffer();
//        IntStream.range(0, nSimDurations).forEach(durationNo -> {
//            double simulationDuration = params.getSimulationDurations()[durationNo];
//            GameConfiguration conf = new GameConfiguration.Builder().mergeFrom(this.params.getConfigBuilder()).setTotalSimulationDuration(simulationDuration).build();
//            Supplier<Game> supplier = Game.supplier(conf);
//            params.log("\rPerforming simulation set " + (durationNo + 1) + "/" + nSimDurations);
//            IntStream simulationNoStream = IntStream.range(1, params.getNumberOfSimulations());
//            if (params.isParallel()) {
//                List<NumberedGame> l = simulationNoStream.mapToObj(simNo -> NumberedGame.buffer(simNo, durationNo, simulationDuration, supplier.get())).collect(Collectors.toList());
//                l.stream().parallel()
//                        .forEach(simulator -> calculateAndSetGain(simulator.game(), simulator.gameNo(), durationNo));
//            } else {
//                simulationNoStream.forEach(simNo -> calculateAndSetGain(supplier.get(), simNo, durationNo));
//            }
//        });
//        params.log("\n");
//    }

    public void write() throws IOException {
        if (!params.getFile().isPresent()) {
            throw new UnsupportedOperationException("You have to configure an output file");
        }
        checkNotNull(results, "You have to compute before writing to file.");
        params.log("Constructing data structures\n");
        Collection<MLArray> arrays = new ArrayList<>();
        arrays.add(results);
        arrays.add(new MLDouble(params.getArrayNames().getDuration(), params.getSimulationDurations(), 1));
        params.getTarget().ifPresent(target ->
                arrays.add(new MLDouble(target.getArrayName(), new double[]{target.getValue()}, 1))
        );
        params.log("Writing to file\n");
        new MatFileWriter(params.getFile().orElse(null), arrays);
    }


    public static void main(String[] args) throws IOException {
        System.out.println("Starting...");
        long startTime = System.currentTimeMillis();
        Periodic.Mean mean = new Periodic.Mean.Builder()
                .setAttackerPeriod(1)
                .setDefenderPeriod(2)
                .build();
        Params params = new Params.Builder()
                .setConfigBuilder(mean.toGameConfiguration())
                .setSimulationDurations(DoubleStream.iterate(1, i -> i + 1).limit(150).toArray())
                .setNumberOfSimulations(500*1000)
                .setParallel(true)
                .mutateArrayNames(b -> b
                        .setResults("periodicGains")
                        .setDuration("periodicDurations")
                )
                .setTarget(new Params.Target.Builder()
                        .setValue(mean.gain(PlayerType.ATTACKER))
                        .setArrayName("periodicTarget")
                        .build()
                )
                .setFile(new File("out/matlab/periodicGains.mat"))
                .build();
        params.getArrayNames().getResults();

        DurationsSimulator writer = new DurationsSimulator(params);
        System.out.println("System set up, starting simulations");
        writer.compute();
        writer.write();
        System.out.println("Done!");
        long stopTime = System.currentTimeMillis();
        double elapsedTime = new Long(stopTime - startTime).doubleValue()/1000;
        System.out.println("Elapsed time: " + new DecimalFormat("#.00").format(elapsedTime) + " seconds.");
    }
}
