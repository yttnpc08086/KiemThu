package com.nhom4;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class CartTest {

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

    @AfterClass
    public void tearDown() {
        // Đóng trình duyệt sau khi kiểm tra xong
        if (driver != null) {
            driver.quit();
        }
    }

    @Test()
    public void loginTest() {
        driver.get(baseUrl + "/login");  // Truy cập trang đăng nhập

        // Nhập thông tin đăng nhập (Cập nhật theo form đăng nhập của bạn)
        WebElement usernameField = driver.findElement(By.xpath("//input[@placeholder='Nhập tài khoản của bạn']"));
        WebElement passwordField = driver.findElement(By.xpath("//input[@placeholder='Nhập mật khẩu của bạn']"));
        WebElement loginButton = driver.findElement(By.xpath("//button[@type='submit']"));

        usernameField.sendKeys("user");  // Thay đổi theo thông tin đăng nhập của bạn
        passwordField.sendKeys("123");  // Thay đổi theo mật khẩu của bạn

        // Nhấn nút đăng nhập
        loginButton.click();

        // Sử dụng WebDriverWait mới
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            // Chờ để trang chuyển hướng hoặc phần tử mới xuất hiện
            WebElement header = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[@class='text-2xl font-bold']")));
            String headerText = header.getText();
            Assert.assertEquals(headerText, "TECH SMART", "Đăng nhập không thành công!");

        } catch (TimeoutException e) {
            Assert.fail("Không tìm thấy từ khóa TECH SMART!");
        }
    }

    @Test(dependsOnMethods = "loginTest")
    public void addProductToCartTest() {
        driver.get(baseUrl + "/products"); // Truy cập trang sản phẩm

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

            // Thêm sản phẩm vào giỏ hàng
            addProductToCart(wait);

            // Kiểm tra giỏ hàng và chọn sản phẩm
            viewAndSelectProductInCart(wait);

            // Thanh toán sản phẩm
            proceedToPayment(wait);

            // Nhập thông tin thanh toán
            fillPaymentDetails(wait);

            // Kiểm tra thanh toán thành công
            verifyPaymentSuccess(wait);

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Lỗi trong quá trình thanh toán: " + e.getMessage());
        }
    }

    private void addProductToCart(WebDriverWait wait) throws Exception {

        WebElement cardProduct = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[@class='aos-init aos-animate'])[5]"))
        );
        Actions actions = new Actions(driver);
        actions.moveToElement(cardProduct).perform();

        WebElement addToCartButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("(//*[name()='path'][@fill='currentColor'])[13]"))
        );
        addToCartButton.click();

        WebElement messageSuccess = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='swal2-html-container']"))
        );
        Assert.assertEquals(messageSuccess.getText(), "Thêm vào giỏ hàng thành công!", "Thêm sản phẩm vào giỏ hàng không thành công!");
    }

    private void viewAndSelectProductInCart(WebDriverWait wait) throws Exception {
        WebElement viewCart = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'Xem giỏ hàng')]"))
        );
        viewCart.click();

        WebElement checkProduct = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@type='checkbox']"))
        );
        checkProduct.click();

        Assert.assertTrue(checkProduct.isSelected(), "Checkbox sản phẩm không được chọn!");
    }

    private void proceedToPayment(WebDriverWait wait) throws Exception {
        WebElement paymentButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'TIẾN HÀNH THANH TOÁN')]"))
        );
        paymentButton.click();
    }

    private void fillPaymentDetails(WebDriverWait wait) throws Exception {
        WebElement logoVNPay = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//img[@alt='VNP Logo']"))
        );
        logoVNPay.click();

        WebElement orderBtn = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'ĐẶT HÀNG')]"))
        );
        orderBtn.click();
        WebElement bankButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("(//div[@class='list-method-item accordion-item'])[1]"))
        );
        bankButton.click();

        WebElement bankName = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[@search-value='ngan hang ncb'])[1]")));
        bankName.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='card_number_mask']"))).sendKeys("9704198526191432198");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//input[@id='cardHolder'])[1]"))).sendKeys("NGUYEN VAN A");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//input[@id='cardDate'])[1]"))).sendKeys("07/15");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//a[@id='btnContinue'])[1]"))).click();

        handleConfirmationDialog(wait);
        enterOTP(wait);
    }

    private void handleConfirmationDialog(WebDriverWait wait) {
        try {
            WebElement okButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//a[@id='btnAgree'])[1]")));
            okButton.click();
        } catch (Exception ex) {
            WebElement noButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//a[@class='ubg-secondary ubox-size-button-default ubg-hover ubg-active ubtn'])[5]")));
            noButton.click();
        }
    }

    private void enterOTP(WebDriverWait wait) throws Exception {
        WebElement otp = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//input[@id='otpvalue'])[1]")));
        otp.sendKeys("123456");
        try {
            WebElement okButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//button[@id='btnConfirm'])[1]")));
            okButton.click();
        } catch (Exception e) {
            WebElement cancelButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//a[@data-bs-toggle='modal'])[1]")));
            cancelButton.click();
        }

    }

    private void verifyPaymentSuccess(WebDriverWait wait) throws Exception {
        WebElement successfulPayment = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("(//h1[normalize-space()='Thanh toán thành công!'])[1]"))
        );
        Assert.assertEquals(successfulPayment.getText(), "Thanh toán thành công!", "Thanh toán không thành công!");
    }

}