package com.nhom4;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class RegisterTest {

    private WebDriver driver;
    private final String url = "http://localhost:3000/login";

    @BeforeMethod
    public void openBrowser() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        driver.navigate().to(url);
    }

    @AfterMethod
    public void closeBrowser() {
        System.out.println("Closing browser");
        if (driver != null) {
            driver.quit(); // Close the entire browser
        }
    }

//    @Test(priority = 2)
//    public void testValidRegister() {
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10000));
//
//        WebElement registerLink = driver.findElement(By.xpath("//button[contains(text(),'Đăng ký')]"));
//        registerLink.click();
//        WebElement registerForm = driver.findElement(By.xpath("//h2[contains(text(),'Đăng Ký')]"));
//        if (registerForm.isDisplayed()) {
//            Register("Trần Thị Như Ý", "tranthinhuy28012020@gmail.com", "0367173020", "nhuy", "123", "123");
//            System.out.println("Hiện form đăng kí");
//            driver.manage().timeouts().implicitlyWait(50, TimeUnit.SECONDS);
//            System.out.println("Chờ mã OTP");
//        }
//        try {
//            // Wait for success message to appear
//            WebElement dialogElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='Toastify__toast-container Toastify__toast-container--top-right']")));
//            Assert.assertTrue(dialogElement.isDisplayed(), "Thông báo thành công hiển thị.");
//            System.out.println("Test case passed");
//        } catch (Exception e) {
//            Assert.fail("Không tìm thấy thông báo thành công.", e);
//        }
//    }

    @Test(dataProvider = "RegisterData", priority = 1)
    public void testRegisterFail(String fullname, String email, String phone, String username, String password, String passConfirm, String expectedMessage) {
        Register(fullname, email, phone, username, password, passConfirm);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // Wait for error dialog to appear
            WebElement dialogElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@role='dialog']")));
            String actualMessage = dialogElement.getText();
            Assert.assertEquals(actualMessage, expectedMessage);

            // Click OK button if available
            WebElement okButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[normalize-space()='OK']")));
            okButton.click();
        } catch (Exception e) {
            Assert.fail("Dialog không xuất hiện hoặc không thể tương tác với nút 'OK'.", e);
        }
    }

    @DataProvider(name = "RegisterData")
    public Object[][] RegisterData() {
        return new Object[][]{
                {"", "nguyenvana@gmail.com", "0987654321", "nguyenvana", "password123", "password123", "Thông tin không hợp lệ"}, // Invalid email format
                {"Trần Thị Như Ý", "nguyenvana@gmail.com", "123456789", "admin", "1234", "1234", "Tài khoản đã tồn tại"}, // Username exists
                {"Trần Thị Như Ý", "nguyenvana@gmail.com", "123456789", "", "1234", "123", "Mật khẩu không khớp"} // Password mismatch
        };
    }

    private void Register(String fullname, String email, String phone, String username, String password, String passConfirm) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // Locate and fill in the registration fields
            WebElement fullnameField = driver.findElement(By.xpath("//input[@placeholder='Nhập họ tên của bạn']"));
            WebElement emailField = driver.findElement(By.xpath("//input[@placeholder='Nhập email của bạn']"));
            WebElement phoneField = driver.findElement(By.xpath("//input[@placeholder='Nhập số điện thoại của bạn']"));
            WebElement usernameField = driver.findElement(By.xpath("//input[@placeholder='Nhập tài khoản của bạn']"));
            WebElement passwordField = driver.findElement(By.xpath("//input[@placeholder='Nhập mật khẩu của bạn']"));
            WebElement passConfirmField = driver.findElement(By.xpath("//input[@placeholder='Nhập lại mật khẩu của bạn']"));
            WebElement registerButton = driver.findElement(By.xpath("//button[contains(text(),'Đăng Ký')]"));

            fullnameField.clear();
            fullnameField.sendKeys(fullname);

            emailField.clear();
            emailField.sendKeys(email);

            phoneField.clear();
            phoneField.sendKeys(phone);

            usernameField.clear();
            usernameField.sendKeys(username);

            passwordField.clear();
            passwordField.sendKeys(password);

            passConfirmField.clear();
            passConfirmField.sendKeys(passConfirm);

            registerButton.click();
        } catch (Exception e) {
            Assert.fail("Không tìm thấy các trường cần thiết để đăng kí.", e);
        }
    }
}
