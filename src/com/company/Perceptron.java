package com.company;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Perceptron {

    List<double[]> pLayers;
    List<double[]> pLayers_errors;
    List<double[][]> pWeights;

    List<Double[][]> pWeights_old;
    List<Double[]> last_b;

    List<Integer> pConf;
    List<learnSet> learnSetList;

    double learnFactor = 0.1;


    class learnSet {
        learnSet(int inputsN, int outputsN) {
            inputs = new double[inputsN];
            outputs = new double[outputsN];
        }

        public double[] inputs;
        public double[] outputs;
    }


    //private Double[] enterNeurons;
    //private Double[] pLayers;
    //private Double[] outNeurons;
    //private Double[][] enterToHiddenWeights;
    //private Double[][] hiddenToOutWeights;
    private Double[][] fromEnterToHiddenWeights_last;
    private Double[][] fromHiddenToOutWeights_last;
    //private Double[][] last_b;
    //private Double[][][] weights;
    boolean isLearning = true;

    private double[][] inputData = {
            {0.0, 0.0}, {1.0, 0.0}, {0.0, 1.0}, {1.0, 1.0}
    };
    private double[][] outputData = {
            {0.0, 1.0, 1.0, 0.0}
    };

    private static int currentIndex = -1;
    private Double[] expectedOutput;
    Double[] result;

    Perceptron(String fileNameLearnSer, String fileNamePerceptronConf) {
/*
        try {
            inputData = readData(fileNameInput);
            outputData = readData(fileNameTest);
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        pConf = readPerceptron(fileNamePerceptronConf);
        learnSetList = readLearnSet(fileNameLearnSer);

        pLayers = new ArrayList<>();
        pLayers_errors = new ArrayList<>();
        for (Integer layerSize : pConf) {
            pLayers.add(new double[layerSize]);
            pLayers_errors.add(new double[layerSize]);
        }

        pWeights = new ArrayList<>();
        for (int i = 0; i < pLayers.size() - 1; i++) {
            pWeights.add(new double[pConf.get(i + 1)][pConf.get(i)]); //TODO check i - index (correct)
        }

        init();

       /* int layers = 2;
        int hiddenNeuronNum1 = 4;
        int hiddenNeuronNum2 = 3;*/
        //enterNeurons = new Double[inputData[0].length];
        //pLayers = new Double[hiddenNeuronNum];
      /*  pLayers = new ArrayList<double[]>();
        pLayers.add(new double[inputData[0].length]);
        pLayers.add(new double[hiddenNeuronNum1]);
        pLayers.add(new double[hiddenNeuronNum2]);
        pLayers.add(new double[outNeuronNum]);

        pLayers_errors = new ArrayList<double[]>();
        pLayers_errors.add(new double[inputData[0].length]);
        pLayers_errors.add(new double[hiddenNeuronNum1]);
        pLayers_errors.add(new double[hiddenNeuronNum2]);
        pLayers_errors.add(new double[outNeuronNum]);

        pWeights = new ArrayList<double[][]>();
        pWeights.add(new double[inputData[0].length][hiddenNeuronNum1]);
        pWeights.add(new double[hiddenNeuronNum1][hiddenNeuronNum2]);
        pWeights.add(new double[hiddenNeuronNum2][outNeuronNum]);*/

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////


     /*   pWeights_old = new ArrayList<Double[][]>();
        pWeights_old.add(new Double[inputData[0].length][hiddenNeuronNum1]);
        pWeights_old.add(new Double[hiddenNeuronNum1][hiddenNeuronNum2]);
        pWeights_old.add(new Double[hiddenNeuronNum2][outNeuronNum]);

        last_b = new ArrayList<Double[]>();
        last_b.add(new Double[inputData[0].length]);
        last_b.add(new Double[hiddenNeuronNum1]);
        last_b.add(new Double[hiddenNeuronNum2]);
        last_b.add(new Double[outNeuronNum]);

        result = new Double[outNeuronNum];*/

        //outNeurons = new Double[outNeuronNum];
        //enterToHiddenWeights = new Double[enterNeurons.length][pLayers.get(0).length];
        //hiddenToOutWeights = new Double[pLayers.get(pLayers.size() - 1).length][outNeurons.length];

        //fromEnterToHiddenWeights_last = new Double[enterNeurons.length][pLayers.get(0).length];
        //fromHiddenToOutWeights_last = new Double[pLayers.get(pLayers.size() - 1).length][outNeurons.length];
        //last_b = new Double[pLayers.get(0).length][inputData[0].length];

//        weights = new Double[][][]

        // study();


    }

    public void masTrain() {
        for (learnSet set : learnSetList) {
            train(set.inputs, set.outputs);
        }
    }

    public void manualUse() {
        double[] manualInput = new double[pLayers.get(0).length];

        Scanner in = new Scanner(System.in);
        for (int i = 0; i < manualInput.length; i++) {
            System.out.println("Enter data in neuron " + i);
            manualInput[i] = in.nextDouble();
            in.nextLine();
        }

        forwardPropagation(manualInput);

        printOutNeurons();

    }

    public static double[][] toMatrix(double[] arr) {
        double[][] matr = new double[arr.length][1];
        for (int i = 0; i < arr.length; i++) {
            matr[i][0] = arr[i];
        }
        return matr;
    }

    public static double[] toArray(double[][] matr) {
        double[] arr = new double[matr.length];

        for (int i = 0; i < matr.length; i++) {
            arr[i] = matr[i][0];

        }
        return arr;
    }

    public static double[][] subtractMatrix(double[][] a, double[][] b) {

        double[][] res = new double[a.length][b[0].length];

        int rows = res.length;
        int columns = res[0].length;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                res[row][col] = a[row][col] - b[row][col];
            }
        }

        return res;
    }

    public static double[][] addMatrix(double[][] a, double[][] b) {

        double[][] res = new double[a.length][b[0].length];

        int rows = res.length;
        int columns = res[0].length;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                res[row][col] = a[row][col] + b[row][col];
            }
        }

        return res;
    }

    public static double[][] transposeMatrix(double[][] m) {
        double[][] temp = new double[m[0].length][m.length];
        for (int i = 0; i < m.length; i++)
            for (int j = 0; j < m[0].length; j++)
                temp[j][i] = m[i][j];
        return temp;
    }

    public static double[][] multiplyByMatrix(double[][] m1, double[][] m2) {
        int m1ColLength = m1[0].length; // m1 columns length
        int m2RowLength = m2.length;    // m2 rows length
        if (m1ColLength != m2RowLength) {
            return null; // matrix multiplication is not possible
        }
        int mRRowLength = m1.length;    // m result rows length
        int mRColLength = m2[0].length; // m result columns length

        double[][] mResult = new double[mRRowLength][mRColLength];

        for (int i = 0; i < mRRowLength; i++) {         // rows from m1
            for (int j = 0; j < mRColLength; j++) {     // columns from m2
                for (int k = 0; k < m1ColLength; k++) { // columns from m1
                    mResult[i][j] += m1[i][k] * m2[k][j];
                }
            }
        }
        return mResult;
    }

    private Double function(Double x) {
        return 1d / (1 + Math.exp(-x));
    }

    public Double derivative(Double x) {
        return function(x) * (1d - function(x));
    }


    private Integer next(String numbers[]) {
        ++currentIndex;
        while (currentIndex < numbers.length
                && numbers[currentIndex].equals(""))
            ++currentIndex;
        return currentIndex < numbers.length ? Integer
                .parseInt(numbers[currentIndex]) : null;
    }

    private Double nextDouble(String numbers[]) {
        ++currentIndex;
        while (currentIndex < numbers.length
                && numbers[currentIndex].equals(""))
            ++currentIndex;
        return currentIndex < numbers.length ? Double
                .parseDouble(numbers[currentIndex]) : null;
    }


    private Double[][] readData(String fileName) throws IOException {
        FileInputStream inFile = new FileInputStream(fileName);

        byte[] str = new byte[inFile.available()];

        inFile.read(str);
        String text = new String(str);

        String[] numbers = text.split(" |\r\n");
        int i, j;
        int n;
        n = next(numbers);
        int m;
        m = next(numbers);
        Double matr[][] = new Double[n][m];

        for (i = 0; i < n; ++i)
            for (j = 0; j < m; ++j)
                matr[i][j] = nextDouble(numbers);

        currentIndex = -1;
        return matr;
    }

    private List<Integer> readPerceptron(String fileName) {

        List<Integer> perceptronConf = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line = br.readLine();
            //perceptronConf.add(Integer.parseInt(line));

            while (line != null) {
                perceptronConf.add(Integer.parseInt(line));
                line = br.readLine();

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return perceptronConf;
    }

    private List<learnSet> readLearnSet(String fileName) {

        List<learnSet> learnSetList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            //StringBuilder sb = new StringBuilder();
            String line;
            line = br.readLine();
            String[] tempMas = line.split(" ");
            int inputN = Integer.parseInt(tempMas[0]);
            int outputN = Integer.parseInt(tempMas[1]);

            line = br.readLine();
            while (line != null) {
                tempMas = line.split(" ");

                learnSet newSet = new learnSet(inputN, outputN);
                for (int i = 0; i < inputN; i++) {
                    newSet.inputs[i] = Double.parseDouble(tempMas[i]);
                }
                for (int i = inputN; i < inputN + outputN; i++) {
                    newSet.outputs[i - inputN] = Double.parseDouble(tempMas[i]);
                }

                learnSetList.add(newSet);
                line = br.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return learnSetList;
    }

    /*
        public void work() {
            Scanner in = new Scanner(System.in);

            double[] enterNeurons = pLayers.get(0);
            for (int i = 0; i < enterNeurons.length; i++) {
                System.out.println("Enter data in neuron " + i);
                enterNeurons[i] = in.nextDouble();
                in.nextLine();
            }

            forwardPropagation();
            printOutNeurons();
        }

        public void work(String fileName) {
            Double[][] data = null;

            try {
                data = readData(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }

            double[] enterNeurons = pLayers.get(0);
            for (int p = 0; p < data.length; p++) {
                for (int i = 0; i < enterNeurons.length; i++) {
                    enterNeurons[i] = data[p][i];
                }

                forwardPropagation();

                System.out.println("=== Data set № " + p + " ===|");
                printOutNeurons();
                System.out.println("---------------------/");
            }
        }
    */
    private void printOutNeurons() {
        double[] outNeurons = pLayers.get(pLayers.size() - 1);
        for (double outNeuron : outNeurons) {
            System.out.println(outNeuron);
        }
    }

    private void init() {

       /* for (int i = 0; i < enterNeurons.length; i++) {
            for (int j = 0; j < pLayers.get(0).length; j++) {
                enterToHiddenWeights[i][j] = Math.random() * 0.2 + 0.1;
            }
        }*/


        initMatrix(pWeights);
        //initMatrix(pWeights_old);
        //initMas(pLayers);
        //initMas(pLayers_errors);
        //initMas(last_b);



     /*   for (int i = 0; i < pLayers.get(pLayers.size() - 1).length; i++) {
            for (int j = 0; j < outNeurons.length; j++) {
                hiddenToOutWeights[i][j] = Math.random() * 0.2 + 0.1;
            }
        }*/

    }

    private void initMatrix(List<double[][]> pWeights) {
        for (double[][] curWeights : pWeights) {

            int rows = curWeights.length;
            int columns = curWeights[0].length;
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    curWeights[i][j] = Math.random() * 0.2 + 0.1;

                }
            }
        }
    }

    private void initMas(List<double[]> pLayers) {
        for (double[] curLayer : pLayers) {

            int rows = curLayer.length;
            for (int i = 0; i < rows; i++) {
                curLayer[i] = 0.0;


            }
        }
    }


    private void forwardPropagation(double[] input) {
        //init();
        pLayers.set(0, input);

        //forward
        for (int i = 0; i < pLayers.size() - 1; i++) {
            double[][] curLayer = toMatrix(pLayers.get(i));
            double[][] weights = pWeights.get(i);
            //calc next layer
            double[][] outputs = multiplyByMatrix(weights, curLayer);

            //activation function
            int rows = outputs.length;
            int columns = outputs[0].length;
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < columns; col++) {
                    outputs[row][col] = function(outputs[row][col]);

                }
            }
            pLayers.set(i + 1, toArray(outputs));
        }

    }

    public void train(double[] inputs, double[] expectedOutputs) {
        forwardPropagation(inputs);

        //double[] tempOutputData = {1, 0};

        double[][] outputs = toMatrix(pLayers.get(pLayers.size() - 1));
        double[][] targets = toMatrix(expectedOutputs);

        double[][] outputErrors = subtractMatrix(targets, outputs); //TODO kvadratich otklonenie
        pLayers_errors.set(pLayers_errors.size() - 1, toArray(outputErrors));

        for (int l = pLayers.size() - 2; l >= 1; l--) {

            double[][] transposeWeights = transposeMatrix(pWeights.get(l));
            //calc errors for each layer
            double[][] curLayerErrors = multiplyByMatrix(transposeWeights, outputErrors);

            pLayers_errors.set(pLayers_errors.size() - 1 - l, toArray(curLayerErrors));

            outputErrors = curLayerErrors;
        }//calc all neurons errors

        //change weights

        for (int l = pLayers.size() - 2; l >= 0; l--) {// to 0 or 1
            double[] curOutputErrors = pLayers_errors.get(l + 1);

            double[] curOutput = pLayers.get(l + 1);

            int rows = curOutput.length;
            double[] gradients = new double[rows];
            for (int row = 0; row < rows; row++) {
                gradients[row] = learnFactor * curOutputErrors[row] * derivative(curOutput[row]);
            }

            double[] curLayer = pLayers.get(l);

            double[][] deltaWeights = multiplyByMatrix(toMatrix(gradients), transposeMatrix(toMatrix(curLayer)));

            double[][] resWeights = addMatrix(pWeights.get(l), deltaWeights);
            pWeights.set(l, resWeights);
            //pWeights.set(l, addMatrix(pWeights.get(l), deltaWeights));

        }

    }
}

/*
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void backPropagation2() {
//        Iterate layers in reverse order
        for (int l = pLayers.size() - 1; l >= 1; l--) {
            Double[] curLayer = pLayers.get(l);
            Double[] prevLayer = pLayers.get(l - 1);

            for (int n = 0; n < curLayer.length; n++) {
                Double curNeurone = curLayer[n];
                Double[][] curWeight = pWeights.get(l - 1);
                Double[][] last_weights = pWeights_old.get(l - 1);

                for (int w = 0; w < curWeight.length; w++) {
//                    Delta calculation
                    Double old_weight = curWeight[n][w];
                    curWeight[n][w] = weightCorrection(l, n, w);
                    last_weights[n][w] = old_weight;
                }
            }
        }
    }


    private Double weightCorrection(int l, int n, int w) {
        Double b = 0.0;
        Double[] layer = pLayers.get(l);
        Double curNeurone = layer[n];


        if (l == pLayers.size() - 1) {
            b = (expectedOutput[n] - layer[n]) * derivative(curNeurone);
        } else {
            Double[][] weights = pWeights.get(l);
            Double[][] last_weights = pWeights_old.get(l);
            Double[] lastB = last_b.get(l);
            for (int in = 0; in < weights.length; in++) {
                b += lastB[in] * weights[in][n]; //!
            }
            b *= derivative(curNeurone);
        }

        last_b.get(l - 1)[n] = b;

        Double[][] weights = pWeights.get(l - 1);
        Double[][] last_weights = pWeights_old.get(l - 1);
        return weights[n][w] + 0.2 * (weights[n][w] - last_weights[n][w]) + 0.8 * b * layer[w];
    }

}

  /*  public void study() {
        initWeights();


        Double[] err = new Double[pLayers.length];
        Double gError;
        do {
            gError = 0;
            for (int p = 0; p < inputData.length; p++) {
                System.arraycopy(inputData[p], 0, enterNeurons, 0, enterNeurons.length);

                calculateOuter();


                Double[] lErr = new Double[outNeurons.length];

                for (int i = 0; i < outNeurons.length; i++) {
                    Double oldWeight = weightCorrection(1, i);
                    // в функции проходим по всему слою layer и получайм сумму из произведений
                    lErr[i] = outputData[p][i] - outNeurons[i];
                    gError += Math.abs(lErr[i]);
                }


                for (int i = 0; i < pLayers.length; i++) {
                    for (int j = 0; j < outNeurons.length; j++) {
                        err[i] = lErr[j] * hiddenToOutWeights[i][j]; //////////////////////////////// <-
                    }
                }


                ///////////----------------------------------///////////

                for (int i = 0; i < enterNeurons.length; i++) {
                    for (int j = 0; j < pLayers.length; j++) {
                        enterToHiddenWeights[i][j] += 0.1 * err[j] * enterNeurons[i];

                    }
                }


                for (int i = 0; i < pLayers.length; i++) {
                    for (int j = 0; j < outNeurons.length; j++) {
                        hiddenToOutWeights[i][j] += 0.1 * lErr[j] * pLayers[i];
                    }
                }
            }
        } while (gError != 0);
    }*/

