package org.example;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

    public class FirstSeleniumTest {

        public static void main(String[] args) {
            WebDriver driver = new ChromeDriver();
            driver.get("https://www.saucedemo.com");
            System.out.println("Page title is: " + driver.getTitle());
            driver.findElement(By.id("user-name")).sendKeys("standard_user");
            driver.findElement(By.id("password")).sendKeys("secret_sauce");
            driver.findElement(By.id("login-button")).click();
            System.out.println("Page title is: " + driver.getCurrentUrl());
            driver.quit();
        }
    }

