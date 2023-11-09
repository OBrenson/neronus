package com.boi.neronus.layers;

import com.boi.neronus.neurons.BasicNeuron;
import com.boi.neronus.neurons.InputNeuron;
import com.boi.neronus.neurons.Neuron;
import com.boi.neronus.neurons.NeuronsFactory;

import java.util.ArrayList;
import java.util.List;

public class InputLayer implements Layer {

    private final List<InputNeuron> neurons;

    public InputLayer(double[] inputs, int num) {
        neurons = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            neurons.add(NeuronsFactory.createInputNeurone(inputs[i]));
        }
    }

    public void setSignals(double[] data) {
        for(int i = 0; i < data.length; i++) {
            neurons.get(i).setSignal(data[i]);
        }
    }

    @Override
    public List<InputNeuron> getNeurons() {
        return neurons;
    }
}
