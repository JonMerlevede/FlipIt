package eu.merlevede.flipit.br;

import eu.merlevede.flipit.br.sa.AcceptanceFunction;
import eu.merlevede.flipit.br.sa.CoolingSchedule;
import eu.merlevede.flipit.br.sa.SAOptimizer;
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

/**
 * Created by jonat on 1/06/2016.
 */
public class SAOptimizerTest {
    @Test
    public void solve() throws Exception {
        System.out.println(new QuadraticState(1,-8,11,4));
        QuadraticState initialState = new QuadraticState(1, -8, 11, -2);
        SAOptimizer.Params params = new SAOptimizer.Params.Builder()
                .setRandom(new Random(2))
                .setMaximumNumberOfSteps(10000)
                .setMaximization(false)
                .setInitialTemperature(10)
                .setAcceptanceFunction(AcceptanceFunction.BOLTZ)
                .setCoolingSchedule(CoolingSchedule.BOLTZ)
                .setStepsizeFunctionSupplier(StepsizeFunctions.constant(0.01))
                .build();
        SAOptimizer optimizer = SAOptimizer.create(params);
        SAOptimizer.Result<QuadraticState> sol = optimizer.optimize(initialState);
        System.out.println(sol);
        Assert.assertEquals(4, sol.getState().getPosition(), 0.02);
    }

}