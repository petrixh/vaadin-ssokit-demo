package com.example.application;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.By;

import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.testbench.BrowserTest;
import com.vaadin.testbench.BrowserTestBase;
import com.vaadin.testbench.TestBench;

public class SmokeIT extends BrowserTestBase {

    @BeforeEach
    public void setup() throws Exception {
        // Open the application
        getDriver().get("http://localhost:8080/");
    }

    // Please note that since TestBench 9 test methods
    // must be annotated with helper @BrowserTest annotation.
    @BrowserTest
    public void clickButton() {

        doKeycloakLogin("test", "test");


        // Find the first button (<vaadin-button>) on the page
        ButtonElement button = $(ButtonElement.class).first();

        // Click it
        button.click();

        // Check that text of the button is "Clicked"
        Assertions.assertEquals("Say hello", button.getText());

        //Logout
        $(ButtonElement.class).withCaption("Logout").first().click();   

        Assertions.assertTrue(isKcLoginPage(), "Keycloak login page not found");
    }

    protected void doKeycloakLogin(String user, String pass){

        Assertions.assertTrue(isKcLoginPage(), "Keycloak login page not found");

        getDriver().findElement(By.id("username")).sendKeys(user);
        getDriver().findElement(By.id("password")).sendKeys(pass);
        getDriver().findElement(By.id("kc-login")).click();

    }

    private boolean isKcLoginPage() {
                //Check if the body has the ID keycloak-bg
        return getDriver().findElement(By.id("keycloak-bg")).isDisplayed();
    }

}