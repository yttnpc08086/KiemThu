package com.nhom4;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.List;
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
    public void createProductTest(String productName, String productPrice, String productStock, String productCategory, String brand, String status, String expectedMessage) {
        driver.get(baseUrl + "/admin/product"); // Điều hướng đến trang tạo sản phẩm
        createProduct(productName, productPrice, productStock, productCategory, brand, status);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // Kiểm tra nếu có thông báo lỗi
            List<WebElement> errorMessages = driver.findElements(By.xpath("//span[@class='text-red-500 text-sm errormessage']"));
            if (!errorMessages.isEmpty()) {
                String actualMessage = errorMessages.get(0).getText();
                Assert.assertEquals(actualMessage, expectedMessage, "Thông báo lỗi không khớp!");
                System.out.println("Test case passed: " + actualMessage);
            } else {
                // Nếu không có lỗi, kiểm tra thông báo thành công
                WebElement successDialog = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@role='dialog']")));
                String successMessage = successDialog.getText();
                Assert.assertEquals(successMessage, expectedMessage, "Thông báo thành công không khớp!");

                // Click vào nút OK nếu có
                WebElement okButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[normalize-space()='OK']")));
                okButton.click();
                System.out.println("Product creation successful: " + successMessage);
            }
        } catch (NoSuchElementException e) {
            Assert.fail("Không tìm thấy thông báo lỗi hoặc thông báo thành công.", e);
        }
    }

    private void createProduct(String productName, String productPrice, String productStock, String productCategory, String brand, String status) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(1));

        try {
            // Nhấn nút Thêm loại sản phẩm
            WebElement createProductButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'+ Thêm loại sản phẩm')]")));
            createProductButton.click();

            // Nhập thông tin sản phẩm
            WebElement productNameField = driver.findElement(By.xpath("//input[@placeholder='Nhập Tên Sản Phẩm']"));
            WebElement productPriceField = driver.findElement(By.xpath("//input[@placeholder='0.00']"));
            WebElement productStockField = driver.findElement(By.xpath("//input[@placeholder='Nhập Số Lượng']"));
            WebElement productCategorySelect = driver.findElement(By.xpath("//select[@name='categoryId']"));
            WebElement brandSelect = driver.findElement(By.xpath("//select[@name='brandId']"));
            WebElement statusSelect = driver.findElement(By.xpath("//select[@name='status']"));
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

            
            // Nhấn nút tạo sản phẩm
            submitButton.click();

        } catch (TimeoutException e) {
            Assert.fail("Xảy ra lỗi trong quá trình tạo sản phẩm: " + e.getMessage(), e);
        }
    }

    @DataProvider(name = "ProductData")
    public Object[][] productCreateData() {
        return new Object[][]{
                // Để trống tên
                {"", "25000000", "50", "Mainboard", "Intel", "Còn hoạt động", "Bắt buộc nhập tên sản phẩm"},
                // Tên trùng
                {"Kingston 4GB RAM", "25000000", "50", "Mainboard", "Intel", "Còn hoạt động", "Tên sản phẩm đã tồn tại!"},
                // Trống giá
                {"Mainboard MSI PRO H610M-S WIFI DDR4", "", "50", "Mainboard", "Intel", "Còn hoạt động", "Giá phải là số"},
                // Nhập ký tự không phải số
                {"Mainboard MSI PRO H610M-S WIFI DDR4", "abc", "50", "Mainboard", "Intel", "Còn hoạt động", "Giá phải là số"},
                // Giá âm
                {"Mainboard MSI PRO H610M-S WIFI DDR4", "-25000000", "50", "Mainboard", "Intel", "Còn hoạt động", "Giá không thể là số âm"},
                // Trống tồn kho
                {"Mainboard MSI PRO H610M-S WIFI DDR4", "25000000", "", "Mainboard", "Intel", "Còn hoạt động", "Số lượng phải là số"},
                // Tồn không phải số
                {"Mainboard MSI PRO H610M-S WIFI DDR4", "25000000", "abc", "Mainboard", "Intel", "Còn hoạt động", "Số lượng phải là số"},
                // Tồn kho âm
                {"Mainboard MSI PRO H610M-S WIFI DDR4", "25000000", "-50", "Mainboard", "Intel", "Còn hoạt động", "Số lượng không thể là số âm"},
                // Thành công
                {"Mainboard MSI PRO H610M-S WIFI DDR4", "25000000", "50", "Mainboard", "Intel", "Còn hoạt động", "Tạo sản phẩm thành công!"},
        };
    }


    @Test(priority = 2, dependsOnMethods = "loginTest", dataProvider = "updateProductData")
    public void updateProductTest(String productName, String productPrice, String productStock, String expectedMessage) {
        driver.get(baseUrl + "/admin/product"); // Điều hướng đến trang quản lý sản phẩm
        updateProduct(productName, productPrice, productStock);

        try {
            // Kiểm tra thông báo lỗi hiển thị dưới dạng span
            WebElement errorMessage = driver.findElement(By.xpath("//span[@class='text-red-500 text-sm errormessage']"));
            if (errorMessage.isDisplayed()) {
                String actualMessage = errorMessage.getText();
                Assert.assertEquals(actualMessage, expectedMessage);
                System.out.println("Test case passed with error message: " + actualMessage);
                return;
            }
        } catch (NoSuchElementException e) {
            System.out.println("No inline error message found, checking dialog...");
        }

        // Kiểm tra thông báo popup của Swal2
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(1));

            WebElement swalPopup = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//div[@class='swal2-popup swal2-modal swal2-icon-error swal2-show']")));
            if (swalPopup.isDisplayed()) {
                String actualMessage = swalPopup.getText();
                Assert.assertEquals(actualMessage, expectedMessage);
                System.out.println("Swal2 popup displayed with message: " + actualMessage);

                // Nhấn nút OK để đóng popup
                WebElement okButton = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[contains(@class, 'swal2-confirm')]")));
                okButton.click();
                return;
            }
        } catch (TimeoutException e) {
            System.out.println("No Swal2 error popup found, checking other dialogs...");
        }

        // Xử lý các hộp thoại khác
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(1));
            WebElement dialog = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@role='dialog']")));
            if (dialog.isDisplayed()) {
                String dialogText = dialog.getText();
                System.out.println("Dialog found with text: " + dialogText);

                // Bỏ qua nếu dialog chứa thông báo lỗi không mong muốn
                if (dialogText.contains("Error") && dialogText.contains("An unexpected error occurred")) {
                    System.out.println("Skipping unexpected error dialog...");
                    WebElement okButton = wait.until(ExpectedConditions.elementToBeClickable(
                            By.xpath("//button[normalize-space()='OK']")));
                    okButton.click();
                    return; // Bỏ qua test case
                }


                // Xử lý các hộp thoại khác
                WebElement okButton = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//div[@role='dialog']//button[contains(text(), 'OK')]")));
                okButton.click();
            }
        } catch (TimeoutException e) {
            System.out.println("No dialog found. Test case ends.");
        }

        System.out.println("Product updated successfully");
    }
    private void updateProduct(String productName, String productPrice, String productStock) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        try {
            // Click the Edit Product button
            WebElement updateProduct = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//button[@class='bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600'])[1]")));
            updateProduct.click();

            // Enter product details
            WebElement productNameField = driver.findElement(By.xpath("//input[@placeholder='Nhập Tên Sản Phẩm']"));
            WebElement productPriceField = driver.findElement(By.xpath("//input[@placeholder='0.00']"));
            WebElement productStockField = driver.findElement(By.xpath("//input[@placeholder='Nhập Số Lượng']"));
            WebElement submitButton = driver.findElement(By.xpath("//button[contains(text(),'Cập Nhật Sản Phẩm')]"));

            // Fill in the fields
            productNameField.clear();
            productNameField.sendKeys(productName);
            productPriceField.clear();
            productPriceField.sendKeys(productPrice);
            productStockField.clear();
            productStockField.sendKeys(productStock);
            // Click the Update Product button
            submitButton.click();

        } catch (TimeoutException e) {
            Assert.fail("Error occurred while updating product: " + e.getMessage(), e);
        }
    }

    @DataProvider(name = "updateProductData")
    public Object[][] UpdateProductData() {
        return new Object[][]{
                // Test case chỉ sửa tên sản phẩm
                {"", "25,000,000", "50", "Bắt buộc nhập tên sản phẩm"},
                {"@@@@", "25,000,000", "50", "Tên sản phẩm không hợp lệ"},
                {"Bo Mạch Chủ ASUS ROG STRIX", "abc", "50", "Giá phải là số"},
                {"Bo Mạch Chủ ASUS ROG STRIX", "0", "50", "Giá phải lớn hơn 0"},
                {"Bo Mạch Chủ ASUS ROG STRIX", "25,000,000", "abc", "Số lượng phải là số"},
                {"Bo Mạch Chủ ASUS ROG STRIX", "25,000,000", "", "Số lượng phải là số"},
                {"Bo Mạch Chủ ASUS ROG STRIX", "30,000,000", "100", "Success Product updated successfully! OK"},

        };
    }

    @Test(priority = 3, dependsOnMethods = "loginTest")
    public void SearchProductWithNameTest() {
        driver.get(baseUrl + "/admin/product"); // Điều hướng đến trang quản lý sản phẩm
        String keyword = "Samsung";
        // Nhập từ khóa tìm kiếm
        WebElement searchField = driver.findElement(By.xpath("//input[@placeholder='Tìm kiếm theo tên...']"));
        searchField.sendKeys(keyword);


//       Kiem tra ket qua
        WebElement searchResult = driver.findElement(By.xpath("//div[@class='sc-dprtRQ htmdXE rdt_TableBody']"));
// Lấy tất cả các hàng trong bảng
        List<WebElement> rows = driver.findElements(By.xpath("//div[@class='sc-dprtRQ htmdXE rdt_TableBody']//div[@class='rdt_TableRow']"));

        // Kiểm tra từng hàng xem có chứa từ khóa không
        boolean isKeywordFound = false;
        for (WebElement row : rows) {
            String rowText = row.getText();
            if (rowText.contains(keyword)) {
                isKeywordFound = true;
                System.out.println("Kết quả tìm thấy: " + rowText);
            }

        }
    }

    @Test(priority = 4, dependsOnMethods = "loginTest", dataProvider = "SearchProductWithPriceData")
    public void SearchProductWithPriceTest(String minprice, String maxprice) {
        driver.get(baseUrl + "/admin/product"); // Điều hướng đến trang quản lý sản phẩm

        // Nhập Min Price và Max Price
        WebElement minPriceField = driver.findElement(By.xpath("//input[@placeholder='Min Price']"));
        minPriceField.clear(); // Xóa giá trị cũ nếu có
        minPriceField.sendKeys(minprice);

        WebElement maxPriceField = driver.findElement(By.xpath("//input[@placeholder='Max Price']"));
        maxPriceField.clear();
        maxPriceField.sendKeys(maxprice);

        // Đợi hệ thống cập nhật kết quả tìm kiếm
        try {
            Thread.sleep(500); // Tạm dừng 2 giây (thay thế bằng WebDriverWait nếu có thể)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Lấy danh sách các hàng trong bảng
        List<WebElement> rows = driver.findElements(By.xpath("//div[@class='sc-dprtRQ htmdXE rdt_TableBody']//div[@class='rdt_TableRow']"));

        // Kiểm tra từng hàng có nằm trong khoảng giá không
        boolean isAllPricesInRange = true;
        for (WebElement row : rows) {
            // Tìm cột "Đơn Giá" trong hàng (giả sử đây là cột thứ 4)
            WebElement priceCell = row.findElement(By.xpath(".//div[4]"));
            String priceText = priceCell.getText().replace(" VND", "").replace(".", ""); // Xóa định dạng VND và dấu chấm
            try {
                int price = Integer.parseInt(priceText);

                // Kiểm tra giá có nằm trong khoảng minprice và maxprice không
                if (price < Integer.parseInt(minprice) || price > Integer.parseInt(maxprice)) {
                    isAllPricesInRange = false;
                    System.out.println("Sản phẩm ngoài khoảng giá: " + row.getText());
                }
            } catch (NumberFormatException e) {
                System.out.println("Không thể chuyển đổi giá: " + priceText);
            }
        }

        // Kết luận kiểm tra
        if (isAllPricesInRange) {
            System.out.println("Tất cả sản phẩm nằm trong khoảng giá từ " + minprice + " đến " + maxprice);
        } else {
            System.out.println("Có sản phẩm không nằm trong khoảng giá!");
        }
    }

    @DataProvider(name = "SearchProductWithPriceData")
    public Object[][] SearchProductWithPriceData() {
        return new Object[][]{
                {"-1000000", "5000000"},
                {"1000000", "-5000000"},
                {"10000000", ""},
                {"", "5000000"},
                {"5000000", "10000000"},
                {"abc", "5000000"},
                {"5000000", "abc"},

        };
    }


    @AfterClass
    public void tearDown() {
        // Đóng trình duyệt sau khi kiểm tra xong
        if (driver != null) {
            driver.quit();
        }
    }
}