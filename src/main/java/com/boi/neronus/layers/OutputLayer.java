package com.boi.neronus.layers;

public class OutputLayer {

    private double outputSignal;

    public OutputLayer(double outputSignal) {
        this.outputSignal = outputSignal;
    }

    public double getOutputSignal() {
        return outputSignal;
    }

    public void setOutputSignal(double outputSignal) {
        this.outputSignal = outputSignal;
    }
}
