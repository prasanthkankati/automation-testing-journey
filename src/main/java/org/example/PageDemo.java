package org.example;

public class PageDemo {

    public static void main(String[] args) {


        LoginPage lp = new LoginPage();
        lp.open();
        System.out.println(lp.getTitle());

    }
}
