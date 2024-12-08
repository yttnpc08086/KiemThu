package com.nhom4;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

public class AddressTest {
    private WebDriver driver;
    private String baseUrl = "http://localhost:3000";
    private String url = "http://localhost:3000/profile/province-select";
    private WebDriverWait wait;

    private void login() {
        // Điều hướng đến trang đăng nhập
        driver.get(baseUrl + "/login");

        // Tìm các trường nhập liệu và nút đăng nhập
        WebElement usernameField = driver.findElement(By.xpath("//input[@placeholder='Nhập tài khoản của bạn']"));
        WebElement passwordField = driver.findElement(By.xpath("//input[@placeholder='Nhập mật khẩu của bạn']"));
        WebElement loginButton = driver.findElement(By.xpath("//button[@type='submit']"));

        // Nhập thông tin đăng nhập
        usernameField.sendKeys("user"); // Thay bằng tài khoản thực tế
        passwordField.sendKeys("123"); // Thay bằng mật khẩu thực tế

        // Nhấn nút đăng nhập
        loginButton.click();

        // Chờ xử lý modal Swal2 (nếu có)
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
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

        // Chuyển đến danh sách địa chỉ
        WebElement addressListLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@href='/profile/address-list']")));
        addressListLink.click();

        // Nhấn vào nút "Thêm địa chỉ"
        WebElement addAddressButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'Thêm địa chỉ')]")));
        addAddressButton.click();

        // Kiểm tra URL hiện tại (nếu cần)
        String currentUrl = driver.getCurrentUrl();
        Assert.assertEquals(currentUrl, "http://localhost:3000/profile/province-select", "Không chuyển hướng đến danh sách địa chỉ đúng!");
    }


    @BeforeClass
    public void openBrowser() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.navigate().to(url);
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    private WebElement waitForElement(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    private List<WebElement> waitForOptions(By locator) {
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
    }

    private void selectOptionById(String id, String visibleText) {
        WebElement selectElement = waitForElement(By.id(id));
        Select dropdown = new Select(selectElement);

        // Đợi các tùy chọn trong dropdown được tải
        List<WebElement> options = waitForOptions(By.cssSelector("#" + id + " option"));
        Assert.assertFalse(options.isEmpty(), "Danh sách tùy chọn trống.");

        // Chọn tùy chọn bằng văn bản
        dropdown.selectByVisibleText(visibleText);
    }

    private void fillInputField(String id, String value) {
        WebElement inputField = waitForElement(By.id(id));
        inputField.clear();
        inputField.sendKeys(value);
    }

    private void clickSaveButton() {
        WebElement saveButton = waitForElement(By.xpath("//button[text()='Lưu địa chỉ']"));
        saveButton.click();
    }

    private String getErrorMessage() {
        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class, 'text-red')]")));
        return errorMessage.getText();
    }

    private String getPas() {
        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class, 'text-green')]")));
        return errorMessage.getText();
    }

    @Test
    public void testValidAddress() {
        login();
        selectOptionById("province", "Tỉnh Hà Giang");
        selectOptionById("district", "Huyện Đồng Văn");
        selectOptionById("ward", "Xã Lũng Cú");

        fillInputField("street-address", "Số 10, Phố Tràng Tiền");
        fillInputField("phone-number", "0123456789");

        // Thêm kiểm tra khác trước khi tìm success-message
        // Click vào nút Lưu
        clickSaveButton();

        Assert.assertEquals(getPas(), "Lưu địa chỉ thành công!");

//// Tạo WebDriverWait để sử dụng cho việc chờ
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
//
//// Chờ sự xuất hiện của thông báo lỗi (nếu có)
//        WebElement errorIndicator = wait.until(ExpectedConditions.presenceOfElementLocated(
//                By.xpath("//div[contains(@class, 'text-red-500') and contains(text(),'Đã xảy ra lỗi khi lưu địa chỉ: Lỗi từ server.')]")
//        ));
//        Assert.assertNotNull(errorIndicator, "Không tìm thấy thông báo lỗi.");
//
//// Kiểm tra phần tử lỗi hiển thị chính xác
//        Assert.assertTrue(errorIndicator.isDisplayed(), "Thông báo lỗi không hiển thị đúng.");
    }

    @Test
    public void testInvalidPhoneNumberbt() {
        login();
        selectOptionById("province", "Tỉnh Hà Giang");
        selectOptionById("district", "Huyện Đồng Văn");
        selectOptionById("ward", "Xã Lũng Cú");

        fillInputField("street-address", "Số 10, Phố Tràng Tiền");

        clickSaveButton();
        Assert.assertEquals(getErrorMessage(), "Số điện thoại không hợp lệ. Vui lòng nhập lại.");
    }

    @Test
    public void testInvalidPhoneNumber() {
        login();
        selectOptionById("province", "Tỉnh Hà Giang");
        selectOptionById("district", "Huyện Đồng Văn");
        selectOptionById("ward", "Xã Lũng Cú");

        fillInputField("street-address", "Số 10, Phố Tràng Tiền");
        fillInputField("phone-number", "abc123");

        clickSaveButton();
        Assert.assertEquals(getErrorMessage(), "Số điện thoại không hợp lệ. Vui lòng nhập lại.");
    }

    @Test
    public void testInvalitress() {
        login();
        selectOptionById("province", "Tỉnh Hà Giang");
        selectOptionById("district", "Huyện Đồng Văn");
        selectOptionById("ward", "Xã Lũng Cú");

        fillInputField("street-address", "@!#$%^");
        fillInputField("phone-number", "0987654321");

        clickSaveButton();
        Assert.assertEquals(getPas(), "Địa chỉ không hợp lệ");
    }

    @Test
    public void testEmptyFields() {
        login();
        selectOptionById("province", "Tỉnh Hà Giang");
        selectOptionById("district", "Huyện Đồng Văn");


        fillInputField("street-address", "Số 10, Phố Tràng Tiền");
        fillInputField("phone-number", "0123456789");
        clickSaveButton();
        Assert.assertEquals(getErrorMessage(), "Vui lòng nhập đầy đủ thông tin địa chỉ.");
    }

    @Test
    public void testMissingProvince() {
        login();
        fillInputField("street-address", "Số 10, Phố Tràng Tiền");
        fillInputField("phone-number", "0123456789");

        // Không chọn tỉnh
        clickSaveButton();
        Assert.assertEquals(getErrorMessage(), "Vui lòng nhập đầy đủ thông tin địa chỉ.");
    }

    @Test
    public void testMissingStreetAddress() {
        login();
        selectOptionById("province", "Tỉnh Hà Giang");
        selectOptionById("district", "Huyện Đồng Văn");
        selectOptionById("ward", "Xã Lũng Cú");

        // Không nhập địa chỉ đường
        fillInputField("phone-number", "0123456789");
        clickSaveButton();
        Assert.assertEquals(getErrorMessage(), "Vui lòng nhập đầy đủ thông tin địa chỉ.");
    }

    @AfterClass
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }
}