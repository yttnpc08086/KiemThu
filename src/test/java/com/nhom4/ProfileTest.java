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
import org.testng.annotations.Test;

import java.time.Duration;

public class ProfileTest {
    private WebDriver driver;
    private String baseUrl = "http://localhost:3000"; // URL của trang web

    @BeforeClass
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        login(); // Gọi hàm đăng nhập trước khi chạy test
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit(); // Đóng trình duyệt sau khi test xong
        }
    }

    private void login() {
        // Điều hướng đến trang đăng nhập
        driver.get(baseUrl + "/login");

        // Tìm các trường nhập liệu và nút đăng nhập
        WebElement usernameField = driver.findElement(By.xpath("//input[@placeholder='Nhập tài khoản của bạn']"));
        WebElement passwordField = driver.findElement(By.xpath("//input[@placeholder='Nhập mật khẩu của bạn']"));
        WebElement loginButton = driver.findElement(By.xpath("//button[@type='submit']"));

        // Nhập thông tin đăng nhập
        usernameField.sendKeys("user1"); // Thay bằng tài khoản thực tế
        passwordField.sendKeys("123"); // Thay bằng mật khẩu thực tế

        // Nhấn nút đăng nhập
        loginButton.click();

        // Chờ xử lý modal Swal2 (nếu có)
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        try {
            WebElement closeButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".swal2-close")));
            closeButton.click();
        } catch (Exception e) {
            // Không có modal nào, tiếp tục
        }

        // Chuyển đến biểu tượng tài khoản (fas fa-user)
        WebElement userIcon = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//i[@class='fas fa-user text-gray-700 hover:text-orange-500 cursor-pointer']")));
        userIcon.click();

        // Chuyển đến mục "Hồ sơ"
        WebElement profileLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(),'Hồ sơ')]")));
        profileLink.click();
    }

    @Test
    public void testUpdateProfileWithSpecialCharacter() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));

        // Tìm các trường thông tin dựa trên thuộc tính label
        WebElement fullNameInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//label[text()='Tên đầy đủ']/following-sibling::input")));
        WebElement emailInput = driver.findElement(By.xpath("//label[text()='Email']/following-sibling::input"));
        WebElement phoneInput = driver.findElement(By.xpath("//label[text()='Số điện thoại']/following-sibling::input"));

        // Nhập thông tin mới
        fullNameInput.clear();
        fullNameInput.sendKeys("Nhuy###");

        emailInput.clear();
        emailInput.sendKeys("yttnpc08086@fpt.edu.vn");

        phoneInput.clear();
        phoneInput.sendKeys("03671713020");

        // Chọn ảnh mới (Cần cung cấp đường dẫn file hợp lệ trên máy)
        WebElement profileImageInput = driver.findElement(By.xpath("//input[@type='file']"));
        String filePath = "D:\\image\\Mainboardicon.webp"; // Thay bằng đường dẫn file ảnh trên máy
        profileImageInput.sendKeys(filePath);

        // Nhấn nút "Lưu Thay Đổi"
        WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(), 'Lưu Thay Đổi')]"));
        saveButton.click();

        // Kiểm tra sự xuất hiện của Toast thông qua văn bản thông báo
        try {
            // Chờ và kiểm tra sự xuất hiện của thông báo Toast
            WebElement toastSuccessMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),'Cập nhật thất bại. Vui lòng thử lại.')]")));
            Assert.assertTrue(toastSuccessMessage.isDisplayed(), "Thông báo 'Cập nhật thất bại. Vui lòng thử lại.' không xuất hiện!");
        } catch (Exception e) {
            Assert.fail("Thông báo Toast không xuất hiện!");
        }

        // Xác nhận thông tin đã được cập nhật
        Assert.assertEquals(fullNameInput.getAttribute("value"), "Nhuy###", "Tên đầy đủ không được cập nhật chính xác!");
        Assert.assertEquals(emailInput.getAttribute("value"), "yttnpc08086@fpt.edu.vn", "Email không được cập nhật chính xác!");
        Assert.assertEquals(phoneInput.getAttribute("value"), "03671713020", "Số điện thoại không được cập nhật chính xác!");
    }

    @Test
    public void testUpdateProfileWithEmptyFullName() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));

        // Tìm các trường thông tin dựa trên thuộc tính label
        WebElement fullNameInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//label[text()='Tên đầy đủ']/following-sibling::input")));
        WebElement emailInput = driver.findElement(By.xpath("//label[text()='Email']/following-sibling::input"));
        WebElement phoneInput = driver.findElement(By.xpath("//label[text()='Số điện thoại']/following-sibling::input"));

        // Để trống trường "Tên đầy đủ"
        fullNameInput.clear(); // Không nhập gì vào trường này

        // Nhập thông tin vào các trường khác
        emailInput.clear();
        emailInput.sendKeys("yttnpc08086@fpt.edu.vn");

        phoneInput.clear();
        phoneInput.sendKeys("03671713020");

        // Nhấn nút "Lưu Thay Đổi"
        WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(), 'Lưu Thay Đổi')]"));
        saveButton.click();

        // Kiểm tra sự xuất hiện của Toast thông qua văn bản thông báo
        try {
            // Chờ và kiểm tra sự xuất hiện của thông báo Toast "Cập nhật thất bại. Vui lòng thử lại."
            WebElement toastErrorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),'Cập nhật thất bại. Vui lòng thử lại.')]")));
            Assert.assertTrue(toastErrorMessage.isDisplayed(), "Thông báo 'Cập nhật thất bại. Vui lòng thử lại.' không xuất hiện!");
        } catch (Exception e) {
            Assert.fail("Thông báo Toast không xuất hiện!");
        }

        // Kiểm tra các trường thông tin khác vẫn được cập nhật đúng
        Assert.assertEquals(emailInput.getAttribute("value"), "yttnpc08086@fpt.edu.vn", "Email không được cập nhật chính xác!");
        Assert.assertEquals(phoneInput.getAttribute("value"), "03671713020", "Số điện thoại không được cập nhật chính xác!");
    }

    @Test
    public void testUpdateProfileWithNumberInFullName() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));

        // Tìm các trường thông tin dựa trên thuộc tính label
        WebElement fullNameInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//label[text()='Tên đầy đủ']/following-sibling::input")));
        WebElement emailInput = driver.findElement(By.xpath("//label[text()='Email']/following-sibling::input"));
        WebElement phoneInput = driver.findElement(By.xpath("//label[text()='Số điện thoại']/following-sibling::input"));

        // Nhập số vào trường "Tên đầy đủ"
        fullNameInput.clear();
        fullNameInput.sendKeys("123");

        // Nhập thông tin vào các trường khác
        emailInput.clear();
        emailInput.sendKeys("yttnpc08086@fpt.edu.vn");

        phoneInput.clear();
        phoneInput.sendKeys("03671713020");

        // Nhấn nút "Lưu Thay Đổi"
        WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(), 'Lưu Thay Đổi')]"));
        saveButton.click();

        // Kiểm tra thông báo lỗi dưới trường "Tên đầy đủ"
        try {
            // Chờ và kiểm tra sự xuất hiện của thông báo lỗi dưới trường "Tên đầy đủ"
            WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[text()='Tên đầy đủ']/following-sibling::input/following-sibling::p[text()='Tên không được chứa số.']")));
            Assert.assertTrue(errorMessage.isDisplayed(), "Thông báo lỗi 'Tên không được chứa số.' không xuất hiện dưới trường 'Tên đầy đủ'!");
        } catch (Exception e) {
            Assert.fail("Thông báo lỗi dưới trường 'Tên đầy đủ' không xuất hiện!");
        }

        // Kiểm tra các trường thông tin khác vẫn được cập nhật đúng
        Assert.assertEquals(emailInput.getAttribute("value"), "yttnpc08086@fpt.edu.vn", "Email không được cập nhật chính xác!");
        Assert.assertEquals(phoneInput.getAttribute("value"), "03671713020", "Số điện thoại không được cập nhật chính xác!");
    }

    @Test
    public void testUpdateProfileWithEmptyEmail() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));

        // Tìm các trường thông tin dựa trên thuộc tính label
        WebElement fullNameInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//label[text()='Tên đầy đủ']/following-sibling::input")));
        WebElement emailInput = driver.findElement(By.xpath("//label[text()='Email']/following-sibling::input"));
        WebElement phoneInput = driver.findElement(By.xpath("//label[text()='Số điện thoại']/following-sibling::input"));

        // Nhập thông tin vào trường "Tên đầy đủ" và "Số điện thoại"
        fullNameInput.clear();
        fullNameInput.sendKeys("Nhuy");

        emailInput.clear();  // Bỏ trống trường "Email"

        phoneInput.clear();
        phoneInput.sendKeys("03671713020");

        // Nhấn nút "Lưu Thay Đổi"
        WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(), 'Lưu Thay Đổi')]"));
        saveButton.click();

        // Kiểm tra thông báo lỗi về việc trường "Email" bị bỏ trống
        try {
            // Chờ và kiểm tra sự xuất hiện của thông báo lỗi
            WebElement toastErrorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),'Cập nhật thất bại. Vui lòng thử lại.')]")));
            Assert.assertTrue(toastErrorMessage.isDisplayed(), "Thông báo 'Cập nhật thất bại. Vui lòng thử lại.' không xuất hiện!");
        } catch (Exception e) {
            Assert.fail("Thông báo lỗi không xuất hiện khi bỏ trống trường 'Email'!");
        }

        // Kiểm tra các trường thông tin khác không bị thay đổi
        Assert.assertEquals(fullNameInput.getAttribute("value"), "Nhuy", "Tên đầy đủ không được cập nhật chính xác!");
        Assert.assertEquals(phoneInput.getAttribute("value"), "03671713020", "Số điện thoại không được cập nhật chính xác!");
    }

    @Test
    public void testUpdateProfileWithInvalidEmail() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));

        // Tìm các trường thông tin dựa trên thuộc tính label
        WebElement fullNameInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//label[text()='Tên đầy đủ']/following-sibling::input")));
        WebElement emailInput = driver.findElement(By.xpath("//label[text()='Email']/following-sibling::input"));
        WebElement phoneInput = driver.findElement(By.xpath("//label[text()='Số điện thoại']/following-sibling::input"));

        // Nhập thông tin vào trường "Tên đầy đủ", email không hợp lệ và "Số điện thoại"
        fullNameInput.clear();
        fullNameInput.sendKeys("Nhuy");

        emailInput.clear();
        emailInput.sendKeys("yttnpc08086#fpt.edu.vn");  // Email không hợp lệ

        phoneInput.clear();
        phoneInput.sendKeys("03671713020");

        // Nhấn nút "Lưu Thay Đổi"
        WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(), 'Lưu Thay Đổi')]"));
        saveButton.click();

        // Kiểm tra sự xuất hiện của thông báo lỗi dưới trường "Email"
        try {
            // Chờ và kiểm tra sự xuất hiện của thông báo lỗi
            WebElement emailErrorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[text()='Email']/following-sibling::input/../p[contains(text(),'Email không hợp lệ. Vui lòng nhập đúng định dạng email.')]")));
            Assert.assertTrue(emailErrorMessage.isDisplayed(), "Thông báo lỗi 'Email không hợp lệ. Vui lòng nhập đúng định dạng email.' không xuất hiện dưới trường email!");
        } catch (Exception e) {
            Assert.fail("Thông báo lỗi không xuất hiện khi nhập email không hợp lệ!");
        }

        // Kiểm tra các trường thông tin khác không bị thay đổi
        Assert.assertEquals(fullNameInput.getAttribute("value"), "Nhuy", "Tên đầy đủ không được cập nhật chính xác!");
        Assert.assertEquals(phoneInput.getAttribute("value"), "03671713020", "Số điện thoại không được cập nhật chính xác!");
    }

    @Test
    public void testUpdateProfileWithLongEmail() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));

        // Tìm các trường thông tin dựa trên thuộc tính label
        WebElement fullNameInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//label[text()='Tên đầy đủ']/following-sibling::input")));
        WebElement emailInput = driver.findElement(By.xpath("//label[text()='Email']/following-sibling::input"));
        WebElement phoneInput = driver.findElement(By.xpath("//label[text()='Số điện thoại']/following-sibling::input"));

        // Nhập thông tin vào trường "Tên đầy đủ", email quá dài và "Số điện thoại"
        fullNameInput.clear();
        fullNameInput.sendKeys("Nhuy");

        emailInput.clear();
        emailInput.sendKeys("yttnpc08086qwertyuiopasdfghjklnbvcxzawertyuqwertyuiopasdfghjkloiuytrewqazxcvbnmmmiopasdfghjklzzzz@fpt.edu.vn");  // Email quá dài

        phoneInput.clear();
        phoneInput.sendKeys("03671713020");

        // Nhấn nút "Lưu Thay Đổi"
        WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(), 'Lưu Thay Đổi')]"));
        saveButton.click();

        // Kiểm tra sự xuất hiện của thông báo lỗi dưới trường "Email"
        try {
            // Chờ và kiểm tra sự xuất hiện của thông báo lỗi email quá dài
            WebElement emailErrorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[text()='Email']/following-sibling::input/../p[contains(text(),'Email quá dài. Vui lòng nhập email ngắn hơn 100 ký tự.')]")));
            Assert.assertTrue(emailErrorMessage.isDisplayed(), "Thông báo lỗi 'Email quá dài. Vui lòng nhập email ngắn hơn 100 ký tự.' không xuất hiện dưới trường email!");
        } catch (Exception e) {
            Assert.fail("Thông báo lỗi không xuất hiện khi email quá dài!");
        }

        // Kiểm tra các trường thông tin khác không bị thay đổi
        Assert.assertEquals(fullNameInput.getAttribute("value"), "Nhuy", "Tên đầy đủ không được cập nhật chính xác!");
        Assert.assertEquals(phoneInput.getAttribute("value"), "03671713020", "Số điện thoại không được cập nhật chính xác!");
    }

    @Test
    public void testUpdateProfileWithEmptyPhone() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));

        // Tìm các trường thông tin dựa trên thuộc tính label
        WebElement fullNameInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//label[text()='Tên đầy đủ']/following-sibling::input")));
        WebElement emailInput = driver.findElement(By.xpath("//label[text()='Email']/following-sibling::input"));
        WebElement phoneInput = driver.findElement(By.xpath("//label[text()='Số điện thoại']/following-sibling::input"));

        // Nhập thông tin vào trường "Tên đầy đủ", "Email" và để trống "Số điện thoại"
        fullNameInput.clear();
        fullNameInput.sendKeys("Nhuy");

        emailInput.clear();
        emailInput.sendKeys("yttnpc08086@fpt.edu.vn");

        phoneInput.clear();

        // Nhấn nút "Lưu Thay Đổi"
        WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(), 'Lưu Thay Đổi')]"));
        saveButton.click();

        // Kiểm tra sự xuất hiện của thông báo lỗi dưới trường "Số điện thoại"
        try {
            // Chờ và kiểm tra sự xuất hiện của thông báo lỗi "Số điện thoại không được để trống."
            WebElement phoneErrorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[text()='Số điện thoại']/following-sibling::input/../p[contains(text(),'Số điện thoại không được để trống.')]")));
            Assert.assertTrue(phoneErrorMessage.isDisplayed(), "Thông báo lỗi 'Số điện thoại không được để trống.' không xuất hiện dưới trường SĐT!");
        } catch (Exception e) {
            Assert.fail("Thông báo lỗi không xuất hiện khi trường 'Số điện thoại' bị bỏ trống!");
        }

        // Kiểm tra các trường thông tin khác không bị thay đổi
        Assert.assertEquals(fullNameInput.getAttribute("value"), "Nhuy", "Tên đầy đủ không được cập nhật chính xác!");
        Assert.assertEquals(emailInput.getAttribute("value"), "yttnpc08086@fpt.edu.vn", "Email không được cập nhật chính xác!");
    }

    @Test
    public void testUpdateProfileWithInvalidPhoneFormat() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));

        // Tìm các trường thông tin
        WebElement fullNameInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//label[text()='Tên đầy đủ']/following-sibling::input")));
        WebElement emailInput = driver.findElement(By.xpath("//label[text()='Email']/following-sibling::input"));
        WebElement phoneInput = driver.findElement(By.xpath("//label[text()='Số điện thoại']/following-sibling::input"));

        // Nhập thông tin không hợp lệ
        fullNameInput.clear();
        fullNameInput.sendKeys("Nhuy");

        emailInput.clear();
        emailInput.sendKeys("yttnpc08086@fpt.edu.vn");

        phoneInput.clear();
        phoneInput.sendKeys("03671713XXX");


        // Kiểm tra thông báo lỗi hiển thị dưới trường "Số điện thoại"
        WebElement phoneErrorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[text()='Số điện thoại']/following-sibling::input/../p[contains(text(),'Số điện thoại không hợp lệ. Vui lòng nhập đúng số điện thoại.')]")));
        Assert.assertTrue(phoneErrorMessage.isDisplayed(), "Thông báo lỗi 'Số điện thoại không hợp lệ' không xuất hiện!");
    }

    @Test
    public void testUpdateProfileWithLongPhoneNumber() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));

        // Tìm các trường thông tin dựa trên thuộc tính label
        WebElement fullNameInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//label[text()='Tên đầy đủ']/following-sibling::input")));
        WebElement emailInput = driver.findElement(By.xpath("//label[text()='Email']/following-sibling::input"));
        WebElement phoneInput = driver.findElement(By.xpath("//label[text()='Số điện thoại']/following-sibling::input"));

        // Nhập thông tin vào trường "Tên đầy đủ", "Email" và "Số điện thoại quá dài"
        fullNameInput.clear();
        fullNameInput.sendKeys("Nhuy");

        emailInput.clear();
        emailInput.sendKeys("yttnpc08086@fpt.edu.vn");

        phoneInput.clear();
        phoneInput.sendKeys("036717130201234567890"); // Số điện thoại quá dài

        // Nhấn nút "Lưu Thay Đổi"
        WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(), 'Lưu Thay Đổi')]"));
        saveButton.click();

        // Kiểm tra sự xuất hiện của Toast thông qua văn bản thông báo
        try {
            // Chờ và kiểm tra sự xuất hiện của thông báo Toast
            WebElement toastSuccessMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),'Cập nhật thất bại. Vui lòng thử lại.')]")));
            Assert.assertTrue(toastSuccessMessage.isDisplayed(), "Thông báo 'Cập nhật thất bại. Vui lòng thử lại.' không xuất hiện!");
        } catch (Exception e) {
            Assert.fail("Thông báo Toast không xuất hiện!");
        }

        // Kiểm tra các trường khác không bị thay đổi
        Assert.assertEquals(fullNameInput.getAttribute("value"), "Nhuy", "Tên đầy đủ không được cập nhật chính xác!");
        Assert.assertEquals(emailInput.getAttribute("value"), "yttnpc08086@fpt.edu.vn", "Email không được cập nhật chính xác!");
    }

    @Test
    public void testUploadInvalidFileFormat() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        // Tìm các trường thông tin và trường upload ảnh
        WebElement fullNameInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//label[text()='Tên đầy đủ']/following-sibling::input")));
        WebElement emailInput = driver.findElement(By.xpath("//label[text()='Email']/following-sibling::input"));
        WebElement phoneInput = driver.findElement(By.xpath("//label[text()='Số điện thoại']/following-sibling::input"));
        WebElement uploadInput = driver.findElement(By.xpath("//input[@type='file']"));

        // Nhập thông tin vào các trường "Tên đầy đủ", "Email" và "Số điện thoại"
        fullNameInput.clear();
        fullNameInput.sendKeys("Nhuy");

        emailInput.clear();
        emailInput.sendKeys("yttnpc08086@fpt.edu.vn");

        phoneInput.clear();
        phoneInput.sendKeys("03671713020");

        // Tải tệp không đúng định dạng
        uploadInput.sendKeys("D:\\image\\test.pdf");

        // Nhấn nút "Lưu Thay Đổi"
        WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(), 'Lưu Thay Đổi')]"));
        saveButton.click();

        // Kiểm tra sự xuất hiện của Toast thông qua văn bản thông báo
        try {
            // Chờ và kiểm tra sự xuất hiện của thông báo Toast
            WebElement toastSuccessMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),'Cập nhật thất bại. Vui lòng thử lại.')]")));
            Assert.assertTrue(toastSuccessMessage.isDisplayed(), "Thông báo 'Cập nhật thất bại. Vui lòng thử lại.' không xuất hiện!");
        } catch (Exception e) {
            Assert.fail("Thông báo Toast không xuất hiện!");
        }

        // Kiểm tra các trường khác không bị thay đổi
        Assert.assertEquals(fullNameInput.getAttribute("value"), "Nhuy", "Tên đầy đủ không được cập nhật chính xác!");
        Assert.assertEquals(emailInput.getAttribute("value"), "yttnpc08086@fpt.edu.vn", "Email không được cập nhật chính xác!");
        Assert.assertEquals(phoneInput.getAttribute("value"), "03671713020", "Số điện thoại không được cập nhật chính xác!");
    }

    @Test
    public void testUploadImageExceedsSizeLimit() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        // Tìm các trường thông tin và trường upload ảnh
        WebElement fullNameInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//label[text()='Tên đầy đủ']/following-sibling::input")));
        WebElement emailInput = driver.findElement(By.xpath("//label[text()='Email']/following-sibling::input"));
        WebElement phoneInput = driver.findElement(By.xpath("//label[text()='Số điện thoại']/following-sibling::input"));
        WebElement uploadInput = driver.findElement(By.xpath("//input[@type='file']"));

        // Nhập thông tin vào các trường "Tên đầy đủ", "Email" và "Số điện thoại"
        fullNameInput.clear();
        fullNameInput.sendKeys("Nhuy");

        emailInput.clear();
        emailInput.sendKeys("yttnpc08086@fpt.edu.vn");

        phoneInput.clear();
        phoneInput.sendKeys("03671713020");

        // Tải tệp vượt quá dung lượng cho phép
        uploadInput.sendKeys("D:\\image\\cat.jpg"); // File vượt dung lượng tối đa (giả định)

        // Nhấn nút "Lưu Thay Đổi"
        WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(), 'Lưu Thay Đổi')]"));
        saveButton.click();

        // Kiểm tra sự xuất hiện của Toast thông qua văn bản thông báo
        try {
            // Chờ và kiểm tra sự xuất hiện của thông báo Toast
            WebElement toastSuccessMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),'Cập nhật thất bại. Vui lòng thử lại.')]")));
            Assert.assertTrue(toastSuccessMessage.isDisplayed(), "Thông báo 'Cập nhật thất bại. Vui lòng thử lại.' không xuất hiện!");
        } catch (Exception e) {
            Assert.fail("Thông báo Toast không xuất hiện!");
        }

        // Kiểm tra các trường khác không bị thay đổi
        Assert.assertEquals(fullNameInput.getAttribute("value"), "Nhuy", "Tên đầy đủ không được cập nhật chính xác!");
        Assert.assertEquals(emailInput.getAttribute("value"), "yttnpc08086@fpt.edu.vn", "Email không được cập nhật chính xác!");
        Assert.assertEquals(phoneInput.getAttribute("value"), "03671713020", "Số điện thoại không được cập nhật chính xác!");
    }

    @Test
    public void testValidProfileUpdate() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        // Tìm các trường thông tin và trường upload ảnh
        WebElement fullNameInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//label[text()='Tên đầy đủ']/following-sibling::input")));
        WebElement emailInput = driver.findElement(By.xpath("//label[text()='Email']/following-sibling::input"));
        WebElement phoneInput = driver.findElement(By.xpath("//label[text()='Số điện thoại']/following-sibling::input"));
        WebElement uploadInput = driver.findElement(By.xpath("//input[@type='file']"));

        // Nhập thông tin hợp lệ vào các trường "Tên đầy đủ", "Email", và "Số điện thoại"
        fullNameInput.clear();
        fullNameInput.sendKeys("Thái");

        emailInput.clear();
        emailInput.sendKeys("tranquangthai.02nct@gmail.com");

        phoneInput.clear();
        phoneInput.sendKeys("0394027614");

        // Tải tệp ảnh hợp lệ
        uploadInput.sendKeys("D:\\image\\gpuicon.jpg");

        // Nhấn nút "Lưu Thay Đổi"
        WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(), 'Lưu Thay Đổi')]"));
        saveButton.click();

        try {
            // Chờ và kiểm tra sự xuất hiện của thông báo Toast
            WebElement toastSuccessMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),'Cập nhật thành công!')]")));
            Assert.assertTrue(toastSuccessMessage.isDisplayed(), "Thông báo 'Cập nhật thành công !' không xuất hiện!");
        } catch (Exception e) {
            Assert.fail("Thông báo Toast không xuất hiện!");
        }

        // Kiểm tra lại các giá trị sau khi lưu
        Assert.assertEquals(fullNameInput.getAttribute("value"), "Thái", "Tên đầy đủ không được cập nhật chính xác!");
        Assert.assertEquals(emailInput.getAttribute("value"), "tranquangthai.02nct@gmail.com", "Email không được cập nhật chính xác!");
        Assert.assertEquals(phoneInput.getAttribute("value"), "0394027614", "Số điện thoại không được cập nhật chính xác!");
    }


}
