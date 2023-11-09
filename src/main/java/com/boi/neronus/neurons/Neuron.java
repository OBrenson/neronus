package com.boi.neronus.neurons;

import com.boi.neronus.neurons.exceptions.NoOutputSignal;

public interface Neuron {

    double getSignal() throws NoOutputSignal;

    void setSignal(double signal);
}
