package RW500PKG;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RW500PKGSMOKE {
	static StringBuilder msg = new StringBuilder();
	static WebDriver driver;
	static JavascriptExecutor mzexecutor;
	public static GenerateData genData;

	public static String RdyTime;
	public static String RecMsg, st;

	public static int rcount;
	public static int RWpcs, SHPpcs;
	public static int i;
	public static Properties storage = new Properties();

	@BeforeSuite
	public void startup() throws IOException {
		storage = new Properties();
		FileInputStream fi = new FileInputStream(".\\src\\main\\resources\\config.properties");
		storage.load(fi);

		// --Opening Chrome Browser
		DesiredCapabilities capabilities = new DesiredCapabilities();
		WebDriverManager.chromedriver().setup();
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless", "--window-size=1920,1200");
		options.addArguments("--incognito");
		options.addArguments("--test-type");
		options.addArguments("--no-proxy-server");
		options.addArguments("--proxy-bypass-list=*");
		options.addArguments("--disable-extensions");
		options.addArguments("--no-sandbox");
		String downloadFilepath = System.getProperty("user.dir") + "\\src\\main\\resources\\Downloads";
		HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
		chromePrefs.put("profile.default_content_settings.popups", 0);
		chromePrefs.put("download.prompt_for_download", "false");
		chromePrefs.put("safebrowsing.enabled", "false");
		chromePrefs.put("download.default_directory", downloadFilepath);
		options.setExperimentalOption("prefs", chromePrefs);
		capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		capabilities.setCapability(ChromeOptions.CAPABILITY, options);
		capabilities.setPlatform(Platform.ANY);
		driver = new ChromeDriver(options);

		// Set new size
		// 1032, 776
		/*
		 * Dimension newDimension = new Dimension(1366, 788);
		 * driver.manage().window().setSize(newDimension); currentDimension =
		 * driver.manage().window().getSize(); height = currentDimension.getHeight();
		 * width = currentDimension.getWidth(); System.out.println("New height: " +
		 * height); System.out.println("New width: " + width);
		 * System.out.println("New window size==" + driver.manage().window().getSize());
		 */
		// Getting Dimension newSetDimension = driver.manage().window().getSize();
		/*
		 * int newHeight = newSetDimension.getHeight(); int newWidth =
		 * newSetDimension.getWidth(); System.out.println("Current height: " +
		 * newHeight); System.out.println("Current width: " + newWidth);
		 */
	}

	public static String getScreenshot(WebDriver driver, String screenshotName) throws IOException {

		TakesScreenshot ts = (TakesScreenshot) driver;
		File source = ts.getScreenshotAs(OutputType.FILE);
		// after execution, you could see a folder "FailedTestsScreenshots" under src
		// folder
		String destination = System.getProperty("user.dir") + "\\src\\main\\resources\\Screenshots\\" + screenshotName
				+ ".png";
		File finalDestination = new File(destination);
		FileUtils.copyFile(source, finalDestination);
		return destination;
	}

	public void login() throws InterruptedException, IOException {
		WebDriverWait wait = new WebDriverWait(driver, 50);
		JavascriptExecutor js = (JavascriptExecutor) driver;

		String Env = storage.getProperty("Env");
		System.out.println("Env " + Env);

		if (Env.equalsIgnoreCase("Pre-Prod")) {
			String baseUrl = storage.getProperty("PREPRODURL");
			driver.get(baseUrl);
			try {
				String UserName = storage.getProperty("PREPRODUserName");
				wait.until(ExpectedConditions.elementToBeClickable(By.id("txtUserId")));
				driver.findElement(By.id("txtUserId")).clear();
				driver.findElement(By.id("txtUserId")).sendKeys(UserName);
				String Password = storage.getProperty("PREPRODPassword");
				driver.findElement(By.id("txtPassword")).clear();
				driver.findElement(By.id("txtPassword")).sendKeys(Password);
			} catch (Exception e) {
				msg.append("URL is not working==FAIL");
				getScreenshot(driver, "LoginIssue");
				driver.quit();
				Env = storage.getProperty("Env");
				String File = ".\\src\\main\\resources\\Screenshots\\LoginIssue.png";
				String subject = "Selenium Automation Script: " + Env + " : Route Work Details-500Packages";

				try {
					Email.sendMail(
							"ravina.prajapati@samyak.com,asharma@samyak.com,parth.doshi@samyak.com, saurabh.jain@samyak.com, himanshu.dholakia@samyak.com",
							subject, msg.toString(), File);
				} catch (Exception ex) {
					Logger.getLogger(RW500PKGSMOKE.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		} else if (Env.equalsIgnoreCase("STG")) {
			String baseUrl = storage.getProperty("STGURL");
			driver.get(baseUrl);
			try {
				String UserName = storage.getProperty("STGUserName");
				wait.until(ExpectedConditions.elementToBeClickable(By.id("txtUserId")));
				driver.findElement(By.id("txtUserId")).clear();
				driver.findElement(By.id("txtUserId")).sendKeys(UserName);
				String Password = storage.getProperty("STGPassword");
				driver.findElement(By.id("txtPassword")).clear();
				driver.findElement(By.id("txtPassword")).sendKeys(Password);
			} catch (Exception e) {
				msg.append("URL is not working==FAIL");
				getScreenshot(driver, "LoginIssue");
				driver.quit();
				Env = storage.getProperty("Env");
				String File = ".\\src\\main\\resources\\Screenshots\\LoginIssue.png";
				String subject = "Selenium Automation Script: " + Env + " : Route Work Details-500Packages";

				try {
					Email.sendMail(
							"ravina.prajapati@samyak.com,asharma@samyak.com,parth.doshi@samyak.com, saurabh.jain@samyak.com, himanshu.dholakia@samyak.com",
							subject, msg.toString(), File);
				} catch (Exception ex) {
					Logger.getLogger(RW500PKGSMOKE.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		} else if (Env.equalsIgnoreCase("DEV")) {
			String baseUrl = storage.getProperty("DEVURL");
			driver.get(baseUrl);
			try {
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("Fedextitle")));
				String UserName = storage.getProperty("DEVUserName");
				wait.until(ExpectedConditions.elementToBeClickable(By.id("txtUserId")));
				driver.findElement(By.id("txtUserId")).clear();
				driver.findElement(By.id("txtUserId")).sendKeys(UserName);
				String Password = storage.getProperty("DEVPassword");
				driver.findElement(By.id("txtPassword")).clear();
				driver.findElement(By.id("txtPassword")).sendKeys(Password);
			} catch (Exception e) {
				msg.append("URL is not working==FAIL");
				getScreenshot(driver, "LoginIssue");
				driver.quit();
				Env = storage.getProperty("Env");
				String File = ".\\src\\main\\resources\\Screenshots\\LoginIssue.png";
				String subject = "Selenium Automation Script: " + Env + " : Route Work Details-500Packages";

				try {
					Email.sendMail(
							"ravina.prajapati@samyak.com,asharma@samyak.com,parth.doshi@samyak.com, saurabh.jain@samyak.com, himanshu.dholakia@samyak.com",
							subject, msg.toString(), File);
				} catch (Exception ex) {
					Logger.getLogger(RW500PKGSMOKE.class.getName()).log(Level.SEVERE, null, ex);
				}
			}

		} else if (Env.equalsIgnoreCase("PROD")) {

			String baseUrl = storage.getProperty("PRODURL");
			driver.get(baseUrl);
			try {
				String UserName = storage.getProperty("PRODUserName");
				wait.until(ExpectedConditions.elementToBeClickable(By.id("txtUserId")));
				driver.findElement(By.id("txtUserId")).clear();
				driver.findElement(By.id("txtUserId")).sendKeys(UserName);
				String Password = storage.getProperty("PRODPassword");
				driver.findElement(By.id("txtPassword")).clear();
				driver.findElement(By.id("txtPassword")).sendKeys(Password);
			} catch (Exception e) {
				msg.append("URL is not working==FAIL");
				getScreenshot(driver, "LoginIssue");
				driver.quit();
				Env = storage.getProperty("Env");
				String subject = "Selenium Automation Script: " + Env + " : Route Work Smoke";
				String File = ".\\src\\main\\resources\\Screenshots\\LoginIssue.png";
				try {
					Email.sendMail(
							"ravina.prajapati@samyak.com,asharma@samyak.com,parth.doshi@samyak.com, saurabh.jain@samyak.com, himanshu.dholakia@samyak.com",
							subject, msg.toString(), File);

				} catch (Exception ex) {
					Logger.getLogger(RW500PKGSMOKE.class.getName()).log(Level.SEVERE, null, ex);
				}
			}

		}
		Thread.sleep(2000);
		WebElement RWRadio = driver.findElement(By.id("rbRouteWork"));
		js.executeScript("arguments[0].click();", RWRadio);

		WebElement Login = driver.findElement(By.id("cmdLogin"));
		js.executeScript("arguments[0].click();", Login);
		Thread.sleep(2000);
		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("content")));
	}

	@Test
	public void rw500PCKG() throws Exception {
		Robot robot = new Robot();
		JavascriptExecutor js = (JavascriptExecutor) driver;
		String Env = storage.getProperty("Env");

		mzexecutor = (JavascriptExecutor) driver;
		genData = new GenerateData();

		// --login
		login();

		// Open Menu > Submenu > Submenu -- "RW Form page"
		WebDriverWait wait = new WebDriverWait(driver, 50);
		Actions act = new Actions(driver);

		driver.findElement(By.linkText("Admin")).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Route Work")));

		WebElement menu = driver.findElement(By.linkText("Route Work"));
		act.moveToElement(menu).perform();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Route Work Form")));

		driver.findElement(By.linkText("Route Work Form")).click();
		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("content1")));

		// Process2: RW Form Fill up

		// Route WorkId
		WebElement RWId = driver.findElement(By.id("txtRouteWorkId"));

		if (RWId.isEnabled()) {
			throw new Error("\nRWId is enable. It is an issue");
		}

		// Route Work Name

		driver.findElement(By.id("txtrouteworkdescription")).sendKeys("Route Name Automation ");
		driver.findElement(By.id("txtrouteworkdescription")).sendKeys(genData.generateRandomNumber(7));
		String RWName = driver.findElement(By.id("txtrouteworkdescription")).getAttribute("value");
		System.out.println(RWName);

		// --Enter customer

		if (Env.equalsIgnoreCase("STG")) {
			driver.findElement(By.id("txtCustCode")).sendKeys("117117117");
			driver.findElement(By.id("txtCustCode")).sendKeys(Keys.TAB);

		} else if (Env.equalsIgnoreCase("Pre-Prod")) {
			driver.findElement(By.id("txtCustCode")).sendKeys("117117117");
			driver.findElement(By.id("txtCustCode")).sendKeys(Keys.TAB);

		} else if (Env.equalsIgnoreCase("Prod")) {
			driver.findElement(By.id("txtCustCode")).sendKeys("777777777");
			driver.findElement(By.id("txtCustCode")).sendKeys(Keys.TAB);

		}

		// Declare Value
		driver.findElement(By.id("declared_value")).clear();
		driver.findElement(By.id("declared_value")).sendKeys("444");

		// Route Work Reference#2 and #4
		driver.findElement(By.id("txtRouteWorkRef2")).sendKeys("reference2");
		driver.findElement(By.id("txtRouteWorkRef4")).sendKeys("reference4");

		// Executed By
		driver.findElement(By.id("rdbNGL")).click();

		// Generate Route(No. of hours)
		driver.findElement(By.id("txtGenerateRoute")).clear();
		driver.findElement(By.id("txtGenerateRoute")).sendKeys("1");

		// Preferred Courier Route
		driver.findElement(By.id("txtPrefCourierRoute")).sendKeys("PCRoute001");

		// Start-End Date
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy ");
		Date stdate = new Date();
		String stdate1 = dateFormat.format(stdate);
		// System.out.println(stdate1);

		Date enddate = new Date();
		// --changed Add days date from 5 to 1//--Ravina
		Date addedDate1 = addDays(enddate, 1);
		String enddate1 = dateFormat.format(addedDate1);
		// System.out.println(enddate1);

		driver.findElement(By.id("txtStartDate")).sendKeys(stdate1);
		driver.findElement(By.id("txtEndDate")).sendKeys(enddate1);

		// driver.findElement(By.xpath(".//*[@id='row1']/img")).click();
		// driver.findElement(By.xpath("//a[contains(.,'Today')]")).click();
		// sleep(500);
		// driver.findElement(By.xpath(".//*[@id='row3']/img")).click();
		// driver.findElement(By.xpath("//a[contains(.,'Today')]")).click();

		// Ready-Due Time
		driver.findElement(By.id("ReadyHourDropDownList")).sendKeys("8");
		driver.findElement(By.id("ReadyMinuteDropDownList")).sendKeys("30");
		driver.findElement(By.id("DueHourDropDownList")).sendKeys("2");
		driver.findElement(By.id("DueMinuteDropDownList")).sendKeys("45");
		driver.findElement(By.id("DueFormatDropDownList")).sendKeys("PM");

		String RdyHH1 = driver.findElement(By.id("ReadyHourDropDownList")).getAttribute("value");
		int RdyHH2 = Integer.parseInt(RdyHH1);
		// System.out.println("Convert into Int:" + RdyHH2);

		if (RdyHH2 < 10) {
			String RdyHH = "0" + RdyHH2;
			// System.out.println("Ready HR:" + RdyHH);
			String RdyMM = driver.findElement(By.id("ReadyMinuteDropDownList")).getAttribute("value");
			// System.out.println(RdyMM);
			String RdyAP = driver.findElement(By.id("ReadyFormatDropDownList")).getAttribute("value");
			// System.out.println(RdyAP);

			RdyTime = RdyHH + ":" + RdyMM + " " + RdyAP;
			System.out.println("Ready Time: " + RdyTime);
		} else {
			int RdyHH = RdyHH2;

			String RdyMM = driver.findElement(By.id("ReadyMinuteDropDownList")).getAttribute("value");
			// System.out.println(RdyMM);
			String RdyAP = driver.findElement(By.id("ReadyFormatDropDownList")).getAttribute("value");
			// System.out.println(RdyAP);

			RdyTime = RdyHH + ":" + RdyMM + " " + RdyAP;
			System.out.println("Ready Time: " + RdyTime);
		}

		// System.out.println(RdyTime);

		// Recurrence
		driver.findElement(By.id("rdbtrecurrence_0")).click();
		driver.findElement(By.id("txtDaysDaily")).sendKeys("2");
		String RecType = driver.findElement(By.id("rdbtrecurrence_0")).getAttribute("value");
		// System.out.println("Rec Type: " + RecType);
		String RecValue = driver.findElement(By.id("txtDaysDaily")).getAttribute("value");
		// System.out.println("Rec Value: " + RecValue);

		// Exempt Date
		Date exmdate = new Date();
		// --changed Add days date from 4 to 1//--Ravina
		Date addedDate2 = addDays(exmdate, 1);
		String exmdate1 = dateFormat.format(addedDate2);
		// System.out.println(exmdate1);

		// driver.findElement(By.id("txtExemptDateDaily")).sendKeys("Holidays");
		driver.findElement(By.id("txtExemptDateDaily")).sendKeys(";");
		driver.findElement(By.id("txtExemptDateDaily")).sendKeys(exmdate1);

		String ExemptDate = driver.findElement(By.id("txtExemptDateDaily")).getAttribute("value");

		// AlterGenerateDate
		Date altdate = new Date();
		// --changed Add days date from 5 to 1//--Ravina
		Date addedDate3 = addDays(altdate, 2);
		String altdate1 = dateFormat.format(addedDate3);
		// System.out.println(altdate1);

		driver.findElement(By.id("txtAlternateGenerationDateDaily")).sendKeys(altdate1);
		String AlertGenerationDate = driver.findElement(By.id("txtAlternateGenerationDateDaily")).getAttribute("value");

		// First Generation
		Date fgdate = new Date();
		String fgdate1 = dateFormat.format(fgdate);
		// System.out.println(fgdate1);

		driver.findElement(By.id("txtDate")).sendKeys(fgdate1);
		String FirstGenerationDate = driver.findElement(By.id("txtDate")).getAttribute("value");

		driver.findElement(By.id("ddlHoursTime")).sendKeys("12");
		driver.findElement(By.id("ddlMinuteTime")).sendKeys("10");
		driver.findElement(By.id("ddlTimeMinute")).sendKeys("AM");

		String fghh = driver.findElement(By.id("ddlHoursTime")).getAttribute("value");
		String fgmm = driver.findElement(By.id("ddlMinuteTime")).getAttribute("value");
		String fgampm = driver.findElement(By.id("ddlTimeMinute")).getAttribute("value");

		String FirstGenerationTime = fghh + ":" + fgmm + " " + fgampm;

		// Manifest
		driver.findElement(By.id("txtRouteWorkEmail")).sendKeys("pdoshi1@samyak.com");

		// Flat Rate
		driver.findElement(By.id("txtFlatRate")).sendKeys("234.56");

		// Add RW Master Packages upto 500

		File srcCR = new File(".\\src\\main\\resources\\Add500Packages.xls");

		FileInputStream fisCR = new FileInputStream(srcCR);
		Workbook workbookCR = WorkbookFactory.create(fisCR);
		Sheet shCR = workbookCR.getSheet("Sheet1");

		DataFormatter formatter = new DataFormatter();

		String rwpcs = formatter.formatCellValue(shCR.getRow(1).getCell(0));
		RWpcs = Integer.parseInt(rwpcs);

		driver.findElement(By.id("routepieces")).clear();
		driver.findElement(By.id("routepieces")).sendKeys(rwpcs);
		WebElement webElementRpcs = driver.findElement(By.id("routepieces"));
		webElementRpcs.sendKeys(Keys.TAB);
		Thread.sleep(2000);

		driver.findElement(By.id("routerdbNo")).click();
		Thread.sleep(2000);

		driver.findElement(By.id("txtrouteContents")).clear();
		driver.findElement(By.id("txtrouteContents")).sendKeys("Invitation Letter");
		Thread.sleep(1000);

		for (i = 0; i < RWpcs; i++) {
			driver.findElement(By.id("txtRouteQty" + i)).clear();
			driver.findElement(By.id("txtRouteQty" + i)).sendKeys("1");

			driver.findElement(By.id("txtRouteDimLenN" + i)).clear();
			driver.findElement(By.id("txtRouteDimLenN" + i)).sendKeys(getRandomInteger(st));
			driver.findElement(By.id("txtRouteDimWidN" + i)).clear();
			driver.findElement(By.id("txtRouteDimWidN" + i)).sendKeys(getRandomInteger(st));
			driver.findElement(By.id("txtRouteDimHtN" + i)).clear();
			driver.findElement(By.id("txtRouteDimHtN" + i)).sendKeys(getRandomInteger(st));
			driver.findElement(By.id("txtRouteActWtN" + i)).clear();
			driver.findElement(By.id("txtRouteActWtN" + i)).sendKeys(getRandomInteger(st));

			int cvl = i + 1;
			int divisor = 10;

			if (cvl % divisor == 0) {
				driver.findElement(By.id("idRouteNext")).click();
				Thread.sleep(2000);
			}

		}

		// Return Un-deliverable Shipments To
		driver.findElement(By.id("txtUDCompany")).sendKeys("JOHN COMP");
		driver.findElement(By.id("txtUDContact")).sendKeys("JOHN STARVIS");
		driver.findElement(By.id("txtUDAddr1")).sendKeys("9011 CAPITAL KING, 7TH STRT");
		driver.findElement(By.id("txtUDAddr2")).sendKeys("No#771");
		driver.findElement(By.id("txtUDzip")).sendKeys("90003");
		robot.keyPress(KeyEvent.VK_TAB);
		Thread.sleep(2000);
		driver.findElement(By.id("txtUDPhone")).sendKeys("4115116110");
		driver.findElement(By.id("txtUDDeliveryInst")).sendKeys("Please call before 4h of delivery process");

		// Process3: Add Shipments (1-2,1-3,1-4,1-6)

		// Shipment Details - 1
		// From
		driver.findElement(By.id("txtFromStopSeq")).sendKeys("1");
		driver.findElement(By.id("txtFromCompany")).sendKeys("CREATIVE ARTIST AGENCY");
		driver.findElement(By.id("txtFromContact")).sendKeys("Client Trust");
		driver.findElement(By.id("txtFromAddr1")).sendKeys("2000 AVENUE OF THE STARS");
		driver.findElement(By.id("txtFromAddr2")).sendKeys("");
		driver.findElement(By.id("txtFromZip")).sendKeys("90067");
		robot.keyPress(KeyEvent.VK_TAB);
		Thread.sleep(2000);
		driver.findElement(By.id("txtFromPhone")).sendKeys("(424) 288-2125");
		driver.findElement(By.id("txtPUInst")).sendKeys("Art Work");
		driver.findElement(By.id("txtShipperEmail")).sendKeys("pdoshi1@samyak.com");
		driver.findElement(By.id("chkShpOrderRcvd")).click();
		driver.findElement(By.id("chkShpPickup")).click();
		driver.findElement(By.id("chkShpDelivery")).click();
		// TO
		driver.findElement(By.id("txtToStopSeq")).sendKeys("2");
		driver.findElement(By.id("txtToCompany")).sendKeys("SHOWTIME");
		driver.findElement(By.id("txtToContact")).sendKeys("GARY LEVINE");
		driver.findElement(By.id("txtToAddr1")).sendKeys("10880 WHILSHIRE BLVD");
		driver.findElement(By.id("txtToAddr2")).sendKeys("#1600");
		driver.findElement(By.id("txtToZip")).sendKeys("90024");
		robot.keyPress(KeyEvent.VK_TAB);
		Thread.sleep(2000);
		driver.findElement(By.id("txtToPhone")).sendKeys("(424) 288-2125");
		driver.findElement(By.id("txtDelInst")).sendKeys("Art Work");
		driver.findElement(By.id("txtRecipientEmail")).sendKeys("pdoshi2@samyak.com");
		driver.findElement(By.id("chkRecpOrderRcvd")).click();
		driver.findElement(By.id("chkRecpQDTChange")).click();
		driver.findElement(By.id("chkRecpException")).click();
		// Package for Shipment-1
		driver.findElement(By.id("pieces")).clear();
		driver.findElement(By.id("pieces")).sendKeys("1");
		Thread.sleep(1000);
		driver.findElement(By.id("txtContents")).clear();
		driver.findElement(By.id("txtContents")).sendKeys("FLOWERS with Chocolate");
		Thread.sleep(1000);
		driver.findElement(By.id("txtDimLen0")).clear();
		driver.findElement(By.id("txtDimLen0")).sendKeys(getRandomInteger(st));
		driver.findElement(By.id("txtDimWid0")).clear();
		driver.findElement(By.id("txtDimWid0")).sendKeys(getRandomInteger(st));
		driver.findElement(By.id("txtDimHt0")).clear();
		driver.findElement(By.id("txtDimHt0")).sendKeys(getRandomInteger(st));
		driver.findElement(By.id("txtActWt0")).clear();
		driver.findElement(By.id("txtActWt0")).sendKeys(getRandomInteger(st));
		driver.findElement(By.id("txtRouteWorkRef1")).sendKeys("Ref1 Ship1");
		driver.findElement(By.id("txtRouteWorkRef3")).sendKeys("Ref3 Ship1");
		driver.findElement(By.id("btnaddshipment")).click();
		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("gvShipmentDetails")));

		// Shipment Details - 2
		// From
		driver.findElement(By.id("txtFromStopSeq")).clear();
		driver.findElement(By.id("txtFromStopSeq")).sendKeys("1");
		robot.keyPress(KeyEvent.VK_TAB);
		Thread.sleep(2000);
		// TO
		driver.findElement(By.id("txtToStopSeq")).clear();
		driver.findElement(By.id("txtToStopSeq")).sendKeys("3");
		driver.findElement(By.id("txtToCompany")).sendKeys("PROTOCOL ENTERTAINMENT");
		driver.findElement(By.id("txtToContact")).sendKeys("JARRY CROSS");
		driver.findElement(By.id("txtToAddr1")).sendKeys("16128 SHERMAN WAY");
		driver.findElement(By.id("txtToAddr2")).clear();
		driver.findElement(By.id("txtToZip")).sendKeys("91406");
		robot.keyPress(KeyEvent.VK_TAB);
		Thread.sleep(2000);
		driver.findElement(By.id("txtToPhone")).sendKeys("(424) 288-2125");
		driver.findElement(By.id("txtDelInst")).sendKeys("Art Work");
		driver.findElement(By.id("txtRecipientEmail")).sendKeys("pdoshi@samyak.com");
		driver.findElement(By.id("chkRecpOrderRcvd")).click();
		// Package for Shipment-2 Taken from Master
		driver.findElement(By.id("txtRouteWorkRef1")).sendKeys("Ref1 Ship2");
		driver.findElement(By.id("txtRouteWorkRef3")).sendKeys("Ref3 Ship2");
		driver.findElement(By.id("btnaddshipment")).click();
		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("gvShipmentDetails")));

		// Shipment Details - 3
		// From
		driver.findElement(By.id("txtFromStopSeq")).clear();
		driver.findElement(By.id("txtFromStopSeq")).sendKeys("1");
		robot.keyPress(KeyEvent.VK_TAB);
		Thread.sleep(2000);
		// TO
		driver.findElement(By.id("txtToStopSeq")).clear();
		driver.findElement(By.id("txtToStopSeq")).sendKeys("4");
		driver.findElement(By.id("txtToCompany")).sendKeys("HBO");
		driver.findElement(By.id("txtToContact")).sendKeys("MR. MICHAEL LOMBARDO");
		driver.findElement(By.id("txtToAddr1")).sendKeys("2500 BROADWAY");
		driver.findElement(By.id("txtToAddr2")).sendKeys("#400");
		driver.findElement(By.id("txtToZip")).sendKeys("91404");
		robot.keyPress(KeyEvent.VK_TAB);
		Thread.sleep(2000);
		driver.findElement(By.id("txtToPhone")).sendKeys("(424) 288-2125");
		driver.findElement(By.id("txtDelInst")).sendKeys("Art Work");
		driver.findElement(By.id("txtRecipientEmail")).sendKeys("pdoshi@samyak.com");
		driver.findElement(By.id("chkRecpOrderRcvd")).click();
		driver.findElement(By.id("chkRecpQDTChange")).click();
		driver.findElement(By.id("chkRecpException")).click();
		// Package for Shipment-3
		driver.findElement(By.id("pieces")).clear();
		driver.findElement(By.id("pieces")).sendKeys("1");
		driver.findElement(By.id("txtContents")).clear();
		driver.findElement(By.id("txtContents")).sendKeys("Only Chocolate");
		Thread.sleep(500);
		driver.findElement(By.id("txtDimLen0")).clear();
		driver.findElement(By.id("txtDimLen0")).sendKeys(getRandomInteger(st));
		driver.findElement(By.id("txtDimWid0")).clear();
		driver.findElement(By.id("txtDimWid0")).sendKeys(getRandomInteger(st));
		driver.findElement(By.id("txtDimHt0")).clear();
		driver.findElement(By.id("txtDimHt0")).sendKeys(getRandomInteger(st));
		driver.findElement(By.id("txtActWt0")).clear();
		driver.findElement(By.id("txtActWt0")).sendKeys(getRandomInteger(st));
		driver.findElement(By.id("txtRouteWorkRef1")).sendKeys("Ref1 Ship3");
		driver.findElement(By.id("txtRouteWorkRef3")).sendKeys("Ref3 Ship3");
		driver.findElement(By.id("btnaddshipment")).click();
		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("gvShipmentDetails")));

		// Shipment Details - 4
		// From
		driver.findElement(By.id("txtFromStopSeq")).clear();
		driver.findElement(By.id("txtFromStopSeq")).sendKeys("1");
		robot.keyPress(KeyEvent.VK_TAB);
		Thread.sleep(2000);
		// TO
		driver.findElement(By.id("txtToStopSeq")).clear();
		driver.findElement(By.id("txtToStopSeq")).sendKeys("6");
		driver.findElement(By.id("txtToCompany")).sendKeys("ORLY ADELSON PRODUCTIONS");
		driver.findElement(By.id("txtToContact")).sendKeys("SQUINTAR FILMS INC");
		driver.findElement(By.id("txtToAddr1")).sendKeys("2900 OLYMPIC BLVD");
		driver.findElement(By.id("txtToAddr2")).clear();
		driver.findElement(By.id("txtToZip")).sendKeys("91404");
		robot.keyPress(KeyEvent.VK_TAB);
		Thread.sleep(2000);
		driver.findElement(By.id("txtToPhone")).sendKeys("(424) 288-2125");
		driver.findElement(By.id("txtDelInst")).sendKeys("Art Work");
		driver.findElement(By.id("txtRecipientEmail")).sendKeys("pdoshi@samyak.com");
		driver.findElement(By.id("chkRecpOrderRcvd")).click();
		// Package for Shipment-4
		js.executeScript("window.scrollBy(0,-25)", "");
		String shppcs = formatter.formatCellValue(shCR.getRow(1).getCell(1));
		SHPpcs = Integer.parseInt(shppcs);
		driver.findElement(By.id("pieces")).clear();
		driver.findElement(By.id("pieces")).sendKeys(shppcs);
		WebElement webElementshppcs = driver.findElement(By.id("pieces"));
		webElementshppcs.sendKeys(Keys.TAB);
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath(".//*[@id='rdbNo']")));
		WebElement rdbNo = driver.findElement(By.xpath(".//*[@id='rdbNo']"));
		act.moveToElement(rdbNo).click().perform();
		Thread.sleep(1000);
		driver.findElement(By.id("txtContents")).clear();
		driver.findElement(By.id("txtContents")).sendKeys("Chocolate Voucher");
		Thread.sleep(2000);

		for (i = 0; i < SHPpcs; i++) {
			wait.until(ExpectedConditions.elementToBeClickable(By.id("txtQty" + i)));
			WebElement Qty = driver.findElement(By.id("txtQty" + i));
			act.moveToElement(Qty).build().perform();
			driver.findElement(By.id("txtQty" + i)).clear();
			driver.findElement(By.id("txtQty" + i)).sendKeys("1");

			driver.findElement(By.id("txtDimLenN" + i)).clear();
			driver.findElement(By.id("txtDimLenN" + i)).sendKeys(getRandomInteger(st));
			driver.findElement(By.id("txtDimWidN" + i)).clear();
			driver.findElement(By.id("txtDimWidN" + i)).sendKeys(getRandomInteger(st));
			driver.findElement(By.id("txtDimHtN" + i)).clear();
			driver.findElement(By.id("txtDimHtN" + i)).sendKeys(getRandomInteger(st));
			driver.findElement(By.id("txtActWtNew" + i)).clear();
			driver.findElement(By.id("txtActWtNew" + i)).sendKeys(getRandomInteger(st));

			int cvl = i + 1;
			int divisor = 10;

			if (cvl % divisor == 0) {
				driver.findElement(By.id("idNext")).click();
				Thread.sleep(2000);
			}

		}

		driver.findElement(By.id("txtRouteWorkRef1")).sendKeys("Ref1 Ship4");
		driver.findElement(By.id("txtRouteWorkRef3")).sendKeys("Ref3 Ship4");
		driver.findElement(By.id("btnaddshipment")).click();
		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("gvShipmentDetails")));

		// Process4: Check validation on Save for missing shipment sequence

		// Click on Done for validation
		WebElement BtnDone = driver.findElement(By.id("btndone"));
		js.executeScript("arguments[0].scrollIntoView();", BtnDone);
		Thread.sleep(2000);
		act.moveToElement(BtnDone).click().perform();
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[@src=\"images/ajax-loader.gif\"]")));
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("lblShipmentCountErr")));
			String DoneValidation = driver.findElement(By.id("lblShipmentCountErr")).getText();
			System.out.println("ActualMsg==" + DoneValidation);

			String Val1 = "Shipment Stop Sequence is missing. Please include all stops in sequence to generate route.";

			if (DoneValidation.equals(Val1)) {
				System.out.println("Display this validation when seq not proper: " + DoneValidation);
			} else {
				throw new Error("\nStop sequence validation not proper");
			}
		} catch (Exception ValMsg) {
			System.out.println("Validation for sequence is not proper is not displayed.");

		}

		// Process5: Create RW with Draft

		// Click SaveforLater for Draft
		driver.findElement(By.id("btnsaveforlater")).click();
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[@src=\"images/ajax-loader.gif\"]")));
		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("currentForm")));

		// Get Generated RWId
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("lmsg")));
		String RWIdtext = driver.findElement(By.id("lmsg")).getText();
		getScreenshot(driver, "CreatedRW500");

		String[] RWSplit = RWIdtext.split(" ");
		String RWid = RWSplit[5];

		String RWid1 = RWid.substring(0, 10);

		System.out.println("RouteWorkId: " + RWid1);

		// Process6: Edit Draft RW + Edit Stop Seq + Add Shipment + Done

		// Search with generated RWId
		driver.findElement(By.id("ddlStatus")).sendKeys("All");
		Thread.sleep(2000);
		driver.findElement(By.id("txtRouteWorkId")).sendKeys(RWid1);
		Thread.sleep(2000);
		driver.findElement(By.id("btnSearch")).click();
		Thread.sleep(2000);
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[@src=\"images/ajax-loader.gif\"]")));

		// Edit RW
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(".//*[@id='dgRWList_lbEdit_0']/img")));
		} catch (Exception EditVis) {
			WebDriverWait wait1 = new WebDriverWait(driver, 60);
			wait1.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(".//*[@id='dgRWList_lbEdit_0']/img")));

		}
		driver.findElement(By.xpath(".//*[@id='dgRWList_lbEdit_0']/img")).click();
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[@src=\"images/ajax-loader.gif\"]")));
		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("content1")));

		WebElement el = driver.findElement(By.id("btnaddshipment"));
		js.executeScript("arguments[0].scrollIntoView();", el);
		Thread.sleep(5000);
		WebElement Edit = driver.findElement(By.id("btnDownEditStops"));
		act.moveToElement(Edit).build().perform();
		Thread.sleep(5000);

		// Change Sequence of shipment-4
		// *[@id="gvShipmentDetails_ctl06_lbEdit"]
		try {
			wait.until(
					ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[@src=\"images/ajax-loader.gif\"]")));
			WebElement element4 = driver.findElement(By.xpath(".//*[@id='gvShipmentDetails_ctl06_lbEdit']"));
			act.moveToElement(element4).build().perform();
			wait.until(ExpectedConditions.elementToBeClickable(element4));
			act.moveToElement(element4).click().perform();
			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("content1")));
		} catch (Exception ctl006) {
			wait.until(
					ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[@src=\"images/ajax-loader.gif\"]")));
			WebElement element4 = driver.findElement(By.xpath(".//*[@id='gvShipmentDetails_ctl06_lbEdit']"));
			act.moveToElement(element4).build().perform();
			wait.until(ExpectedConditions.elementToBeClickable(element4));
			act.moveToElement(element4).click().perform();
			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("content1")));
		}

		driver.findElement(By.id("txtToStopSeq")).clear();
		driver.findElement(By.id("txtToStopSeq")).sendKeys("5");
		robot.keyPress(KeyEvent.VK_TAB);

		WebElement el2 = driver.findElement(By.id("chkRecpOrderRcvd"));
		js.executeScript("arguments[0].scrollIntoView();", el2);
		Thread.sleep(2000);

		driver.findElement(By.id("btnaddshipment")).click();
		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("gvShipmentDetails")));

		// add new Shipment Details - 5 (1-6)
		// From
		driver.findElement(By.id("txtFromStopSeq")).clear();
		driver.findElement(By.id("txtFromStopSeq")).sendKeys("1");
		robot.keyPress(KeyEvent.VK_TAB);
		// TO
		driver.findElement(By.id("txtToStopSeq")).clear();
		driver.findElement(By.id("txtToStopSeq")).sendKeys("6");
		driver.findElement(By.id("txtToCompany")).sendKeys("JAVE PRODUCTIONS, INC");
		driver.findElement(By.id("txtToContact")).sendKeys("BARRY SIEGAL");
		driver.findElement(By.id("txtToAddr1")).sendKeys("2850 OCEAN PARK BLVD");
		driver.findElement(By.id("txtToAddr2")).sendKeys("#300");
		driver.findElement(By.id("txtToZip")).sendKeys("90405");
		robot.keyPress(KeyEvent.VK_TAB);
		Thread.sleep(2000);
		driver.findElement(By.id("txtToPhone")).sendKeys("(424) 288-2125");
		driver.findElement(By.id("txtDelInst")).sendKeys("Art Work");
		driver.findElement(By.id("txtRecipientEmail")).sendKeys("pdoshi@samyak.com");
		driver.findElement(By.id("chkRecpOrderRcvd")).click();
		driver.findElement(By.id("chkRecpPickup")).click();
		driver.findElement(By.id("chkRecpDelivery")).click();

		driver.findElement(By.id("txtRouteWorkRef1")).sendKeys("Ref1 Ship5");
		driver.findElement(By.id("txtRouteWorkRef3")).sendKeys("Ref3 Ship5");

		driver.findElement(By.id("btnaddshipment")).click();
		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("gvShipmentDetails")));

		// Click no Done.
		BtnDone = driver.findElement(By.id("btndone"));
		js.executeScript("arguments[0].scrollIntoView();", BtnDone);
		Thread.sleep(2000);
		act.moveToElement(BtnDone).click().perform();
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[@src=\"images/ajax-loader.gif\"]")));
		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("newcontent")));
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ddlStatus")));

		// Process7: Edit Pending RW + Add new shipment (3-6) + Edit stop as 2-6 from
		// 3-6 + Done

		// Search with generated RWId
		driver.findElement(By.id("ddlStatus")).sendKeys("All");
		driver.findElement(By.id("txtRouteWorkId")).clear();
		driver.findElement(By.id("txtRouteWorkId")).sendKeys(RWid1);
		driver.findElement(By.id("btnSearch")).click();
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[@src=\"images/ajax-loader.gif\"]")));
		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("currentForm")));

		// Edit RW
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(".//*[@id='dgRWList_lbEdit_0']/img")));
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath(".//*[@id='dgRWList_lbEdit_0']/img")));
		WebElement imgEdit = driver.findElement(By.xpath(".//*[@id='dgRWList_lbEdit_0']/img"));
		act.moveToElement(imgEdit).build().perform();
		js.executeScript("arguments[0].click();", imgEdit);
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[@src=\"images/ajax-loader.gif\"]")));
		try {
			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("content1")));
		} catch (Exception ContE) {
			WebDriverWait wait1 = new WebDriverWait(driver, 50);
			wait1.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("content1")));

		}

		WebElement el1 = driver.findElement(By.id("btnaddshipment"));
		js.executeScript("arguments[0].scrollIntoView();", el1);
		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("gvShipmentDetails")));

		// add new Shipment Details - 6 (3-6)
		// From
		driver.findElement(By.id("txtFromStopSeq")).clear();
		driver.findElement(By.id("txtFromStopSeq")).sendKeys("3");
		robot.keyPress(KeyEvent.VK_TAB);
		// TO
		driver.findElement(By.id("txtToStopSeq")).clear();
		driver.findElement(By.id("txtToStopSeq")).sendKeys("6");
		robot.keyPress(KeyEvent.VK_TAB);
		// Package for Shipment-6 (3-6)
		driver.findElement(By.id("pieces")).clear();
		driver.findElement(By.id("pieces")).sendKeys("1");
		driver.findElement(By.id("txtContents")).clear();
		driver.findElement(By.id("txtContents")).sendKeys("Only 26-36 Chocolate");
		driver.findElement(By.id("txtDimLen0")).clear();
		driver.findElement(By.id("txtDimLen0")).sendKeys(getRandomInteger(st));
		driver.findElement(By.id("txtDimWid0")).clear();
		driver.findElement(By.id("txtDimWid0")).sendKeys(getRandomInteger(st));
		driver.findElement(By.id("txtDimHt0")).clear();
		driver.findElement(By.id("txtDimHt0")).sendKeys(getRandomInteger(st));
		driver.findElement(By.id("txtActWt0")).clear();
		driver.findElement(By.id("txtActWt0")).sendKeys(getRandomInteger(st));
		driver.findElement(By.id("txtRouteWorkRef1")).sendKeys("Ref1 Ship6");
		driver.findElement(By.id("txtRouteWorkRef3")).sendKeys("Ref3 Ship6");
		driver.findElement(By.id("btnaddshipment")).click();
		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("gvShipmentDetails")));

		el = driver.findElement(By.id("btnaddshipment"));
		js.executeScript("arguments[0].scrollIntoView();", el);
		Thread.sleep(5000);

		// Change Sequence of shipment-6
		wait.until(
				ExpectedConditions.visibilityOfElementLocated(By.xpath(".//*[@id='gvShipmentDetails_ctl08_lbEdit']")));
		WebElement element6 = driver.findElement(By.xpath(".//*[@id='gvShipmentDetails_ctl08_lbEdit']"));
		act.moveToElement(element6).build().perform();
		act.moveToElement(element6).click().perform();
		Thread.sleep(2000);
		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("content1")));
		Thread.sleep(2000);

		driver.findElement(By.id("txtFromStopSeq")).clear();
		driver.findElement(By.id("txtFromStopSeq")).sendKeys("2");
		robot.keyPress(KeyEvent.VK_TAB);
		WebElement el6 = driver.findElement(By.id("chkRecpOrderRcvd"));
		js.executeScript("arguments[0].scrollIntoView();", el6);
		Thread.sleep(2000);
		driver.findElement(By.id("btnaddshipment")).click();
		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("gvShipmentDetails")));

		// Click on Done
		BtnDone = driver.findElement(By.id("btndone"));
		js.executeScript("arguments[0].scrollIntoView();", BtnDone);
		Thread.sleep(2000);
		act.moveToElement(BtnDone).click().perform();
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[@src=\"images/ajax-loader.gif\"]")));
		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("newcontent")));

		// Process8: Active RW + Edit + Add Ship (4-6) + change seq (3-6) + Done +
		// Active
		// Active RW
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(".//*[@id='dgRWList_lbActivate_0']/img")));
		driver.findElement(By.xpath(".//*[@id='dgRWList_lbActivate_0']/img")).click();
		Thread.sleep(2000);

		driver.switchTo().alert();
		driver.switchTo().alert().accept();
		Thread.sleep(5000);

		// Edit RW
		driver.findElement(By.xpath(".//*[@id='dgRWList_lbEdit_0']/img")).click();
		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("content1")));
		WebElement el7 = driver.findElement(By.id("btnaddshipment"));
		js.executeScript("arguments[0].scrollIntoView();", el7);
		Thread.sleep(2000);

		// add ship (4-6)
		// From
		driver.findElement(By.id("txtFromStopSeq")).clear();
		driver.findElement(By.id("txtFromStopSeq")).sendKeys("4");
		robot.keyPress(KeyEvent.VK_TAB);
		// TO
		driver.findElement(By.id("txtToStopSeq")).clear();
		driver.findElement(By.id("txtToStopSeq")).sendKeys("6");
		robot.keyPress(KeyEvent.VK_TAB);
		driver.findElement(By.id("txtRouteWorkRef1")).sendKeys("Ref1 Ship7");
		driver.findElement(By.id("txtRouteWorkRef3")).sendKeys("Ref3 Ship7");
		driver.findElement(By.id("btnaddshipment")).click();
		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("gvShipmentDetails")));

		// Change Sequence of shipment-7
		// *[@id="gvShipmentDetails_ctl06_lbEdit"]
		WebElement element7 = driver.findElement(By.xpath(".//*[@id='gvShipmentDetails_ctl09_lbEdit']"));
		act.moveToElement(element7).click().build().perform();
		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("content1")));
		driver.findElement(By.id("txtFromStopSeq")).clear();
		driver.findElement(By.id("txtFromStopSeq")).sendKeys("3");
		robot.keyPress(KeyEvent.VK_TAB);
		WebElement el77 = driver.findElement(By.id("chkRecpOrderRcvd"));
		js.executeScript("arguments[0].scrollIntoView();", el77);
		Thread.sleep(2000);

		WebElement AddShipment = driver.findElement(By.id("btnaddshipment"));
		act.moveToElement(AddShipment).build().perform();
		wait.until(ExpectedConditions.elementToBeClickable(AddShipment));
		js.executeScript("arguments[0].click();", AddShipment);
		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("gvShipmentDetails")));

		// Click on Done
		WebElement eld7 = driver.findElement(By.id("btndone"));
		js.executeScript("arguments[0].scrollIntoView();", eld7);
		BtnDone = driver.findElement(By.id("btndone"));
		js.executeScript("arguments[0].scrollIntoView();", BtnDone);
		Thread.sleep(2000);
		act.moveToElement(BtnDone).click().perform();
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[@src=\"images/ajax-loader.gif\"]")));
		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("newcontent")));
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ddlStatus")));

		// BtnDone.click();
		// Active RW
		driver.findElement(By.xpath(".//*[@id='dgRWList_lbActivate_0']/img")).click();
		Thread.sleep(2000);

		driver.switchTo().alert();
		Thread.sleep(2000);
		driver.switchTo().alert().accept();
		Thread.sleep(2000);

		// Get message after activation of RW
		String NextGen = driver.findElement(By.id("lmsg")).getText();
		System.out.println(NextGen);
		getScreenshot(driver, "CreatedRW500After7Pack");

		// Click on search
		WebElement Search = driver.findElement(By.id("btnSearch"));
		act.moveToElement(Search).build().perform();
		Search.click();
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[@src=\"images/ajax-loader.gif\"]")));
		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("currentForm")));

		// Process9: Check Recurrence

		// Verify Recurrence schedule

		// Store the current window handle
		String winHandleBefore = driver.getWindowHandle();
		// Perform the click operation that opens new window
		driver.findElement(By.xpath(".//*[@id='dgRWList_lbRWOccurance_0']/img")).click();
		// Switch to new window opened
		for (String winHandle : driver.getWindowHandles()) {
			driver.switchTo().window(winHandle);
		}
		// Perform the actions on new window
		// String ReadyTime = "08:30 AM";

		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("dgRWOccurance")));
		String ActGen1 = driver.findElement(By.xpath("//*[@id=\"dgRWOccurance\"]//tr[2]/td[6]")).getText();
		// System.out.println(ActGen1);
		// String ActGen2 =
		// driver.findElement(By.xpath("//*[@id=\"dgRWOccurance\"]//tr[3]/td[6]")).getText();
		// System.out.println(ActGen2);
		// String ActGen3 =
		// driver.findElement(By.xpath("//*[@id=\"dgRWOccurance\"]//tr[4]/td[6]")).getText();
		// System.out.println(ActGen3);

		Date SchGen1 = new Date();
		String Expdate1 = dateFormat.format(SchGen1);
		// System.out.println(Expdate1);

		/*
		 * Date SchGen2 = new Date(); Date addedSchDate1 = addDays(SchGen2, 2); String
		 * Expdate2 = dateFormat.format(addedSchDate1); // System.out.println(Expdate2);
		 * 
		 * Date SchGen3 = new Date(); Date addedSchDate2 = addDays(SchGen3, 3); String
		 * Expdate3 = dateFormat.format(addedSchDate2);
		 */
		// System.out.println(Expdate3);

		String Expdate1final = Expdate1 + RdyTime;
		// System.out.println(Expdate1final);
		/*
		 * String Expdate2final = Expdate2 + RdyTime; //
		 * System.out.println(Expdate2final); String Expdate3final = Expdate3 + RdyTime;
		 * // System.out.println(Expdate3final);
		 */
		if (ActGen1.contains(Expdate1final)) {
			/*
			 * if (ActGen2.contains(Expdate2final)) { if (ActGen3.contains(Expdate3final)) {
			 * RecMsg = "All Schedule will generate proper as per recurrence set";
			 * System.out.println(RecMsg); } }
			 */
		}

		// Close the new window, if that window no more required
		driver.close();
		// Switch back to original browser (first window)
		driver.switchTo().window(winHandleBefore);
		Thread.sleep(1000);
		// Continue with original browser (first window)

		// Send Route Work Details email
		msg.append("Route Work Id : " + RWid1 + "\n");
		msg.append("Route Work Name : " + RWName + "\n\n");

		msg.append("Message on Activation of RW : " + NextGen + "\n\n");

		msg.append("Start Date : " + stdate1 + "\n");
		msg.append("End Date : " + enddate1 + "\n");
		msg.append("Ready Time : " + RdyTime + "\n\n");

		msg.append("Recurrence Detail : " + RecType + " - " + "Every" + " " + RecValue + " " + "Day(s)" + "\n");
		msg.append("Exempt Date : " + ExemptDate + "\n");
		msg.append("Alternate Generation Date : " + AlertGenerationDate + "\n\n");

		msg.append("First Generation Dttm : " + FirstGenerationDate + " " + FirstGenerationTime + "\n\n");

		msg.append("Schedule in Order Queue : " + "\n");
		msg.append(ActGen1 + "\n");
		/*
		 * msg.append(ActGen2 + "\n"); msg.append(ActGen3 + "\n\n");
		 */
		msg.append("Recurrence Verification : " + RecMsg + "\n\n");

		msg.append("*** This is automated generated email and send through automation script" + "\n");
		String baseUrl = storage.getProperty("PREPRODURL");

		msg.append("Process URL : " + baseUrl);
		Env = storage.getProperty("Env");
		String subject = "Selenium Automation Script: " + Env + " : Route Work Details-500Packages";

		try {
			Email.sendMail(
					"ravina.prajapati@samyak.com,asharma@samyak.com,parth.doshi@samyak.com, saurabh.jain@samyak.com, himanshu.dholakia@samyak.com",
					subject, msg.toString(), "");
		} catch (Exception ex) {
			Logger.getLogger(RW500PKGSMOKE.class.getName()).log(Level.SEVERE, null, ex);
		}
		driver.quit();
	}

	public static String getRandomInteger(String st) {
		Random rn = new Random();
		int ans = rn.nextInt(98) + 1;
		st = String.valueOf(ans);
		return st;
	}

	public static Date addDays(Date d, int days) {
		d.setTime(d.getTime() + days * 1000 * 60 * 60 * 24);
		return d;
	}

	@AfterSuite
	public void end() {
		// driver.close();
		driver.quit();
	}
}

//----
/*
 * Add shipment 1=1-2 2=1-3 3=1-4 4=1-6
 * 
 * 
 * Edit shipment 4 4=1-6 --> 1-5
 * 
 * Add new shipment 5=1-6
 * 
 * add new shipment' 6=3-6
 * 
 * Edit shipment 6 6=2-6
 * 
 * Activate route
 * 
 * Add new Shipment 7=4-6
 * 
 * Edit shipment 7 7=3-6
 */