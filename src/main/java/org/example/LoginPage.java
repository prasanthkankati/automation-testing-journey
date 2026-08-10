package org.example;
 class LoginPage implements BasePage {

     public void open() {
         System.out.println("Opening the loginPage");
     }

     public String getTitle() {

         return "LoginPage";

     }


 }