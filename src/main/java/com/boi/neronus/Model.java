package com.boi.neronus;

import com.boi.neronus.data.DataUtil;
import com.boi.neronus.layers.InputLayer;
import com.boi.neronus.layers.Layer;
import com.boi.neronus.layers.PerceptronLayer;
import com.boi.neronus.neurons.BasicNeuron;
import com.boi.neronus.neurons.FuzzyLayer;
import com.boi.neronus.neurons.Neuron;
import com.boi.neronus.neurons.Weight;
import com.boi.neronus.neurons.afunctions.InverseRelu;
import com.boi.neronus.neurons.afunctions.Relu;
import com.boi.neronus.neurons.exceptions.NoOutputSignal;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

public class Model {

    public static final String PATH = "/Users/19722883-mobile/Desktop/neronus/src/main/resources/";

    Map<String,Map<String, List<Double>>> history = new HashMap<>();

    public double createAndExecute(ModelConfig config, boolean isLoad, double[] rmse) throws NoOutputSignal {

        double[][] data = config.getData();

        int trainSize = config.getTrainSize();
        int testSize = config.getTestSize();
        double[][] train = new double[trainSize][];
        double[][] test = new double[testSize][];

        System.arraycopy(data, 0, train, 0, trainSize);
        System.arraycopy(data, trainSize, test, 0, testSize);


        InputLayer inputLayer = new InputLayer(train[0], 14);
        Perceptron perceptron = new Perceptron();

        int fSize = config.getFuzzySize();
        if(fSize == 0) {
            fSize = 14;
        }
        FuzzyLayer fuzzyLayer = new FuzzyLayer(fSize, inputLayer, 2, "0");

        int pSize = config.getPerceptronSize();
        if(pSize == 0) {
            pSize = 7;
        }
        PerceptronLayer firstLayer = new PerceptronLayer(pSize, new Relu(), new InverseRelu(), fuzzyLayer, "1");
        PerceptronLayer secondLayer = new PerceptronLayer(3, new Relu(), new InverseRelu(), firstLayer, "2");
        perceptron.addLayer(firstLayer);
        perceptron.addLayer(secondLayer);

        history.put(fuzzyLayer.getName(), createHistory(fuzzyLayer));
        history.put(firstLayer.getName(), createHistory(firstLayer));
        history.put(secondLayer.getName(), createHistory(secondLayer));

        if(isLoad) {
            fuzzyLayer.load(PATH + fuzzyLayer.getName());
            firstLayer.load(PATH + firstLayer.getName());
            secondLayer.load(PATH + secondLayer.getName());
        }

        Map<Neuron, Double> refs = new HashMap<>();
        for(int i = 0; i < secondLayer.getNeurons().size(); i++) {
            refs.put(secondLayer.getNeurons().get(i), config.getNormA());
//            secondLayer.getNeurons().get(i).setName(Integer.toString(i));
        }
        fuzzyLayer.train(config.getBorder(), config.getDifBorder());
        for(int i = 0; i < config.getEpochs(); i++) {
            for (double[] bucket : train) {
                inputLayer.setSignals( Arrays.copyOfRange(bucket, 1, bucket.length));
                refs.put(secondLayer.getNeurons().get((int)bucket[0] - 1), 1.0);
                fuzzyLayer.train(config.getBorder(), config.getDifBorder());
                perceptron.train(config.getNu(), config.getAlpha(), refs);
                refs.put(secondLayer.getNeurons().get((int)bucket[0] - 1), config.getNormA());
//                System.out.printf("Data row: %d", index);
//                index++;
            }
        }

        int counter = 0;
        double[] resCounter = {0,0,0};
        double[] example = {0,0,0};

        double sum = 0;
        for(double[] bucket : train) {
            resCounter = new double[]{0,0,0};
            example = new double[]{0,0,0};

            inputLayer.setSignals( Arrays.copyOfRange(bucket, 1, bucket.length));
            fuzzyLayer.feed();
            double[] res = perceptron.feed();

            example[(int)bucket[0] - 1] = 1;

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
            resCounter[maxI] = 1;
            normalize(res);
            for(int i = 0; i < secondLayer.getNeurons().size(); i++) {
                sum += Math.sqrt(Math.pow(res[i] - example[i], 2));
            }

            if (winner.equals(config.getPam().get((int)bucket[0] - 1))) {
                counter++;
            }
        }

        double trainRes = sum / (train.length * secondLayer.getNeurons().size() - 1.0);

        System.out.printf("\nTrain result: %d / %d;", counter, train.length);

        rmse[0] = trainRes;
        counter = 0;
        sum = 0;
        for(double[] bucket : test) {
            resCounter = new double[]{0,0,0};
            example = new double[]{0,0,0};

            inputLayer.setSignals( Arrays.copyOfRange(bucket, 1, bucket.length));
            fuzzyLayer.feed();
            double[] res = perceptron.feed();

            example[(int)bucket[0] - 1] = 1;

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
            resCounter[maxI] = 1;
            normalize(res);
            for(int i = 0; i < secondLayer.getNeurons().size(); i++) {
                sum += Math.sqrt(Math.pow(res[i] - example[i], 2));
            }

            if (winner.equals(config.getPam().get((int)bucket[0] - 1))) {
                counter++;
            }
        }

        double testRes = sum / (test.length * secondLayer.getNeurons().size() - 1.0);
        System.out.printf("\nTest result: %d / %d;", counter, test.length);

        rmse[1] = testRes;
        return testRes;
    }

    public void save() {
        for(Map.Entry<String, Map<String, List<Double>>> entry : history.entrySet()) {
            try (BufferedWriter fw = new BufferedWriter(new FileWriter(
                    PATH + entry.getKey()))) {
                for(int i = 0 ; i < entry.getValue().size(); i++) {
                    fw.write(i + ",");
                    for(double d : entry.getValue().get(Integer.toString(i))) {
                        fw.write(d + ",");
                    }
                    fw.write("\n");
                    fw.flush();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public Map<String, List<Double>> createHistory(Layer layer) {
        List<BasicNeuron> neurons = layer.getNeurons();
        Map<String, List<Double>> res = new HashMap<>();
        for(BasicNeuron n : neurons) {
            Map<String, Weight> names = new HashMap<>();
            for(Map.Entry<Neuron, Weight> entry : n.getInputSignalsList().entrySet()) {
                names.put((entry.getKey()).getName(), entry.getValue());
            }
            List<Double> list = new ArrayList<>();
            for(int i =0; i < names.size(); i++) {
                list.add(names.get(Integer.toString(i)).getValue());
            }
            res.put(n.getName(), list);
        }
        return res;
    }

    public void normalize(double[] data) {

        double min = 10;
        double max = -10;
        for(int i = 0; i < data.length; i++) {
            if(data[i] > max) {
                max = data[i];
            }
            if(data[i] < min) {
                min = data[i];
            }
        }
        for(int i = 0; i < data.length; i++) {
            if(max - min == 0) {
                data[i] = 0;
            } else {
                data[i] = 0 + (((data[i] - min) / (max - min)) * (1 - 0));
            }
        }
    }
}
