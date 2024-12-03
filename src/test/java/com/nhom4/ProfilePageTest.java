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
import java.util.concurrent.TimeUnit;

public class ProfilePageTest {
    WebDriver driver;

    @BeforeClass
    public void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().window().maximize();

        // Truy cập vào trang profile với userId = 3
        driver.get("http://localhost:3000/profile?userId=2"); // Đường dẫn trang ProfilePage của userId = 3
    }

    @Test(priority = 1)
    public void verifyPageTitle() {
        String pageTitle = driver.getTitle();
        Assert.assertEquals(pageTitle, "Profile Page", "Sai tiêu đề trang!");
    }

    @Test(priority = 2)
    public void verifyUserProfileDisplayed() {
        // Đợi phần tử <div> có id="full-name" xuất hiện
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement fullNameDiv = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("full-name")));

        // Kiểm tra xem phần tử có hiển thị hay không
        Assert.assertTrue(fullNameDiv.isDisplayed(), "Tên đầy đủ không hiển thị!");
    }


    @Test(priority = 3)
    public void updateProfileImage() {
        // Tìm phần tử nút tải ảnh bằng id "profileImage"
        WebElement uploadButton = driver.findElement(By.id("profileImage"));
        uploadButton.sendKeys("C:\\path\\to\\image.jpg");

        // Sử dụng id của nút lưu
        WebElement saveButton = driver.findElement(By.id("save-button"));
        saveButton.click();

        // Tìm phần tử thông báo thành công bằng class
        WebElement toastMessage = driver.findElement(By.cssSelector(".Toastify__toast--success"));
        Assert.assertTrue(toastMessage.isDisplayed(), "Không hiển thị thông báo thành công!");
    }

    @Test(priority = 4)
    public void updateProfileFields() {
        // Tìm input bằng id full-name
        WebElement fullNameInput = driver.findElement(By.id("full-name"));
        fullNameInput.clear();
        fullNameInput.sendKeys("Người Dùng Mới");

        // Sử dụng id của nút lưu
        WebElement saveButton = driver.findElement(By.id("save-button"));
        saveButton.click();

        // Tìm thông báo thành công
        WebElement toastMessage = driver.findElement(By.cssSelector(".Toastify__toast--success"));
        Assert.assertTrue(toastMessage.isDisplayed(), "Không hiển thị thông báo thành công!");

        // Kiểm tra lại giá trị trong input
        String updatedName = fullNameInput.getAttribute("value");
        Assert.assertEquals(updatedName, "Người Dùng Mới", "Tên không được cập nhật!");
    }

    @AfterClass
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
