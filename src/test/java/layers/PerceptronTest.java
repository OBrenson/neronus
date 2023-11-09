package layers;

import com.boi.neronus.Model;
import com.boi.neronus.ModelConfig;
import com.boi.neronus.data.DataUtil;
import com.boi.neronus.neurons.exceptions.NoOutputSignal;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class PerceptronTest {

    private static final Map<String, Double> map = new HashMap<>();
    private static final List<String> pam = new ArrayList<>();
    static {
        map.put("Iris-virginica", 1.0);
        map.put("Iris-versicolor", 2.0);
        map.put("Iris-setosa", 3.0);

        pam.add(0, "Iris-virginica");
        pam.add(1, "Iris-versicolor");
        pam.add(2, "Iris-setosa");
    }

    @Test
    public void testPerceptron() throws NoOutputSignal {

        ModelConfig config = new ModelConfig();
        config.setData(readIrisData());
        config.setNormA(-1);
        config.setNormB(1);
        config.setTrainSize(100);
        config.setTestSize(50);
        config.setBorder(0.01);
        config.setDifBorder(0.000001);
        config.setNu(0.5);
        config.setAlpha(0.000001);
        config.setPam(pam);

        Model model = new Model();
        model.createAndExecute(config);
    }

    private double[][] readIrisData()  {
        double[][] res = new double[150][5];
        try (FileReader fr = new FileReader(AdalineLayerTest.class.getResource("/iris.data").getPath());
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
