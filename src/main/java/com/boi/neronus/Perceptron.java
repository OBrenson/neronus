package com.boi.neronus;
import com.boi.neronus.layers.Layer;
import com.boi.neronus.layers.PerceptronLayer;
import com.boi.neronus.neurons.DeepNeurone;
import com.boi.neronus.neurons.Neuron;
import com.boi.neronus.neurons.Weight;
import com.boi.neronus.neurons.exceptions.NoOutputSignal;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

public class Perceptron {

    private final LinkedList<Layer> layers;

    public Perceptron() {
        layers = new LinkedList<>();
    }

    public Perceptron addLayer(Layer newLayer) {
        if(layers.size() != 0) {
            Layer layer = layers.getLast();
            for(DeepNeurone neurone : ((PerceptronLayer)layer).getNeurons()) {
                for(DeepNeurone n : ((PerceptronLayer) newLayer).getNeurons()) {
                    Weight weight = n.getInputSignalsList().get(neurone);
                    neurone.putOutputSignal(n, weight);
                }
            }
        }
        layers.addLast(newLayer);
        return this;
    }

    public void train(double nu, double alpha, Map<Neuron, Double> refs) throws NoOutputSignal {
        for (Layer layer : layers) {
            ((PerceptronLayer) layer).calcActivation();
        }

//        Layer l = layers.getLast();
//        for(int i = 0; i < l.getNeurons().size(); i++) {
//            DeepNeurone n = ((PerceptronLayer)layers.getLast()).getNeurons().get(i);
//            n.calcInverseActivation(n.getSignal() - res[i]);
//        }

        PerceptronLayer outLayer = (PerceptronLayer) layers.getLast();
        Map<Neuron, Map<Neuron, Double>> outWeights = outLayer.trainOutLayer(nu, alpha, refs);

        PerceptronLayer hiddenLayer = (PerceptronLayer) layers.getFirst();
        if(hiddenLayer != outLayer) {
            Map<Neuron, Map<Neuron, Double>> hiddenWeights = hiddenLayer.train(nu, alpha, refs);
            hiddenLayer.setNewWeights(hiddenWeights);
        }
        outLayer.setNewWeights(outWeights);
    }

    public void printWeights() {
        for (Layer layer : layers) {
            for(DeepNeurone dn : ((PerceptronLayer)layer).getNeurons()) {
                dn.printWeights();
                System.out.println();
            }
            System.out.println("\n");
        }
        System.out.println("\n------------------\n");
    }

    public double[] feed() throws NoOutputSignal {
        Layer l = layers.getLast();
        double res[] = new double[l.getNeurons().size()];
        for (Layer layer : layers) {
            ((PerceptronLayer) layer).calcActivation();
        }
        for(int i = 0; i < res.length; i++) {
            res[i] = l.getNeurons().get(i).calcActivation();
        }
        return res;
    }
}
