package com.nhom4;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import java.io.File;
import java.time.Duration;
import java.util.List;

public class CategoryTest {
    private WebDriver driver;
    private String baseUrl = "http://localhost:3000"; // URL của trang web

    // Setup WebDriver
    @BeforeClass
    public void openBrowser() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.navigate().to(baseUrl);
    }

    // Dọn dẹp WebDriver sau khi các bài test
    @AfterClass
    public void closeBrowser() {
        System.out.println("Closing browser");
        if (driver != null) {
            driver.quit(); // Đóng toàn bộ trình duyệt
        }
    }

    @Test
    public void testCreateCategory() throws InterruptedException {
        // Đăng nhập
        login();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));

        // Mở trang tạo danh mục mới
        WebElement addCategoryButton = driver.findElement(By.xpath("//button[contains(text(),'Thêm loại sản phẩm')]"));
        addCategoryButton.click();

        // Điền thông tin người dùng
        WebElement CategoryName = driver.findElement(By.xpath("//input[@name='name']"));
        WebElement Categorydescription = driver.findElement(By.xpath("//input[@name='description']"));
        WebElement imageUploadField = driver.findElement(By.xpath("//input[@type='file']"));

        // Nhập thông tin vào các trường
        CategoryName.sendKeys("Laptop");
        Categorydescription.sendKeys("Thiết bị điện tử");

        // Chọn ảnh cho danh mục
        File imageFile = new File("D:\\study\\JAVA6\\JAVA6_ASM\\DATN-TOPDEV-2024\\FE\\java6-creact_abc\\src\\assets\\images\\imageCategories\\gpuicon.jpg");
        imageUploadField.sendKeys(imageFile.getAbsolutePath());

        // Xử lý Swal tải ảnh lên
        WebDriverWait uploadWait = new WebDriverWait(driver, Duration.ofSeconds(5));

        // Nhấn nút "Thêm danh mục"
        WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),'Thêm Danh Mục')]"));
        saveButton.click();

        // Xử lý Swal lưu thành công
        WebElement saveSuccessSwal = uploadWait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button[class='swal2-confirm swal2-styled']")
        ));
        saveSuccessSwal.click();

        // Chờ bảng cập nhật dữ liệu
        Thread.sleep(2000); // Tạm thời chờ để bảng load dữ liệu mới

        // Kiểm tra tên danh mục trong bảng
        WebElement newCategoryRow = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@data-tag='allowRowEvents'][normalize-space()='Laptop']")
        ));
        Assert.assertNotNull(newCategoryRow, "Danh mục 'Laptop' không được thêm vào bảng dữ liệu!");
    }

    @Test
    public void testCreateCategoryWithEmptyName() {
        // Đăng nhập
        login();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        // Mở trang tạo danh mục mới
        WebElement addCategoryButton = driver.findElement(By.xpath("//button[contains(text(),'Thêm loại sản phẩm')]"));
        addCategoryButton.click();

        // Nhấn nút "Thêm danh mục" mà không điền tên danh mục
        WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),'Thêm Danh Mục')]"));
        saveButton.click();

        // Xác minh thông báo lỗi xuất hiện cho trường "Tên danh mục"
        WebElement categoryNameError = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(text(),'Tên danh mục là bắt buộc')]")
        ));

        // Xác minh nội dung của thông báo lỗi
        Assert.assertEquals(categoryNameError.getText(), "Tên danh mục là bắt buộc", "Lỗi tên danh mục không đúng!");

        // Đảm bảo lỗi hiển thị
        Assert.assertTrue(categoryNameError.isDisplayed(), "Lỗi tên danh mục không hiển thị!");
    }


    @Test
    public void testCreateCategoryWithExistingName() {
        // Đăng nhập
        login();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        // Mở trang tạo danh mục mới
        WebElement addCategoryButton = driver.findElement(By.xpath("//button[contains(text(),'Thêm loại sản phẩm')]"));
        addCategoryButton.click();

        // Nhập tên danh mục đã tồn tại ("CPU")
        WebElement categoryNameField = driver.findElement(By.xpath("//input[@name='name']"));
        categoryNameField.sendKeys("CPU");

        // Nhấn nút "Thêm danh mục"
        WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),'Thêm Danh Mục')]"));
        saveButton.click();

        // Xác minh thông báo lỗi khi danh mục đã tồn tại
        WebElement duplicateCategoryError = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(text(),'Tên danh mục đã tồn tại!')]")
        ));

        // Xác minh nội dung của thông báo lỗi
        Assert.assertEquals(duplicateCategoryError.getText(), "Tên danh mục đã tồn tại!", "Lỗi tên danh mục trùng lặp không đúng!");

        // Đảm bảo lỗi hiển thị
        Assert.assertTrue(duplicateCategoryError.isDisplayed(), "Lỗi tên danh mục trùng lặp không hiển thị!");
    }

    @Test
    public void testCreateCategoryWithInvalidImage() {
        // Đăng nhập
        login();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        // Mở trang tạo danh mục mới
        WebElement addCategoryButton = driver.findElement(By.xpath("//button[contains(text(),'Thêm loại sản phẩm')]"));
        addCategoryButton.click();

        // Nhập tên và mô tả danh mục hợp lệ
        WebElement categoryNameField = driver.findElement(By.xpath("//input[@name='name']"));
        WebElement categoryDescriptionField = driver.findElement(By.xpath("//input[@name='description']"));
        categoryNameField.sendKeys("Laptop");
        categoryDescriptionField.sendKeys("Thiết bị điện tử");

        // Tải file .pdf (ảnh không hợp lệ)
        WebElement imageUploadField = driver.findElement(By.xpath("//input[@type='file']"));
        File invalidImageFile = new File("D:\\image\\test.pdf");
        imageUploadField.sendKeys(invalidImageFile.getAbsolutePath());

        // Nhấn nút "Thêm danh mục"
        WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),'Thêm Danh Mục')]"));
        saveButton.click();

        // Xác minh thông báo lỗi tải ảnh không hợp lệ
        WebElement invalidImageError = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(text(),'Vui lòng chọn file ảnh hợp lệ')]")
        ));

        // Xác minh nội dung của thông báo lỗi
        Assert.assertEquals(invalidImageError.getText(), "Vui lòng chọn file ảnh hợp lệ", "Thông báo lỗi tải ảnh không hợp lệ không đúng!");

        // Đảm bảo lỗi hiển thị
        Assert.assertTrue(invalidImageError.isDisplayed(), "Lỗi tải ảnh không hợp lệ không hiển thị!");
    }

    @Test
    public void testEditCategory() {
        // Đăng nhập
        login();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Mở trang quản lý danh mục
        WebElement categoryLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[normalize-space()='Category']")
        ));
        categoryLink.click();

        // Tìm hàng chứa danh mục "Laptop"
        WebElement categoryRow = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@data-tag='allowRowEvents'][normalize-space()='Laptop']")
        ));

        // Tìm nút Edit tương ứng trong hàng
        WebElement editButton = categoryRow.findElement(By.xpath(".//following-sibling::div//button[contains(@class,'bg-blue-500')]"));
        editButton.click();

        // Chờ form chỉnh sửa hiển thị
        WebElement categoryNameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@name='name']")));
        WebElement categoryDescriptionField = driver.findElement(By.xpath("//input[@name='description']"));
        WebElement imageUploadField = driver.findElement(By.xpath("//input[@type='file']"));

        // Nhập thông tin mới
        categoryNameField.clear();
        categoryNameField.sendKeys("Smartphone"); // Nhập tên mới

        categoryDescriptionField.clear();
        categoryDescriptionField.sendKeys("Thiết bị điện thoại thông minh"); // Nhập mô tả mới

        // Tải ảnh mới lên
        File imageFile = new File("D:\\study\\JAVA6\\JAVA6_ASM\\DATN-TOPDEV-2024\\FE\\java6-creact_abc\\src\\assets\\images\\imageCategories\\cpuicon.jpg");
        imageUploadField.sendKeys(imageFile.getAbsolutePath());

        // Nhấn nút "Lưu thay đổi"
        WebElement saveButton = driver.findElement(By.cssSelector("button[type='submit']"));
        saveButton.click();

        // Xác minh thông báo thành công (Swal)
        WebElement confirmButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button[class='swal2-confirm swal2-styled']")
        ));
        confirmButton.click();

        // Kiểm tra lại dữ liệu sau khi cập nhật
        WebElement updatedCategoryRow = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@data-tag='allowRowEvents'][normalize-space()='Smartphone']")
        ));


        // Kiểm tra dữ liệu cập nhật
        Assert.assertNotNull(updatedCategoryRow, "Tên danh mục không được cập nhật!");
    }

    @Test
    public void testEditCategoryWithEmptyName() {
        // Đăng nhập
        login();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Mở trang quản lý danh mục
        WebElement categoryLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[normalize-space()='Category']")
        ));
        categoryLink.click();

        // Tìm hàng chứa danh mục "Laptop"
        WebElement categoryRow = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@data-tag='allowRowEvents'][normalize-space()='Smartphone']")
        ));

        // Tìm nút Edit tương ứng trong hàng
        WebElement editButton = categoryRow.findElement(By.xpath(".//following-sibling::div//button[contains(@class,'bg-blue-500')]"));
        editButton.click();

        // Chờ form chỉnh sửa hiển thị
        WebElement categoryNameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@name='name']")));

        // Xóa nội dung trong trường tên danh mục
        categoryNameField.clear();

        // Nhấn nút "Lưu thay đổi"
        WebElement saveButton = driver.findElement(By.cssSelector("button[type='submit']"));
        saveButton.click();

        // Xác minh thông báo lỗi xuất hiện
        WebElement nameErrorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(text(),'Tên danh mục là bắt buộc')]")
        ));

        // Kiểm tra nội dung thông báo lỗi
        Assert.assertEquals(nameErrorMessage.getText(), "Tên danh mục là bắt buộc", "Thông báo lỗi không đúng!");

        // Kiểm tra thông báo lỗi hiển thị
        Assert.assertTrue(nameErrorMessage.isDisplayed(), "Thông báo lỗi không hiển thị!");
    }

    @Test
    public void testEditCategoryWithoutChanges() {
        // Đăng nhập
        login();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Mở trang quản lý danh mục
        WebElement categoryLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[normalize-space()='Category']")
        ));
        categoryLink.click();

        // Tìm hàng chứa danh mục "Smartphone"
        WebElement categoryRow = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@data-tag='allowRowEvents'][normalize-space()='Smartphone']")
        ));

        // Tìm nút Edit tương ứng trong hàng
        WebElement editButton = categoryRow.findElement(By.xpath(".//following-sibling::div//button[contains(@class,'bg-blue-500')]"));
        editButton.click();

        // Chờ form chỉnh sửa hiển thị
        WebElement categoryNameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@name='name']")));
        WebElement categoryDescriptionField = driver.findElement(By.xpath("//input[@name='description']"));

        // Nhấn nút "Lưu thay đổi" mà không chỉnh sửa gì
        WebElement saveButton = driver.findElement(By.cssSelector("button[type='submit']"));
        saveButton.click();

        // Xác minh thông báo "Danh mục đã được cập nhật."
        WebElement successMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@class='swal2-html-container' and normalize-space()='Danh mục đã được cập nhật.']")
        ));
        Assert.assertNotNull(successMessage, "Thông báo cập nhật không được hiển thị!");

        // Nhấn nút OK trên Swal
        WebElement confirmButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button[class='swal2-confirm swal2-styled']")
        ));
        confirmButton.click();
    }

    @Test
    public void testUsedDeleteCategory() {
        // Đăng nhập
        login();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Mở trang quản lý danh mục
        WebElement categoryLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[normalize-space()='Category']")
        ));
        categoryLink.click();

        // Tìm hàng chứa danh mục "Smartphone"
        WebElement categoryRow = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@data-tag='allowRowEvents'][normalize-space()='Smartphone']")
        ));

        // Tìm nút Delete tương ứng trong hàng
        WebElement deleteButton = categoryRow.findElement(By.xpath(".//following-sibling::div//button[contains(@class,'bg-red-500')]"));
        deleteButton.click();

        // Nhấn nút "Đồng ý"
        WebElement confirmButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(),'Đồng ý')]")
        ));
        confirmButton.click();

        // Xác minh thông báo thành công
        WebElement successMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@class='swal2-html-container' and normalize-space()='Danh mục đã được xóa.']")
        ));
        Assert.assertNotNull(successMessage, "Thông báo xóa thành công không hiển thị!");

        // Nhấn nút "Đóng" để hoàn tất
        WebElement closeButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(),'Đóng')]")
        ));
        closeButton.click();

        // Xác minh danh mục đã bị xóa khỏi bảng
        List<WebElement> deletedCategoryRow = driver.findElements(
                By.xpath("//div[@data-tag='allowRowEvents'][normalize-space()='Smartphone']")
        );
        Assert.assertTrue(deletedCategoryRow.isEmpty(), "Danh mục vẫn tồn tại sau khi xóa!");
    }

    @Test
    public void testUsedDeleteCancelCategory() {
        // Đăng nhập
        login();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Mở trang quản lý danh mục
        WebElement categoryLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[normalize-space()='Category']")
        ));
        categoryLink.click();

        // Tìm hàng chứa danh mục "Smartphone"
        WebElement categoryRow = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@data-tag='allowRowEvents'][normalize-space()='Smartphone']")
        ));

        // Tìm nút Delete tương ứng trong hàng
        WebElement deleteButton = categoryRow.findElement(By.xpath(".//following-sibling::div//button[contains(@class,'bg-red-500')]"));
        deleteButton.click();

        // Nhấn nút "Hủy" trong hộp thoại xác nhận
        WebElement cancelButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(),'Hủy')]")
        ));
        cancelButton.click();

        // Xác minh danh mục vẫn còn trong bảng
        WebElement categoryStillExists = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@data-tag='allowRowEvents'][normalize-space()='Smartphone']")
        ));
        Assert.assertNotNull(categoryStillExists, "Danh mục bị xóa sau khi hủy xóa!");
    }

    @Test
    public void testSearchCategoryByName() {
        // Đăng nhập
        login();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        // Nhập tên danh mục "CPU" vào ô tìm kiếm
        String searchTerm = "CPU";
        WebElement searchInput = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//input[@placeholder='Tìm kiếm theo tên...']")
        ));
        searchInput.clear();  // Xóa nội dung trong ô tìm kiếm nếu có
        searchInput.sendKeys(searchTerm);  // Nhập từ khóa tìm kiếm

        // Chờ và kiểm tra xem danh mục "CPU" có xuất hiện trong danh sách không
        WebElement categoryRow = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@data-tag='allowRowEvents'][normalize-space()='CPU']")
        ));

        // Kiểm tra nếu tìm thấy danh mục "CPU"
        Assert.assertTrue(categoryRow.isDisplayed(), "Danh mục không xuất hiện sau khi tìm kiếm!");
    }

    @Test
    public void testSearchCategoryWithInvalidName() {
        // Đăng nhập
        login();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        // Nhập từ khóa tìm kiếm không hợp lệ "Tablet"
        String searchTerm = "Tablet";
        WebElement searchInput = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//input[@placeholder='Tìm kiếm theo tên...']")
        ));
        searchInput.clear();  // Xóa nội dung trong ô tìm kiếm nếu có
        searchInput.sendKeys(searchTerm);  // Nhập từ khóa tìm kiếm

        // Kiểm tra rằng không có danh mục "Tablet" hiển thị trong danh sách
        List<WebElement> categoryRows = driver.findElements(
                By.xpath("//div[@data-tag='allowRowEvents'][normalize-space()='Tablet']")
        );

        // Kiểm tra nếu danh mục "Tablet" không xuất hiện
        Assert.assertTrue(categoryRows.isEmpty(), "Danh mục không nên xuất hiện sau khi tìm kiếm với từ khóa không hợp lệ.");
    }

    @Test
    public void testSearchCategoryWithSpecialCharacters() {
        // Đăng nhập
        login();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        // Nhập ký tự đặc biệt "@#!"
        String searchTerm = "@#!";
        WebElement searchInput = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//input[@placeholder='Tìm kiếm theo tên...']")
        ));
        searchInput.clear();  // Xóa nội dung trong ô tìm kiếm nếu có
        searchInput.sendKeys(searchTerm);  // Nhập từ khóa tìm kiếm

        // Kiểm tra rằng không có danh mục nào có tên là "@#!"
        List<WebElement> categoryRows = driver.findElements(
                By.xpath("//div[@data-tag='allowRowEvents'][normalize-space()='@#!']")
        );

        // Kiểm tra nếu không có kết quả tìm kiếm với ký tự đặc biệt
        Assert.assertTrue(categoryRows.isEmpty(), "Danh mục không nên xuất hiện sau khi tìm kiếm với ký tự đặc biệt.");
    }

    @Test
    public void testSearchCategoryWithEmptyStringAfterText() {
        // Đăng nhập
        login();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        // Nhập từ khóa tìm kiếm ban đầu (ví dụ "CPU")
        String initialSearchTerm = "CPU";
        WebElement searchInput = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//input[@placeholder='Tìm kiếm theo tên...']")
        ));
        searchInput.clear();  // Xóa bất kỳ nội dung nào trong ô tìm kiếm
        searchInput.sendKeys(initialSearchTerm);  // Nhập từ khóa tìm kiếm

        // Kiểm tra các danh mục có chứa từ khóa "CPU" (hoặc bất kỳ từ khóa hợp lệ nào)
        List<WebElement> categoryRowsAfterSearch = driver.findElements(
                By.xpath("//div[@data-tag='allowRowEvents'][normalize-space()='" + initialSearchTerm + "']")
        );

        // Kiểm tra nếu có ít nhất một danh mục chứa từ khóa tìm kiếm
        Assert.assertTrue(categoryRowsAfterSearch.size() > 0, "Không tìm thấy danh mục sau khi tìm kiếm với từ khóa ban đầu.");

        // Xóa từ khóa tìm kiếm và nhập chuỗi rỗng
        searchInput.clear();  // Xóa nội dung trong ô tìm kiếm
        searchInput.sendKeys("");  // Nhập chuỗi rỗng

        // Kiểm tra tất cả các danh mục có trong danh sách
        List<WebElement> categoryRowsAfterEmptySearch = driver.findElements(
                By.xpath("//div[@data-tag='allowRowEvents']")
        );

        // Kiểm tra nếu danh sách không rỗng sau khi tìm kiếm với chuỗi rỗng
        Assert.assertTrue(categoryRowsAfterEmptySearch.size() > 0, "Không có danh mục hiển thị sau khi tìm kiếm với chuỗi rỗng.");
    }




    // Phương thức phụ để thực hiện đăng nhập
    private void login() {
        driver.get(baseUrl + "/login");

        // Tìm các trường nhập liệu và nút đăng nhập
        WebElement usernameField = driver.findElement(By.xpath("//input[@placeholder='Nhập tài khoản của bạn']"));
        WebElement passwordField = driver.findElement(By.xpath("//input[@placeholder='Nhập mật khẩu của bạn']"));
        WebElement loginButton = driver.findElement(By.xpath("//button[@type='submit']"));

        // Nhập thông tin đăng nhập
        usernameField.sendKeys("admin"); // Thay bằng tài khoản admin thực tế
        passwordField.sendKeys("123");  // Thay bằng mật khẩu thực tế

        // Nhấn nút đăng nhập
        loginButton.click();

        // Chờ cho modal Swal2 (nếu có) xuất hiện và đóng nó
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        try {
            // Đợi cho đến khi phần tử đóng modal có thể nhấp được và đóng nó
            WebElement closeButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".swal2-close")));
            closeButton.click();
        } catch (Exception e) {
            // Không có modal nào xuất hiện
        }


        // Chờ để kiểm tra chuyển hướng đến trang /admin/user
        WebElement userLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[normalize-space()='Category']")));

        // Nhấp vào liên kết "User" để chuyển đến trang quản lý người dùng
        userLink.click();

        // Chờ để kiểm tra chuyển hướng đến trang quản lý người dùng
        wait.until(ExpectedConditions.urlToBe("http://localhost:3000/admin/category"));

        // Kiểm tra URL sau khi chuyển hướng
        String currentUrl = driver.getCurrentUrl();
        Assert.assertEquals(currentUrl, "http://localhost:3000/admin/category", "Chuyển hướng không đúng trang!");
    }
}
