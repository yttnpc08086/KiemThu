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

public class ProfileTest {
    private WebDriver driver;
    private String baseUrl = "http://localhost:3000";
    private WebDriverWait wait;

    @DataProvider(name = "profileTestData")
    public Object[][] getProfileTestData() {
        return new Object[][] {
                // Format: fullName, email, phone, imagePath, expectedMessage, isValid

                // Valid case
                {"Nguyen Van A", "test@email.com", "0367171302", "D:\\image\\Mainboardicon.webp", "Cập nhật thành công!", true},

                // Special characters in name
                {"Nhuy###", "yttnpc08086@fpt.edu.vn", "03671713020", "D:\\image\\Mainboardicon.webp", "Cập nhật thất bại. Vui lòng thử lại.", false},

                // Empty name
                {"", "yttnpc08086@fpt.edu.vn", "03671713020", "D:\\image\\Mainboardicon.webp", "Cập nhật thất bại. Vui lòng thử lại.", false},

                // Numbers in name
                {"123", "yttnpc08086@fpt.edu.vn", "03671713020", "D:\\image\\Mainboardicon.webp", "Tên không được chứa số.", false},

                // Empty email
                {"Nhuy", "", "03671713020", "D:\\image\\Mainboardicon.webp", "Cập nhật thất bại. Vui lòng thử lại.", false},

                // Invalid email format
                {"Nhuy", "yttnpc08086#fpt.edu.vn", "03671713020", "D:\\image\\Mainboardicon.webp", "Email không hợp lệ. Vui lòng nhập đúng định dạng email.", false},

                // Long email
                {"Nhuy", "yttnpc08086qwertyuiopasdfghjklnbvcxz@fpt.edu.vn", "03671713020", "D:\\image\\Mainboardicon.webp", "Email không được vượt quá 50 ký tự.", false},

                // Empty phone
                {"Nhuy", "test@email.com", "", "D:\\image\\Mainboardicon.webp", "Cập nhật thất bại. Vui lòng thử lại.", false},

                // Invalid phone format
                {"Nhuy", "test@email.com", "abc123", "D:\\image\\Mainboardicon.webp", "Cập nhật thất bại. Vui lòng thử lại.", false},

                // Phone number too short
                {"Nhuy", "test@email.com", "0123", "D:\\image\\Mainboardicon.webp", "Cập nhật thất bại. Vui lòng thử lại.", false},

                // Invalid image format - PDF
                {"Nhuy", "test@email.com", "0367171302", "D:\\image\\test.pdf", "Cập nhật thất bại. Vui lòng thử lại.", false},

                // Large image file (assuming max size is 1MB)
                {"Nhuy", "test@email.com", "0367171302", "D:\\image\\cat.jpg", "Cập nhật thất bại. Vui lòng thử lại.", false}
        };
    }

    @BeforeClass
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        login();
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void login() {
        driver.get(baseUrl + "/login");

        WebElement usernameField = driver.findElement(By.xpath("//input[@placeholder='Nhập tài khoản của bạn']"));
        WebElement passwordField = driver.findElement(By.xpath("//input[@placeholder='Nhập mật khẩu của bạn']"));
        WebElement loginButton = driver.findElement(By.xpath("//button[@type='submit']"));

        usernameField.sendKeys("user1");
        passwordField.sendKeys("123");
        loginButton.click();

        try {
            WebElement closeButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".swal2-close")));
            closeButton.click();
        } catch (Exception e) {
            // No modal, continue
        }

        WebElement userIcon = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//i[@class='fas fa-user text-gray-700 hover:text-orange-500 cursor-pointer']")));
        userIcon.click();

        WebElement profileLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(text(),'Hồ sơ')]")));
        profileLink.click();
    }

    @Test(dataProvider = "profileTestData")
    public void testProfileUpdate(String fullName, String email, String phone,
                                  String imagePath, String expectedMessage, boolean isValid) {
        WebElement fullNameInput = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//label[text()='Tên đầy đủ']/following-sibling::input")));
        WebElement emailInput = driver.findElement(
                By.xpath("//label[text()='Email']/following-sibling::input"));
        WebElement phoneInput = driver.findElement(
                By.xpath("//label[text()='Số điện thoại']/following-sibling::input"));

        fullNameInput.clear();
        fullNameInput.sendKeys(fullName);

        emailInput.clear();
        emailInput.sendKeys(email);

        phoneInput.clear();
        phoneInput.sendKeys(phone);

        if (imagePath != null && !imagePath.isEmpty()) {
            WebElement profileImageInput = driver.findElement(By.xpath("//input[@type='file']"));
            profileImageInput.sendKeys(imagePath);
        }

        WebElement saveButton = driver.findElement(
                By.xpath("//button[contains(text(), 'Lưu Thay Đổi')]"));
        saveButton.click();

        try {
            WebElement message = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[contains(text(),'" + expectedMessage + "')]")));
            Assert.assertTrue(message.isDisplayed(),
                    "Expected message '" + expectedMessage + "' was not displayed!");

            if (isValid) {
                Assert.assertEquals(fullNameInput.getAttribute("value"), fullName);
                Assert.assertEquals(emailInput.getAttribute("value"), email);
                Assert.assertEquals(phoneInput.getAttribute("value"), phone);
            }
        } catch (Exception e) {
            Assert.fail("Expected message did not appear: " + expectedMessage);
        }
    }
}