package layers;

import com.boi.neronus.Model;
import com.boi.neronus.ModelConfig;
import com.boi.neronus.data.DataUtil;
import com.boi.neronus.neurons.exceptions.NoOutputSignal;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModelTest {

    @Test
    public void testModel() throws NoOutputSignal {
        double[][] data = readMnistData();

        ModelConfig config = new ModelConfig();
        config.setData(data);
        config.setNormA(0);
        config.setNormB(1);
        config.setTrainSize(60000);
        config.setTestSize(10000);
        config.setBorder(0.001);
        config.setDifBorder(0.001);
        config.setNu(0.1);
        config.setAlpha(0.000001);
        DataUtil.shuffle(data, 10);

        List<String> pam = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            pam.add(i, Integer.toString(i));
        }
        config.setPam(pam);

        Model model = new Model();
        model.createAndExecute(config);
    }

    private double[][] readMnistData() {
        double[][] res = new double[70000][785];
        readCsv(res, 0, ModelTest.class.getResource("/mnist_train.csv").getPath());
        readCsv(res, 60000, ModelTest.class.getResource("/mnist_test.csv").getPath());
        return res;
    }

    private void readCsv(double[][] data, int start, String fileName) {
        double digit;
        int index = start;
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                double[] values = Arrays.stream(line.split(",")).mapToDouble(Double::parseDouble).toArray();
                data[index] = values;
                index++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
