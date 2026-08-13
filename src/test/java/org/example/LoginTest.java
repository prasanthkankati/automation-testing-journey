package org.example;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.Assert;

public class LoginTest {

    WebDriver driver;

    @BeforeMethod
    public void setUp() {
        driver=new ChromeDriver();

    }
    @Test
    public void verifyLoginSuccess(){
        LoginPage lp = new LoginPage(driver);
        lp.open();
        lp.enterUsername("standard_user");
        lp.enterPassword("secret_sauce");
        lp.clickLogin();
        Assert.assertTrue(driver.getCurrentUrl().contains("inventory"));
    }

    @AfterMethod
    public void close() {

        driver.quit();
    }

}
