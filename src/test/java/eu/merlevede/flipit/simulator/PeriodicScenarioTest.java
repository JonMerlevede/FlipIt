package eu.merlevede.flipit.simulator;

import eu.merlevede.flipit.GameResult;
import eu.merlevede.flipit.theory.Periodic;
import org.junit.*;

import java.util.Random;

import static eu.merlevede.flipit.simulator.actors.PlayerType.ATTACKER;
import static eu.merlevede.flipit.simulator.actors.PlayerType.DEFENDER;

/**
 * Created by Jonathan on 22/04/2016.
 */
public class PeriodicScenarioTest {
    Simulator bothFast;
    Simulator slowDefender;

    @Before
    public void setUp() {
    }

//    public static double defenderGain(double dPeriod, double aPeriod) {
//        double r = dPeriod < aPeriod ? dPeriod/aPeriod : aPeriod/dPeriod;
//        return dPeriod < aPeriod ? 1 - r/2 : r/2;
//    }
//    public static double attackerGain(double dPeriod, double aPeriod) {
//        double r = dPeriod < aPeriod ? dPeriod/aPeriod : aPeriod/dPeriod;
//        return dPeriod < aPeriod ? r/2 : 1 - r/2;
//    }
//    public static double cost(double period, double flipCost) {
//        return flipCost/period;
//    }

    @org.junit.Test
    public void testRandom() {
        double maxPeriod = 5;
        double maxCost = 3;
        double requiredPrecision = 0.01;
        Random random = new Random(1);
        Simulator sim = Simulator.create();
        for (int i=0; i<200; i++) {
            double dPeriod = random.nextDouble()*maxPeriod;
            double aPeriod = random.nextDouble()*maxPeriod;
            double dCost = random.nextDouble()*maxCost;
            double aCost = random.nextDouble()*maxCost;
            Periodic.Mean theoreticResults = new Periodic.Mean.Builder()
                    .setDefenderPeriod(dPeriod)
                    .setAttackerPeriod(aPeriod)
                    .setDefenderFlipCost(dCost)
                    .setAttackerFlipCost(aCost)
                    .build();
            GameConfiguration config = theoreticResults.toGameConfiguration()
                    .setTotalSimulationDuration(100)
                    .build();
            GameResult gameResult = sim.simulate(Game.supplier(config), 5000);
            Assert.assertEquals(theoreticResults.gain(DEFENDER), gameResult.getPlayerResults().get(DEFENDER).getGain(), requiredPrecision);
            Assert.assertEquals(theoreticResults.gain(ATTACKER), gameResult.getPlayerResults().get(ATTACKER).getGain(), requiredPrecision);
            Assert.assertEquals(theoreticResults.cost(DEFENDER), gameResult.getPlayerResults().get(DEFENDER).getCost(), requiredPrecision);
            Assert.assertEquals(theoreticResults.cost(ATTACKER), gameResult.getPlayerResults().get(ATTACKER).getCost(), requiredPrecision);
            Assert.assertEquals(theoreticResults.benefit(DEFENDER), gameResult.getPlayerResults().get(DEFENDER).getBenefit(), requiredPrecision);
            Assert.assertEquals(theoreticResults.benefit(ATTACKER), gameResult.getPlayerResults().get(ATTACKER).getBenefit(), requiredPrecision);
        }
    }
}
