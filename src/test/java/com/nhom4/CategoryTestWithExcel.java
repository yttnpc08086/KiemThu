package com.nhom4;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.*;
import java.time.Duration;
import java.util.*;

public class CategoryTestWithExcel {
    private WebDriver driver;
    private WebDriverWait wait;
    private String baseUrl = "http://localhost:3000";
    private XSSFWorkbook workbook;
    private Map<String, Object[]> testNGResults;
    private Map<String, String[]> dataCategoryCreate, dataCategoryEdit, dataCategorySearch, dataCategoryDelete;
    private final String EXCEL_DIR = "D:\\excel\\CategoryTestData.xlsx";
    private final String IMAGE_DIR = "D:\\excel\\images";
    private final String RESULT_DIR = "D:\\excel\\CategoryTestResult_" + System.currentTimeMillis() + ".xlsx";

    @BeforeClass
    public void setup() throws Exception {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        workbook = new XSSFWorkbook(new FileInputStream(EXCEL_DIR));
        testNGResults = new LinkedHashMap<>();
    }

    private void login() {
        driver.get(baseUrl + "/login");

        WebElement usernameField = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@placeholder='Nhập tài khoản của bạn']")));
        WebElement passwordField = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@placeholder='Nhập mật khẩu của bạn']")));
        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@type='submit']")));

        usernameField.sendKeys("admin");
        passwordField.sendKeys("123");
        loginButton.click();

        try {
            WebElement closeButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".swal2-close")));
            closeButton.click();
        } catch (Exception e) {
            // No alert
        }

        WebElement categoryLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[normalize-space()='Category']")));
        categoryLink.click();

        wait.until(ExpectedConditions.urlToBe("http://localhost:3000/admin/category"));
        String currentUrl = driver.getCurrentUrl();
        Assert.assertEquals(currentUrl, "http://localhost:3000/admin/category", "Incorrect redirect!");
    }

    private void readDataFromExcel() {
        dataCategoryCreate = readSheetData("categoryCreateData", 5);
        dataCategoryEdit = readSheetData("categoryEditData", 6);
        dataCategorySearch = readSheetData("categorySearchData", 3);
        dataCategoryDelete = readSheetData("categoryDeleteData", 3);
    }

    private Map<String, String[]> readSheetData(String sheetName, int columnCount) {
        Map<String, String[]> data = new HashMap<>();
        Sheet sheet = workbook.getSheet(sheetName);
        DataFormatter formatter = new DataFormatter();
        if (sheet == null) return data;

        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue; // Skip header
            String key = formatter.formatCellValue(row.getCell(0));
            String[] values = new String[columnCount];
            for (int i = 0; i < columnCount; i++) {
                values[i] = formatter.formatCellValue(row.getCell(i));
            }
            data.put(key, values);
        }
        return data;
    }

    private void writeImage(String imgSrc, Row row, Cell cell, XSSFSheet sheet) throws Exception {
        InputStream is = new FileInputStream(imgSrc);
        byte[] bytes = IOUtils.toByteArray(is);
        int pictureIdx = sheet.getWorkbook().addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
        is.close();

        CreationHelper helper = sheet.getWorkbook().getCreationHelper();
        Drawing<?> drawing = sheet.createDrawingPatriarch();
        ClientAnchor anchor = helper.createClientAnchor();

        // Set image position relative to the cell
        anchor.setCol1(cell.getColumnIndex());
        anchor.setRow1(row.getRowNum());
        anchor.setCol2(cell.getColumnIndex() + 1);
        anchor.setRow2(row.getRowNum() + 1);

        drawing.createPicture(anchor, pictureIdx);
    }


    private void takeScreenshot(String path) throws IOException {
        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(screenshot, new File(path));
    }

    @Test(priority = 1)
    public void testCategoryCreate() throws Exception {
        login();
        readDataFromExcel();
        for (String key : dataCategoryCreate.keySet()) {
            String[] values = dataCategoryCreate.get(key);
            String name = values[0];
            String description = values[1];
            String imagePath = values[2];
            String expectedResult = values[3];
            boolean isSuccess = Boolean.parseBoolean(values[4]);

            try {
                driver.navigate().to(baseUrl + "/admin/category");
                wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'Thêm loại sản phẩm')]"))).click();

                if (!name.isEmpty()) {
                    WebElement nameField = driver.findElement(By.xpath("//input[@name='name']"));
                    nameField.sendKeys(name);
                }

                if (!description.isEmpty()) {
                    WebElement descriptionField = driver.findElement(By.xpath("//input[@name='description']"));
                    descriptionField.sendKeys(description);
                }

                if (!imagePath.isEmpty()) {
                    WebElement imageField = driver.findElement(By.xpath("//input[@type='file']"));
                    imageField.sendKeys(new File(imagePath).getAbsolutePath());
                }

                driver.findElement(By.xpath("//button[contains(text(),'Thêm Danh Mục')]")).click();

                if (isSuccess) {
                    WebElement successMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".swal2-html-container")));
                    Assert.assertTrue(successMessage.getText().contains("Danh mục đã được thêm mới."));
                } else {
                    WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),'" + expectedResult + "')]")));
                    Assert.assertNotNull(errorMessage);
                }

                testNGResults.put(key, new Object[]{key, "Category creation passed", "Success"});
            } catch (Exception e) {
                String screenshotPath = IMAGE_DIR + "\\create_error_" + key + ".png";
                takeScreenshot(screenshotPath);
                testNGResults.put(key, new Object[]{key, "Category creation failed", e.getMessage(), screenshotPath});
            }
        }
    }

    @AfterClass
    public void teardown() throws Exception {
        XSSFWorkbook resultWorkbook = new XSSFWorkbook();
        XSSFSheet resultSheet = resultWorkbook.createSheet("Test Results");
        int rowNum = 0;

        for (String key : testNGResults.keySet()) {
            Object[] data = testNGResults.get(key);
            Row row = resultSheet.createRow(rowNum++);
            int cellNum = 0;

            for (Object field : data) {
                Cell cell = row.createCell(cellNum++);
                if (field instanceof String && field.toString().endsWith(".png")) {
                    // Write image directly into the cell
                    try {
                        writeImage(field.toString(), row, cell, resultSheet);
                    } catch (Exception e) {
                        cell.setCellValue("Error loading image");
                    }
                } else if (field instanceof String) {
                    cell.setCellValue((String) field);
                } else if (field instanceof Double) {
                    cell.setCellValue((Double) field);
                } else if (field instanceof Boolean) {
                    cell.setCellValue((Boolean) field);
                }
            }
        }

        // Autosize columns for better readability
        for (int i = 0; i < testNGResults.values().iterator().next().length; i++) {
            resultSheet.autoSizeColumn(i);
        }

        FileOutputStream fileOut = new FileOutputStream(RESULT_DIR);
        resultWorkbook.write(fileOut);
        fileOut.close();

        driver.quit();
    }

}
