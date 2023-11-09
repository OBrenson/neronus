package com.boi.neronus.neurons;

import com.boi.neronus.neurons.afunctions.ActivationFunction;
import com.boi.neronus.neurons.exceptions.NoOutputSignal;

import java.util.Map;
import java.util.Optional;

public class FuzzyNeurone extends BasicNeuron {

    private final double m;

    public FuzzyNeurone(Map<Neuron, Weight> inputSignalsList, ActivationFunction activationFunction, double m) {
        super(inputSignalsList, activationFunction);
        this.m = m;
    }

    @Override
    public double calcActivation() throws NoOutputSignal {
        double up = 0;
        double down = 0;
        for(Map.Entry<Neuron, Weight> entry : inputSignalsList.entrySet()) {
            up += Math.pow(entry.getValue().getValue(), m) * entry.getKey().getSignal();
            down += Math.pow(entry.getValue().getValue(), m);
        }
        double c = up / down;
        outputSignal = Optional.of(c);
        return c;
    }

    @Override
    public double getSignal() throws NoOutputSignal {
        return outputSignal.orElseThrow(NoOutputSignal::new);
    }

    @Override
    public void setSignal(double signal) {

    }
}
