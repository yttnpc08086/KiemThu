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
    private String url = "http://localhost:3000/profile/edit-address"; // Address for update page
    private WebDriverWait wait;

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

    @Test
    public void testUpdateValidAddress() {
        selectOptionById("province-select", "Tỉnh Hà Giang");
        selectOptionById("district-select", "Huyện Đồng Văn");
        selectOptionById("ward-select", "Xã Lũng Cú");

        fillInputField("street-address", "Số 10, Phố Tràng Tiền");
        fillInputField("phone-number", "0123456789");

        // Click on Update button
        clickUpdateButton();

        // Verify success message
        Assert.assertEquals(getErrorMessage(), "Cập nhật địa chỉ thành công");
    }

    @Test
    public void testUpdateInvalidPhoneNumber() {
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
        selectOptionById("province-select", "Tỉnh Hà Giang");
        selectOptionById("district-select", "Huyện Đồng Văn");
        selectOptionById("ward-select", "Xã Lũng Cú");

        fillInputField("street-address", ""); // Leave street address empty
        fillInputField("phone-number", "0123456789");

        // Click on Update button
        clickUpdateButton();

        // Verify error message for empty street address
        Assert.assertEquals(getErrorMessage(), "Vui lòng điền đầy đủ các trường thông tin.");
    }

    @Test
    public void testUpdateWithoutProvince() {
        // Do not select province

        fillInputField("street-address", "Số 10, Phố Tràng Tiền");
        fillInputField("phone-number", "0123456789");

        // Click on Update button
        clickUpdateButton();

        // Verify error message for missing province selection
        Assert.assertEquals(getErrorMessage(), "Vui lòng điền đầy đủ các trường thông tin.");
    }

    @Test
    public void testUpdateWithoutWard() {
        selectOptionById("province-select", "Tỉnh Hà Giang");
        selectOptionById("district-select", "Huyện Đồng Văn");
        // Do not select ward

        fillInputField("street-address", "Số 10, Phố Tràng Tiền");
        fillInputField("phone-number", "0123456789");

        // Click on Update button
        clickUpdateButton();

        // Verify error message for missing ward selection
        Assert.assertEquals(getErrorMessage(), "Vui lòng chọn xã/phường.");
    }

    @Test
    public void testUpdateWithSpecialCharactersInAddress() {
        selectOptionById("province-select", "Tỉnh Hà Giang");
        selectOptionById("district-select", "Huyện Đồng Văn");
        selectOptionById("ward-select", "Xã Lũng Cú");

        fillInputField("street-address", "@#$%&*!"); // Address with special characters
        fillInputField("phone-number", "0123456789");

        // Click on Update button
        clickUpdateButton();

        // Verify error message for invalid address input
        Assert.assertEquals(getErrorMessage(), "Địa chỉ chứa ký tự không hợp lệ.");
    }



    @AfterClass
    public void closeBrowser() {
        driver.quit();
    }
}
