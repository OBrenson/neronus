package layers;

import com.boi.neronus.Model;
import com.boi.neronus.ModelConfig;
import com.boi.neronus.data.DataUtil;
import com.boi.neronus.neurons.exceptions.NoOutputSignal;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.ChartUtilities;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
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

    Map<String, String> tMap = new HashMap<>();
    {
        tMap.put("malware", "1");
        tMap.put("goodware", "0");
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
        System.out.println(model.createAndExecute(config, true, new double[]{0,0}));
    }

    @Test
    public void testByConveyor() throws NoOutputSignal {
        List<String> pam = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            pam.add(i, Integer.toString(i + 1));
        }
        double[][] data = readWineData();
        DataUtil.shuffle(data, (int) new Date().getTime());

        ModelConfig config = new ModelConfig();
        config.setNormA(-1);
        config.setNormB(1);
        config.setData(data);
        DataUtil.normalize(data, config.getNormA(), config.getNormB());
        config.setPam(pam);

//        DataUtil.save(Model.PATH + "prepData", data);

        config.setTrainSize(50);
        config.setTestSize(128);
        config.setBorder(0.00001);
        config.setDifBorder(0.00001);

        double borderDelta = 0.1;
        double difBorderDelta = 0.000001;
        double nuDelta = 0.1;
        double alphaDelta = 0.07;


        double bestNu = 0;
        double bestAlpha = 0;
        double bestEpochs = 0;
        double res = 1;
        int bestP = 1;
        int bestF = 1;
        for (int epochs = 1; epochs <= 10; epochs++) {
            for (double nu = 0.1; nu <= 0.9; nu += nuDelta) {
                for (double alpha = 0.01; alpha <= 0.1; alpha += alphaDelta) {
                    config.setNu(nu);
                    config.setAlpha(alpha);
                    config.setEpochs(epochs);

                    Model model = new Model();
                    double r = model.createAndExecute(config, false, new double[]{0, 0});
                    if (r < res) {
                        res = r;
                        bestNu = nu;
                        bestAlpha = alpha;
                        bestEpochs = epochs;
//                        model.save();
                    }

                    System.out.printf("\nNU: %f\nALPHA: %f\nEPOCHS: %d\n", nu, alpha, epochs);
                }
            }
        }


        System.out.println("\n" + bestNu + " " + bestAlpha + " " + bestEpochs + " " + bestP + " " + bestF + " " + res);
    }

    @Test
    public void testTrainNu() throws NoOutputSignal, IOException {
        List<String> pam = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            pam.add(i, Integer.toString(i + 1));
        }
        double[][] data = readWineData();
        DataUtil.shuffle(data, (int)new Date().getTime());
        DataUtil.normalize(data, -1, 1);
        ModelConfig config = new ModelConfig();
        config.setNormA(-1);
        config.setNormB(1);
        config.setData(data);
        config.setPam(pam);

        config.setTrainSize(50);
        config.setTestSize(128);
        config.setBorder(0.00001);
        config.setDifBorder(0.00001);

        double nuDelta = 0.01;


        config.setAlpha(0.01);
        config.setEpochs(8);
        List<Double> x = new ArrayList<>();
        List<Double> y = new ArrayList<>();
        for (double nu = 0.01; nu <= 0.99; nu += nuDelta) {
            config.setNu(nu);

            Model model = new Model();

            double[] res = {0.0, 0.0};
            double r = model.createAndExecute(config, true, res);
            x.add(nu);
            y.add(res[0]);
        }
        draw(x, y, "Nu", "Train Loss", "зависимость погрешности обучения от значений коэффициента обучения", "trainNu", "погрешность");
        int a = 0;
    }

    @Test
    public void testTestFuzzy() throws NoOutputSignal, IOException {
        List<String> pam = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            pam.add(i, Integer.toString(i + 1));
        }
//        double[][] data = readReadyData();
        double[][] data = readWineData();

        DataUtil.shuffle(data, 10);
        DataUtil.normalize(data, -1, 1);

        ModelConfig config = new ModelConfig();
        config.setNormA(-1);
        config.setNormB(1);
        config.setData(data);
        config.setPam(pam);

        config.setTrainSize(50);
        config.setTestSize(128);
        config.setBorder(0.00001);
        config.setDifBorder(0.00001);

        double nuDelta = 0.01;


        config.setAlpha(0.01);
        config.setEpochs(8);
        config.setNu(0.3);
        List<Double> x = new ArrayList<>();
        List<Double> y = new ArrayList<>();
        for (int i = 1; i <= 11; i++) {
            config.setFuzzySize(i);

            Model model = new Model();

            double[] res = {0.0, 0.0};
            double r = model.createAndExecute(config, true, res);
            x.add((double) i);
            y.add(res[1]);
        }
        draw(x, y, "Neurons Number", "Train Loss", "зависимость погрешности классификации от числа нейронов нечеткого слоя", "testFuzzy1", "погрешность");
        int a = 0;
    }

    @Test
    public void testTestTrainPerceptron() throws NoOutputSignal, IOException {
        List<String> pam = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            pam.add(i, Integer.toString(i + 1));
        }
//        double[][] data = readReadyData();
        double[][] data = readWineData();

        DataUtil.shuffle(data, 10);
        DataUtil.normalize(data, -1, 1);

        ModelConfig config = new ModelConfig();
        config.setNormA(-1);
        config.setNormB(1);
        config.setData(data);
        config.setPam(pam);

        config.setTrainSize(50);
        config.setTestSize(128);
        config.setBorder(0.00001);
        config.setDifBorder(0.00001);

        double nuDelta = 0.01;


        config.setAlpha(0.01);
        config.setEpochs(8);
        config.setNu(0.3);
        List<Double> x = new ArrayList<>();
        List<Double> y = new ArrayList<>();
        List<Double> X = new ArrayList<>();
        List<Double> Y = new ArrayList<>();
        for (int i = 1; i <= 7; i++) {
            config.setPerceptronSize(i);
            Model model = new Model();

            double[] res = {0.0, 0.0};
            double r = model.createAndExecute(config, false, res);
            x.add((double) i);
            y.add(res[1]);
            X.add((double) i);
            Y.add(res[0]);
        }
        draw(x, y,
                "Neurons Number",
                "Train Loss",
                "зависимость погрешности обучения и классификации от количества нейронов скрытого слоя",
                "TestPerceptron",
                X, Y, "тестирование", "обучение");
        int a = 0;
    }

    @Test
    public void testTestDataset() throws NoOutputSignal, IOException {
        List<String> pam = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            pam.add(i, Integer.toString(i + 1));
        }
        double[][] data = readWineData();
        DataUtil.normalize(data, -1, 1);
        ModelConfig config = new ModelConfig();
        config.setNormA(-1);
        config.setNormB(1);
        config.setData(data);
        config.setPam(pam);

        config.setTrainSize(50);
        config.setTestSize(128);
        config.setBorder(0.00001);
        config.setDifBorder(0.00001);

        double nuDelta = 0.01;


        config.setAlpha(0.01);
        config.setEpochs(8);
        List<Double> x = new ArrayList<>();
        List<Double> y = new ArrayList<>();
        config.setNu(0.3);
        config.setTestSize(128);
        for (int i = 10; i < 178; i += 10) {
            config.setTestSize(178-i);
            config.setTrainSize(i);
            DataUtil.shuffle(data, (int)new Date().getTime());

            Model model = new Model();

            double[] res = {0.0, 0.0};
            double r = model.createAndExecute(config, true, res);
            x.add((double)i);
            y.add(res[1]);
        }
        draw(x, y, "Train Dataset Size", "Train Loss", "зависимость погрешности классификации от размера выборки обучения", "testDataset", "погрешность");
        int a = 0;
    }

    @Test
    public void testTestEpochs() throws NoOutputSignal, IOException {
        List<String> pam = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            pam.add(i, Integer.toString(i + 1));
        }
        double[][] data = readWineData();
        DataUtil.normalize(data, -1, 1);
        DataUtil.shuffle(data, (int)new Date().getTime());
        ModelConfig config = new ModelConfig();
        config.setNormA(-1);
        config.setNormB(1);
        config.setData(data);
        config.setPam(pam);

        config.setTrainSize(50);
        config.setTestSize(128);
        config.setBorder(0.00001);
        config.setDifBorder(0.00001);

        double nuDelta = 0.01;


        config.setAlpha(0.01);
        config.setEpochs(8);
        List<Double> x = new ArrayList<>();
        List<Double> y = new ArrayList<>();
        config.setNu(0.3);
        config.setTestSize(128);
        config.setTrainSize(50);
        for (int i = 1; i < 20; i ++) {
            config.setEpochs(i);

            Model model = new Model();

            double[] res = {0.0, 0.0};
            model.createAndExecute(config, true, res);
            x.add((double)i);
            y.add(res[1]);
        }
        draw(x, y, "Epochs Number", "Train Loss", "зависимость погрешности классификации от количества эпох", "testEpochs", "погрешность");
        int a = 0;
    }

    private void draw(List<Double> x, List<Double> y, String xName, String yName, String title, String fileName, String name) throws IOException {
        draw(x, y, xName, yName, title, fileName, null, null, name, null);
    }

    private void draw(List<Double> x, List<Double> y, String xName, String yName, String title, String fileName,
                      List<Double> X, List<Double> Y, String fName, String sName) throws IOException {
        final XYSeries series = new XYSeries( fName );
        for(int i = 0; i < x.size(); i++) {
            series.add(x.get(i), y.get(i));
        }
        final XYSeriesCollection dataset = new XYSeriesCollection( );
        dataset.addSeries( series );

        if(X != null) {
            final XYSeries seriesS = new XYSeries( sName );
            for(int i = 0; i < X.size(); i++) {
                seriesS.add(X.get(i), Y.get(i));
            }
            dataset.addSeries(seriesS);
        }

        JFreeChart xylineChart = ChartFactory.createXYLineChart(
                title,
                xName,
                yName,
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        int width = 640;   /* Width of the image */
        int height = 480;  /* Height of the image */
        File XYChart = new File( fileName + ".jpeg" );
        ChartUtilities.saveChartAsJPEG( XYChart, xylineChart, width, height);
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

    private double[][] readReadyData() {
        double[][] res = new double[178][15];
        Function<String, String>[] mappers = new Function[]{l -> wineMap.get((String)l)};
        readCsv(res, 0, Model.PATH + "prepData", new int[]{1}, mappers);
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
        } catch (IOException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }
}
