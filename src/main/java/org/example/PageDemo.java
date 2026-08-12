package org.example;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
public class PageDemo {

    public static void main(String[] args) {
        WebDriver driver=new ChromeDriver();

        LoginPage lp = new LoginPage(driver);
        InventoryPage ip=new InventoryPage(driver);

        lp.open();
        System.out.println(lp.getTitle());
        lp.enterUsername("standard_user");
        lp.enterPassword("secret_sauce");
        lp.clickLogin();
        System.out.println(lp.getCurrentTitle());
        System.out.println(ip.getPageHeaderText());
        ip.addItemToCart("add-to-cart-sauce-labs-backpack");
        System.out.println("Cart count: " + ip.getCartBadgeCount());
        driver.quit();


    }

}
