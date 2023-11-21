package com.boi.neronus.neurons;

import java.util.List;

public class InputNeuron implements Neuron{

    private double outputSignal;

    private String name;

    public InputNeuron() {
    }

    public InputNeuron(double outputSignal) {
        this.outputSignal = outputSignal;
    }

    public InputNeuron(double outputSignal, String name) {
        this.outputSignal = outputSignal;
        this.name = name;
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

    public double getOutputSignal() {
        return outputSignal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
