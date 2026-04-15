package com.automation.tests;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.*;
import io.github.bonigarcia.wdm.WebDriverManager;

public class FirstTestNGTest {

    WebDriver driver;

    @BeforeClass
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test
    public void verifyTitle() {
        driver.get("https://www.google.com");
        String title = driver.getTitle();
        Assert.assertEquals(title, "Google", "Title mismatch!");
    }

    @Test
    public void verifyUrl() {
        driver.get("https://www.google.com");
        String url = driver.getCurrentUrl();
        Assert.assertTrue(url.contains("google"), "URL mismatch!");
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) driver.quit();
    }
}
