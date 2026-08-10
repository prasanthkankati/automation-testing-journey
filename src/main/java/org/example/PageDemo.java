package org.example;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
public class PageDemo {

    public static void main(String[] args) {


        LoginPage lp = new LoginPage();



        lp.open();
        System.out.println(lp.getTitle());

    }

}
