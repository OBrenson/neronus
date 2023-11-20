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
import java.util.function.Function;

public class ModelTest {

    Map<String, String> wineMap = new HashMap<>();
    {
        wineMap.put("Barolo", "1");
        wineMap.put("Grignolino", "2");
        wineMap.put("Barbera", "3");
    }

    @Test
    public void testModel() throws NoOutputSignal {
        double[][] data = readWineData();

        DataUtil.shuffle(data, 10);
        ModelConfig config = new ModelConfig();
        config.setData(data);
        config.setNormA(-1);
        config.setNormB(1);
        config.setTrainSize(50);
        config.setTestSize(128);
        config.setBorder(0.001);
        config.setDifBorder(0.00001);
        config.setNu(0.3);
        config.setAlpha(0.06);
        DataUtil.normalize(data, config.getNormA(), config.getNormB());
        config.setEpochs(5);

        List<String> pam = new ArrayList<>();
        for(int i = 0; i < 3; i++) {
            pam.add(i, Integer.toString(i + 1));
        }
        config.setPam(pam);

        Model model = new Model();
        System.out.println(model.createAndExecute(config, true));
    }

    @Test
    public void testByConveyor() throws NoOutputSignal {
        List<String> pam = new ArrayList<>();
        for(int i = 0; i < 3; i++) {
            pam.add(i, Integer.toString(i + 1));
        }
        double[][] data = readWineData();
        DataUtil.shuffle(data, (int)new Date().getTime());

        DataUtil.save(Model.PATH + "prepData", data);
        ModelConfig config = new ModelConfig();
        config.setNormA(-1);
        config.setNormB(1);
        config.setData(data);
        DataUtil.normalize(data, config.getNormA(), config.getNormB());
        config.setPam(pam);

        config.setTrainSize(50);
        config.setTestSize(128);
        config.setBorder(0.001);
        config.setDifBorder(0.00001);

        double borderDelta = 0.1;
        double difBorderDelta = 0.000001;
        double nuDelta = 0.1;
        double alphaDelta = 0.05;


        double bestNu = 0;
        double bestAlpha = 0;
        double bestEpochs = 0;
        double res = 1;
        for(int epochs = 1; epochs < 6; epochs++) {
            for(double nu = 0.1; nu <= 0.9; nu += nuDelta) {
                for (double alpha = 0.01; alpha <= 0.1; alpha += alphaDelta) {
                    config.setNu(nu);
                    config.setAlpha(alpha);
                    config.setEpochs(epochs);

                    Model model = new Model();
                    double r = model.createAndExecute(config, false);
                    if(r < res) {
                        res = r;
                        bestNu = nu;
                        bestAlpha = alpha;
                        bestEpochs = epochs;
//                        model.save();
                    }

//                    System.out.printf("\nNU: %f\nALPHA: %f\nEPOCHS: %d\n", nu, alpha, epochs);
                }
            }
        }

        System.out.println("\n" + bestNu + " " + bestAlpha + " " + bestEpochs + " " + res);
    }

    private double[][] readMnistData() {
        double[][] res = new double[70000][785];
        readCsv(res, 0, ModelTest.class.getResource("/mnist_train.csv").getPath(),new int[]{}, null);
        readCsv(res, 60000, ModelTest.class.getResource("/mnist_test.csv").getPath(),new int[]{}, null);
        return res;
    }

    private double[][] readWineData() {
        double[][] res = new double[178][15];
        Function<String, String>[] mappers = new Function[]{l -> wineMap.get((String)l)};
        readCsv(res, 0, ModelTest.class.getResource("/wine.csv").getPath(), new int[]{1}, mappers);
        return res;
    }

    private void readCsv(double[][] data, int start, String fileName, int[] strColumns, Function<String, String>[] mappers) {
        int index = start;
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] lines = line.split(",");
                for(int i = 0; i < strColumns.length; i++) {
                    lines[strColumns[i]] = "0";
                }
                double[] values = Arrays.stream(lines).mapToDouble(Double::parseDouble).toArray();
                data[index] = values;
                index++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
