package com.nhom4;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class loginTestWithExcel {
    public WebDriver driver;
    private XSSFWorkbook workbook;
    private XSSFSheet worksheet;
    private Map<String, Object[]> testNGResults;
    private Map<String, String[]> dataLogin;
    private final String EXCEL_DIR = "D:\\FPOLY\\KTNC\\KiemThu\\src\\main\\resources\\dataTest";
    private final String IMAGE_DIR = "D:\\FPOLY\\KTNC\\KiemThu\\src\\main\\resources\\image";

    public void readDataFromExcel() {
        try {
            dataLogin = new HashMap<>();
            worksheet = workbook.getSheet("logindata");

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
                        String username = "";
                        String password = "";
                        String expected = "";

                        while (cellIterator.hasNext()) {
                            Cell cell = cellIterator.next();
                            if (cell.getColumnIndex() == 0) {
                                key = dataformat.formatCellValue(cell);
                            } else if (cell.getColumnIndex() == 1) {
                                username = dataformat.formatCellValue(cell);
                            } else if (cell.getColumnIndex() == 2) {
                                password = dataformat.formatCellValue(cell);
                            } else if (cell.getColumnIndex() == 3) {
                                expected = dataformat.formatCellValue(cell);
                            }
                        }
                        String[] myArr = {username, password, expected};
                        dataLogin.put(key, myArr);
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
            driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
            workbook = new XSSFWorkbook(new FileInputStream(EXCEL_DIR + "\\LOGIN_TEST.xlsx"));
            worksheet = workbook.getSheet("logindata");
            readDataFromExcel();

            workbook = new XSSFWorkbook();
            worksheet = workbook.createSheet("Result");

            CellStyle rowStyle = workbook.createCellStyle();
            rowStyle.setAlignment(HorizontalAlignment.CENTER);
            rowStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            rowStyle.setWrapText(true);

            testNGResults.put("1", new Object[]{"Test Step No.", "Action", "Username", "Password", "Expected Output", "Actual Output", "Status", "Link", "Image"});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loginTest() throws Exception {
        try {
            Set<String> keySet = dataLogin.keySet();
            int index = 1;
            for (String key : keySet) {
                String[] value = dataLogin.get(key);
                String username = value[0];
                String password = value[1];
                String expected = value[2];

                String url = "http://localhost:3000/login";
                driver.get(url);
                driver.findElement(By.xpath("//input[@placeholder='Nhập tài khoản của bạn']")).sendKeys(username);
                driver.findElement(By.xpath("//input[@placeholder='Nhập mật khẩu của bạn']")).sendKeys(password);
                driver.findElement(By.xpath("//button[@type='submit']")).click();

                Thread.sleep(3000);
                String actual = driver.getTitle();
                if (actual.equalsIgnoreCase(expected)) {
                    testNGResults.put(String.valueOf(index + 1), new Object[]{String.valueOf(index + 1), "TestLogin success with username and password", username, password, expected, actual, "Login success", ""});
                } else {
                    String path = IMAGE_DIR + "failure_" + System.currentTimeMillis() + ".png";
                    takeScreenshot(driver, path);
                    testNGResults.put(String.valueOf(index + 1), new Object[]{String.valueOf(index + 1), "TestLogin failed with username and password", username, password, expected, actual, "Failed", path.replace("\\", "/")});
                }
                index++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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