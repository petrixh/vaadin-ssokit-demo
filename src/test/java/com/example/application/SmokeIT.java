package com.example.application;

import java.util.logging.Logger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.By;

import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.testbench.BrowserTest;
import com.vaadin.testbench.BrowserTestBase;
import com.vaadin.testbench.TestBench;

public class SmokeIT extends BrowserTestBase {

    private static Logger log = Logger.getLogger(SmokeIT.class.getName());

    @BeforeEach
    public void setup() throws Exception {
        // Open the application
        getDriver().get("http://localhost:8080/");
    }

    // Please note that since TestBench 9 test methods
    // must be annotated with helper @BrowserTest annotation.
    @BrowserTest
    public void clickButton() {

        log.info("Starting test...");

        log.info("URL: " + getDriver().getCurrentUrl());
        //log.info("Src: " + getDriver().getPageSource());

        doKeycloakLogin("test", "test");

        log.info("Login done...");

        log.info("URL: " + getDriver().getCurrentUrl());
        //log.info("Src: " + getDriver().getPageSource());

        log.info("Sleeping...");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        log.info("Done sleeping");

        log.info("URL: " + getDriver().getCurrentUrl());
        //log.info("Src: " + getDriver().getPageSource());


        // Find the first button (<vaadin-button>) on the page
        ButtonElement button = $(ButtonElement.class).first();

        log.info("Found button: " + button);

        Assertions.assertTrue(button.isDisplayed(), "Button not found");

        log.info("Attempting logout...");

        //Logout
        log.info("URL: " + getDriver().getCurrentUrl());
        ButtonElement logoutButton = $(ButtonElement.class).withCaption("Logout").first();
        log.info("Found logout button: " + button);
        logoutButton.click();   

        //Check if we are back to the login page
        log.info("URL: " + getDriver().getCurrentUrl());
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