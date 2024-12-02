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
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class ProductTest {

    private WebDriver driver;
    private String baseUrl = "http://localhost:3000"; // URL của ứng dụng web của bạn

    @BeforeClass
    public void openBrowser() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        driver.navigate().to(baseUrl);
    }

    @Test(priority = 1)
    public void loginTest() {
        driver.get(baseUrl + "/login");  // Truy cập trang đăng nhập

        // Nhập thông tin đăng nhập (Cập nhật theo form đăng nhập của bạn)
        WebElement usernameField = driver.findElement(By.xpath("//input[@placeholder='Nhập tài khoản của bạn']"));
        WebElement passwordField = driver.findElement(By.xpath("//input[@placeholder='Nhập mật khẩu của bạn']"));
        WebElement loginButton = driver.findElement(By.xpath("//button[@type='submit']"));

        usernameField.sendKeys("admin");  // Thay đổi theo thông tin đăng nhập của bạn
        passwordField.sendKeys("123");  // Thay đổi theo mật khẩu của bạn

        // Nhấn nút đăng nhập
        loginButton.click();

        // Kiểm tra xem đăng nhập thành công hay không (kiểm tra bằng URL hoặc một phần tử xuất hiện sau khi đăng nhập)
        WebElement loggedInElement = driver.findElement(By.xpath("//div[@class='h-screen bg-white transition-width duration-300 w-64']"));
        Assert.assertNotNull(loggedInElement, "Đăng nhập không thành công!");
    }

    @Test(priority = 2, dependsOnMethods = "loginTest", dataProvider = "ProductData")
    public void createProductTest(String productName, String productPrice, String productStock, String productCategory, String brand, String status, String description, String imagePath, String expectedMessage) {
        driver.get(baseUrl + "/admin/product"); // Điều hướng đến trang tạo sản phẩm
        createProduct(productName, productPrice, productStock, productCategory, brand, status, description, imagePath, expectedMessage);

        if(){

        }
        // Kiểm tra thông báo
        WebElement errorMessage = driver.findElement(By.className("text-red-500"));
        String actualMessage = errorMessage.getText();
        Assert.assertEquals(actualMessage, expectedMessage);

        WebElement successMessage = driver.findElement(By.xpath())
    }

    private void createProduct(String productName, String productPrice, String productStock, String productCategory, String brand, String status, String description, String imagePath, String expectedMessage) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // Nhấn nút Thêm loại sản phẩm
            WebElement createProductButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'+ Thêm loại sản phẩm')]")));
            createProductButton.click();

            // Nhập thông tin sản phẩm
            WebElement productNameField = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@placeholder='Nhập Tên Sản Phẩm']")));
            WebElement productPriceField = driver.findElement(By.xpath("//input[@placeholder='0.00']"));
            WebElement productStockField = driver.findElement(By.xpath("//input[@placeholder='Nhập Số Lượng']"));
            WebElement productCategorySelect = driver.findElement(By.xpath("//select[@name='categoryId']"));
            WebElement brandSelect = driver.findElement(By.xpath("//select[@name='brandId']"));
            WebElement statusSelect = driver.findElement(By.xpath("//select[@name='status']"));
            WebElement descriptionField = driver.findElement(By.xpath("//textarea[@placeholder='Nhập Mô Tả Sản Phẩm']"));
            WebElement imageUploadField = driver.findElement(By.xpath("//input[@id='image-upload']"));
            WebElement submitButton = driver.findElement(By.xpath("//button[contains(text(),'Tạo Sản Phẩm')]"));

            // Điền dữ liệu vào các trường
            productNameField.clear();
            productNameField.sendKeys(productName);
            productPriceField.clear();
            productPriceField.sendKeys(productPrice);
            productStockField.clear();
            productStockField.sendKeys(productStock);

            new Select(productCategorySelect).selectByVisibleText(productCategory);
            new Select(brandSelect).selectByVisibleText(brand);
            new Select(statusSelect).selectByVisibleText(status);

            descriptionField.clear();
            descriptionField.sendKeys(description);

            // Tải lên hình ảnh
            imageUploadField.sendKeys(imagePath);

            // Nhấn nút tạo sản phẩm
            submitButton.click();

        } catch (Exception e) {
            Assert.fail("Xảy ra lỗi trong quá trình tạo sản phẩm: " + e.getMessage(), e);
        }
    }

    @DataProvider(name = "ProductData")
    public Object[][] productCreateData() {
        return new Object[][]{
                {"", "15,000,000", "50", "CPU", "Intel", "Còn hoạt động", "CPU", "C:\\Users\\ADMIN\\Pictures\\Saved Pictures\\nen.jpg", "Bắt buộc nhập tên sản phẩm"},
                {"Intel Core i3", "abc", "50", "CPU", "Intel", "Còn hoạt động", "CPU", "C:\\Users\\ADMIN\\Pictures\\Saved Pictures\\nen.jpg", "Giá phải là số"},
                {"Samsung SSD 1TB", "25,000,000", "-5", "CPU", "Intel", "Còn hoạt động", "CPU", "C:\\Users\\ADMIN\\Pictures\\Saved Pictures\\nen.jpg", "Số lượng phải là số dương"},
                {"AMD Ryzen 3", "10,000,000", "50", "", "AMD", "Còn hoạt động", "CPU", "C:\\Users\\ADMIN\\Pictures\\Saved Pictures\\nen.jpg", "Danh mục là bắt buộc"},
                {"AMD Ryzen 5", "15,000,000", "50", "CPU", "AMD", "", "CPU", "C:\\Users\\ADMIN\\Pictures\\Saved Pictures\\nen.jpg", "Trạng thái là bắt buộc"},
        };
    }

    @Test(priority = 3, dependsOnMethods = "loginTest")
    public void updateProductTest() {
        // Điều hướng đến trang quản lý sản phẩm
        driver.get(baseUrl + "/admin/product");
        // Nhấn nút sửa sản phẩm (giả sử sửa sản phẩm đầu tiên trong danh sách)
        WebElement updateProduct = driver.findElement(By.xpath("//div[@id='cell-7-1']//button[contains(@class, 'bg-blue-500')]"));
        updateProduct.click();
        // Nhập thông tin mới vào form
         WebElement statusSelect = driver.findElement(By.xpath("//select[@name='status']"));
        WebElement submitButton = driver.findElement(By.xpath("//button[contains(text(),'Cập Nhật Sản Phẩm')]"));
        // Cập nhật trạng thái
        Select statusDropdown = new Select(statusSelect);
        statusDropdown.selectByVisibleText("Còn hoạt động");
        // Nhấn nút cập nhật sản phẩm
        submitButton.click();
    }

    @AfterClass
    public void tearDown() {
        // Đóng trình duyệt sau khi kiểm tra xong
        if (driver != null) {
            driver.quit();
        }
    }


}
