package com.boi.neronus.neurons.exceptions;

import com.boi.neronus.neurons.Neuron;

public class OutputNeurone implements Neuron {

    private double signal;

    @Override
    public double getSignal() throws NoOutputSignal {
        return signal;
    }

    public void setSignal(double signal) {
        this.signal = signal;
    }
}
