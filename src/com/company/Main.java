package com.company;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);

        System.out.println("Enter name of file with perceptron config");
        String fileName1 = in.nextLine();
        System.out.println("Enter name of file with learn sets");
        String fileName2 = in.nextLine();

        //fileName1 = "LearningInputData_1.txt";
        //fileName2 = "LearningOutputData_1.txt";
        Perceptron perceptron = new Perceptron(fileName2, fileName1);

        while (true) {

            System.out.println("0 - train on data from file;  1 - manual use");
            int userChoice = in.nextInt();
            in.nextLine();

            switch (userChoice) {
                case 0:
                    perceptron.masTrain();
                    break;
                case 1:
                    perceptron.manualUse();
                    break;
                case 2:
                    System.out.println("Enter name of file with Data");
                    String fileNameData = in.nextLine();
                    //perceptron.work(fileNameData);
                    break;
                default:
                    return;
            }

        }


    }

}


