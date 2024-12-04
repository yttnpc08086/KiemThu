package com.nhom4;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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

    @Test(priority = 4)
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
    public void testLogin(String username, String password, String expectedMessage) {
        login(username, password);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            Thread.sleep(1000);
//            // Chờ hộp thoại thông báo hiển thị
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
                {"", "", "Tên tài khoản hoặc mật khẩu rỗng. Vui lòng nhập đầy đủ"},
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
                {"", "", "Đăng nhập không thành công\n" +
                        "Tài khoản hoặc mật khẩu không đúng\n" +
                        "OK"},
                {"", "", "Đăng nhập không thành công\n" +
                        "Tài khoản hoặc mật khẩu không đúng\n" +
                        "OK"},
//                {"admin", "123", "Đăng nhập thành công"},
        };
    }
    public void login(String username, String password) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // Tìm và điền thông tin vào các trường đăng nhập
            WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@placeholder='Nhập tài khoản của bạn']")));
            WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@placeholder='Nhập mật khẩu của bạn']")));
            WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@type='submit']")));
            WebElement remember = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@type='checkbox']")));

            usernameField.clear();
            usernameField.sendKeys(username);
            passwordField.clear();
            passwordField.sendKeys(password);
            remember.click();
            loginButton.click();
        } catch (Exception e) {
            Assert.fail("Không tìm thấy các trường cần thiết để đăng nhập.", e);
        }
    }

    @Test(priority = 2)
    public void Remember() {
        login("admin", "123");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // Đợi một thời gian sau khi đăng nhập (tuỳ thuộc vào ứng dụng của bạn)
            wait.until(ExpectedConditions.urlContains("home")); // Giả sử sau khi đăng nhập, URL sẽ chứa 'home'

            // Kiểm tra localStorage
            JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
            String authToken = (String) jsExecutor.executeScript("return localStorage.getItem('authToken');");

            // Kiểm tra xem giá trị đã được lưu trong localStorage hay chưa
            Assert.assertNotNull(authToken, "authToken không được lưu trong localStorage.");

        } catch (Exception e) {
            Assert.fail("Đã xảy ra lỗi khi kiểm tra đăng nhập.", e);
        }
    }

}
