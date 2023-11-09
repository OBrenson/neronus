package com.boi.neronus.neurons.afunctions;

public class Relu implements ActivationFunction{
    @Override
    public double calc(double num) {
        return Math.max(0, num);
    }
}
