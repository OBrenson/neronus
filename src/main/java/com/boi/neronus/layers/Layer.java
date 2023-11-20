package com.boi.neronus.layers;

import com.boi.neronus.neurons.BasicNeuron;
import com.boi.neronus.neurons.Neuron;
import com.boi.neronus.neurons.Weight;
import com.boi.neronus.neurons.exceptions.NoOutputSignal;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface Layer {

    <T extends BasicNeuron>List<T> getNeurons();

    String getName();

    default void load(String path) {
        try(BufferedReader br = new BufferedReader(new FileReader(path))) {
            Map<String, List<Double>> doubleMap = new HashMap<>();
            String line;
            while ((line = br.readLine()) != null) {
                List<Double> doubles = Arrays.stream(line.split(",")).map(Double::parseDouble).collect(Collectors.toList());
                doubleMap.put(Integer.toString(doubles.get(0).intValue()), doubles.subList(1, doubles.size()));
            }

            for(BasicNeuron n : getNeurons()) {
                Map<String, Weight> weights = new HashMap<>();
                for (Map.Entry<Neuron, Weight> ne : n.getInputSignalsList().entrySet()) {
                    weights.put(ne.getKey().getName(), ne.getValue());
                }
                List<Double> doubles = doubleMap.get(n.getName());
                for(int i = 0; i < weights.size(); i++) {
                    weights.get(Integer.toString(i)).setValue(doubles.get(i));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
