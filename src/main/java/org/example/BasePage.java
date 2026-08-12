package org.example;

 interface BasePage {
     void open();
     String getTitle();
     void enterUsername(String username);
     void enterPassword(String password);
     void clickLogin();


}

