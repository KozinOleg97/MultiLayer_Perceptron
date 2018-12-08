package com.company;

import java.io.FileInputStream;
import java.io.IOException;


public class Perceptron {

    double[] enterNeurons;
    double[] hiddenNeurons;
    double[] outNeurons;
    double[][] fromEnterToHiddenWeights;
    double[][] fromHiddenToOutWeights;
    double[][] inputData = {
            {0, 0}, {1, 0}, {0, 1}, {1, 1}
    };
    double[][] outputData = {
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


        initWeights();
        study();

        for (int p = 0; p < inputData.length; p++) {
            for (int i = 0; i < enterNeurons.length; i++)
                enterNeurons[i] = inputData[p][i];

            countOuter();
            for (int j = 0; j < outNeurons.length; j++) {
                System.out.println(outNeurons[j]);
            }
            System.out.println("-----------------------");
        }

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


    public double[][] readData(String fileName) throws IOException {
        FileInputStream inFile = new FileInputStream(fileName);

        byte[] str = new byte[inFile.available()];

        inFile.read(str);
        String text = new String(str);

        String[] numbers = text.split(" |\r\n");
        int i, j;
        int n = next(numbers), m = next(numbers);
        double matr[][] = new double[n][m];

        for (i = 0; i < n; ++i)
            for (j = 0; j < m; ++j)
                matr[i][j] = nextDouble(numbers);

        currentIndex = -1;
        return matr;
    }


    public void initWeights() {
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

    public void countOuter() {
        for (int i = 0; i < hiddenNeurons.length; i++) {
            hiddenNeurons[i] = 0;
            for (int j = 0; j < enterNeurons.length; j++) {
                hiddenNeurons[i] += enterNeurons[j] * fromEnterToHiddenWeights[j][i];
            }
            if (hiddenNeurons[i] > 0.5) hiddenNeurons[i] = 1;
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
            if (outNeurons[i] > 0.5) { /////////////////////////////////////////////////////////  <---
                outNeurons[i] = 1;
            } else {
                outNeurons[i] = 0;
            }
        }
    }

    public void study() {
        double[] err = new double[hiddenNeurons.length];
        double gError = 0;
        do {
            gError = 0;
            for (int p = 0; p < inputData.length; p++) {
                for (int i = 0; i < enterNeurons.length; i++)
                    enterNeurons[i] = inputData[p][i];

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