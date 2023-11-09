package com.boi.neronus.layers;

import com.boi.neronus.neurons.AdalineNeurone;
import com.boi.neronus.neurons.Neuron;
import com.boi.neronus.neurons.Weight;
import com.boi.neronus.neurons.exceptions.NoOutputSignal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdalineLayer implements Layer {

    private List<AdalineNeurone> neurons;

    private Double nu;

    public AdalineLayer(List<AdalineNeurone> neurons, Double nu) {
        this.neurons = neurons;
        this.nu = nu;
    }

    public void train(String winnerName) throws NoOutputSignal {
        for (AdalineNeurone neuron : neurons) {
            double trainResult = 0;
            if(neuron.getName().equals(winnerName)) {
                trainResult = 1.0;
            }
            double res = neuron.calcActivation();
            for(Map.Entry<Neuron, Weight> entry : neuron.getInputSignalsList().entrySet()) {
                double weight = entry.getValue().getValue();
                weight += nu * entry.getKey().getSignal() * (trainResult - res);
                neuron.getInputSignalsList().put(entry.getKey(), new Weight(weight));
            }
        }
    }

    public Map<String, Double> feed() throws NoOutputSignal {
        Map<String, Double> res = new HashMap<>();
        for (AdalineNeurone neuron : neurons) {
            neuron.calcActivation();
            res.put(neuron.getName(), neuron.getSignal());
        }
        return res;
    }

    public List<AdalineNeurone> getNeurons() {
        return neurons;
    }
}
