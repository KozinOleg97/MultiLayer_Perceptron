package com.company;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);

        System.out.println("Enter name of file with Input Data");
        String fileName1 = in.nextLine();
        System.out.println("Enter name of file with Test Data");
        String fileName2 = in.nextLine();

        fileName1 = "LearningInputData_1.txt";
        fileName2 = "LearningOutputData_1.txt";
        Perceptron perceptron = new Perceptron(fileName1, fileName2, 2, 1);

        while (true) {

            System.out.println("0 - manual data enter;  1 - data from file");
            int userChoice = in.nextInt();
            in.nextLine();

            switch (userChoice) {
                case 0:
                    perceptron.work();
                    break;
                case 1:
                    System.out.println("Enter name of file with Data");
                    String fileNameData = in.nextLine();
                    perceptron.work(fileNameData);
                    break;
                default:
                    return;
            }

        }


    }

}


