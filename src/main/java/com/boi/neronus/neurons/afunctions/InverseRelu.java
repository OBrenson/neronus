package com.boi.neronus.neurons.afunctions;

public class InverseRelu implements ActivationFunction {

    @Override
    public double calc(double num) {
        return num > 0 ? 1 : 0;
    }
}
