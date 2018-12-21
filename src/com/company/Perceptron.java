package com.company;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;


public class Perceptron {

    List<double[]> pLayers;
    List<double[]> pLayers_errors;
    List<double[][]> pWeights;
    List<double[]> pBaises;

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

        pConf = readPerceptron(fileNamePerceptronConf);
        learnSetList = readLearnSet(fileNameLearnSer);

        pLayers = new ArrayList<>();
        pLayers_errors = new ArrayList<>();
        for (Integer layerSize : pConf) {
            pLayers.add(new double[layerSize]);
            pLayers_errors.add(new double[layerSize]);
        }

        pWeights = new ArrayList<>();
        pBaises = new ArrayList<>();
        for (int i = 0; i < pLayers.size() - 1; i++) {
            pWeights.add(new double[pConf.get(i + 1)][pConf.get(i)]);
            pBaises.add(new double[pLayers.get(i + 1).length]);
        }

        init();
    }

    public void masTrain() {//TODO accumulate weights deltas, and add them after train epoch
        Scanner in = new Scanner(System.in);

        System.out.println("Would you like to ste biases? y/n");
        String ans = in.nextLine();

        if (ans.equals("y")) {
            for (int i = 0; i < pBaises.size(); i++) {
                double[] curBias = pBaises.get(i);
                for (int j = 0; j < curBias.length; j++) {

                    System.out.println("Enter layer - " + (i + 1) + ", neuron " + j + " bias");
                    double value = in.nextDouble();
                    in.nextLine();
                    curBias[j] = value;
                }
            }
        }

        System.out.println("Enter the number of iterations");
        int iters = in.nextInt();
        in.nextLine();

        //iter's times train
        for (int i = 0; i < iters; i++) {

            //clone learn sets to temp list
            List<learnSet> shuffledLearnSets = new ArrayList<>();
            for (learnSet set : learnSetList) {
                shuffledLearnSets.add(set);
            }
            //shuffle temp list
            Collections.shuffle(shuffledLearnSets);
            //use shuffled list
            for (learnSet set : shuffledLearnSets) {
                train(set.inputs, set.outputs);
            }
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
        return x * (1 - x);
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

    private void printOutNeurons() {
        double[] outNeurons = pLayers.get(pLayers.size() - 1);
        for (double outNeuron : outNeurons) {
            System.out.println(outNeuron);
        }
    }

    private void init() {
        initMatrix(pWeights);
        initMas(pBaises);
        //initMas(pLayers);
        //initMas(pLayers_errors);
    }

    private void initMatrix(List<double[][]> pWeights) {
        for (double[][] curWeights : pWeights) {

            int rows = curWeights.length;
            int columns = curWeights[0].length;
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    //curWeights[i][j] = 0.5 + (Math.random() * ((Max - Min) + 1))
                    curWeights[i][j] = Math.random() - 0.5;//* 0.2 + 0.1;

                }
            }
        }
    }

    private void initMas(List<double[]> pLayers) {
        for (double[] curLayer : pLayers) {

            int rows = curLayer.length;
            for (int i = 0; i < rows; i++) {
                curLayer[i] = Math.random() - 0.5;


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

            double[] curBais = pBaises.get(i);

            //activation function
            int rows = outputs.length;
            int columns = outputs[0].length;
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < columns; col++) {
                    outputs[row][col] = function(outputs[row][col] + curBais[row]);

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

        //calc err of last layer (output)
        double[][] outputErrors = subtractMatrix(targets, outputs); //TODO kvadratich otklonenie
        pLayers_errors.set(pLayers_errors.size() - 1, toArray(outputErrors));

        for (int l = pLayers.size() - 2; l >= 1; l--) {

            double[][] transposeWeights = transposeMatrix(pWeights.get(l));
            //calc errors for each layer
            double[][] curLayerErrors = multiplyByMatrix(transposeWeights, outputErrors);//TODO check this

            pLayers_errors.set(l, toArray(curLayerErrors));

            outputErrors = curLayerErrors;
        }//calc all neurons errors

        //////change weights
        //   // calc gradients
        for (int l = pLayers.size() - 2; l >= 0; l--) {// to 0 or 1
            double[] curOutputErrors = pLayers_errors.get(l + 1);

            double[] curOutput = pLayers.get(l + 1);

            int rows = curOutput.length;
            double[] gradients = new double[rows];
            for (int row = 0; row < rows; row++) {
                gradients[row] = learnFactor * curOutputErrors[row] * derivative(curOutput[row]);
            }

            //change cur layers bias
            double[] curBias = pBaises.get(l);
            curBias = toArray(addMatrix(toMatrix(curBias), toMatrix(gradients)));
            pBaises.set(l, curBias);

            //calc weights delta
            double[] curLayer = pLayers.get(l);
            double[][] deltaWeights = multiplyByMatrix(toMatrix(gradients), transposeMatrix(toMatrix(curLayer)));

            double[][] resWeights = addMatrix(pWeights.get(l), deltaWeights);
            pWeights.set(l, resWeights);
            //pWeights.set(l, addMatrix(pWeights.get(l), deltaWeights));

        }

    }
}
