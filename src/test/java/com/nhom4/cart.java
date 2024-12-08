package com.nhom4;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class cart {
    public WebDriver driver;
    private XSSFWorkbook workbook;
    private XSSFSheet worksheet;
    private Map<String, Object[]> testNGResults;
    private Map<String, String[]> dataLogin;
    private Map<String, String[]> cartdata;
    private final String EXCEL_DIR = "D:\\FPOLY\\KTNC\\KiemThu\\src\\main\\resources\\dataTest";
    private final String IMAGE_DIR = "D:\\FPOLY\\KTNC\\KiemThu\\src\\main\\resources\\image";
    String baseURL = "http://localhost:3000"; // URL của ứng dụng web của bạn

    public void takeScreenshot(WebDriver driver, String outputScreen) throws Exception {
        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(screenshot, new File(outputScreen));
    }

    public void writeImage(String imgSrc, Row row, Cell cell, XSSFSheet sheet) throws Exception {
        InputStream is = new FileInputStream(imgSrc);
        byte[] bytes = IOUtils.toByteArray(is);
        int idImg = sheet.getWorkbook().addPicture(bytes, XSSFWorkbook.PICTURE_TYPE_PNG);
        is.close();

        XSSFDrawing drawing = sheet.createDrawingPatriarch();
        ClientAnchor anchor = new XSSFClientAnchor();

        anchor.setCol1(cell.getColumnIndex() + 1);
        anchor.setRow1(row.getRowNum());
        anchor.setCol2(cell.getColumnIndex() + 2);
        anchor.setRow2(row.getRowNum() + 1);

        drawing.createPicture(anchor, idImg);
    }


    @Test()
    public void loginTest() {
        driver.get(baseURL + "/login");  // Truy cập trang đăng nhập

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
    @AfterMethod
    public void suiteTestdown() {
        try {
            Set<String> keyset = testNGResults.keySet();
            int rownum = 0;

            for (String key : keyset) {
                Row row = worksheet.createRow(rownum++);
                Object[] objArr = testNGResults.get(key);
                int cellnum = 0;
                for (Object obj : objArr) {
                    Cell cell = row.createCell(cellnum++);
                    if (obj instanceof String) {
                        cell.setCellValue((String) obj);
                    } else if (obj instanceof Date) {
                        cell.setCellValue((Date) obj);
                    } else if (obj instanceof Boolean) {
                        cell.setCellValue((Boolean) obj);
                    } else if (obj instanceof Double) {
                        cell.setCellValue((Double) obj);
                    }

                    if (obj.toString().contains("Failed") || obj.toString().contains(".png")) {
                        try {
                            row.setHeightInPoints(80);
                            writeImage(obj.toString(), row, cell, worksheet);
                            CreationHelper createHelper = worksheet.getWorkbook().getCreationHelper();
                            XSSFHyperlink hyperlink = (XSSFHyperlink) createHelper.createHyperlink(HyperlinkType.URL);
                            hyperlink.setAddress(obj.toString().replace("\\", "/"));
                            cell.setHyperlink(hyperlink);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }

        try (FileOutputStream fileOut = new FileOutputStream(EXCEL_DIR + "\\TestNG_Result_" + System.currentTimeMillis() + ".xlsx")) {
            workbook.write(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //
//
    @BeforeClass
    public void setupSuite() {
        try {
            testNGResults = new LinkedHashMap<>();
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver();
            driver.manage().window().maximize();
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

            // Xử lý dữ liệu cho cartdata
            workbook = new XSSFWorkbook(new FileInputStream(EXCEL_DIR + "\\LOGIN_TEST.xlsx"));
            cartdata = new HashMap<>();
            worksheet = workbook.getSheet("cartdata");
            readDataFromExcel();


            // Tạo sheet mới cho kết quả
            workbook = new XSSFWorkbook();
            worksheet = workbook.createSheet("ResultCart");

            CellStyle rowStyle = workbook.createCellStyle();
            rowStyle.setAlignment(HorizontalAlignment.CENTER);
            rowStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            rowStyle.setWrapText(true);

            testNGResults.put("2", new Object[]{
                    "Test Step No.", "Action", "Product ID", "Card Number", "Card Holder", "Card Date", "OTP", "Expected Output"
                    , "Actual Output", "Status", "Link", "Image"
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void readDataFromExcel() {
        try {
            dataLogin = new HashMap<>();
            worksheet = workbook.getSheet("cartdata");

            if (worksheet == null) {
                System.out.println("Không tìm thấy sheet");
            } else {
                Iterator<Row> rowIterator = worksheet.iterator();
                DataFormatter dataformat = new DataFormatter();

                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();
                    if (row.getRowNum() >= 1) { //bỏ qua tiêu đề
                        Iterator<Cell> cellIterator = row.cellIterator();
                        String key = "";
                        String productName = "";
                        String quantity = "";
                        String cardNumber = "";
                        String cardHolder = "";
                        String cardDate = "";
                        String expected = "";

                        while (cellIterator.hasNext()) {
                            Cell cell = cellIterator.next();
                            if (cell.getColumnIndex() == 0) {
                                key = dataformat.formatCellValue(cell);
                            } else if (cell.getColumnIndex() == 1) {
                                productName = dataformat.formatCellValue(cell);
                            } else if (cell.getColumnIndex() == 2) {
                                quantity = dataformat.formatCellValue(cell);
                            } else if (cell.getColumnIndex() == 3) {
                                cardNumber = dataformat.formatCellValue(cell);
                            }else if (cell.getColumnIndex() == 4) {
                                cardHolder = dataformat.formatCellValue(cell);
                            }else if (cell.getColumnIndex() == 3) {
                                cardDate = dataformat.formatCellValue(cell);
                            }else if (cell.getColumnIndex() == 5) {
                                expected = dataformat.formatCellValue(cell);}
                        }
                        String[] myArr = {productName, quantity, cardNumber,cardHolder, cardDate, expected};
                        dataLogin.put(key, myArr);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test(priority = 2, dependsOnMethods = "loginTest")
    public void CartTest() throws Exception {
        try {
            Set<String> keySet = cartdata.keySet();
            int index = 1;
            for (String key : keySet) {
                String[] value = cartdata.get(key);

                String productName = value[0];
                String quantity = value[1];
                String cardNumber = value[2];
                String cardHolder = value[3];
                String cardDate = value[4];
                String OTP = value[5];
                String expected = value[6];

                String resultStatus = "Failed";
                String screenshotPath = "";

                try {

                    addProductToCart(productName, quantity);
                    proceedToCheckout();
                    selectPaymentMethod(cardNumber, cardHolder, cardDate);
                    enterOTP(OTP);
                    String actualText = getPaymentConfirmationText();

                    if (actualText.equalsIgnoreCase(expected)) {
                        resultStatus = "Success";
                    } else {
                        screenshotPath = captureFailureScreenshot();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    screenshotPath = captureFailureScreenshot();
                }

                // Lưu kết quả
                testNGResults.put(
                        String.valueOf(index),
                        new Object[]{String.valueOf(index), "Cart Test Result", cardNumber, cardHolder, cardDate, OTP, expected, resultStatus, screenshotPath}
                );

                index++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void addProductToCart(String productName, String quantity) throws Exception {
        try {
            // Navigate to product page
            driver.get(baseURL+"/products");

            WebElement productname = driver.findElement(By.xpath("//div[contains(@class, 'text-sm font-bold mb-2') and text()='" + productName + "']"));
            if (productname != null) {
                Actions actions = new Actions(driver);
                actions.moveToElement(productname).perform();

                WebElement CartButton = driver.findElement(By.xpath("//svg[contains(@class, 'hover:text-orange-900')]"));
                CartButton.click();

                Thread.sleep(1000);
                WebElement dialogMessage = driver.findElement(By.xpath("//div[@role='dialog']"));
                String message = dialogMessage.getText();
                if (message.contains("Thành công")) {
                    WebElement viewCartButton = driver.findElement(By.xpath("//button[contains(text(),'Xem giỏ hàng')]"));
                    viewCartButton.click();
                } else {
                    System.out.println("không hiện thông báo thành công");
                }

                driver.get(baseURL+"/cart");
                WebElement checkProduct = driver.findElement(By.xpath("//input[@type='checkbox']"));
                if (productname != null) {
                    WebElement quantityField = driver.findElement(By.xpath("//input[@type='number']"));
                    quantityField.clear();
                    quantityField.sendKeys(quantity);


                    checkProduct.click();
                    Assert.assertTrue(checkProduct.isSelected(), "Checkbox sản phẩm không được chọn!");
                } else {
                    WebElement quantityField = driver.findElement(By.xpath("//input[@type='number']"));
                    quantityField.clear();
                    quantityField.sendKeys("1");
                    checkProduct.click();
                    Assert.assertTrue(checkProduct.isSelected(), "Checkbox sản phẩm không được chọn!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e; // Rethrow the exception to handle it in the test
        }

        driver.findElement(By.xpath("//button[contains(text(),'TIẾN HÀNH THANH TOÁN')]")).click();
    }

    private void proceedToCheckout() {
        driver.get(baseURL+"/order");
        driver.findElement(By.xpath("//img[@alt='VNP Logo']")).click();
        driver.findElement(By.xpath("//button[contains(text(),'ĐẶT HÀNG')]")).click();

    }

    private void selectPaymentMethod(String cardNumber, String cardHolder, String cardDate) throws Exception {
        driver.get("https://sandbox.vnpayment.vn/paymentv2/Transaction/PaymentMethod.html?token=dafbcb9e37fa444dbf335ff526979a47");
        driver.findElement(By.xpath("(//div[@class='list-method-item accordion-item'])[1]")).click();
        driver.findElement(By.xpath("(//div[@search-value='ngan hang ncb'])[1]")).click();
        try {
            driver.get("https://sandbox.vnpayment.vn/paymentv2/Ncb/Transaction/Index.html?token=77a39515714e4e58a72104d0bee777bb");
            driver.findElement(By.xpath("//input[@id='card_number_mask']")).sendKeys(cardNumber);
            driver.findElement(By.xpath("(//input[@id='cardHolder'])[1]")).sendKeys(cardHolder);
            driver.findElement(By.xpath("(//input[@id='cardDate'])[1]")).sendKeys(cardDate);
            driver.findElement(By.xpath("(//a[@id='btnContinue'])[1]")).click();
        } catch (Exception e) {
            throw new Exception("Failed to input payment details", e);
        }
    }

    private void enterOTP(String OTP) throws Exception {
        try {
            driver.findElement(By.xpath("(//a[@id='btnAgree'])[1]")).click();
        } catch (Exception ex) {
            driver.findElement(By.xpath("(//a[@class='ubg-secondary ubox-size-button-default ubg-hover ubg-active ubtn'])[5]")).click();
        }
        driver.findElement(By.xpath("(//input[@id='otpvalue'])[1]")).sendKeys(OTP);
        try {
            driver.findElement(By.xpath("(//button[@id='btnConfirm'])[1]")).click();
        } catch (Exception e) {
            driver.findElement(By.xpath("(//a[@data-bs-toggle='modal'])[1]")).click();
        }
    }

    private String getPaymentConfirmationText() {
        WebElement confirmationElement = driver.findElement(By.xpath("(//h1[normalize-space()='Thanh toán thành công!'])[1]"));
        String confirmationElementText = confirmationElement.getText();
        if (confirmationElementText.contains("Thanh toán thành công")) {
            System.out.println("Test case thành công");
        }
        return getPaymentConfirmationText();
    }

    private String captureFailureScreenshot() {
        String path = IMAGE_DIR + "failure_" + System.currentTimeMillis() + ".png";
        try {
            takeScreenshot(driver, path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path.replace("\\", "/");
    }

}