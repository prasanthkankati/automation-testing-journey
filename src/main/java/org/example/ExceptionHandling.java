package org.example;

public class ExceptionHandling {



    public static void main(String[] args) {
        int[] arr={10,20,20};
        try {
            System.out.println(arr[5]);
        } catch (Exception e) {
            System.out.println("Error: Tried to access an index that doesn't exist - " + e.getMessage());
        }
        finally {

            System.out.println("Done attempting to access array");

        }

    }
}
