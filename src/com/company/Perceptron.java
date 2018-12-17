package com.company;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Perceptron {

    List<double[]> pLayers;
    List<double[][]> pWeights;

    List<double[][]> pWeights_old;
    //private double[] enterNeurons;
    //private double[] pLayers;
    //private double[] outNeurons;
    //private double[][] enterToHiddenWeights;
    //private double[][] hiddenToOutWeights;
    private double[][] fromEnterToHiddenWeights_last;
    private double[][] fromHiddenToOutWeights_last;
    private double[][] last_b;
    //private double[][][] weights;
    boolean isLearning = true;

    private double[][] inputData = {
            {0, 0}, {1, 0}, {0, 1}, {1, 1}
    };
    private double[][] outputData = {
            {0, 1, 1, 0}
    };

    private static int currentIndex = -1;

    Perceptron(String fileNameInput, String fileNameTest, Integer hiddenNeuronNum, Integer outNeuronNum) {

        try {
            inputData = readData(fileNameInput);
            outputData = readData(fileNameTest);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int layers = 2;
        int hiddenNeuronNum1 = 4;
        int hiddenNeuronNum2 = 3;
        //enterNeurons = new double[inputData[0].length];
        //pLayers = new double[hiddenNeuronNum];
        pLayers = new ArrayList<double[]>();
        pLayers.add(new double[inputData[0].length]);
        pLayers.add(new double[hiddenNeuronNum1]);
        pLayers.add(new double[hiddenNeuronNum2]);
        pLayers.add(new double[outNeuronNum]);

        pWeights = new ArrayList<double[][]>();
        pWeights.add(new double[inputData[0].length][hiddenNeuronNum1]);
        pWeights.add(new double[hiddenNeuronNum1][hiddenNeuronNum2]);
        pWeights.add(new double[hiddenNeuronNum2][outNeuronNum]);


        //outNeurons = new double[outNeuronNum];
        //enterToHiddenWeights = new double[enterNeurons.length][pLayers.get(0).length];
        //hiddenToOutWeights = new double[pLayers.get(pLayers.size() - 1).length][outNeurons.length];

        //fromEnterToHiddenWeights_last = new double[enterNeurons.length][pLayers.get(0).length];
        //fromHiddenToOutWeights_last = new double[pLayers.get(pLayers.size() - 1).length][outNeurons.length];
        last_b = new double[pLayers.get(0).length][inputData[0].length];

//        weights = new double[][][]

        study();


    }

    private double function(double x) {
        return 1d / (1 + Math.exp(-x));
    }

    public double derivative(double x) {
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


    private double[][] readData(String fileName) throws IOException {
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
        double matr[][] = new double[n][m];

        for (i = 0; i < n; ++i)
            for (j = 0; j < m; ++j)
                matr[i][j] = nextDouble(numbers);

        currentIndex = -1;
        return matr;
    }


    public void work() {
        Scanner in = new Scanner(System.in);

        double[] enterNeurons = pLayers.get(0);
        for (int i = 0; i < enterNeurons.length; i++) {
            System.out.println("Enter data in neuron " + i);
            enterNeurons[i] = in.nextDouble();
            in.nextLine();
        }

        calculateOuter();
        printOutNeurons();
    }

    public void work(String fileName) {
        double[][] data = null;

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

            calculateOuter();

            System.out.println("=== Data set № " + p + " ===|");
            printOutNeurons();
            System.out.println("---------------------/");
        }
    }

    private void printOutNeurons() {
        double[] outNeurons = pLayers.get(0);
        for (double outNeuron : outNeurons) {
            System.out.println(outNeuron);
        }
    }

    private void initWeights() {

       /* for (int i = 0; i < enterNeurons.length; i++) {
            for (int j = 0; j < pLayers.get(0).length; j++) {
                enterToHiddenWeights[i][j] = Math.random() * 0.2 + 0.1;
            }
        }*/


        for (double[][] curWeights : pWeights) {

            int rows = curWeights.length;
            int columns = curWeights[0].length;
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    curWeights[i][j] = Math.random() * 0.2 + 0.1;
                }
            }
        }


     /*   for (int i = 0; i < pLayers.get(pLayers.size() - 1).length; i++) {
            for (int j = 0; j < outNeurons.length; j++) {
                hiddenToOutWeights[i][j] = Math.random() * 0.2 + 0.1;
            }
        }*/

    }

    private void calculateOuter() {
        double[] previousLayer = null;
        double[][] weightsBetween;
        int layerNumb = 0;
        for (double[] curLayer : pLayers) {

            if (layerNumb == 0) {
                layerNumb++;
                previousLayer = curLayer;
                continue;
            }

            weightsBetween = pWeights.get(layerNumb - 1);

            for (int i = 0; i < curLayer.length; i++) {
                curLayer[i] = 0;
                for (int j = 0; j < previousLayer.length; j++) {
                    curLayer[i] += previousLayer[j] * weightsBetween[j][i];
                }
                curLayer[i] = function(curLayer[i]); //что делать с последним слоем?
            }

            previousLayer = curLayer;
            layerNumb++;
        }

        if (isLearning) {
            backPropagation();
        }
    }

    private void backPropagation() {
//        Iterate layers in reverse order
        for (int l = pLayers.size() - 1; l >= 1; l--) {
            double[] curLayer = pLayers.get(l);
            double[] prevLayer = pLayers.get(l - 1);

            for (int n = 0; n < curLayer.length; n++) {
                double curNeurone = curLayer[n];
                double[][] curWeight = pWeights.get(l - 1);
                double[][] last_weights = pWeights_old.get(l - 1);

                for (int w = 0; w < curWeight.length; w++) {
//                    Delta calculation
                    double old_weight = curWeight[w][n];
                    curWeight[w][n] = weightCorrection(curLayer, curNeurone, w);
                    last_weights[w][n] = old_weight;
                }
            }
        }
    }


    private double weightCorrection(double[] layer, double neuron, int w) {
        double b = 0;

        for (int i = 0; i < pLayers.length; i++) {
            b += last_b[][] /
        }

    }
}

  /*  public void study() {
        initWeights();


        double[] err = new double[pLayers.length];
        double gError;
        do {
            gError = 0;
            for (int p = 0; p < inputData.length; p++) {
                System.arraycopy(inputData[p], 0, enterNeurons, 0, enterNeurons.length);

                calculateOuter();


                double[] lErr = new double[outNeurons.length];

                for (int i = 0; i < outNeurons.length; i++) {
                    double oldWeight = weightCorrection(1, i);
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

