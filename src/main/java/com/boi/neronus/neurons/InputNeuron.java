package com.boi.neronus.neurons;

import java.util.List;

public class InputNeuron implements Neuron{

    private double outputSignal;

    public InputNeuron() {
    }

    public InputNeuron(double outputSignal) {
        this.outputSignal = outputSignal;
    }

    @Override
    public double getSignal() {
        return outputSignal;
    }

    @Override
    public void setSignal(double signal) {
        this.outputSignal = signal;
    }

    public void setOutputSignal(double outputSignal) {
        this.outputSignal = outputSignal;
    }

    public static void setSignals(List<InputNeuron> inputs, double [] data) {
        for(int i = 0; i < inputs.size(); i++) {
            inputs.get(i).setOutputSignal(data[i]);
        }
    }
}
