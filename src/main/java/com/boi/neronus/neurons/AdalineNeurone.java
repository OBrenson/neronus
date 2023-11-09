package com.boi.neronus.neurons;

import com.boi.neronus.neurons.afunctions.Signum;
import com.boi.neronus.neurons.exceptions.NoOutputSignal;

import java.util.Map;
import java.util.Optional;

public class AdalineNeurone extends BasicNeuron {

    InputNeuron basis = new InputNeuron();

    public AdalineNeurone(Map<Neuron, Weight> inputSignalsList, double basisWeight) {
        super(inputSignalsList, new Signum());
        basis = new InputNeuron(basisWeight);
    }

    @Override
    public double calcActivation() throws NoOutputSignal {
        double sum = 0;
        for (Map.Entry<Neuron, Weight> entry : inputSignalsList.entrySet()) {
            sum += entry.getValue().getValue() * entry.getKey().getSignal();
        }
        sum += basis.getSignal();
        outputSignal = Optional.of(sum);
        return sum;
    }

    @Override
    public double getSignal() throws NoOutputSignal {
        return outputSignal.orElseThrow(NoOutputSignal::new);
    }

    @Override
    public void setSignal(double signal) {

    }
}
