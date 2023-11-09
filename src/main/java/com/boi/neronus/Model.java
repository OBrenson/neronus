package com.boi.neronus;

import com.boi.neronus.data.DataUtil;
import com.boi.neronus.layers.InputLayer;
import com.boi.neronus.layers.PerceptronLayer;
import com.boi.neronus.neurons.FuzzyLayer;
import com.boi.neronus.neurons.Neuron;
import com.boi.neronus.neurons.afunctions.InverseRelu;
import com.boi.neronus.neurons.afunctions.Relu;
import com.boi.neronus.neurons.exceptions.NoOutputSignal;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Model {

    public void createAndExecute(ModelConfig config) throws NoOutputSignal {

        double[][] data = config.getData();
        DataUtil.normalize(data, config.getNormA(), config.getNormB());

        int trainSize = config.getTrainSize();
        int testSize = config.getTestSize();
        double[][] train = new double[trainSize][];
        double[][] test = new double[testSize][];

        System.arraycopy(data, 0, train, 0, trainSize);
        System.arraycopy(data, trainSize, test, 0, testSize);


        InputLayer inputLayer = new InputLayer(train[0], 784);
        Perceptron perceptron = new Perceptron();

        FuzzyLayer fuzzyLayer = new FuzzyLayer(784, inputLayer, 2);

        PerceptronLayer firstLayer = new PerceptronLayer(64, new Relu(), new InverseRelu(), fuzzyLayer);
        PerceptronLayer secondLayer = new PerceptronLayer(10, new Relu(), new InverseRelu(), firstLayer);
        perceptron.addLayer(firstLayer);
        perceptron.addLayer(secondLayer);

        Map<Neuron, Double> refs = new HashMap<>();
        for(int i = 0; i < 10; i++) {
            refs.put(secondLayer.getNeurons().get(i), config.getNormA());
            secondLayer.getNeurons().get(i).setName(Integer.toString(i));
        }

        for(int i = 0; i < 1; i++) {
            int index = 0;
            for (double[] bucket : train) {
                inputLayer.setSignals( Arrays.copyOfRange(bucket, 1, bucket.length));
                refs.put(secondLayer.getNeurons().get((int)bucket[0]), 1.0);
                fuzzyLayer.train(config.getBorder(), config.getDifBorder());
                perceptron.train(config.getNu(), config.getAlpha(), refs);
                refs.put(secondLayer.getNeurons().get((int)bucket[0]), config.getNormA());
//                System.out.printf("Data row: %d", index);
//                index++;
            }
        }

        int counter = 0;
        int[] resCounter = {0,0,0,0,0,0,0,0,0,0,0};
        for(double[] bucket : test) {
            inputLayer.setSignals( Arrays.copyOfRange(bucket, 1, bucket.length));
            fuzzyLayer.feed();
            double[] res = perceptron.feed();
            double max = -1000000;
            int maxI = -1;
            String winner = null;
            for (int i = 0; i < res.length; i++) {
                if (res[i] > max) {
                    max = res[i];
                    maxI = i;
                    winner = config.getPam().get(i);
                }
            }
            if(maxI < 0) {
                resCounter[10]++;
            } else {
                resCounter[maxI]++;
            }
            if (winner.equals(config.getPam().get((int)bucket[0]))) {
                counter++;
            }
        }
        System.out.println(Arrays.toString(resCounter));
        System.out.printf("Result: %d / %d; %f ", counter, test.length, (double)counter/(double) test.length);
    }
}
