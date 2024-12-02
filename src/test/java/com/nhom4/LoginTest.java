package com.nhom4;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.Duration;

public class LoginTest {
    private WebDriver driver;
    private final String url = "http://localhost:3000/login";

    @BeforeClass
    public void openBrowser() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.navigate().to(url);
    }

    @AfterClass
    public void closeBrowser() {
        System.out.println("Closing browser");
        if (driver != null) {
            driver.quit(); // Đóng toàn bộ trình duyệt
        }
    }

    @Test(priority = 2)
    public void testValidLogin() {
        login("admin", "123");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // Chờ thông báo thành công hiển thị
            WebElement dialogElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@role='dialog']")));
            Assert.assertTrue(dialogElement.isDisplayed(), "Thông báo thành công không hiển thị.");
            System.out.println("Test case passed");
        } catch (Exception e) {
            Assert.fail("Không tìm thấy thông báo thành công.", e);
        }
    }

    @Test(dataProvider = "loginData", priority = 1)
    public void testLoginFail(String username, String password, String expectedMessage) {
        login(username, password);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // Chờ hộp thoại thông báo hiển thị
            WebElement dialogElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@role='dialog']")));

            // Lấy lại phần tử nút OK sau khi hộp thoại đã hiển thị
            WebElement okButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[normalize-space()='OK']")));

            // Click vào nút OK
            okButton.click();

            // Kiểm tra thông báo
            String actualMessage = dialogElement.getText();
            Assert.assertEquals(actualMessage, expectedMessage);
        } catch (Exception e) {
            Assert.fail("Không tìm thấy thông báo hoặc không thể tương tác với nút 'OK'.", e);
        }
    }

    @DataProvider(name = "loginData")
    public Object[][] loginData() {
        return new Object[][]{
//                {"", "", "Tên tài khoản hoặc mật khẩu rỗng. Vui lòng nhập đầy đủ"},
                {"aaa", "123456", "Đăng nhập không thành công\n" +
                        "Tài khoản hoặc mật khẩu không đúng\n" +
                        "OK"},
                {"admin", "123456", "Đăng nhập không thành công\n" +
                        "Tài khoản hoặc mật khẩu không đúng\n" +
                        "OK"},
                {"aaa", "123", "Đăng nhập không thành công\n" +
                        "Tài khoản hoặc mật khẩu không đúng\n" +
                        "OK"},
                {"             ", "123", "Đăng nhập không thành công\n" +
                        "Tài khoản hoặc mật khẩu không đúng\n" +
                        "OK"},
                {"admin", "             ", "Đăng nhập không thành công\n" +
                        "Tài khoản hoặc mật khẩu không đúng\n" +
                        "OK"},
                {"admin@", "123", "Đăng nhập không thành công\n" +
                        "Tài khoản hoặc mật khẩu không đúng\n" +
                        "OK"},
//                {"admin", "", "Đăng nhập không thành công\n" +
//                        "Tài khoản hoặc mật khẩu không đúng\n" +
//                        "OK"},
        };
    }
    private void login(String username, String password) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // Tìm và điền thông tin vào các trường đăng nhập
            WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@placeholder='Nhập tài khoản của bạn']")));
            WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@placeholder='Nhập mật khẩu của bạn']")));
            WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@type='submit']")));

            usernameField.clear();
            usernameField.sendKeys(username);
            passwordField.clear();
            passwordField.sendKeys(password);
            loginButton.click();
        } catch (Exception e) {
            Assert.fail("Không tìm thấy các trường cần thiết để đăng nhập.", e);
        }
    }
}
