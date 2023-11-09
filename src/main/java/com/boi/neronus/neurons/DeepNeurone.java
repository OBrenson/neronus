package com.boi.neronus.neurons;

import com.boi.neronus.neurons.afunctions.ActivationFunction;
import com.boi.neronus.neurons.exceptions.NoOutputSignal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DeepNeurone extends BasicNeuron {

    private final ActivationFunction inverseFunction;

    private final Map<Weight, Double> prevWights = new HashMap<>();
    private final Map<Neuron, Weight> outputSignals = new HashMap<>();
    private final Map<Neuron, Weight> weightsStorage = new HashMap<>();

    public DeepNeurone(Map<Neuron, Weight> inputSignalsList, ActivationFunction activationFunction,
                       ActivationFunction inverseFunction) {
        super(inputSignalsList, activationFunction);
        this.inverseFunction = inverseFunction;
    }

    @Override
    public double calcActivation() throws NoOutputSignal {
        double sum = 0;
        for (Map.Entry<Neuron, Weight> entry : inputSignalsList.entrySet()) {
            sum += entry.getValue().getValue() * entry.getKey().getSignal();
        }
        outputSignal = Optional.of(activationFunction.calc(sum));
        return sum;
    }

    public double calcInverseActivation() throws NoOutputSignal {
        double sum = 0;
        for (Map.Entry<Neuron, Weight> entry : inputSignalsList.entrySet()) {
            sum += entry.getValue().getValue() * entry.getKey().getSignal();
        }
        return inverseFunction.calc(sum);
    }

    public void calcInverseActivation(double value) throws NoOutputSignal {
        outputSignal = Optional.of(inverseFunction.calc(value));
    }

    @Override
    public double getSignal() throws NoOutputSignal {
        return outputSignal.orElseThrow(NoOutputSignal::new);
    }

    @Override
    public void setSignal(double signal) {
        this.outputSignal = Optional.of(signal);
    }

    public Map<Weight, Double> getPrevWights() {
        return prevWights;
    }

    public Map<Neuron, Weight> getWeightsStorage() {
        return weightsStorage;
    }

    public Map<Neuron, Weight> getOutputSignals() {
        return outputSignals;
    }

    public void putOutputSignal(Neuron n, Weight w) {
        outputSignals.put(n, w);
    }

    public void printWeights() {
        for (Map.Entry<Neuron, Weight> entry : inputSignalsList.entrySet()) {
            System.out.format("%.5f ",entry.getValue().getValue());
        }
    }
}
