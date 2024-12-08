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

import java.io.File;
import java.time.Duration;
import java.util.List;

public class CategoryTest {
    private WebDriver driver;
    private String baseUrl = "http://localhost:3000";
    private WebDriverWait wait;

    @BeforeClass
    public void openBrowser() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.navigate().to(baseUrl);
    }

    @AfterClass
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void login() {
        driver.get(baseUrl + "/login");

        WebElement usernameField = driver.findElement(By.xpath("//input[@placeholder='Nhập tài khoản của bạn']"));
        WebElement passwordField = driver.findElement(By.xpath("//input[@placeholder='Nhập mật khẩu của bạn']"));
        WebElement loginButton = driver.findElement(By.xpath("//button[@type='submit']"));

        usernameField.sendKeys("admin");
        passwordField.sendKeys("123");
        loginButton.click();

        try {
            WebElement closeButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".swal2-close")));
            closeButton.click();
        } catch (Exception e) {
            // Ignore if no modal appears
        }

        WebElement categoryLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[normalize-space()='Category']")));
        categoryLink.click();

        wait.until(ExpectedConditions.urlToBe("http://localhost:3000/admin/category"));
        String currentUrl = driver.getCurrentUrl();
        Assert.assertEquals(currentUrl, "http://localhost:3000/admin/category", "Incorrect redirect!");
    }

    @DataProvider(name = "categoryCreateData")
    public Object[][] getCategoryCreateData() {
        return new Object[][] {
                // name, description, imagePath, expectedResult, isSuccess
                {"Laptop", "Thiết bị điện tử", "D:\\study\\JAVA6\\JAVA6_ASM\\DATN-TOPDEV-2024\\FE\\java6-creact_abc\\src\\assets\\images\\imageCategories\\gpuicon.jpg", true, true},
                {"", "Thiết bị điện tử", "", "Tên danh mục là bắt buộc", false},
                {"CPU", "CPU Description", "D:\\study\\JAVA6\\JAVA6_ASM\\DATN-TOPDEV-2024\\FE\\java6-creact_abc\\src\\assets\\images\\imageCategories\\gpuicon.jpg", "Tên danh mục đã tồn tại!", false},
                {"Test Category", "Test Description", "D:\\image\\test.pdf", "Vui lòng chọn file ảnh hợp lệ", false}
        };
    }

    @Test(priority = 1, dataProvider = "categoryCreateData")
    public void testCreateCategory(String name, String description, String imagePath, Object expectedResult, boolean isSuccess) {
        login();

        WebElement addCategoryButton = driver.findElement(By.xpath("//button[contains(text(),'Thêm loại sản phẩm')]"));
        addCategoryButton.click();

        if (!name.isEmpty()) {
            WebElement categoryName = driver.findElement(By.xpath("//input[@name='name']"));
            categoryName.sendKeys(name);
        }

        if (!description.isEmpty()) {
            WebElement categoryDescription = driver.findElement(By.xpath("//input[@name='description']"));
            categoryDescription.sendKeys(description);
        }

        if (!imagePath.isEmpty()) {
            WebElement imageUpload = driver.findElement(By.xpath("//input[@type='file']"));
            imageUpload.sendKeys(new File(imagePath).getAbsolutePath());
        }

        WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),'Thêm Danh Mục')]"));
        saveButton.click();

        if (isSuccess) {
            WebElement saveSuccessSwal = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button[class='swal2-confirm swal2-styled']")
            ));
            saveSuccessSwal.click();

            WebElement newCategoryRow = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[@data-tag='allowRowEvents'][normalize-space()='" + name + "']")
            ));
            Assert.assertNotNull(newCategoryRow, "Category '" + name + "' was not added to the table!");
        } else {
            WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(text(),'" + expectedResult + "')]")
            ));
            Assert.assertEquals(errorMessage.getText(), expectedResult, "Error message not matching!");
        }
    }

    @DataProvider(name = "categoryEditData")
    public Object[][] getCategoryEditData() {
        return new Object[][] {
                // oldName, newName, newDescription, newImagePath, expectedResult, isSuccess
                {"Laptop", "Smartphone", "Thiết bị điện thoại thông minh", "D:\\study\\JAVA6\\JAVA6_ASM\\DATN-TOPDEV-2024\\FE\\java6-creact_abc\\src\\assets\\images\\imageCategories\\cpuicon.jpg", "Danh mục đã được cập nhật.", true},
                {"Smartphone", "", "Test Description", "", "Tên danh mục là bắt buộc", false},
                {"Smartphone", "Smartphone", "Thiết bị điện thoại thông minh", "", "Danh mục đã được cập nhật.", true}
        };
    }

    @Test(priority = 2, dataProvider = "categoryEditData")
    public void testEditCategory(String oldName, String newName, String newDescription, String newImagePath, Object expectedResult, boolean isSuccess) {
        login();

        WebElement categoryRow = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@data-tag='allowRowEvents'][normalize-space()='" + oldName + "']")
        ));
        WebElement editButton = categoryRow.findElement(By.xpath(".//following-sibling::div//button[contains(@class,'bg-blue-500')]"));
        editButton.click();

        WebElement nameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@name='name']")));
        WebElement descriptionField = driver.findElement(By.xpath("//input[@name='description']"));

        nameField.clear();
        if (!newName.isEmpty()) {
            nameField.sendKeys(newName);
        }

        descriptionField.clear();
        descriptionField.sendKeys(newDescription);

        if (!newImagePath.isEmpty()) {
            WebElement imageUpload = driver.findElement(By.xpath("//input[@type='file']"));
            imageUpload.sendKeys(new File(newImagePath).getAbsolutePath());
        }

        WebElement saveButton = driver.findElement(By.cssSelector("button[type='submit']"));
        saveButton.click();

        if (isSuccess) {
            WebElement successMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[@class='swal2-html-container']")
            ));
            Assert.assertTrue(successMessage.getText().contains(expectedResult.toString()));

            WebElement confirmButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button[class='swal2-confirm swal2-styled']")
            ));
            confirmButton.click();
        } else {
            WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(text(),'" + expectedResult + "')]")
            ));
            Assert.assertEquals(errorMessage.getText(), expectedResult);
        }
    }

    @DataProvider(name = "categorySearchData")
    public Object[][] getCategorySearchData() {
        return new Object[][] {
                // searchTerm, expectedResults, shouldFind
                {"CPU", "CPU", true},
                {"Tablet", "Tablet", false},
                {"@#!", "@#!", false},
                {"", "", true}
        };
    }

    @Test(priority = 3, dataProvider = "categorySearchData")
    public void testSearchCategory(String searchTerm, String expectedResult, boolean shouldFind) {
        login();

        WebElement searchInput = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//input[@placeholder='Tìm kiếm theo tên...']")
        ));
        searchInput.clear();
        searchInput.sendKeys(searchTerm);

        List<WebElement> results = driver.findElements(
                By.xpath("//div[@data-tag='allowRowEvents'][normalize-space()='" + expectedResult + "']")
        );

        if (shouldFind) {
            Assert.assertFalse(results.isEmpty(), "Expected to find results for: " + searchTerm);
        } else {
            Assert.assertTrue(results.isEmpty(), "Should not find results for: " + searchTerm);
        }
    }

    @DataProvider(name = "categoryDeleteData")
    public Object[][] getCategoryDeleteData() {
        return new Object[][] {
                // categoryName, confirmDelete, expectedMessage
                {"Smartphone", true, "Danh mục đã được xóa."},
                {"CPU", false, null}
        };
    }

    @Test(priority = 4, dataProvider = "categoryDeleteData")
    public void testDeleteCategory(String categoryName, boolean confirmDelete, String expectedMessage) {
        login();

        WebElement categoryRow = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@data-tag='allowRowEvents'][normalize-space()='" + categoryName + "']")
        ));

        WebElement deleteButton = categoryRow.findElement(By.xpath(".//following-sibling::div//button[contains(@class,'bg-red-500')]"));
        deleteButton.click();

        if (confirmDelete) {
            WebElement confirmButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(),'Đồng ý')]")
            ));
            confirmButton.click();

            WebElement successMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[@class='swal2-html-container']")
            ));
            Assert.assertEquals(successMessage.getText(), expectedMessage);

            WebElement closeButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(),'Đóng')]")
            ));
            closeButton.click();

            List<WebElement> deletedCategory = driver.findElements(
                    By.xpath("//div[@data-tag='allowRowEvents'][normalize-space()='" + categoryName + "']")
            );
            Assert.assertTrue(deletedCategory.isEmpty(), "Category still exists after deletion!");
        } else {
            WebElement cancelButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(),'Hủy')]")
            ));
            cancelButton.click();

            WebElement categoryStillExists = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[@data-tag='allowRowEvents'][normalize-space()='" + categoryName + "']")
            ));
            Assert.assertNotNull(categoryStillExists, "Category should still exist after canceling deletion!");
        }
    }
}