package com.boi.neronus.layers;

import com.boi.neronus.neurons.BasicNeuron;
import com.boi.neronus.neurons.exceptions.NoOutputSignal;

import java.util.List;

public interface Layer {

    <T extends BasicNeuron>List<T> getNeurons();
}
