package com.boi.neronus.neurons;

import com.boi.neronus.neurons.afunctions.ActivationFunction;
import com.boi.neronus.neurons.exceptions.NoOutputSignal;

import java.util.Map;
import java.util.Optional;

public abstract class BasicNeuron implements Neuron {

    private String name;

    protected ActivationFunction activationFunction;

    protected Optional<Double> outputSignal;

    // key - нейрон от которого исходит сигнал
    // value - вес данного сигнала
    protected Map<Neuron, Weight> inputSignalsList;

    public BasicNeuron(Map<Neuron, Weight> inputSignalsList, ActivationFunction activationFunction) {
        this.inputSignalsList = inputSignalsList;
        this.activationFunction = activationFunction;
    }

    public abstract double calcActivation() throws NoOutputSignal;

    public Map<Neuron, Weight> getInputSignalsList() {
        return inputSignalsList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
