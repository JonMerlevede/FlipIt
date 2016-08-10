package eu.merlevede.flipit;

import com.jmatio.types.MLDouble;
import eu.merlevede.flipit.DurationsSimulator.Params;
import eu.merlevede.flipit.simulator.GameConfiguration;
import eu.merlevede.flipit.simulator.StrategyFactories;
import org.junit.Assert;

import java.util.stream.DoubleStream;
import java.util.stream.Stream;

/**
 * Created by jonat on 25/05/2016.
 */
public class DurationsSimulatorTest {
    private void assertEquals(String message, final MLDouble mld1, final MLDouble mld2, final double delta) {
        Assert.assertArrayEquals(mld1.getDimensions(), mld2.getDimensions());
        for (int i = 0; i < mld1.getSize(); i++) {
            Assert.assertEquals(message + " (erorr at index " + i + ")",
                    mld1.get(i),
                    mld2.get(i),
                    delta);
        }

    }

    @org.junit.Test
    public void testDeterminism() {
        double delta = 0;
        Params.Builder sequential = new Params.Builder()
                .setConfigBuilder(new GameConfiguration.Builder()
                        .setDefenderStrategyFactory(StrategyFactories.periodic(1))
                        .setAttackerStrategyFactory(StrategyFactories.periodic(5))
                        .setSeed(1)
                )
                .setSimulationDurations(DoubleStream.iterate(1, i-> i+1.33).limit(10).toArray())
                .setNumberOfSimulations(500)
                .setSilent(true)
                .setParallel(false);
        Params.Builder parallel = new Params.Builder().mergeFrom(sequential).setParallel(true);
        DurationsSimulator[] sDSs = Stream
                .generate(() -> new DurationsSimulator(sequential.build()))
                .peek(DurationsSimulator::compute)
                .limit(3).toArray(DurationsSimulator[]::new);
        DurationsSimulator[] pDSs = Stream
                .generate(() -> new DurationsSimulator(parallel.build()))
                .peek(DurationsSimulator::compute)
                .limit(3).toArray(DurationsSimulator[]::new);
        for (int i=1; i < sDSs.length; i++) {
            assertEquals("Sequential simulations are not deterministic", sDSs[0].getResults(), sDSs[i].getResults(), delta);
        }
        for (int i=1; i < pDSs.length; i++) {
            assertEquals("Parallel simulations are not deterministic", pDSs[0].getResults(), pDSs[i].getResults(), delta);
        }
        assertEquals("Sequential and parallel simulations do not produce the same results", sDSs[0].getResults(), pDSs[0].getResults(), delta);
    }
}