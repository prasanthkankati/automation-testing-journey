package org.example;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

    public class FirstSeleniumTest {

        public static void main(String[] args) {
            WebDriver driver = new ChromeDriver();
            driver.get("https://www.saucedemo.com");
            System.out.println("Page title is: " + driver.getTitle());

            driver.quit();
        }
    }

