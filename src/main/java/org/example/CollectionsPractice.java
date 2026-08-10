package org.example;

import java.util.*;

public class CollectionsPractice {
    public static void main(String[] args) {
        // List: ordered, duplicates allowed
       List<String> testCases=new ArrayList<>();
        testCases.add("Login Test");
        testCases.add("Checkout Test");
        testCases.add("Search Test");


        // Map: key-value, tracks each test's result
      Map<String, String> results=new HashMap<>();
        results.put("Login Test","Pass");
        results.put("Checkout Test","Fail");
        results.put("Search Test","Pass");

        // Loop through the map and print a summary
        int passCount = 0, failCount = 0;
        for(Map.Entry<String, String> entry: results.entrySet()){

            System.out.println(entry.getKey()+" : "+entry.getValue());
            if (entry.getValue().equals("Pass")){

                passCount++;
                System.out.println("Counted as Pass :"+passCount);
            }
            else {
                failCount++;
                System.out.println("Counted as Fail :" + failCount);
            }

        }
        System.out.println("Total Pass: " + passCount + ", Total Fail: " + failCount);
        Set<String> browser=new LinkedHashSet<>();

        browser.add("Firefox");
        browser.add("Safari");
        browser.add("Chrome");

        System.out.println(browser);

    }







}