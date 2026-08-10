package org.example;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

public class FileReadingPractice {

    public static void main(String[] args) {
        BufferedReader br = null;   // declared outside try, so finally can reach it

        try {
            FileReader fr = new FileReader("src/main/resources/testdata.txt");
            br = new BufferedReader(fr);

            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }

        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());

        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    System.out.println("Error closing file: " + e.getMessage());
                }
            }
        }
    }
}