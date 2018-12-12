package com.company;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;


public class Perceptron {

    private double[] enterNeurons;
    private double[] hiddenNeurons;
    private double[] outNeurons;
    private double[][] fromEnterToHiddenWeights;
    private double[][] fromHiddenToOutWeights;
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


        enterNeurons = new double[inputData[0].length];
        hiddenNeurons = new double[hiddenNeuronNum];
        outNeurons = new double[outNeuronNum];
        fromEnterToHiddenWeights = new double[enterNeurons.length][hiddenNeurons.length];
        fromHiddenToOutWeights = new double[hiddenNeurons.length][outNeurons.length];


        study();


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

        countOuter();
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

            countOuter();

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
        for (int i = 0; i < enterNeurons.length; i++) {
            for (int j = 0; j < hiddenNeurons.length; j++) {
                fromEnterToHiddenWeights[i][j] = Math.random() * 0.2 + 0.1;
            }
        }
        for (int i = 0; i < hiddenNeurons.length; i++) {
            for (int j = 0; j < outNeurons.length; j++) {
                fromHiddenToOutWeights[i][j] = Math.random() * 0.2 + 0.1;
            }
        }

    }

    private void countOuter() {
        for (int i = 0; i < hiddenNeurons.length; i++) {
            hiddenNeurons[i] = 0;
            for (int j = 0; j < enterNeurons.length; j++) {
                hiddenNeurons[i] += enterNeurons[j] * fromEnterToHiddenWeights[j][i];
            }
            if (hiddenNeurons[i] > 0.5) hiddenNeurons[i] = 1;////TODO sigmoid activation function////////////////////////////////
            else hiddenNeurons[i] = 0;
        }

        for (int i = 0; i < outNeurons.length; i++) {
            outNeurons[i] = 0;
        }

        for (int i = 0; i < outNeurons.length; i++) {
            for (int j = 0; j < hiddenNeurons.length; j++) {
                outNeurons[i] += hiddenNeurons[j] * fromHiddenToOutWeights[j][i];
            }
        }

        for (int i = 0; i < outNeurons.length; i++) {
            if (outNeurons[i] > 0.5) { //////TODO sigmoid activation function///////////////////////////////////////////////////  <---
                outNeurons[i] = 1;
            } else {
                outNeurons[i] = 0;
            }
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

                countOuter();


                double[] lErr = new double[outNeurons.length];

                for (int i = 0; i < outNeurons.length; i++) {
                    lErr[i] = outputData[p][i] - outNeurons[i];
                    gError += Math.abs(lErr[i]);
                }


                for (int i = 0; i < hiddenNeurons.length; i++) {
                    for (int j = 0; j < outNeurons.length; j++) {
                        err[i] = lErr[j] * fromHiddenToOutWeights[i][j]; //////////////////////////////// <-
                    }
                }


                for (int i = 0; i < enterNeurons.length; i++) {
                    for (int j = 0; j < hiddenNeurons.length; j++) {
                        fromEnterToHiddenWeights[i][j] += 0.1 * err[j] * enterNeurons[i];

                    }
                }


                for (int i = 0; i < hiddenNeurons.length; i++) {
                    for (int j = 0; j < outNeurons.length; j++) {
                        fromHiddenToOutWeights[i][j] += 0.1 * lErr[j] * hiddenNeurons[i];
                    }
                }
            }
        } while (gError != 0);
    }
}