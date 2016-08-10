package eu.merlevede.flipit.br.sa;

import eu.merlevede.flipit.br.BufferedState;
import eu.merlevede.flipit.br.State;
import eu.merlevede.flipit.br.StepsizeFunction;
import org.inferred.freebuilder.FreeBuilder;

import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Simulated annealing optimizer.
 *
 * Created by jonat on 29/05/2016.
 */
public final class SAOptimizer {
    @FreeBuilder
    interface StepParams<S extends State<S>> extends AcceptanceFunction.Params, CoolingSchedule.Params, StepsizeFunction.Params {
        @Override
        S getCurrent();
        @Override
        S getProposed();
        @Override
        S getBest();
        StepsizeFunction getStepsizeFunction();
        class Builder<S extends State<S>> extends SAOptimizer_StepParams_Builder<S>
                implements AcceptanceFunction.Params, CoolingSchedule.Params, StepsizeFunction.Params {
            public Builder() {
                setStartTime(0);
                setCurrentStep(0);
                setNumberOfStepsWithoutImprovement(0);
                setMaximizing(true);
            }
        }
    }

    @FreeBuilder
    public interface Params {
        CoolingSchedule getCoolingSchedule();
        AcceptanceFunction getAcceptanceFunction();
        Supplier<StepsizeFunction> getStepsizeFunctionSupplier();
        double getInitialTemperature();
        int getMaximumNumberOfSteps();
        Random getRandom();
        boolean isMaximization();
        Optional<Double> getMinimumValue();
        Optional<Double> getMaximumValue();

        class Builder extends SAOptimizer_Params_Builder {
            public Builder() {
                setMaximization(true);
            }
        }
    }

    @FreeBuilder
    public interface Result<T extends State<T>> {
        T getState();
        int getNumberOfSteps();
        double getTemperature();
        class Builder<T extends State<T>> extends SAOptimizer_Result_Builder<T> {}
    }

    //private final Random random;
    private final Params optimizerParams;

    private SAOptimizer(Params optimizerParams) {
        this.optimizerParams = optimizerParams;
    }

    public static SAOptimizer create(Params params) {
        return new SAOptimizer(params);
    }

    private boolean isBelowMinValue(State state) {
        return state.calculateValue() <= optimizerParams.getMinimumValue().orElse(Double.NaN);
    }
    private boolean isAboveMaxValue(State state) {
        return state.calculateValue() >= optimizerParams.getMaximumValue().orElse(Double.NaN);
    }


    private boolean isStop(StepParams.Builder spb) {
        return isBelowMinValue(spb.getCurrent()) || isAboveMaxValue(spb.getCurrent()) || spb.getCurrentStep() >= spb.getMaxSteps();
    }

    private <T extends State<T>> boolean step(StepParams.Builder<T> stepParams) {
        if (isStop(stepParams)) { return false; }
        CoolingSchedule.Params csp = new CoolingSchedule.Params.Builder().mergeFrom(stepParams).build();
        double temperature = optimizerParams.getCoolingSchedule().temperature(csp);
        StepsizeFunction.Params sfp = new StepsizeFunction.Params.Builder().mergeFrom(stepParams).build();
        double stepSize = stepParams.getStepsizeFunction().stepSize(sfp);
        stepParams.setTemperature(temperature);
        stepParams.setCurrent(stepParams.getCurrent());
        stepParams.setProposed(stepParams.getCurrent().randomNeighbour(optimizerParams.getRandom(), stepSize));
        AcceptanceFunction.Params afp = new AcceptanceFunction.Params.Builder().mergeFrom(stepParams).build();
        if (afp.getDeltaEnergy() < 0) {
            stepParams.setNumberOfStepsWithoutImprovement(0);
        } else {
            stepParams.mapNumberOfStepsWithoutImprovement((i) -> i+1);
        }
        double acceptanceProbability = optimizerParams.getAcceptanceFunction().acceptProbability(afp);
        if (acceptanceProbability > optimizerParams.getRandom().nextDouble()) {
            stepParams.setCurrent(stepParams.getProposed());
            boolean isBest;
            if (stepParams.isMaximizing()) {
                isBest = stepParams.getCurrent().calculateValue() > stepParams.getBest().calculateValue();
            } else {
                isBest = stepParams.getCurrent().calculateValue() < stepParams.getBest().calculateValue();
            }
            if (isBest) {
                stepParams.setBest(stepParams.getCurrent());
            }
        }
        stepParams.mapCurrentStep(i -> i+1);
        return true;
    }

    public <T extends State<T>> Result<T> optimize(T initialState) {
        initialState = BufferedState.buffer(initialState);
        StepParams.Builder<T> spb = new StepParams.Builder<T>()
                .setInitialTemperature(optimizerParams.getInitialTemperature())
                .setMaxSteps(optimizerParams.getMaximumNumberOfSteps())
                .setCurrent(initialState)
                .setStepsizeFunction(optimizerParams.getStepsizeFunctionSupplier().get())
                .setMaximizing(optimizerParams.isMaximization())
                .setBest(initialState);
        while ( step(spb) ) {}
        return new Result.Builder<T>()
                .setTemperature(spb.getTemperature())
                .setState(spb.getBest())
                .setNumberOfSteps(spb.getCurrentStep())
                .build();
    }
}
