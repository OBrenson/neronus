package layers;

import com.boi.neronus.data.DataUtil;
import com.boi.neronus.layers.AdalineLayer;
import com.boi.neronus.neurons.AdalineNeurone;
import com.boi.neronus.neurons.InputNeuron;
import com.boi.neronus.neurons.NeuronsFactory;
import com.boi.neronus.neurons.exceptions.NoOutputSignal;
import org.junit.Test;

import java.io.*;
import java.util.*;
import java.util.stream.IntStream;

public class AdalineLayerTest {

    private static final Map<String, Double> map = new HashMap<>();
    private static final Map<Double, String> pam = new HashMap<>();
    static {
        map.put("Iris-virginica", 1.0);
        map.put("Iris-versicolor", 2.0);
        map.put("Iris-setosa", 3.0);

        pam.put(1.0, "Iris-virginica");
        pam.put(2.0, "Iris-versicolor");
        pam.put(3.0, "Iris-setosa");
    }

    @Test
    public void testAdalineWithIris() throws NoOutputSignal {
        double[][] data = readIrisData();
        DataUtil.normalize(data, -1, 1);

        int trainSize = 75;
        int testSize = 75;
        double[][] train = new double[trainSize][];
        double[][] test = new double[testSize][];

        System.arraycopy(data, 0, train, 0, trainSize);
        System.arraycopy(data, trainSize, test, 0, testSize);

        List<InputNeuron> inputs = new ArrayList<>(4);
        IntStream.range(0, 4).forEach(i -> inputs.add(new InputNeuron()));

        double basisWeight = 0.000001;
        AdalineNeurone virginica = NeuronsFactory.createAdalineNeurone(inputs, basisWeight, "Iris-virginica");
        AdalineNeurone versicolor = NeuronsFactory.createAdalineNeurone(inputs, basisWeight, "Iris-versicolor");
        AdalineNeurone setosa = NeuronsFactory.createAdalineNeurone(inputs, basisWeight, "Iris-setosa");

        List<AdalineNeurone> neuronesList = new ArrayList<>();
        neuronesList.add(virginica);
        neuronesList.add(versicolor);
        neuronesList.add(setosa);

        AdalineLayer adalineLayer = new AdalineLayer(neuronesList, 0.2);
        for (double[] datum : train) {
            InputNeuron.setSignals(inputs, datum);

            adalineLayer.train(pam.get(datum[4]));
        }
        int counter = 0;
        for (double[] datum : test) {
            InputNeuron.setSignals(inputs, datum);
            Map<String, Double> res = adalineLayer.feed();
            double max = -1;
            String winner = "";
            for (Map.Entry<String, Double> entry : res.entrySet()) {
                if (entry.getValue() > max) {
                    max = entry.getValue();
                    winner = entry.getKey();
                }
                System.out.printf("%s : %f\n", entry.getKey(), entry.getValue());
            }
            System.out.println();
            if (winner.equals(pam.get(datum[4]))) {
                counter++;
            }
        }
        System.out.printf("Result: %d / %d; %f ", counter, test.length, (double)counter/(double) test.length);
    }

    private double[][] readIrisData()  {
        double[][] res = new double[150][5];
        try ( FileReader fr = new FileReader(AdalineLayerTest.class.getResource("/iris.data").getPath());
                BufferedReader br = new BufferedReader(fr)){
            String line;
            int i = 0;
            while ((line = br.readLine()) != null) {
                res[i] = convertIrisLineToDouble(line);
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return DataUtil.shuffle(res, 42);
    }

    private static double[] convertIrisLineToDouble(String line) {
        String[] raw = line.split(",");
        double[] res = new double[5];
        for(int i = 0; i < 4; i++) {
            res[i] = Double.parseDouble(raw[i]);
        }
        res[4] = map.get(raw[4]);
        return res;
    }
}
