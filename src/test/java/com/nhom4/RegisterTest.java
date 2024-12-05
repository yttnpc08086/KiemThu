package com.nhom4;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
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


    @Test(dataProvider = "RegisterData", priority = 1)
    public void testRegisterFail(String fullname, String email, String phone, String username, String password, String passConfirm, String expectedMessage) {

        // Mở form đăng ký
        WebElement registerLink = driver.findElement(By.xpath("//button[contains(text(),'Đăng ký')]"));
        registerLink.click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement registerForm = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[contains(text(),'Đăng Ký')]")));


        // Kiểm tra form hiển thị
        Assert.assertTrue(registerForm.isDisplayed(), "Form đăng ký không hiển thị!");

        // Điền thông tin đăng ký
        Register(fullname, email, phone, username, password, passConfirm);
        try {
            // Chờ thông báo xuất hiện
            WebElement toastMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(".Toastify__toast.Toastify__toast-theme--light")));

            // Kiểm tra loại thông báo (thành công hay thất bại)
            String toastClass = toastMessage.getAttribute("class");
            String messageActual = toastMessage.getText();

            if (toastClass.contains("Toastify__toast--success")) {
                Assert.assertEquals(messageActual, expectedMessage, "Thông báo thành công không khớp!");
                System.out.println("Thành công: " + messageActual);
            } else if (toastClass.contains("Toastify__toast--error")) {
                Assert.assertEquals(messageActual, expectedMessage, "Thông báo thất bại không khớp!");
                System.out.println("Thất bại: " + messageActual);
            } else {
                Assert.fail("Không xác định loại thông báo!");
            }
        } catch (TimeoutException e) {
            Assert.fail("Không có thông báo hiển thị!", e);
        }
    }


    @DataProvider(name = "RegisterData")
    public Object[][] RegisterData() {
        return new Object[][]{
                {"", "nguyenvana@gmail.com", "0987654321", "nguyenvana", "123", "123", "Vui lòng nhập họ tên của bạn"}, 
                {"Nguyễn Văn A", "", "0987654321", "nguyenvana", "123", "123", "Vui lòng nhập email của bạn"},
                {"Nguyễn Văn A", "nguyenvana", "0987654321", "nguyenvana", "123", "123", "Email không hợp lệ"},
                {"Nguyễn Văn A", "tranthinhuy28012020@gmail.com", "0987654321", "nguyenvana", "123", "123", "Email đã được sử dụng"},
                {"Nguyễn Văn A", "nguyenvana@gmail.com", "", "nguyenvana", "123", "123", "Vui lòng nhập số điện thoại của bạn"},
                {"Nguyễn Văn A", "nguyenvana@gmail.com", "098765432X", "nguyenvana", "123", "123", "Số điện thoại không hợp lệ"},
                {"Nguyễn Văn A", "nguyenvana@gmail.com", "0367172020", "nguyenvana", "123", "123", "Số điện thoại đã tồn tại"},
                {"Nguyễn Văn A", "nguyenvana@gmail.com", "09876543211", "nguyenvana", "123", "123", "Số điện thoại không hợp lệ"},
                {"Nguyễn Văn A", "nguyenvana@gmail.com", "0987654321", "", "123", "123", "Vui lòng nhập tài khoản của bạn"},
                {"Nguyễn Văn A", "nguyenvana@gmail.com", "0987654321", "admin", "123", "123", "Tên tài khoản đã tồn tại"},
                {"Nguyễn Văn A", "nguyenvana@gmail.com", "0987654321", "nguyenvana", "", "123", "Vui lòng nhập mật khẩu"},
                {"Nguyễn Văn A", "nguyenvana@gmail.com", "0987654321", "nguyenvana", "123", "123", "Mật khẩu phải có ít nhất 6 ký tự"},
                {"Nguyễn Văn A", "nguyenvana@gmail.com", "0987654321", "nguyenvana", "123", "1234", "Mật khẩu không khớp"},
                {"Nguyễn Văn A", "nguyenvana@gmail.com", "0987654321", "###", "123", "123", "Tài khoản không hợp lệ, chỉ được chứa chữ cái, số và dấu gạch dưới"},


        };
    }

    private void Register(String fullname, String email, String phone, String username, String password, String passConfirm) {

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
            e.printStackTrace();
        }
    }
}
