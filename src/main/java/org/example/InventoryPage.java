package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class InventoryPage {

    WebDriver driver;

    InventoryPage(WebDriver driver){
        this.driver=driver;
    }

    String getPageHeaderText(){

       String headerText= driver.findElement(By.cssSelector("[data-test='title']")).getText();
       return headerText;
    }

    void addItemToCart(String productId){

      driver.findElement(By.id(productId)).click();

    }
    String getCartBadgeCount(){

        return driver.findElement(By.className("shopping_cart_badge")).getText();

    }
}
