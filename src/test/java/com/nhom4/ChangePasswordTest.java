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
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.Duration;

public class ChangePasswordTest {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeClass
    public void setUp() {
        // Thiết lập WebDriver và mở trình duyệt
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();

        // Mở rộng cửa sổ trình duyệt
        driver.manage().window().maximize();

        // Khởi tạo WebDriverWait với thời gian chờ 20 giây
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // Điều hướng đến trang đổi mật khẩu
        driver.get("http://localhost:3000/profile/change-password"); // URL chứa form đổi mật khẩu
    }

    @Test(priority = 1)
    public void testDisplayChangePasswordForm() {
        // Kiểm tra tiêu đề trang
        WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h1")));
        Assert.assertEquals(title.getText(), "Đổi Mật Khẩu", "Tiêu đề trang không đúng");

        // Kiểm tra các trường nhập liệu
        WebElement oldPasswordInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("oldPassword")));
        WebElement newPasswordInput = driver.findElement(By.id("newPassword"));
        WebElement confirmPasswordInput = driver.findElement(By.id("confirmPassword"));
        Assert.assertTrue(oldPasswordInput.isDisplayed(), "Trường mật khẩu cũ không hiển thị");
        Assert.assertTrue(newPasswordInput.isDisplayed(), "Trường mật khẩu mới không hiển thị");
        Assert.assertTrue(confirmPasswordInput.isDisplayed(), "Trường xác nhận mật khẩu không hiển thị");
    }

    @Test(priority = 2)
    public void testPasswordMismatch() {
        // Nhập mật khẩu mới và xác nhận mật khẩu không khớp
        driver.findElement(By.id("oldPassword")).sendKeys("OldPassword123");
        driver.findElement(By.id("newPassword")).sendKeys("NewPassword123");
        driver.findElement(By.id("confirmPassword")).sendKeys("DifferentPassword123");

        // Nhấn nút "Đổi mật khẩu"
        WebElement submitButton = driver.findElement(By.tagName("button"));
        submitButton.click();

        // Kiểm tra thông báo lỗi từ toast
        WebElement toastMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("Toastify__toast--error")));
        Assert.assertTrue(toastMessage.getText().contains("Mật khẩu xác nhận không khớp"), "Thông báo lỗi không đúng");
    }

    @Test(priority = 3)
    public void testInvalidOldPassword() {
        // Nhập mật khẩu cũ không đúng
        driver.findElement(By.id("oldPassword")).clear();
        driver.findElement(By.id("oldPassword")).sendKeys("WrongOldPassword123");
        driver.findElement(By.id("newPassword")).clear();
        driver.findElement(By.id("newPassword")).sendKeys("NewPassword123");
        driver.findElement(By.id("confirmPassword")).clear();
        driver.findElement(By.id("confirmPassword")).sendKeys("NewPassword123");

        // Nhấn nút "Đổi mật khẩu"
        WebElement submitButton = driver.findElement(By.tagName("button"));
        submitButton.click();

        // Kiểm tra thông báo lỗi từ toast
        WebElement toastMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("Toastify__toast--error")));
        Assert.assertTrue(toastMessage.getText().contains("Đã xảy ra lỗi"), "Thông báo lỗi không đúng");
    }

    @Test(priority = 4)
    public void testSuccessfulPasswordChange() {
        // Nhập thông tin hợp lệ
        driver.findElement(By.id("oldPassword")).clear();
        driver.findElement(By.id("oldPassword")).sendKeys("OldPassword123");
        driver.findElement(By.id("newPassword")).clear();
        driver.findElement(By.id("newPassword")).sendKeys("NewPassword123");
        driver.findElement(By.id("confirmPassword")).clear();
        driver.findElement(By.id("confirmPassword")).sendKeys("NewPassword123");

        // Nhấn nút "Đổi mật khẩu"
        WebElement submitButton = driver.findElement(By.tagName("button"));
        submitButton.click();

        // Kiểm tra thông báo thành công từ toast
        try {
            WebElement toastMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class, 'Toastify__toast--success')]")));
            Assert.assertTrue(toastMessage.getText().contains("Mật khẩu đã được thay đổi thành công"), "Thông báo thành công không đúng");
        } catch (TimeoutException e) {
            System.out.println("Lỗi: Không tìm thấy thông báo thành công trong thời gian chờ.");
            Assert.fail("Không tìm thấy thông báo thành công");
        }
    }


    @Test(priority = 5)
    public void testEmptyOldPassword() {
        // Để trống trường mật khẩu cũ
        driver.findElement(By.id("oldPassword")).clear();
        driver.findElement(By.id("newPassword")).clear();
        driver.findElement(By.id("newPassword")).sendKeys("NewPassword123");
        driver.findElement(By.id("confirmPassword")).clear();
        driver.findElement(By.id("confirmPassword")).sendKeys("NewPassword123");

        // Nhấn nút "Đổi mật khẩu"
        WebElement submitButton = driver.findElement(By.tagName("button"));
        submitButton.click();

        // Kiểm tra thông báo lỗi từ toast
        WebElement toastMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("Toastify__toast--error")));
        Assert.assertTrue(toastMessage.getText().contains("Mật khẩu cũ không được để trống"), "Thông báo lỗi không đúng khi mật khẩu cũ trống");
    }

    @Test(priority = 6)
    public void testInvalidNewPassword() {
        // Nhập mật khẩu mới không hợp lệ (ít hơn 8 ký tự)
        driver.findElement(By.id("oldPassword")).clear();
        driver.findElement(By.id("oldPassword")).sendKeys("OldPassword123");
        driver.findElement(By.id("newPassword")).clear();
        driver.findElement(By.id("newPassword")).sendKeys("12345");
        driver.findElement(By.id("confirmPassword")).clear();
        driver.findElement(By.id("confirmPassword")).sendKeys("12345");

        // Nhấn nút "Đổi mật khẩu"
        WebElement submitButton = driver.findElement(By.tagName("button"));
        submitButton.click();

        // Kiểm tra thông báo lỗi từ toast
        WebElement toastMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("Toastify__toast--error")));
        Assert.assertTrue(toastMessage.getText().contains("Mật khẩu mới phải có ít nhất 6 ký tự"), "Thông báo lỗi không đúng khi mật khẩu mới không hợp lệ");
    }

    @Test(priority = 7)
    public void testConfirmPasswordInvalidFormat() {
        // Nhập mật khẩu xác nhận không khớp định dạng (chứa khoảng trắng)
        driver.findElement(By.id("oldPassword")).clear();
        driver.findElement(By.id("oldPassword")).sendKeys("OldPassword123");
        driver.findElement(By.id("newPassword")).clear();
        driver.findElement(By.id("newPassword")).sendKeys("NewPassword123");
        driver.findElement(By.id("confirmPassword")).clear();
        driver.findElement(By.id("confirmPassword")).sendKeys("New Password123");

        // Nhấn nút "Đổi mật khẩu"
        WebElement submitButton = driver.findElement(By.tagName("button"));
        submitButton.click();

        // Kiểm tra thông báo lỗi từ toast
        WebElement toastMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("Toastify__toast--error")));
        Assert.assertTrue(toastMessage.getText().contains("Mật khẩu xác nhận không hợp lệ"), "Thông báo lỗi không đúng khi xác nhận mật khẩu sai định dạng");
    }

    @Test(priority = 8)
    public void testLockedAccount() {
        // Giả lập tài khoản bị khóa
        driver.findElement(By.id("oldPassword")).clear();
        driver.findElement(By.id("oldPassword")).sendKeys("OldPassword123");
        driver.findElement(By.id("newPassword")).clear();
        driver.findElement(By.id("newPassword")).sendKeys("NewPassword123");
        driver.findElement(By.id("confirmPassword")).clear();
        driver.findElement(By.id("confirmPassword")).sendKeys("NewPassword123");

        // Nhấn nút "Đổi mật khẩu"
        WebElement submitButton = driver.findElement(By.tagName("button"));
        submitButton.click();

        // Kiểm tra thông báo lỗi từ toast
        WebElement toastMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("Toastify__toast--error")));
        Assert.assertTrue(toastMessage.getText().contains("Tài khoản của bạn đã bị khóa"), "Thông báo lỗi không đúng khi tài khoản bị khóa");
    }


    @AfterClass
    public void tearDown() {
        // Đóng trình duyệt
        if (driver != null) {
            driver.quit();
        }
    }
}
