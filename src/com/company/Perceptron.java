package com.company;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Perceptron {

    List<double[]> hiddenNeurons;
    List<double[][]> hiddenToNextHiddenWeights;
    private double[] enterNeurons;
    //private double[] hiddenNeurons;
    private double[] outNeurons;
    private double[][] enterToHiddenWeights;
    private double[][] hiddenToOutWeights;
    private double[][] fromEnterToHiddenWeights_last;
    private double[][] fromHiddenToOutWeights_last;
    private double[][] last_b;
    private double[][][] weights;

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
        enterNeurons = new double[inputData[0].length];
        //hiddenNeurons = new double[hiddenNeuronNum];
        hiddenNeurons = new ArrayList<double[]>();
        hiddenNeurons.add(new double[hiddenNeuronNum1]);
        hiddenNeurons.add(new double[hiddenNeuronNum2]);

        hiddenToNextHiddenWeights = new ArrayList<double[][]>();
        hiddenToNextHiddenWeights.add(new double[enterNeurons.length][hiddenNeuronNum1]);
        hiddenToNextHiddenWeights.add(new double[hiddenNeuronNum1][hiddenNeuronNum2]);


        outNeurons = new double[outNeuronNum];
        enterToHiddenWeights = new double[enterNeurons.length][hiddenNeurons.get(0).length];
        hiddenToOutWeights = new double[hiddenNeurons.get(hiddenNeurons.size() - 1).length][outNeurons.length];

        fromEnterToHiddenWeights_last = new double[enterNeurons.length][hiddenNeurons.get(0).length];
        fromHiddenToOutWeights_last = new double[hiddenNeurons.get(hiddenNeurons.size() - 1).length][outNeurons.length];
        last_b = new double[hiddenNeurons.get(0).length][outNeurons.length];

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
        for (double outNeuron : outNeurons) {
            System.out.println(outNeuron);
        }
    }

    private void initWeights() {

       /* for (int i = 0; i < enterNeurons.length; i++) {
            for (int j = 0; j < hiddenNeurons.get(0).length; j++) {
                enterToHiddenWeights[i][j] = Math.random() * 0.2 + 0.1;
            }
        }*/



        for (double[][] curHiddenLayerWeights: hiddenToNextHiddenWeights){

            int rows = curHiddenLayerWeights.length;
            int columns = curHiddenLayerWeights[0].length;
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    curHiddenLayerWeights[i][j]= Math.random() * 0.2 + 0.1;
                }
            }
        }


        for (int i = 0; i < hiddenNeurons.get(hiddenNeurons.size() - 1).length; i++) {
            for (int j = 0; j < outNeurons.length; j++) {
                hiddenToOutWeights[i][j] = Math.random() * 0.2 + 0.1;
            }
        }

    }

    private void calculateOuter() {

        for (int i = 0; i < hiddenNeurons.get(0).length; i++) {
            hiddenNeurons[i] = 0;
            for (int j = 0; j < enterNeurons.length; j++) {
                hiddenNeurons[i] += enterNeurons[j] * enterToHiddenWeights[j][i];
            }
            hiddenNeurons[i] = function(hiddenNeurons[i]);
            /*if (hiddenNeurons[i] > 0.5)
                hiddenNeurons[i] = 1;////TODO sigmoid activation function////////////////////////////////
            else hiddenNeurons[i] = 0;*/
        }

        for (int i = 0; i < outNeurons.length; i++) {
            outNeurons[i] = 0;
            for (int j = 0; j < hiddenNeurons.length; j++) {
                outNeurons[i] += hiddenNeurons[j] * hiddenToOutWeights[j][i];
            }
            outNeurons[i] = function(outNeurons[i]);

            /*if (outNeurons[i] > 0.5) { //////TODO sigmoid activation function///////////////////////////////////////////////////  <---
                outNeurons[i] = 1;
            } else {
                outNeurons[i] = 0;
            }*/
        }


    }

    public void study() {
        initWeights();

        double[] err = new double[hiddenNeurons.length];
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


                for (int i = 0; i < hiddenNeurons.length; i++) {
                    for (int j = 0; j < outNeurons.length; j++) {
                        err[i] = lErr[j] * hiddenToOutWeights[i][j]; //////////////////////////////// <-
                    }
                }


                ///////////----------------------------------///////////

                for (int i = 0; i < enterNeurons.length; i++) {
                    for (int j = 0; j < hiddenNeurons.length; j++) {
                        enterToHiddenWeights[i][j] += 0.1 * err[j] * enterNeurons[i];

                    }
                }


                for (int i = 0; i < hiddenNeurons.length; i++) {
                    for (int j = 0; j < outNeurons.length; j++) {
                        hiddenToOutWeights[i][j] += 0.1 * lErr[j] * hiddenNeurons[i];
                    }
                }
            }
        } while (gError != 0);
    }

    private double weightCorrection(int layer, int neuron) {
        double b = 0;

        for (int i = 0; i < hiddenNeurons.length; i++) {
            b += last_b[][] /
        }

    }
}