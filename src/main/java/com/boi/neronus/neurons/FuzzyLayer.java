package com.boi.neronus.neurons;

import com.boi.neronus.layers.Layer;
import com.boi.neronus.neurons.exceptions.NoOutputSignal;

import java.util.*;

public class FuzzyLayer implements Layer {

    private final List<FuzzyNeurone> neurones = new ArrayList<>();

    private final  double m;

    private double lastError = 0;

    private String name;

    public FuzzyLayer(int neuronsNum, Layer prevLayer, double m, String name) {
        this.m = m;
        int size = neuronsNum * prevLayer.getNeurons().size();
        Double[] initC = new Double[size];
        double c = 1.0 / (double) size;
        for(int i = 0; i < size; i++) {
            initC[i] = c;
        }
        Random random = new Random((int)new Date().getTime());
        int border = size % 2 == 0 ? size : size - 1;
        double s = 0;
        for(int i = 0; i < border; i++) {
            if(i % 2 == 0) {
                s = random.nextDouble() * (c / 2);
                initC[i] += s;
            } else {
                initC[i] -= s;
            }
        }
        List<Double> list = Arrays.asList(initC);
        Collections.shuffle(list);
        list.toArray(initC);
        for(int i = 0; i < neuronsNum; i++) {
            Map<Neuron, Weight> inputSignals = new HashMap<>();
            for(int j = neuronsNum * i; j < neuronsNum + neuronsNum * i; j++) {
                inputSignals.put(prevLayer.getNeurons().get(j - neuronsNum * i), new Weight(initC[j]));
            }
            FuzzyNeurone fn = new FuzzyNeurone(inputSignals, null, m);
            fn.setName(Integer.toString(i));
            neurones.add(fn);
        }

        this.name = name;
    }

    public void feed() throws NoOutputSignal {
        for (FuzzyNeurone neurone : neurones) {
            neurone.calcActivation();
        }
    }

    public void train(double border, double difBorder) throws NoOutputSignal {
        while(true) {
            for (FuzzyNeurone neurone : neurones) {
                neurone.calcActivation();
            }

            double error = 0;
            for (FuzzyNeurone neurone : neurones) {
                for (Map.Entry<Neuron, Weight> in : neurone.inputSignalsList.entrySet()) {
                    error += Math.pow(in.getValue().getValue(), m) *
                            Math.pow(neurone.getSignal() - in.getKey().getSignal(), 2.0);
                }
            }
            if (error <= border || difBorder >= (error - lastError)) {
                return;
            }
            lastError = error;
            for (FuzzyNeurone neurone : neurones) {
                double sum = 0.0;
                for (Map.Entry<Neuron, Weight> in : neurone.inputSignalsList.entrySet()) {
                    for (FuzzyNeurone n : neurones) {
                        sum += Math.pow(Math.pow(neurone.getSignal() - in.getKey().getSignal(), 2) /
                                Math.pow(n.getSignal() - in.getKey().getSignal(), 2), 1.0 / (m - 1));
                    }
                    in.getValue().setValue(1.0 / sum);
                }
            }
        }
    }

    @Override
    public List<FuzzyNeurone> getNeurons() {
        return neurones;
    }

    public String getName() {
        return name;
    }
}
