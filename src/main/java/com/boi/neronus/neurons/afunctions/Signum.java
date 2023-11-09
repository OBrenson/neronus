package com.boi.neronus.neurons.afunctions;

public class Signum implements ActivationFunction{

    @Override
    public double calc(double num) {
        return num >= 0 ? 1.0 : -1.0;
    }
}
