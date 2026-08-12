package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

class LoginPage implements BasePage {

     WebDriver driver;
     LoginPage(WebDriver driver){
         this.driver=driver;
    }

     public void open() {

         driver.get("https://www.saucedemo.com");
     }

     public String getTitle() {

         return driver.getTitle();

     }
    public String getCurrentTitle() {

        return driver.getCurrentUrl();

    }
     public void enterUsername(String username){
         driver.findElement(By.id("user-name")).sendKeys(username);

     }
     public void enterPassword(String password){
         driver.findElement(By.id("password")).sendKeys(password);
     }
     public void clickLogin(){
         driver.findElement(By.id("login-button")).click();
     }


 }