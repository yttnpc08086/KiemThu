package com.nhom4;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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

public class ContactTest {

    private WebDriver driver;
    private String baseUrl = "http://localhost:3000/contact";

    // Setup WebDriver
    @BeforeClass
    public void openBrowser() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    // Điều hướng tới trang Contact
    private void navigateToContactPage() {
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
    public void testSubmitContactFormSuccessfully() {
        navigateToContactPage(); // Đảm bảo đúng URL
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Nhập dữ liệu hợp lệ vào form
        WebElement fullNameInput = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@placeholder='Họ tên *']")));
        fullNameInput.sendKeys("Nguyễn Văn A");

        WebElement emailInput = driver.findElement(By.xpath("//input[@placeholder='Email *']"));
        emailInput.sendKeys("example@gmail.com");

        WebElement phoneInput = driver.findElement(By.xpath("//input[@placeholder='Số điện thoại *']"));
        phoneInput.sendKeys("0123456789");

        WebElement subjectInput = driver.findElement(By.xpath("//input[@placeholder='Chủ đề']"));
        subjectInput.sendKeys("Hỗ trợ kỹ thuật");

        WebElement messageTextarea = driver.findElement(By.xpath("//textarea[@placeholder='Nhập nội dung *']"));
        messageTextarea.sendKeys("Tôi cần hỗ trợ với sản phẩm của mình.");

        // Nhấn nút gửi
        WebElement submitButton = driver.findElement(By.xpath("//button[contains(text(),'GỬI LIÊN HỆ')]"));
        submitButton.click();

        // Xác minh thông báo thành công
        WebElement successMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(text(),'Gửi liên hệ thành công!')]")
        ));
        Assert.assertTrue(successMessage.isDisplayed(), "Thông báo thành công không hiển thị!");
    }

    @Test
    public void testValidationErrors() {
        navigateToContactPage(); // Đảm bảo đúng URL
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Nhấn nút gửi mà không nhập dữ liệu
        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'GỬI LIÊN HỆ')]")));
        submitButton.click();

        // Xác minh lỗi ở trường Họ tên
        WebElement fullNameError = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[contains(text(),'Họ tên là bắt buộc.')]")
        ));
        Assert.assertTrue(fullNameError.isDisplayed(), "Thông báo lỗi họ tên không hiển thị!");

        // Xác minh lỗi ở trường Email
        WebElement emailError = driver.findElement(By.xpath("//p[contains(text(),'Email là bắt buộc.')]"));
        Assert.assertTrue(emailError.isDisplayed(), "Thông báo lỗi email không hiển thị!");

        // Xác minh lỗi ở trường Số điện thoại
        WebElement phoneError = driver.findElement(By.xpath("//p[contains(text(),'Số điện thoại là bắt buộc.')]"));
        Assert.assertTrue(phoneError.isDisplayed(), "Thông báo lỗi số điện thoại không hiển thị!");

        // Xác minh lỗi ở trường Nội dung
        WebElement messageError = driver.findElement(By.xpath("//p[contains(text(),'Nội dung là bắt buộc.')]"));
        Assert.assertTrue(messageError.isDisplayed(), "Thông báo lỗi nội dung không hiển thị!");
    }

    @Test
    public void testInvalidEmailFormat() {
        navigateToContactPage(); // Điều hướng đến trang liên hệ
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        ((JavascriptExecutor) driver).executeScript(
                "document.querySelector('form').setAttribute('novalidate', 'novalidate');"
        );

        // Nhập họ tên
        WebElement fullNameInput = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@placeholder='Họ tên *']")));
        fullNameInput.sendKeys("Nguyễn Văn A");

        // Nhập số điện thoại
        WebElement phoneInput = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@placeholder='Số điện thoại *']")));
        phoneInput.sendKeys("0987654321");

        // Nhập chủ đề (không bắt buộc)
        WebElement subjectInput = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@placeholder='Chủ đề']")));
        subjectInput.sendKeys("Vấn đề kỹ thuật");

        // Nhập nội dung
        WebElement messageInput = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//textarea[@placeholder='Nhập nội dung *']")));
        messageInput.sendKeys("Tôi gặp vấn đề khi sử dụng dịch vụ của bạn.");

        // Nhập email không hợp lệ
        WebElement emailInput = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@placeholder='Email *']")));
        emailInput.sendKeys("sadasdasd");

        // Nhấn nút gửi
        WebElement submitButton = driver.findElement(By.xpath("//button[contains(text(),'GỬI LIÊN HỆ')]"));
        submitButton.click();

        // Xác minh lỗi định dạng email
        WebElement emailError = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[contains(text(),'Email không hợp lệ.')]")
        ));
        Assert.assertTrue(emailError.isDisplayed(), "Thông báo lỗi email không hợp lệ không hiển thị!");
    }

    @Test
    public void testMessageTooShort() {
        navigateToContactPage(); // Điều hướng đến trang liên hệ
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Nhập họ tên
        WebElement fullNameInput = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@placeholder='Họ tên *']")));
        fullNameInput.sendKeys("Nguyễn Văn A");

        // Nhập email
        WebElement emailInput = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@placeholder='Email *']")));
        emailInput.sendKeys("example@example.com");

        // Nhập số điện thoại
        WebElement phoneInput = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@placeholder='Số điện thoại *']")));
        phoneInput.sendKeys("0987654321");

        // Nhập chủ đề (không bắt buộc)
        WebElement subjectInput = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@placeholder='Chủ đề']")));
        subjectInput.sendKeys("Phản hồi");

        // Nhập nội dung ngắn hơn 10 ký tự
        WebElement messageInput = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//textarea[@placeholder='Nhập nội dung *']")));
        messageInput.sendKeys("Ngắn");

        // Nhấn nút gửi
        WebElement submitButton = driver.findElement(By.xpath("//button[contains(text(),'GỬI LIÊN HỆ')]"));
        submitButton.click();

        // Xác minh lỗi về độ dài tối thiểu
        WebElement messageError = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[contains(text(),'Nội dung phải có ít nhất 10 ký tự.')]")
        ));
        Assert.assertTrue(messageError.isDisplayed(), "Thông báo lỗi về độ dài tối thiểu của nội dung không hiển thị!");
    }

    @Test
    public void testFullNameTooLong() {
        navigateToContactPage(); // Điều hướng đến trang liên hệ

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Nhập họ tên dài hơn 50 ký tự
        WebElement fullNameInput = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@placeholder='Họ tên *']")));
        String longName = "Nguyễn Văn A B C D E F G H I J K L M N O P Q R S T U V X Y Z";
        fullNameInput.sendKeys(longName);

        // Nhập dữ liệu hợp lệ vào các trường còn lại
        WebElement emailInput = driver.findElement(By.xpath("//input[@placeholder='Email *']"));
        emailInput.sendKeys("example@example.com");

        WebElement phoneInput = driver.findElement(By.xpath("//input[@placeholder='Số điện thoại *']"));
        phoneInput.sendKeys("0123456789");

        WebElement messageInput = driver.findElement(By.xpath("//textarea[@placeholder='Nhập nội dung *']"));
        messageInput.sendKeys("Đây là nội dung phản hồi hợp lệ.");

        // Nhấn nút "GỬI LIÊN HỆ"
        WebElement submitButton = driver.findElement(By.xpath("//button[contains(text(),'GỬI LIÊN HỆ')]"));
        submitButton.click();

        // Kiểm tra thông báo lỗi cho trường họ tên
        WebElement fullNameError = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[contains(text(),'Họ tên không được vượt quá 50 ký tự.')]")
        ));

        // Xác minh thông báo lỗi hiển thị
        Assert.assertTrue(fullNameError.isDisplayed(), "Thông báo lỗi họ tên dài quá mức không hiển thị!");

        // In thông báo để xác nhận
        System.out.println("Thông báo lỗi: " + fullNameError.getText());
    }

    @Test
    public void testFormFieldsDisplayOnFirstLoad() {
        navigateToContactPage(); // Điều hướng đến trang liên hệ

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Kiểm tra trường "Họ tên" hiển thị với placeholder đúng
        WebElement fullNameInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@placeholder='Họ tên *']")));
        Assert.assertTrue(fullNameInput.isDisplayed(), "Trường 'Họ tên' không hiển thị!");
        Assert.assertEquals(fullNameInput.getAttribute("placeholder"), "Họ tên *", "Placeholder của trường 'Họ tên' không đúng!");

        // Kiểm tra trường "Email" hiển thị với placeholder đúng
        WebElement emailInput = driver.findElement(By.xpath("//input[@placeholder='Email *']"));
        Assert.assertTrue(emailInput.isDisplayed(), "Trường 'Email' không hiển thị!");
        Assert.assertEquals(emailInput.getAttribute("placeholder"), "Email *", "Placeholder của trường 'Email' không đúng!");

        // Kiểm tra trường "Số điện thoại" hiển thị với placeholder đúng
        WebElement phoneInput = driver.findElement(By.xpath("//input[@placeholder='Số điện thoại *']"));
        Assert.assertTrue(phoneInput.isDisplayed(), "Trường 'Số điện thoại' không hiển thị!");
        Assert.assertEquals(phoneInput.getAttribute("placeholder"), "Số điện thoại *", "Placeholder của trường 'Số điện thoại' không đúng!");

        // Kiểm tra trường "Chủ đề" hiển thị với placeholder đúng
        WebElement subjectInput = driver.findElement(By.xpath("//input[@placeholder='Chủ đề']"));
        Assert.assertTrue(subjectInput.isDisplayed(), "Trường 'Chủ đề' không hiển thị!");
        Assert.assertEquals(subjectInput.getAttribute("placeholder"), "Chủ đề", "Placeholder của trường 'Chủ đề' không đúng!");

        // Kiểm tra trường "Nội dung" hiển thị với placeholder đúng
        WebElement messageInput = driver.findElement(By.xpath("//textarea[@placeholder='Nhập nội dung *']"));
        Assert.assertTrue(messageInput.isDisplayed(), "Trường 'Nội dung' không hiển thị!");
        Assert.assertEquals(messageInput.getAttribute("placeholder"), "Nhập nội dung *", "Placeholder của trường 'Nội dung' không đúng!");

        // Kiểm tra nút "GỬI LIÊN HỆ" hiển thị
        WebElement submitButton = driver.findElement(By.xpath("//button[contains(text(),'GỬI LIÊN HỆ')]"));
        Assert.assertTrue(submitButton.isDisplayed(), "Nút 'GỬI LIÊN HỆ' không hiển thị!");
    }

    @Test
    public void testFormResetAfterSubmit() {
        navigateToContactPage(); // Điều hướng đến trang liên hệ

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Nhập thông tin hợp lệ vào form
        WebElement fullNameInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@placeholder='Họ tên *']")));
        fullNameInput.sendKeys("Nguyễn Văn A");

        WebElement emailInput = driver.findElement(By.xpath("//input[@placeholder='Email *']"));
        emailInput.sendKeys("example@gmail.com");

        WebElement phoneInput = driver.findElement(By.xpath("//input[@placeholder='Số điện thoại *']"));
        phoneInput.sendKeys("0123456789");

        WebElement subjectInput = driver.findElement(By.xpath("//input[@placeholder='Chủ đề']"));
        subjectInput.sendKeys("Thắc mắc về sản phẩm");

        WebElement messageInput = driver.findElement(By.xpath("//textarea[@placeholder='Nhập nội dung *']"));
        messageInput.sendKeys("Nội dung liên hệ thử nghiệm.");

        // Nhấn nút "GỬI LIÊN HỆ"
        WebElement submitButton = driver.findElement(By.xpath("//button[contains(text(),'GỬI LIÊN HỆ')]"));
        submitButton.click();

        // Đợi thông báo thành công xuất hiện
        WebElement successAlert = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(text(),'Gửi liên hệ thành công!')]")
        ));
        Assert.assertTrue(successAlert.isDisplayed(), "Thông báo thành công không xuất hiện!");

        // Kiểm tra các trường sau khi form được reset
        Assert.assertEquals(fullNameInput.getAttribute("value"), "", "Trường 'Họ tên' không được reset!");
        Assert.assertEquals(emailInput.getAttribute("value"), "", "Trường 'Email' không được reset!");
        Assert.assertEquals(phoneInput.getAttribute("value"), "", "Trường 'Số điện thoại' không được reset!");
        Assert.assertEquals(subjectInput.getAttribute("value"), "", "Trường 'Chủ đề' không được reset!");
        Assert.assertEquals(messageInput.getAttribute("value"), "", "Trường 'Nội dung' không được reset!");
    }



}
