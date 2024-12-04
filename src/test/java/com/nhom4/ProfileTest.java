package com.nhom4;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class ProfileTest {

    private WebDriver driver;
    private final String baseUrl = "http://localhost:3000";

    @BeforeClass
    public void openBrowser() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.navigate().to(baseUrl);
    }

    @AfterClass
    public void closeBrowser() {
        System.out.println("Closing browser");
        if (driver != null) {
            driver.quit(); // Close the entire browser
        }
    }

    @Test(priority = 1)
    public void loginTest() throws InterruptedException {
        driver.get(baseUrl + "/login");  // Truy cập trang đăng nhập

        // Nhập thông tin đăng nhập (Cập nhật theo form đăng nhập của bạn)
        WebElement usernameField = driver.findElement(By.xpath("//input[@placeholder='Nhập tài khoản của bạn']"));
        WebElement passwordField = driver.findElement(By.xpath("//input[@placeholder='Nhập mật khẩu của bạn']"));
        WebElement loginButton = driver.findElement(By.xpath("//button[@type='submit']"));

        usernameField.sendKeys("user");  // Thay đổi theo thông tin đăng nhập của bạn
        passwordField.sendKeys("123");  // Thay đổi theo mật khẩu của bạn

        // Nhấn nút đăng nhập
        loginButton.click();
        Thread.sleep(50000);
        // Kiểm tra xem đăng nhập thành công hay không (kiểm tra bằng URL hoặc một phần tử xuất hiện sau khi đăng nhập)
//        WebElement loggedInElement = driver.findElement(By.xpath("//);div[@class='h-screen bg-white transition-width duration-300 w-64']"));
//        Assert.assertNotNull(loggedInElement, "Đăng nhập không thành công!"
    }

    @DataProvider(name = "profileData")
    public Object[][] profileData() {
        return new Object[][]{
                {"Nhuy###", "yttnpc08086@fpt.edu.vn", "0367173020", "Cập nhật thất bại.Vui lòng thử lại."},
                {"", "yttnpc08086@fpt.edu.vn", "0367173020", "Cập nhật thất bại.Vui lòng thử lại."},
                {"123", "yttnpc08086@fpt.edu.vn", "0367173020", "Tên không được chứa số."},
                {"Nhuy", "", "0367173020", "Cập nhật thất bại.Vui lòng thử lại."},
                {"Nhuy", "yttnpc08086qwertyuiopasdfghjklnbvcxzawertyuqwertyuiopasdfghjkloiuytrewqazxcvbnmmmiopasdfghjklzzzz@fpt.edu.vn", "0367173020", "Cập nhật thất bại.Vui lòng thử lại."},
                {"Nhuy", "yttnpc08086@fpt.edu.vn", "", "Cập nhật thất bại.Vui lòng thử lại."},
                {"Nhuy", "yttnpc08086@fpt.edu.vn", "0367173XXX", "Cập nhật thất bại.Vui lòng thử lại."},
                {"Nhuy", "yttnpc08086@fpt.edu.vn", "036717130201234567890", "Cập nhật thất bại.Vui lòng thử lại."},
                {"Nhuy", "yttnpc08086@fpt.edu.vn", "0367173020", "Cập nhật thành công!"}
        };
    }


    @Test(dataProvider = "profileData", priority = 2, dependsOnMethods = "loginTest")
    public void profileTest(String fullName, String email, String phone, String expectedMessage) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30)); // Increased wait time
        driver.get(baseUrl + "/profile");

        try {
            WebElement userIcon = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//i[@class='fas fa-user text-gray-700 hover:text-orange-500 cursor-pointer']")));
            userIcon.click();

            // Chuyển đến mục "Hồ sơ"
            WebElement profileLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(),'Hồ sơ')]")));
            profileLink.click();

            profile(fullName, email, phone);

            WebElement messageElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class, 'Toastify__toast') and contains(@class, 'Toastify__toast-theme--light') and contains(@class, 'Toastify__toast--success')]")));
            String messageActual = messageElement.getText();
            Assert.assertEquals(messageActual, expectedMessage);
            System.out.println("Test case passed");

        } catch (TimeoutException e) {
            Assert.fail("Element not found or not clickable within the specified time", e);
        } catch (NoSuchElementException e) {
            Assert.fail("Không tìm thấy thông báo", e);
        }
    }

    public void profile(String fullName, String email, String phone) {

        WebElement fullNameField = driver.findElement(By.xpath("//input[@placeholder='Nhập tên đầy đủ']"));
        fullNameField.clear();
        fullNameField.sendKeys(fullName);

        WebElement emailField = driver.findElement(By.xpath("//input[@placeholder='Nhập email']"));
        emailField.clear();
        emailField.sendKeys(email);

        WebElement phoneField = driver.findElement(By.xpath("//input[@placeholder='Nhập số điện thoại']"));
        phoneField.clear();
        phoneField.sendKeys(phone);

        WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(), 'Lưu Thay Đổi')]"));
        saveButton.click();
    }
}


