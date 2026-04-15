package com.automation.tests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.*;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;

public class BBMSTests {

    private WebDriver driver;
    private final String baseUrl = "http://localhost:8082/bbms";

    @BeforeMethod
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        // options.addArguments("--headless");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();
    }

    @Test(priority = 1, description = "Verify successful admin login")
    public void testAdminLogin() {
        driver.get(baseUrl + "/login");
        driver.findElement(By.id("userId")).sendKeys("ADMIN001");
        driver.findElement(By.id("password")).sendKeys("Admin@123");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        String headerText = driver.findElement(By.className("page-title")).getText();
        Assert.assertEquals(headerText, "Admin Dashboard", "Admin login failed or dashboard not reached!");
    }

    @Test(priority = 2, description = "Verify successful student login")
    public void testStudentLogin() {
        driver.get(baseUrl + "/login");
        driver.findElement(By.id("userId")).sendKeys("STU2024001");
        driver.findElement(By.id("password")).sendKeys("Student@123");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Student dashboard says "Welcome, Arjun Mehta 👋" in an h1 with class page-title
        String welcomeText = driver.findElement(By.className("page-title")).getText();
        Assert.assertTrue(welcomeText.contains("Welcome"), "Student login failed!");
    }

    @Test(priority = 3, description = "Verify invalid login attempt")
    public void testInvalidLogin() {
        driver.get(baseUrl + "/login");
        driver.findElement(By.id("userId")).sendKeys("WRONG_USER");
        driver.findElement(By.id("password")).sendKeys("WrongPass");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        WebElement errorMsg = driver.findElement(By.className("alert-danger"));
        Assert.assertTrue(errorMsg.isDisplayed(), "Error message NOT shown on invalid login!");
    }

    @Test(priority = 4, description = "Verify book search functionality")
    public void testBookSearch() {
        // Login as student first
        driver.get(baseUrl + "/login");
        driver.findElement(By.id("userId")).sendKeys("STU2024001");
        driver.findElement(By.id("password")).sendKeys("Student@123");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Navigate to books
        driver.findElement(By.linkText("Books")).click();

        // Search for a book
        String keyword = "Clean Code";
        driver.findElement(By.name("keyword")).sendKeys(keyword);
        driver.findElement(By.xpath("//button[text()='Search']")).click();

        // Verify result in table
        WebElement table = driver.findElement(By.tagName("table"));
        String tableText = table.getText();
        Assert.assertTrue(tableText.contains(keyword), "Search result doesn't contain the keyword: " + keyword);
    }

    @Test(priority = 5, description = "Verify logout functionality")
    public void testLogout() {
        // Login as student
        driver.get(baseUrl + "/login");
        driver.findElement(By.id("userId")).sendKeys("STU2024001");
        driver.findElement(By.id("password")).sendKeys("Student@123");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Click logout
        driver.findElement(By.linkText("Logout")).click();

        // Verify return to login page
        String url = driver.getCurrentUrl();
        Assert.assertTrue(url.contains("/login"), "Logout didn't redirect to login page! Current URL: " + url);
        Assert.assertTrue(driver.findElement(By.id("userId")).isDisplayed(), "Login form not visible after logout!");
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
