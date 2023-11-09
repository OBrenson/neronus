package com.boi.neronus.neurons;

import com.boi.neronus.neurons.afunctions.ActivationFunction;

import java.util.*;

public class NeuronsFactory {

//    (int)new Date().getTime()
    private static final Random random = new Random();

    public static <T extends Neuron> AdalineNeurone createAdalineNeurone(List<T> inputNeurons, double basisWeight, String name) {
        Map<Neuron, Weight> initialWeights = initWeights(inputNeurons, -1, 1, (int)new Date().getTime());
        AdalineNeurone adalineNeurone = new AdalineNeurone(initialWeights, basisWeight);
        adalineNeurone.setName(name);
        return adalineNeurone;
    }

    public static <T extends Neuron> DeepNeurone createDeepNeurone(List<T> inputNeurons,
                                                                   ActivationFunction activationFunction,
                                                                   ActivationFunction inverseFunction) {
        Map<Neuron, Weight> initialWeights = initWeights(inputNeurons, -1, 1, (int)new Date().getTime());
        return new DeepNeurone(initialWeights, activationFunction, inverseFunction);
    }

    public static InputNeuron createInputNeurone(double val) {
        return new InputNeuron(val);
    }

    private static <T extends Neuron> Map<Neuron, Weight> initWeights(List<T> inputs, double a, double b, int seed) {

        Map<Neuron, Weight> res = new HashMap<>();
        for(Neuron neuron : inputs) {
            double w = a + random.nextDouble() * (b - a);
            res.put(neuron, new Weight(w));
//            System.out.println(w);
        }
        return res;
    }
}
