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

public class AddressUpdateTest {
    private WebDriver driver;
    private String baseUrl = "http://localhost:3000";
    private String url = "http://localhost:3000/profile/edit-address"; // Address for update page
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
        passwordField.sendKeys("123456"); // Thay bằng mật khẩu thực tế

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

        // Nhấn vào nút "Sửa"
        WebElement editAddressButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'Sửa')]")));
        editAddressButton.click();

        // Kiểm tra URL hiện tại (nếu cần)
        String currentUrl = driver.getCurrentUrl();
        Assert.assertEquals(currentUrl, "http://localhost:3000/profile/edit-address", "Không chuyển hướng đến danh sách địa chỉ đúng!");
    }


    @BeforeClass
    public void openBrowser() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.navigate().to(url);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
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

        // Wait for the options to be loaded
        List<WebElement> options = waitForOptions(By.cssSelector("#" + id + " option"));
        Assert.assertFalse(options.isEmpty(), "Options list is empty.");

        // Select option by visible text
        dropdown.selectByVisibleText(visibleText);
    }

    private void fillInputField(String id, String value) {
        WebElement inputField = waitForElement(By.id(id));
        inputField.clear();
        inputField.sendKeys(value);
    }

    private void clickUpdateButton() {
        WebElement updateButton = waitForElement(By.xpath("//button[text()='Cập nhật địa chỉ']"));
        updateButton.click();
    }

    private String getErrorMessage() {
        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("error-message")));
        return errorMessage.getText();
    }

    private String getPas() {
        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("success-message")));
        return errorMessage.getText();
    }

    @Test
    public void testUpdateValidAddress() {
        login();
        selectOptionById("province-select", "Tỉnh Hà Giang");
        selectOptionById("district-select", "Huyện Đồng Văn");
        selectOptionById("ward-select", "Xã Lũng Cú");

        fillInputField("street-address", "Số 10, Phố Tràng Tiền");
        fillInputField("phone-number", "0123456789");

        // Click on Update button
        clickUpdateButton();

        // Verify success message
        Assert.assertEquals(getPas(), "Cập nhật địa chỉ thành công!");
    }

    @Test
    public void testUpdateInvalidPhoneNumber() {
        login();
        selectOptionById("province-select", "Tỉnh Hà Giang");
        selectOptionById("district-select", "Huyện Đồng Văn");
        selectOptionById("ward-select", "Xã Lũng Cú");

        fillInputField("street-address", "Số 10, Phố Tràng Tiền");
        fillInputField("phone-number", "abc123"); // Invalid phone number

        // Click on Update button
        clickUpdateButton();

        // Verify error message for invalid phone number
        Assert.assertEquals(getErrorMessage(), "Số điện thoại không hợp lệ. Vui lòng nhập đúng.");
    }

    @Test
    public void testUpdateEmptyStreetAddress() {
        login();
        selectOptionById("province-select", "Tỉnh Hà Giang");
        selectOptionById("district-select", "Huyện Đồng Văn");
        selectOptionById("ward-select", "Xã Lũng Cú");

        fillInputField("street-address", ""); // Leave street address empty
        fillInputField("phone-number", "0123456789");

        // Click on Update button
        clickUpdateButton();

        // Verify error message for empty street address
        Assert.assertEquals(getPas(), "Vui lòng điền đầy đủ các trường thông tin.");
    }

    @Test
    public void testUpdateWithoutProvince() {
        // Do not select province
        login();
        fillInputField("street-address", "Số 10, Phố Tràng Tiền");
        fillInputField("phone-number", "0123456789");

        // Click on Update button
        clickUpdateButton();

        // Verify error message for missing province selection
        Assert.assertEquals(getPas(), "Vui lòng điền đầy đủ các trường thông tin.");
    }

    @Test
    public void testUpdateWithoutWard() {
        login();
        selectOptionById("province-select", "Tỉnh Hà Giang");
        selectOptionById("district-select", "Huyện Đồng Văn");
        // Do not select ward

        fillInputField("street-address", "Số 10, Phố Tràng Tiền");
        fillInputField("phone-number", "0123456789");

        // Click on Update button
        clickUpdateButton();

        // Verify error message for missing ward selection
        Assert.assertEquals(getPas(), "Vui lòng chọn xã/phường.");
    }

    @Test
    public void testUpdateWithSpecialCharactersInAddress() {
        login();
        selectOptionById("province-select", "Tỉnh Hà Giang");
        selectOptionById("district-select", "Huyện Đồng Văn");
        selectOptionById("ward-select", "Xã Lũng Cú");

        fillInputField("street-address", "@#$%&*!"); // Address with special characters
        fillInputField("phone-number", "0123456789");

        // Click on Update button
        clickUpdateButton();

        // Verify error message for invalid address input
        Assert.assertEquals(getPas(), "Cập nhật địa chỉ thành công!");
    }



    @AfterClass
    public void closeBrowser() {
        driver.quit();
    }
}
