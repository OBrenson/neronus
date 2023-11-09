package com.boi.neronus.layers;

import com.boi.neronus.neurons.DeepNeurone;
import com.boi.neronus.neurons.Neuron;
import com.boi.neronus.neurons.NeuronsFactory;
import com.boi.neronus.neurons.Weight;
import com.boi.neronus.neurons.afunctions.ActivationFunction;
import com.boi.neronus.neurons.exceptions.NoOutputSignal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PerceptronLayer implements Layer {

    private final List<DeepNeurone> neurons;

    public PerceptronLayer(int size, ActivationFunction activationFunction, ActivationFunction inverseFunction,
                           Layer prevLayer) {
        neurons = new ArrayList<>(size);
        for(int i = 0; i < size; i++) {
            neurons.add(NeuronsFactory.createDeepNeurone(prevLayer.getNeurons(), activationFunction, inverseFunction));
        }
    }

    public List<DeepNeurone> getNeurons() {
        return neurons;
    }

    public void calcActivation() throws NoOutputSignal {
        for(DeepNeurone neurone : neurons) {
            neurone.calcActivation();
        }
    }

    public Map<Neuron, Map<Neuron, Double>> trainOutLayer(double nu, double alpha, Map<Neuron, Double> refs) throws NoOutputSignal {
        Map<Neuron, Map<Neuron, Double>> res = new HashMap<>();
        for(DeepNeurone dn : neurons) {
            Map<Neuron, Double> reCalculatedWeights = new HashMap<>();
            for(Map.Entry<Neuron, Weight> in : dn.getInputSignalsList().entrySet()) {
                double d = (dn.getSignal() - refs.get(dn)) * dn.calcInverseActivation() * in.getKey().getSignal();
                Double lastWeight = dn.getPrevWights().getOrDefault(in.getValue(), 0.0);
                double newWeight = in.getValue().getValue() - nu * d + alpha * (in.getValue().getValue() - lastWeight);
                dn.getPrevWights().put(in.getValue(), in.getValue().getValue());
                reCalculatedWeights.put(in.getKey(), newWeight);
            }
            res.put(dn, reCalculatedWeights);
        }
        return res;
    }

    public void setNewWeights(Map<Neuron, Map<Neuron, Double>> reCalculatedWeights) {
        for(DeepNeurone dn : neurons) {
            for(Neuron key : dn.getInputSignalsList().keySet()) {
                dn.getInputSignalsList().get(key).setValue(reCalculatedWeights.get(dn).get(key));
            }
        }
    }

    public  Map<Neuron, Map<Neuron, Double>> train(double nu, double alpha, Map<Neuron, Double> refs) throws NoOutputSignal {
        Map<Neuron, Map<Neuron, Double>> res = new HashMap<>();
        for(DeepNeurone neurone : neurons) {
            Map<Neuron, Double> reCalculatedWeights = new HashMap<>();
            for(Map.Entry<Neuron, Weight> in : neurone.getInputSignalsList().entrySet()) {
                double d = 0;
                for (Map.Entry<Neuron, Weight> out : neurone.getOutputSignals().entrySet()) {
                    d += (out.getKey().getSignal() - refs.get(out.getKey())) * ((DeepNeurone)out.getKey()).calcInverseActivation() *
                            out.getValue().getValue() * neurone.calcInverseActivation() * in.getKey().getSignal();
                }
                Double lastWeight = neurone.getPrevWights().getOrDefault(in.getValue(), 0.0);
                double newWeight = in.getValue().getValue() - nu * d + alpha * (in.getValue().getValue() - lastWeight);
                neurone.getPrevWights().put(in.getValue(), in.getValue().getValue());
                reCalculatedWeights.put(in.getKey(), newWeight);
            }
            res.put(neurone, reCalculatedWeights);
        }
        return res;
    }
}
