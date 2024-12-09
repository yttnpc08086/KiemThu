
package com.nhom4;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class UserTest_with_Excel {
    public WebDriver driver;
    private XSSFWorkbook workbook;
    private XSSFSheet worksheet;
    private Map<String, Object[]> testNGResults;
    private Map<String, String[]> datauser;
    private final String EXCEL_DIR = "D:\\FPOLY\\KTNC\\KiemThu\\src\\main\\resources\\dataTest";
    private final String IMAGE_DIR = "D:\\FPOLY\\KTNC\\KiemThu\\src\\main\\resources\\image";
    String baseUrl = "http://localhost:3000";

    public void readDataFromExcel() {
        try {
            datauser = new HashMap<>();
            worksheet = workbook.getSheet("userdata");

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
                        String fullname = "";
                        String username = "";
                        String email = "";
                        String phone = "";
                        String image = "";
                        String expected = "";

                        while (cellIterator.hasNext()) {
                            Cell cell = cellIterator.next();
                            if (cell.getColumnIndex() == 0) {
                                key = dataformat.formatCellValue(cell);
                            } else if (cell.getColumnIndex() == 1) {
                                fullname = dataformat.formatCellValue(cell);
                            } else if (cell.getColumnIndex() == 2) {
                                username = dataformat.formatCellValue(cell);
                            } else if (cell.getColumnIndex() == 3) {
                                email = dataformat.formatCellValue(cell);
                            } else if (cell.getColumnIndex() == 4) {
                                phone = dataformat.formatCellValue(cell);
                            } else if (cell.getColumnIndex() == 5) {
                                image = dataformat.formatCellValue(cell);
                            } else if (cell.getColumnIndex() == 6) {
                                expected = dataformat.formatCellValue(cell);
                            }
                        }
                        String[] myArr = {fullname, username, email, phone, image, expected};
                        datauser.put(key, myArr);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    @BeforeClass
    public void suiteTest() {
        try {
            testNGResults = new LinkedHashMap<>();
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver();
            driver.manage().window().maximize();
            driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
            workbook = new XSSFWorkbook(new FileInputStream(EXCEL_DIR + "\\LOGIN_TEST.xlsx"));
            worksheet = workbook.getSheet("userdata");
            readDataFromExcel();

            workbook = new XSSFWorkbook();
            worksheet = workbook.createSheet("Result");

            CellStyle rowStyle = workbook.createCellStyle();
            rowStyle.setAlignment(HorizontalAlignment.CENTER);
            rowStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            rowStyle.setWrapText(true);

            testNGResults.put("1", new Object[]{"Test Step No.", "Action", "fullname", "username", "email", "phone", "image", "Expected Output", "Actual Output", "Status", "Link", "Image"});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test(priority = 1)
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

    @Test(dependsOnMethods = "loginTest", priority = 2)
    public void testCreate() throws Exception {
        driver.get(baseUrl+"/admin/user");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement addUserButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'+ Thêm người dùng')]")));
        addUserButton.isDisplayed();
if( addUserButton.isDisplayed()){
    System.out.println("đã hiện");
}

//        try {
//            Set<String> keySet = datauser.keySet();
//            int index = 1;
//            for (String key : keySet) {
//                String[] value = datauser.get(key);
//                String fullname = value[0];
//                String username = value[1];
//                String email = value[2];
//                String phone = value[3];
//                String expected = value[4];
//
//                driver.findElement(By.xpath("//input[@name='fullName']")).clear();
//                driver.findElement(By.xpath("//input[@name='fullName']")).sendKeys(fullname);
//                driver.findElement(By.xpath("//input[@name='userName']")).clear();
//                driver.findElement(By.xpath("//input[@name='userName']")).sendKeys(username);
//                driver.findElement(By.xpath("//input[@name='email']")).clear();
//                driver.findElement(By.xpath("//input[@name='email']")).sendKeys(email);
//                driver.findElement(By.xpath("//input[@name='phone']")).clear();
//                driver.findElement(By.xpath("//input[@name='phone']")).sendKeys(phone);
//
//                WebElement saveButton = driver.findElement(By.cssSelector("button[type='submit']"));
//                saveButton.click();
//
//
//
//                // Xử lý thông báo kết quả
//
//                WebElement messageSuccess = driver.findElement(By.xpath("//div[@role='dialog']"));
//                String actualmessageSuccess = messageSuccess.getText();
//                WebElement errorMessage = driver.findElement(By.cssSelector("span.text-red-500.text-sm"));
////                WebElement messageFullname = driver.findElement(By.xpath("//span[contains(text(),'Bắt buộc nhập họ và tên')]"));
////                WebElement messageUsernane = driver.findElement(By.xpath("//span[contains(text(),'Bắt buộc nhập tên người dùng')]"));
////                WebElement messageEmail = driver.findElement(By.xpath("//span[contains(text(),'Bắt buộc nhập email')]"));
////                WebElement messagePhone = driver.findElement(By.xpath("//span[contains(text(),'Bắt buộc nhập số điện thoại')]"));
//                if (actualmessageSuccess.contains("Success")) {
//                    testNGResults.put(String.valueOf(index + 1), new Object[]{
//                            String.valueOf(index + 1), "Test Step No.", "Action", fullname, username, email, phone, expected, actualmessageSuccess, "Success", ""
//                    });
//                } else if (errorMessage.isDisplayed()) {
//                    String errorText = errorMessage.getText();
//                    String path = IMAGE_DIR + "failure_" + System.currentTimeMillis() + ".png";
//                    takeScreenshot(driver, path);
//                    testNGResults.put(String.valueOf(index + 1), new Object[]{
//                            String.valueOf(index + 1), "Test Step No.", "Action", fullname, username, email, phone, expected, errorText, "Failed", path.replace("\\", "/")
//                    });
//
//                }
////                } else if (messageFullname.isDisplayed()) {
////                    String actualmessageFullname = messageFullname.getText();
////                    String path = IMAGE_DIR + "failure_" + System.currentTimeMillis() + ".png";
////                    takeScreenshot(driver, path);
////                    testNGResults.put(String.valueOf(index + 1), new Object[]{
////                            String.valueOf(index + 1), "Test Step No.", "Action", fullname, username, email, phone, image, expected, actualmessageFullname, "Failed", path.replace("\\", "/")
////                    });
////                } else if (messageUsernane .isDisplayed()) {
////                    String actualmessageUsername = messageUsernane.getText();
////                    String path = IMAGE_DIR + "failure_" + System.currentTimeMillis() + ".png";
////                    takeScreenshot(driver, path);
////                    testNGResults.put(String.valueOf(index + 1), new Object[]{
////                            String.valueOf(index + 1), "Test Step No.", "Action", fullname, username, email, phone, image, expected, actualmessageUsername, "Failed", path.replace("\\", "/")
////                    });
////                }else if (messageEmail.isDisplayed()) {
////                    String actualmessageEmail = messageEmail.getText();
////                    String path = IMAGE_DIR + "failure_" + System.currentTimeMillis() + ".png";
////                    takeScreenshot(driver, path);
////                    testNGResults.put(String.valueOf(index + 1), new Object[]{
////                            String.valueOf(index + 1), "Test Step No.", "Action", fullname, username, email, phone, image, expected, actualmessageEmail, "Failed", path.replace("\\", "/")
////                    });
////                }else if (messagePhone.isDisplayed()) {
////                    String actualmessagePhone = messagePhone.getText();
////                    String path = IMAGE_DIR + "failure_" + System.currentTimeMillis() + ".png";
////                    takeScreenshot(driver, path);
////                    testNGResults.put(String.valueOf(index + 1), new Object[]{
////                            String.valueOf(index + 1), "Test Step No.", "Action", fullname, username, email, phone, image, expected, actualmessagePhone, "Failed", path.replace("\\", "/")
////                    });
////                }
//                else {
//                    String path = IMAGE_DIR + "failure_" + System.currentTimeMillis() + ".png";
//                    takeScreenshot(driver, path);
//                    testNGResults.put(String.valueOf(index + 1), new Object[]{
//                            String.valueOf(index + 1), "Test Step No.", "Action", fullname, username, email, phone, expected, actualmessageSuccess, "Failed", path.replace("\\", "/")
//                    });
//                }
//                index++;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.println("lỗi ở đây");
//        }

    }

    @AfterClass
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
}
