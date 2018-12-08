package com.company;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);

        System.out.println("Enter name of file with Input Data");
        String fileName1 = in.nextLine();
        System.out.println("Enter name of file with Test Data");
        String fileName2 = in.nextLine();

        fileName1 = "InputData_1.txt";
        fileName2 = "TestData_1.txt";
        Perceptron perceptron = new Perceptron(fileName1, fileName2, 2, 1);


    }

}


