package eu.merlevede.flipit.simulator;

import eu.merlevede.flipit.GameResult;

import java.util.Random;

/**
 * Created by Jonathan on 22/04/2016.
 */
public class PeriodicScenario {
    public static double defenderGain(double dPeriod, double aPeriod) {
        double r = dPeriod < aPeriod ? dPeriod/aPeriod : aPeriod/dPeriod;
        return dPeriod < aPeriod ? 1 - r/2 : r/2;
    }
    public static double attackerGain(double dPeriod, double aPeriod) {
        double r = dPeriod < aPeriod ? dPeriod/aPeriod : aPeriod/dPeriod;
        return dPeriod < aPeriod ? r/2 : 1 - r/2;
    }
    public static double cost(double period, double flipCost) {
        return flipCost/period;
    }

    public static void main(String[] args) {
        double maxPeriod = 5;
        double maxCost = 3;
        double precision = 0.01;
        Random random = new Random(1);
        Simulator sim = Simulator.create();
        for (int i=0; i<200; i++) {
            GameConfiguration conf = new GameConfiguration.Builder()
                    .setDefenderStrategyFactory(StrategyFactories.periodic(random.nextDouble()*maxPeriod))
                    .setAttackerStrategyFactory(StrategyFactories.periodic(random.nextDouble()*maxPeriod))
                    .setDefenderValuatorFactory(ValuatorFactories.mean(random.nextDouble()*maxCost))
                    .setAttackerValuatorFactory(ValuatorFactories.mean(random.nextDouble()*maxCost))
                    .setTotalSimulationDuration(100)
                    .build();
            GameResult gameResult = sim.simulate(Game.supplier(conf), 5000);
            //Assert.assertEquals(defenderGain(dPeriod, aPeriod), gameResult.getPlayerResults().get(PlayerType.DEFENDER).getGain(), precision);
            //Assert.assertEquals(attackerGain(dPeriod, aPeriod), gameResult.getPlayerResults().get(PlayerType.ATTACKER).getGain(), precision);
            //Assert.assertEquals(cost(dPeriod, dCost), gameResult.getPlayerResults().get(PlayerType.DEFENDER).getCost(), precision);
            //Assert.assertEquals(cost(aPeriod, aCost), gameResult.getPlayerResults().get(PlayerType.ATTACKER).getCost(), precision);
        }
    }
}
