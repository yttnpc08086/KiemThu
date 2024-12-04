
package com.nhom4;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class UserTest_with_Excel {
    private WebDriver driver;
    private String baseUrl = "http://localhost:3000"; // URL của trang web
    private Workbook workbook;
    private Sheet sheet;
    private List<Object[]> testResults = new ArrayList<>();
    private String excelOutputPath = "D:\\excel\\TestResults_UserTest.xlsx";

    @BeforeSuite
    public void setupWorkbook() throws IOException {
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("Test Results");
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Test Case Name");
        headerRow.createCell(1).setCellValue("Status");
        headerRow.createCell(2).setCellValue("Details");
    }

    @BeforeClass
    public void openBrowser() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.navigate().to(baseUrl);
    }

    @AfterClass
    public void closeBrowser() {
        if (driver != null) {
            driver.quit(); // Đóng toàn bộ trình duyệt
        }
    }

    @AfterMethod
    public void recordTestResult(Method method, ITestResult result) {
        String testCaseName = method.getName();
        String status = result.isSuccess() ? "Passed" : "Failed";
        String details = result.getThrowable() != null ? result.getThrowable().getMessage() : "N/A";
        testResults.add(new Object[]{testCaseName, status, details});
    }

    @AfterSuite
    public void saveResultsToExcel() throws IOException {
        int rowNum = 1;
        for (Object[] testResult : testResults) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue((String) testResult[0]);
            row.createCell(1).setCellValue((String) testResult[1]);
            row.createCell(2).setCellValue((String) testResult[2]);
        }
        try (FileOutputStream fileOut = new FileOutputStream(excelOutputPath)) {
            workbook.write(fileOut);
        }
        workbook.close();
    }

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
        WebElement userLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[normalize-space()='User']")));

        // Nhấp vào liên kết "User" để chuyển đến trang quản lý người dùng
        userLink.click();

        // Chờ để kiểm tra chuyển hướng đến trang quản lý người dùng
        wait.until(ExpectedConditions.urlToBe("http://localhost:3000/admin/user"));

        // Kiểm tra URL sau khi chuyển hướng
        String currentUrl = driver.getCurrentUrl();
        Assert.assertEquals(currentUrl, "http://localhost:3000/admin/user", "Chuyển hướng không đúng trang!");
    }

    @Test
    public void testCreateUserWithExistingEmail() {
        login();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));

        // Mở trang tạo người dùng mới
        WebElement addUserButton = driver.findElement(By.xpath("//button[contains(text(),'Thêm người dùng')]"));
        addUserButton.click();

        // Điền thông tin người dùng với email đã tồn tại
        WebElement fullNameField = driver.findElement(By.xpath("//input[@name='fullName']"));
        WebElement userNameField = driver.findElement(By.xpath("//input[@name='userName']"));
        WebElement emailField = driver.findElement(By.xpath("//input[@name='email']"));
        WebElement phoneField = driver.findElement(By.xpath("//input[@name='phone']"));
        WebElement statusDropdown = driver.findElement(By.xpath("//select[@name='status']"));
        WebElement imageUploadField = driver.findElement(By.xpath("//input[@type='file']"));

        // Nhập thông tin vào các trường
        fullNameField.sendKeys("Lê Sĩ Thành");
        userNameField.sendKeys("lesithanh");
        emailField.sendKeys("lesithanh160201@gmail.com"); // Email đã tồn tại
        phoneField.sendKeys("0123456789");

        // Chọn trạng thái
        statusDropdown.sendKeys("Active");

        // Chọn ảnh người dùng
        File imageFile = new File("D:\\study\\JAVA6\\JAVA6_ASM\\DATN-TOPDEV-2024\\FE\\java6-creact_abc\\src\\assets\\images\\imageProducts\\NVIDIARTX3080.webp");
        imageUploadField.sendKeys(imageFile.getAbsolutePath());

        // Xử lý Swal tải ảnh lên
        WebElement imageUploadSuccessSwal = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button[class='swal2-confirm swal2-styled']")
        ));
        imageUploadSuccessSwal.click();

        // Nhấn nút Lưu người dùng
        WebElement saveButton = driver.findElement(By.cssSelector("button[type='submit']"));
        saveButton.click();

        // Kiểm tra thông báo lỗi dưới input email
        WebElement emailError = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//span[@class='text-red-500 text-sm']")
        ));
        String emailErrorMessage = emailError.getText();
        Assert.assertEquals(emailErrorMessage, "Email đã tồn tại!", "Thông báo lỗi email không đúng!");

        // Đảm bảo thông báo lỗi hiển thị
        Assert.assertTrue(emailError.isDisplayed(), "Thông báo lỗi không hiển thị!");
    }

    @Test
    public void testCreateUserWithEmptyFields() {
        login();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        // Mở trang tạo người dùng mới
        WebElement addUserButton = driver.findElement(By.xpath("//button[contains(text(),'Thêm người dùng')]"));
        addUserButton.click();

        // Nhấn nút Lưu người dùng mà không điền bất kỳ thông tin nào
        WebElement saveButton = driver.findElement(By.cssSelector("button[type='submit']"));
        saveButton.click();

        // Xác minh thông báo lỗi xuất hiện cho từng trường bắt buộc
        WebElement fullNameError = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@name='fullName']/following-sibling::span[@class='text-red-500 text-sm']")
        ));
        WebElement userNameError = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@name='userName']/following-sibling::span[@class='text-red-500 text-sm']")
        ));
        WebElement emailError = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@name='email']/following-sibling::span[@class='text-red-500 text-sm']")
        ));
        WebElement phoneError = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@name='phone']/following-sibling::span[@class='text-red-500 text-sm']")
        ));

        // Xác minh nội dung của các thông báo lỗi
        Assert.assertEquals(fullNameError.getText(), "Bắt buộc nhập họ và tên", "Lỗi họ và tên không đúng!");
        Assert.assertEquals(userNameError.getText(), "Bắt buộc nhập tên người dùng", "Lỗi tên đăng nhập không đúng!");
        Assert.assertEquals(emailError.getText(), "Bắt buộc nhập email", "Lỗi email không đúng!");
        Assert.assertEquals(phoneError.getText(), "Bắt buộc nhập số điện thoại", "Lỗi số điện thoại không đúng!");

        // Đảm bảo tất cả các lỗi đều hiển thị
        Assert.assertTrue(fullNameError.isDisplayed(), "Lỗi họ và tên không hiển thị!");
        Assert.assertTrue(userNameError.isDisplayed(), "Lỗi tên đăng nhập không hiển thị!");
        Assert.assertTrue(emailError.isDisplayed(), "Lỗi email không hiển thị!");
        Assert.assertTrue(phoneError.isDisplayed(), "Lỗi số điện thoại không hiển thị!");
    }

    @Test
    public void testCreateUserWithInvalidPhoneNumber() {
        login();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        // Mở trang tạo người dùng mới
        WebElement addUserButton = driver.findElement(By.xpath("//button[contains(text(),'Thêm người dùng')]"));
        addUserButton.click();

        // Điền thông tin vào các trường, để SĐT không hợp lệ
        WebElement fullNameField = driver.findElement(By.xpath("//input[@name='fullName']"));
        WebElement userNameField = driver.findElement(By.xpath("//input[@name='userName']"));
        WebElement emailField = driver.findElement(By.xpath("//input[@name='email']"));
        WebElement phoneField = driver.findElement(By.xpath("//input[@name='phone']"));

        fullNameField.sendKeys("Nguyễn Văn Test");
        userNameField.sendKeys("testuser");
        emailField.sendKeys("testuser@example.com");
        phoneField.sendKeys("abc123"); // Số điện thoại không hợp lệ

        // Nhấn nút Lưu người dùng
        WebElement saveButton = driver.findElement(By.cssSelector("button[type='submit']"));
        saveButton.click();

        // Xác minh thông báo lỗi xuất hiện cho trường số điện thoại
        WebElement phoneError = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@name='phone']/following-sibling::span[@class='text-red-500 text-sm']")
        ));

        // Xác minh nội dung của thông báo lỗi
        Assert.assertEquals(phoneError.getText(), "Số điện thoại không hợp lệ", "Lỗi số điện thoại không đúng!");

        // Đảm bảo lỗi hiển thị
        Assert.assertTrue(phoneError.isDisplayed(), "Lỗi số điện thoại không hiển thị!");
    }

    @Test
    public void testCreateUserWithInvalidEmail() {
        login();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        // Mở trang tạo người dùng mới
        WebElement addUserButton = driver.findElement(By.xpath("//button[contains(text(),'Thêm người dùng')]"));
        addUserButton.click();

        // Điền thông tin vào các trường, để email không hợp lệ
        WebElement fullNameField = driver.findElement(By.xpath("//input[@name='fullName']"));
        WebElement userNameField = driver.findElement(By.xpath("//input[@name='userName']"));
        WebElement emailField = driver.findElement(By.xpath("//input[@name='email']"));
        WebElement phoneField = driver.findElement(By.xpath("//input[@name='phone']"));

        fullNameField.sendKeys("Nguyễn Văn Test");
        userNameField.sendKeys("testuser");
        emailField.sendKeys("invalidemail"); // Email không hợp lệ
        phoneField.sendKeys("0123456789");  // Số điện thoại hợp lệ

        // Nhấn nút Lưu người dùng
        WebElement saveButton = driver.findElement(By.cssSelector("button[type='submit']"));
        saveButton.click();

        // Xác minh thông báo lỗi xuất hiện cho trường email
        WebElement emailError = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@name='email']/following-sibling::span[@class='text-red-500 text-sm']")
        ));

        // Xác minh nội dung của thông báo lỗi
        Assert.assertEquals(emailError.getText(), "Email không hợp lệ", "Lỗi email không đúng!");

        // Đảm bảo lỗi hiển thị
        Assert.assertTrue(emailError.isDisplayed(), "Lỗi email không hiển thị!");
    }

    @Test
    public void testCreateUser() throws InterruptedException {
        login();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));

        // Mở trang tạo người dùng mới
        WebElement addUserButton = driver.findElement(By.xpath("//button[contains(text(),'Thêm người dùng')]"));
        addUserButton.click();

        // Điền thông tin người dùng
        WebElement fullNameField = driver.findElement(By.xpath("//input[@name='fullName']"));
        WebElement userNameField = driver.findElement(By.xpath("//input[@name='userName']"));
        WebElement emailField = driver.findElement(By.xpath("//input[@name='email']"));
        WebElement phoneField = driver.findElement(By.xpath("//input[@name='phone']"));
        WebElement statusDropdown = driver.findElement(By.xpath("//select[@name='status']"));
        WebElement imageUploadField = driver.findElement(By.xpath("//input[@type='file']"));

        // Nhập thông tin vào các trường
        fullNameField.sendKeys("Nguyễn Văn A");
        userNameField.sendKeys("nguyenvana");
        emailField.sendKeys("nguyenvana@example.com");
        phoneField.sendKeys("0123456789");

        // Chọn trạng thái
        statusDropdown.sendKeys("Active");

        // Chọn ảnh người dùng
        File imageFile = new File("D:\\study\\JAVA6\\JAVA6_ASM\\DATN-TOPDEV-2024\\FE\\java6-creact_abc\\src\\assets\\images\\imageProducts\\NVIDIARTX3080.webp");
        imageUploadField.sendKeys(imageFile.getAbsolutePath());

        // Xử lý Swal tải ảnh lên
        WebElement imageUploadSuccessSwal = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button[class='swal2-confirm swal2-styled']")
        ));
        imageUploadSuccessSwal.click();

        // Nhấn nút Lưu người dùng
        WebElement saveButton = driver.findElement(By.cssSelector("button[type='submit']"));
        saveButton.click();

        // Xử lý Swal lưu thành công
        WebElement saveSuccessSwal = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button[class='swal2-confirm swal2-styled']")
        ));
        saveSuccessSwal.click();

        // Chờ bảng cập nhật dữ liệu
        Thread.sleep(2000); // Tạm thời chờ để bảng load dữ liệu mới

        // Kiểm tra tên người dùng trong bảng
        WebElement newUserRow = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@data-tag='allowRowEvents'][normalize-space()='Nguyễn Văn A']")
        ));
        Assert.assertNotNull(newUserRow, "Người dùng mới không được thêm vào bảng dữ liệu!");
    }

    @Test
    public void testEditUser() {
        // Giả định rằng trong CreateTest bạn đã tạo một user mới với tên: "Nguyễn Văn A"
        String newlyCreatedFullName = "Nguyễn Văn A";

        login();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Mở trang quản lý người dùng
        WebElement userLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[normalize-space()='User']")
        ));
        userLink.click();

        // Tìm hàng chứa thông tin người dùng vừa được tạo
        WebElement userRow = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@data-tag='allowRowEvents'][normalize-space()='" + newlyCreatedFullName + "']")
        ));

        // Tìm nút Edit tương ứng trong hàng
        WebElement editButton = userRow.findElement(By.xpath(".//following-sibling::div//button[contains(@class,'bg-blue-500')]"));
        editButton.click();

        // Điền lại thông tin người dùng
        WebElement fullNameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@name='fullName']")));
        WebElement userNameField = driver.findElement(By.xpath("//input[@name='userName']"));
        WebElement emailField = driver.findElement(By.xpath("//input[@name='email']"));
        WebElement phoneField = driver.findElement(By.xpath("//input[@name='phone']"));
        WebElement statusDropdown = driver.findElement(By.xpath("//select[@name='status']"));
        WebElement imageUploadField = driver.findElement(By.xpath("//input[@type='file']"));

        // Nhập lại thông tin
        fullNameField.clear();
        fullNameField.sendKeys("Nguyễn Văn B");
        userNameField.clear();
        userNameField.sendKeys("nguyenvanb");
        emailField.clear();
        emailField.sendKeys("nguyenvanb@example.com");
        phoneField.clear();
        phoneField.sendKeys("0987654321");

        // Chọn trạng thái
        Select statusSelect = new Select(statusDropdown);
        statusSelect.selectByVisibleText("Hết Hoạt Động");

        // Chọn ảnh mới
        File imageFile = new File("D:\\study\\JAVA6\\JAVA6_ASM\\DATN-TOPDEV-2024\\FE\\java6-creact_abc\\src\\assets\\images\\imageProducts\\NVIDIARTX3080.webp");
        imageUploadField.sendKeys(imageFile.getAbsolutePath());

        // Xác nhận Swal xuất hiện sau khi tải ảnh
        WebElement imageSwalConfirm = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button[class='swal2-confirm swal2-styled']")
        ));
        imageSwalConfirm.click();

        // Lưu thay đổi
        WebElement saveButton = driver.findElement(By.cssSelector("button[type='submit']"));
        saveButton.click();

        // Kiểm tra thông báo thành công (Swal)
        WebElement confirmButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button[class='swal2-confirm swal2-styled']")
        ));
        confirmButton.click();

        // Kiểm tra lại dữ liệu sau khi cập nhật
        WebElement updatedUserRow = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@data-tag='allowRowEvents'][normalize-space()='Nguyễn Văn B']")
        ));
        Assert.assertNotNull(updatedUserRow, "Dữ liệu người dùng không được cập nhật đúng!");
    }

    @Test
    public void testEditUserWithExistingEmail() {
        login();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        // Giả sử rằng trong test CreateUser bạn đã tạo một người dùng mới với email "lesithanh160201@gmail.com"
        String existingEmail = "lesithanh160201@gmail.com";
        String newUserFullName = "Nguyễn Văn B"; // Tên người dùng đã tạo

        // Mở trang quản lý người dùng
        WebElement userLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[normalize-space()='User']")
        ));
        userLink.click();

        // Tìm hàng chứa thông tin người dùng cần chỉnh sửa
        WebElement userRow = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@data-tag='allowRowEvents'][normalize-space()='" + newUserFullName + "']")
        ));

        // Tìm nút Edit tương ứng trong hàng
        WebElement editButton = userRow.findElement(By.xpath(".//following-sibling::div//button[contains(@class,'bg-blue-500')]"));
        editButton.click();

        // Điền lại thông tin người dùng
        WebElement fullNameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@name='fullName']")));
        WebElement userNameField = driver.findElement(By.xpath("//input[@name='userName']"));
        WebElement emailField = driver.findElement(By.xpath("//input[@name='email']"));
        WebElement phoneField = driver.findElement(By.xpath("//input[@name='phone']"));

        // Nhập lại thông tin
        fullNameField.clear();
        fullNameField.sendKeys("Nguyễn Văn C");
        userNameField.clear();
        userNameField.sendKeys("nguyenvanC");
        emailField.clear();
        emailField.sendKeys(existingEmail); // Nhập email đã tồn tại
        phoneField.clear();
        phoneField.sendKeys("0987654399");

        // Nhấn nút Cập nhật người dùng
        WebElement saveButton = driver.findElement(By.cssSelector("button[type='submit']"));
        saveButton.click();

        // Xác minh thông báo lỗi xuất hiện cho trường email
        WebElement emailError = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@name='email']/following-sibling::span[@class='text-red-500 text-sm']")
        ));

        // Xác minh nội dung của thông báo lỗi
        Assert.assertEquals(emailError.getText(), "Email đã tồn tại!", "Lỗi email không đúng!");

        // Đảm bảo lỗi hiển thị
        Assert.assertTrue(emailError.isDisplayed(), "Lỗi email không hiển thị!");
    }

    @Test
    public void testUpdateUserStatus() {
        login();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        // Giả sử rằng người dùng đã tồn tại và có trạng thái "Còn hoạt động"
        String userName = "Nguyễn Văn B"; // Tên người dùng cần chỉnh sửa trạng thái

        // Mở trang quản lý người dùng
        WebElement userLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[normalize-space()='User']")
        ));
        userLink.click();

        // Tìm hàng chứa thông tin người dùng cần cập nhật trạng thái
        WebElement userRow = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@data-tag='allowRowEvents'][normalize-space()='" + userName + "']")
        ));

        // Tìm nút trạng thái trong hàng người dùng cần cập nhật và nhấn vào nút trạng thái
        WebElement statusButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[@data-tag='allowRowEvents'][normalize-space()='" + userName + "']//following-sibling::div//button[contains(@class,'bg-gray-500 text-white px-4 py-2 rounded hover:bg-orange-600')]")
        ));
        statusButton.click(); // Nhấn vào nút trạng thái

        // Chờ và nhấn nút "OK" của thông báo swal
        WebElement confirmButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button[class='swal2-confirm swal2-styled']")
        ));
        confirmButton.click(); // Nhấn nút xác nhận

        // Kiểm tra trạng thái người dùng đã được cập nhật
        WebElement updatedStatusSpan = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@data-tag='allowRowEvents'][normalize-space()='" + userName + "']//following-sibling::div//span[contains(text(),'Còn Hoạt Động')]")
        ));

        // Kiểm tra trạng thái của người dùng
        String updatedStatus = updatedStatusSpan.getText();
        Assert.assertEquals(updatedStatus, "Còn Hoạt Động", "Trạng thái không thay đổi đúng!");
    }

    @Test
    public void testUpdateUserStatusMultipleTimes() {
        login();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        // Giả sử rằng người dùng đã tồn tại và có trạng thái "Còn hoạt động"
        String userName = "Nguyễn Văn B"; // Tên người dùng cần chỉnh sửa trạng thái

        // Mở trang quản lý người dùng
        WebElement userLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[normalize-space()='User']")
        ));
        userLink.click();

        // Tìm hàng chứa thông tin người dùng cần cập nhật trạng thái
        WebElement userRow = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@data-tag='allowRowEvents'][normalize-space()='" + userName + "']")
        ));

        // Tìm nút trạng thái trong hàng người dùng cần cập nhật
        WebElement statusButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[@data-tag='allowRowEvents'][normalize-space()='" + userName + "']//following-sibling::div//button[contains(@class,'bg-gray-500 text-white px-4 py-2 rounded hover:bg-orange-600')]")
        ));

        // Click trạng thái nhiều lần để kiểm tra sự ổn định
        for (int i = 0; i < 10; i++) {
            statusButton.click();

            WebElement confirmButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button[class='swal2-confirm swal2-styled']")
            ));
            confirmButton.click(); // Nhấn nút xác nhận
        }
    }

    @Test
    public void testSearchUserByName() {
        login();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        // Nhập tên người dùng "LE SI THANH" vào ô tìm kiếm
        String searchTerm = "LE SI THANH";
        WebElement searchInput = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//input[@placeholder='Tìm kiếm...']")
        ));
        searchInput.clear();  // Clear any existing text
        searchInput.sendKeys(searchTerm);  // Nhập từ khóa tìm kiếm

        // Chờ và kiểm tra xem người dùng "LE SI THANH" có xuất hiện trong danh sách không
        WebElement userRow = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@data-tag='allowRowEvents'][normalize-space()='LE SI THANH']")
        ));

        // Kiểm tra nếu tìm thấy người dùng "LE SI THANH"
        Assert.assertTrue(userRow.isDisplayed(), "Người dùng không xuất hiện sau khi tìm kiếm");
    }

    @Test
    public void testSearchUserByNonExistentName() {
        login();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        // Nhập tên người dùng không tồn tại vào ô tìm kiếm
        String searchTerm = "NON EXISTENT USER"; // Tên không tồn tại
        WebElement searchInput = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//input[@placeholder='Tìm kiếm...']")
        ));
        searchInput.clear();  // Clear any existing text
        searchInput.sendKeys(searchTerm);  // Nhập từ khóa tìm kiếm

        // Chờ và kiểm tra thông báo không có dữ liệu
        WebElement noRecordsMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(text(),'There are no records to display')]")
        ));

        // Kiểm tra nếu thông báo "There are no records to display" hiển thị
        Assert.assertTrue(noRecordsMessage.isDisplayed(), "Thông báo không có bản ghi không hiển thị");
    }

    @Test
    public void testSearchUserBySpecialCharacter() {
        login();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));


        String searchTerm = "$%^ &*( _+!@";
        WebElement searchInput = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//input[@placeholder='Tìm kiếm...']")
        ));
        searchInput.clear();
        searchInput.sendKeys(searchTerm);

        // Chờ và kiểm tra thông báo không có dữ liệu
        WebElement noRecordsMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(text(),'There are no records to display')]")
        ));

        // Kiểm tra nếu thông báo "There are no records to display" hiển thị
        Assert.assertTrue(noRecordsMessage.isDisplayed(), "Thông báo không có bản ghi không hiển thị");
    }

    @Test
    public void testSortByActiveStatusAndSearchByName() throws InterruptedException {
        login();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        // 1. Sort by status "Còn Hoạt Động"
        WebElement statusDropdown = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//select[@class='border border-gray-300 px-4 py-2 rounded']")
        ));
        Select dropdown = new Select(statusDropdown);
        dropdown.selectByVisibleText("Còn Hoạt Động");  // Chọn "Còn Hoạt Động" từ dropdown

        // Chờ sau khi đã chọn trạng thái để table được cập nhật
        Thread.sleep(1000);  // Chờ 1 giây để bảng cập nhật (hoặc bạn có thể kiểm tra điều kiện cụ thể hơn)

        // 2. Tìm kiếm theo tên "LE SI THANH"
        String searchTerm = "LE SI THANH";
        WebElement searchInput = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//input[@placeholder='Tìm kiếm...']")
        ));
        searchInput.clear();  // Clear any existing text
        searchInput.sendKeys(searchTerm);  // Nhập từ khóa tìm kiếm

        // Chờ và kiểm tra xem người dùng "LE SI THANH" có xuất hiện trong danh sách không
        WebElement userRow = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@data-tag='allowRowEvents'][normalize-space()='LE SI THANH']")
        ));

        // Kiểm tra nếu tìm thấy người dùng "LE SI THANH"
        Assert.assertTrue(userRow.isDisplayed(), "Người dùng không xuất hiện sau khi tìm kiếm");

        // Kiểm tra lại rằng trạng thái của người dùng "LE SI THANH" là "Còn Hoạt Động"
        WebElement userStatus = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@data-tag='allowRowEvents'][normalize-space()='LE SI THANH']//following-sibling::div//span[contains(text(),'Còn Hoạt Động')]")
        ));

        // Kiểm tra trạng thái của người dùng
        String status = userStatus.getText();
        Assert.assertEquals(status, "Còn Hoạt Động", "Trạng thái người dùng không đúng sau khi lọc theo trạng thái");
    }

    @Test
    public void testSortByActiveStatus() throws InterruptedException {
        login();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        // 1. Sort by status "Còn Hoạt Động"
        WebElement statusDropdown = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//select[@class='border border-gray-300 px-4 py-2 rounded']")
        ));
        Select dropdown = new Select(statusDropdown);
        dropdown.selectByVisibleText("Còn Hoạt Động");  // Chọn "Còn Hoạt Động" từ dropdown

        // Chờ sau khi đã chọn trạng thái để table được cập nhật
        Thread.sleep(1000);  // Chờ 1 giây để bảng cập nhật (hoặc bạn có thể kiểm tra điều kiện cụ thể hơn)

        // Kiểm tra xem các người dùng có phải có trạng thái "Còn Hoạt Động" sau khi lọc
        List<WebElement> userRows = driver.findElements(By.xpath("//div[@data-tag='allowRowEvents']//following-sibling::div//span[contains(text(),'Còn Hoạt Động')]"));

        // Kiểm tra nếu có ít nhất 1 người dùng có trạng thái "Còn Hoạt Động"
        Assert.assertTrue(userRows.size() > 0, "Không có người dùng với trạng thái 'Còn Hoạt Động'");
    }

    @Test
    public void testSortByInactiveStatus() throws InterruptedException {
        login();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        // 1. Sort by status "Hết Hoạt Động"
        WebElement statusDropdown = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//select[@class='border border-gray-300 px-4 py-2 rounded']")
        ));
        Select dropdown = new Select(statusDropdown);
        dropdown.selectByVisibleText("Hết Hoạt Động");  // Chọn "Hết Hoạt Động" từ dropdown

        // Chờ sau khi đã chọn trạng thái để table được cập nhật
        Thread.sleep(1000);  // Chờ 1 giây để bảng cập nhật (hoặc bạn có thể kiểm tra điều kiện cụ thể hơn)

        // Kiểm tra xem các người dùng có phải có trạng thái "Hết Hoạt Động" sau khi lọc
        List<WebElement> userRows = driver.findElements(By.xpath("//div[@data-tag='allowRowEvents']//following-sibling::div//span[contains(text(),'Hết Hoạt Động')]"));

        // Kiểm tra nếu có ít nhất 1 người dùng có trạng thái "Hết Hoạt Động"
        Assert.assertTrue(userRows.size() > 0, "Không có người dùng với trạng thái 'Hết Hoạt Động'");
    }
}
