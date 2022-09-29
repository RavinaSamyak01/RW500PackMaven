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
import org.apache.log4j.Logger;
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
	public static Logger logs;

	@BeforeSuite
	public void startup() throws IOException {
		String logFilename = this.getClass().getSimpleName();
		logs = Logger.getLogger(logFilename);
		storage = new Properties();
		FileInputStream fi = new FileInputStream(".\\src\\main\\resources\\config.properties");
		storage.load(fi);

		// --Opening Chrome Browser
		DesiredCapabilities capabilities = new DesiredCapabilities();
		WebDriverManager.chromedriver().setup();
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless", "--window-size=1920,1200");
		// options.addArguments("--headless", "--window-size=1382, 744");
		// options.addArguments("window-size=1382,744");
		options.addArguments("--incognito");
		options.addArguments("--test-type");
		options.addArguments("--no-proxy-server");
		options.addArguments("--proxy-bypass-list=*");
		options.addArguments("--disable-extensions");
		options.addArguments("--no-sandbox");
		options.addArguments("enable-automation");
		options.addArguments("--dns-prefetch-disable");
		options.addArguments("--disable-gpu");
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
		 * driver.manage().window().setSize(newDimension);
		 */

		driver.manage().window().maximize();
		Dimension currentDimension = driver.manage().window().getSize();
		int height = currentDimension.getHeight();
		int width = currentDimension.getWidth();
		System.out.println("New height: " + height);
		System.out.println("New width: " + width);
		System.out.println("New window size==" + driver.manage().window().getSize());

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
					logs.error(ex);
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
					logs.error(ex);
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
					logs.error(ex);
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
					logs.error(ex);
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
		WebDriverWait wait = new WebDriverWait(driver, 50);
		Actions act = new Actions(driver);
		String logFilename = this.getClass().getSimpleName();
		logs = Logger.getLogger(logFilename);
		Robot robot = new Robot();
		JavascriptExecutor js = (JavascriptExecutor) driver;
		String Env = storage.getProperty("Env");

		mzexecutor = (JavascriptExecutor) driver;
		genData = new GenerateData();

		// --login
		login();

		try {
			// Open Menu > Submenu > Submenu -- "RW Form page"

			driver.findElement(By.linkText("Admin")).click();
			logs.info("Clicked on Admin");
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Route Work")));

			WebElement menu = driver.findElement(By.linkText("Route Work"));
			act.moveToElement(menu).perform();
			logs.info("Moved to Route Work");
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Route Work Form")));

			driver.findElement(By.linkText("Route Work Form")).click();
			logs.info("Clicked on Route work form");
			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("content1")));

			// Process2: RW Form Fill up

			// Route WorkId
			WebElement RWId = driver.findElement(By.id("txtRouteWorkId"));

			if (RWId.isEnabled()) {
				throw new Error("\nRWId is enable. It is an issue");
			}

			// Route Work Name

			driver.findElement(By.id("txtrouteworkdescription")).sendKeys("Route Name Automation ");
			logs.info("Enter RW Description");
			driver.findElement(By.id("txtrouteworkdescription")).sendKeys(genData.generateRandomNumber(7));
			String RWName = driver.findElement(By.id("txtrouteworkdescription")).getAttribute("value");
			System.out.println(RWName);
			logs.info("RWName==" + RWName);

			// --Enter customer

			if (Env.equalsIgnoreCase("STG")) {
				driver.findElement(By.id("txtCustCode")).sendKeys("117117117");
				driver.findElement(By.id("txtCustCode")).sendKeys(Keys.TAB);
				logs.info("Enter Customer Code");

			} else if (Env.equalsIgnoreCase("Pre-Prod")) {
				driver.findElement(By.id("txtCustCode")).sendKeys("117117117");
				driver.findElement(By.id("txtCustCode")).sendKeys(Keys.TAB);
				logs.info("Enter Customer Code");

			} else if (Env.equalsIgnoreCase("Prod")) {
				driver.findElement(By.id("txtCustCode")).sendKeys("777777777");
				driver.findElement(By.id("txtCustCode")).sendKeys(Keys.TAB);
				logs.info("Enter Customer Code");

			}

			// Declare Value
			driver.findElement(By.id("declared_value")).clear();
			driver.findElement(By.id("declared_value")).sendKeys("444");
			logs.info("Enter Declared Value");

			// Route Work Reference#2 and #4
			driver.findElement(By.id("txtRouteWorkRef2")).sendKeys("reference2");
			driver.findElement(By.id("txtRouteWorkRef4")).sendKeys("reference4");
			logs.info("Enter Reference 2 and 4");

			// Executed By
			driver.findElement(By.id("rdbNGL")).click();
			logs.info("Clicked on Executed By");

			// Generate Route(No. of hours)
			driver.findElement(By.id("txtGenerateRoute")).clear();
			driver.findElement(By.id("txtGenerateRoute")).sendKeys("1");
			logs.info("Enter Generate Route 1");

			// Preferred Courier Route
			driver.findElement(By.id("txtPrefCourierRoute")).sendKeys("PCRoute001");
			logs.info("Enter Courier Route");

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
			logs.info("Enter Start Date");

			driver.findElement(By.id("txtEndDate")).sendKeys(enddate1);
			logs.info("Enter End Date");

			// driver.findElement(By.xpath(".//*[@id='row1']/img")).click();
			// driver.findElement(By.xpath("//a[contains(.,'Today')]")).click();
			// sleep(500);
			// driver.findElement(By.xpath(".//*[@id='row3']/img")).click();
			// driver.findElement(By.xpath("//a[contains(.,'Today')]")).click();

			// Ready-Due Time
			driver.findElement(By.id("ReadyHourDropDownList")).sendKeys("8");
			logs.info("Enter Ready Hour");

			driver.findElement(By.id("ReadyMinuteDropDownList")).sendKeys("30");
			logs.info("Enter Ready Minute");

			driver.findElement(By.id("DueHourDropDownList")).sendKeys("2");
			logs.info("Enter Due Hour");

			driver.findElement(By.id("DueMinuteDropDownList")).sendKeys("45");
			logs.info("Enter Due Minute");

			driver.findElement(By.id("DueFormatDropDownList")).sendKeys("PM");
			logs.info("Enter Due Format");

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
				logs.info("Ready Time: " + RdyTime);

			} else {
				int RdyHH = RdyHH2;

				String RdyMM = driver.findElement(By.id("ReadyMinuteDropDownList")).getAttribute("value");
				// System.out.println(RdyMM);
				String RdyAP = driver.findElement(By.id("ReadyFormatDropDownList")).getAttribute("value");
				// System.out.println(RdyAP);

				RdyTime = RdyHH + ":" + RdyMM + " " + RdyAP;
				System.out.println("Ready Time: " + RdyTime);
				logs.info("Ready Time: " + RdyTime);

			}

			// System.out.println(RdyTime);

			// Recurrence
			driver.findElement(By.id("rdbtrecurrence_0")).click();
			logs.info("Clicked on Recurrence");
			driver.findElement(By.id("txtDaysDaily")).sendKeys("2");
			logs.info("Enter 2 in Daily");
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
			logs.info("Enter Exempt Date");

			String ExemptDate = driver.findElement(By.id("txtExemptDateDaily")).getAttribute("value");

			// AlterGenerateDate
			Date altdate = new Date();
			// --changed Add days date from 5 to 1//--Ravina
			Date addedDate3 = addDays(altdate, 2);
			String altdate1 = dateFormat.format(addedDate3);
			// System.out.println(altdate1);

			driver.findElement(By.id("txtAlternateGenerationDateDaily")).sendKeys(altdate1);
			logs.info("Enter Alternate Date");
			String AlertGenerationDate = driver.findElement(By.id("txtAlternateGenerationDateDaily"))
					.getAttribute("value");

			// First Generation
			Date fgdate = new Date();
			String fgdate1 = dateFormat.format(fgdate);
			// System.out.println(fgdate1);

			driver.findElement(By.id("txtDate")).sendKeys(fgdate1);
			logs.info("Enter First Generation Date");
			String FirstGenerationDate = driver.findElement(By.id("txtDate")).getAttribute("value");

			driver.findElement(By.id("ddlHoursTime")).sendKeys("12");
			logs.info("Enter Hour");

			driver.findElement(By.id("ddlMinuteTime")).sendKeys("10");
			logs.info("Enter Minute");

			driver.findElement(By.id("ddlTimeMinute")).sendKeys("AM");
			logs.info("Enter Am/PM");

			String fghh = driver.findElement(By.id("ddlHoursTime")).getAttribute("value");
			String fgmm = driver.findElement(By.id("ddlMinuteTime")).getAttribute("value");
			String fgampm = driver.findElement(By.id("ddlTimeMinute")).getAttribute("value");

			String FirstGenerationTime = fghh + ":" + fgmm + " " + fgampm;

			// Manifest
			driver.findElement(By.id("txtRouteWorkEmail")).sendKeys("pdoshi1@samyak.com");
			logs.info("Enter Route Work Email");

			// Flat Rate
			driver.findElement(By.id("txtFlatRate")).sendKeys("234.56");
			logs.info("Enter Flat Rate");

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
			logs.info("Enter Packages");
			WebElement webElementRpcs = driver.findElement(By.id("routepieces"));
			webElementRpcs.sendKeys(Keys.TAB);
			Thread.sleep(2000);

			driver.findElement(By.id("routerdbNo")).click();
			logs.info("Click on Route No");
			Thread.sleep(2000);

			driver.findElement(By.id("txtrouteContents")).clear();
			driver.findElement(By.id("txtrouteContents")).sendKeys("Invitation Letter");
			logs.info("Enter Content");
			Thread.sleep(1000);

			for (i = 0; i < RWpcs; i++) {
				driver.findElement(By.id("txtRouteQty" + i)).clear();
				driver.findElement(By.id("txtRouteQty" + i)).sendKeys("1");
				logs.info("Enter Route Qty");

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
					logs.info("Click on Next button");
					Thread.sleep(2000);
				}

			}

			// Return Un-deliverable Shipments To
			driver.findElement(By.id("txtUDCompany")).sendKeys("JOHN COMP");
			logs.info("Enter DL Company");
			driver.findElement(By.id("txtUDContact")).sendKeys("JOHN STARVIS");
			logs.info("Enter DL Contact");
			driver.findElement(By.id("txtUDAddr1")).sendKeys("9011 CAPITAL KING, 7TH STRT");
			logs.info("Enter DL Address 1");
			driver.findElement(By.id("txtUDAddr2")).sendKeys("No#771");
			logs.info("Enter DL Address 2");
			driver.findElement(By.id("txtUDzip")).sendKeys("90003");
			logs.info("Enter DL ZipCode");
			robot.keyPress(KeyEvent.VK_TAB);
			Thread.sleep(2000);
			driver.findElement(By.id("txtUDPhone")).sendKeys("4115116110");
			logs.info("Enter DL Phone");
			driver.findElement(By.id("txtUDDeliveryInst")).sendKeys("Please call before 4h of delivery process");
			logs.info("Enter DL Instruction");

			// Process3: Add Shipments (1-2,1-3,1-4,1-6)

			// Shipment Details - 1
			// From
			logs.info("Enter Shipment Details-1");
			driver.findElement(By.id("txtFromStopSeq")).sendKeys("1");
			logs.info("Enter From Stop Seq");
			driver.findElement(By.id("txtFromCompany")).sendKeys("CREATIVE ARTIST AGENCY");
			logs.info("Enter From Company");
			driver.findElement(By.id("txtFromContact")).sendKeys("Client Trust");
			logs.info("Enter From Contact");
			driver.findElement(By.id("txtFromAddr1")).sendKeys("2000 AVENUE OF THE STARS");
			logs.info("Enter From Address");
			driver.findElement(By.id("txtFromAddr2")).sendKeys("");
			logs.info("Enter From Address 2");
			driver.findElement(By.id("txtFromZip")).clear();
			driver.findElement(By.id("txtFromZip")).sendKeys("90067");
			robot.keyPress(KeyEvent.VK_TAB);
			driver.findElement(By.id("txtFromZip")).sendKeys(Keys.TAB);
			Thread.sleep(2000);
			logs.info("Enter From Zip");
			driver.findElement(By.id("txtFromPhone")).sendKeys("(424) 288-2125");
			logs.info("Enter From Phone");
			driver.findElement(By.id("txtPUInst")).sendKeys("Art Work");
			logs.info("Enter From Instruction");
			driver.findElement(By.id("txtShipperEmail")).sendKeys("pdoshi1@samyak.com");
			logs.info("Enter Shipper Email");
			driver.findElement(By.id("chkShpOrderRcvd")).click();
			logs.info("Click on Shipper Order RCVD Checkbox");
			driver.findElement(By.id("chkShpPickup")).click();
			logs.info("Click on Ship Pickup");
			driver.findElement(By.id("chkShpDelivery")).click();
			logs.info("Click on Ship Delivery");

			driver.findElement(By.id("txtToStopSeq")).sendKeys("2");
			logs.info("Enter To StopSeq");
			driver.findElement(By.id("txtToCompany")).sendKeys("SHOWTIME");
			logs.info("Enter To Company");
			driver.findElement(By.id("txtToContact")).sendKeys("GARY LEVINE");
			logs.info("Enter To Contact");
			driver.findElement(By.id("txtToAddr1")).sendKeys("10880 WHILSHIRE BLVD");
			logs.info("Enter To Address 1");
			driver.findElement(By.id("txtToAddr2")).sendKeys("#1600");
			logs.info("Enter To Address 2");
			driver.findElement(By.id("txtToZip")).clear();
			driver.findElement(By.id("txtToZip")).sendKeys("90024");
			logs.info("Enter To Zip");
			robot.keyPress(KeyEvent.VK_TAB);
			driver.findElement(By.id("txtToZip")).sendKeys(Keys.TAB);
			Thread.sleep(2000);
			driver.findElement(By.id("txtToPhone")).sendKeys("(424) 288-2125");
			logs.info("Enter To Phone");
			driver.findElement(By.id("txtDelInst")).sendKeys("Art Work");
			logs.info("Enter To Del Instruction");
			driver.findElement(By.id("txtRecipientEmail")).sendKeys("pdoshi2@samyak.com");
			logs.info("Enter To Recipient Email");
			driver.findElement(By.id("chkRecpOrderRcvd")).click();
			logs.info("Click on Ship Recp Order RCvd");
			driver.findElement(By.id("chkRecpQDTChange")).click();
			logs.info("Click on Recp QDT Changey");
			driver.findElement(By.id("chkRecpException")).click();
			logs.info("Click on Recp Exception");

			// Package for Shipment-1
			logs.info("Enter Package for Shipment 1");
			driver.findElement(By.id("pieces")).clear();
			driver.findElement(By.id("pieces")).sendKeys("1");
			logs.info("Enter Pieces");
			Thread.sleep(1000);
			driver.findElement(By.id("txtContents")).clear();
			driver.findElement(By.id("txtContents")).sendKeys("FLOWERS with Chocolate");
			logs.info("Enter Contents");
			Thread.sleep(1000);
			driver.findElement(By.id("txtDimLen0")).clear();
			driver.findElement(By.id("txtDimLen0")).sendKeys(getRandomInteger(st));
			logs.info("Enter Len");
			driver.findElement(By.id("txtDimWid0")).clear();
			driver.findElement(By.id("txtDimWid0")).sendKeys(getRandomInteger(st));
			logs.info("Enter Wid");
			driver.findElement(By.id("txtDimHt0")).clear();
			driver.findElement(By.id("txtDimHt0")).sendKeys(getRandomInteger(st));
			logs.info("Enter Ht");
			driver.findElement(By.id("txtActWt0")).clear();
			driver.findElement(By.id("txtActWt0")).sendKeys(getRandomInteger(st));
			logs.info("Enter Act Wt");
			driver.findElement(By.id("txtRouteWorkRef1")).sendKeys("Ref1 Ship1");
			logs.info("Enter Ref 1");
			driver.findElement(By.id("txtRouteWorkRef3")).sendKeys("Ref3 Ship1");
			logs.info("Enter Ref 3");

			try {
				driver.findElement(By.id("btnaddshipment")).click();
				logs.info("Click on Add Shipment");
				logs.info("1st shipment added, Stop 1-2");

				WebElement EditShip = driver.findElement(By.id("btnDownEditStops"));
				js.executeScript("arguments[0].scrollIntoView();", EditShip);
				logs.info("Scrollto Edit STop");
				Thread.sleep(2000);
				wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("gvShipmentDetails")));
			} catch (Exception AddShipmente) {
				logs.error(AddShipmente);
				// --Scroll up
				js.executeScript("window.scrollBy(0,-250)", "");
				js.executeScript("scroll(0, -250);");
				Thread.sleep(2000);
				getScreenshot(driver, "AddShipmentissue1_1");

				// --Scroll Down
				js.executeScript("window.scrollBy(0,250)", "");
				js.executeScript("scroll(0, 250);");
				Thread.sleep(2000);
				getScreenshot(driver, "AddShipmentissue2_2");

				WebElement BTNAddShipment = driver.findElement(By.id("btnaddshipment"));
				wait.until(ExpectedConditions.visibilityOf(BTNAddShipment));
				act.moveToElement(BTNAddShipment).build().perform();
				wait.until(ExpectedConditions.elementToBeClickable(BTNAddShipment));
				js.executeScript("arguments[0].click();", BTNAddShipment);
				logs.info("Click on Add Shipment");
				logs.info("1st shipment added, Stop 1-2");

				WebElement EditShip = driver.findElement(By.id("btnDownEditStops"));
				js.executeScript("arguments[0].scrollIntoView();", EditShip);
				logs.info("Scrollto Edit STop");
				Thread.sleep(2000);
				wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("gvShipmentDetails")));

			}

			WebElement ShipmentDetails = driver.findElement(By.id("module.shipment._header"));
			js.executeScript("arguments[0].scrollIntoView();", ShipmentDetails);
			logs.info("Scrollto ShipmentDetail");
			Thread.sleep(5000);

			// Shipment Details - 2
			logs.info("Shipment Details 2");
			// From
			driver.findElement(By.id("txtFromStopSeq")).clear();
			driver.findElement(By.id("txtFromStopSeq")).sendKeys("1");
			logs.info("Enter From Stop 1");
			robot.keyPress(KeyEvent.VK_TAB);
			Thread.sleep(2000);
			// TO
			driver.findElement(By.id("txtToStopSeq")).clear();
			driver.findElement(By.id("txtToStopSeq")).sendKeys("3");
			logs.info("Enter To stop 3");
			driver.findElement(By.id("txtToCompany")).sendKeys("PROTOCOL ENTERTAINMENT");
			logs.info("Enter To Company");
			driver.findElement(By.id("txtToContact")).sendKeys("JARRY CROSS");
			logs.info("Enter To Contact");
			driver.findElement(By.id("txtToAddr1")).sendKeys("16128 SHERMAN WAY");
			logs.info("Enter To Address 1");
			driver.findElement(By.id("txtToAddr2")).clear();
			driver.findElement(By.id("txtToZip")).clear();
			driver.findElement(By.id("txtToZip")).sendKeys("91406");
			logs.info("Enter To Zip");
			robot.keyPress(KeyEvent.VK_TAB);
			driver.findElement(By.id("txtToZip")).sendKeys(Keys.TAB);
			Thread.sleep(2000);
			driver.findElement(By.id("txtToPhone")).sendKeys("(424) 288-2125");
			logs.info("Enter To Phone");
			driver.findElement(By.id("txtDelInst")).sendKeys("Art Work");
			logs.info("Enter To Del Instruction");
			driver.findElement(By.id("txtRecipientEmail")).sendKeys("pdoshi@samyak.com");
			logs.info("Enter To Recipient Email");
			driver.findElement(By.id("chkRecpOrderRcvd")).click();
			logs.info("Click on Reco Order Rcvd");

			// Package for Shipment-2 Taken from Master
			driver.findElement(By.id("txtRouteWorkRef1")).sendKeys("Ref1 Ship2");
			logs.info("Enter To Ref 1");
			driver.findElement(By.id("txtRouteWorkRef3")).sendKeys("Ref3 Ship2");
			logs.info("Enter To Ref 3");
			try {
				driver.findElement(By.id("btnaddshipment")).click();
				logs.info("Click on Add Shipment");
				logs.info("2nd shipment added, Stop 1-3");

				WebElement EditShip = driver.findElement(By.id("btnDownEditStops"));
				js.executeScript("arguments[0].scrollIntoView();", EditShip);
				logs.info("Scrollto Edit STop");
				Thread.sleep(2000);
				wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("gvShipmentDetails")));
			} catch (Exception AddShipmente) {
				logs.error(AddShipmente);
				// --Scroll up
				js.executeScript("window.scrollBy(0,-250)", "");
				js.executeScript("scroll(0, -250);");
				Thread.sleep(2000);
				getScreenshot(driver, "AddShipmentissue1_2");

				// --Scroll Down
				js.executeScript("window.scrollBy(0,250)", "");
				js.executeScript("scroll(0, 250);");
				Thread.sleep(2000);
				getScreenshot(driver, "AddShipmentissue2_2");

				WebElement BTNAddShipment = driver.findElement(By.id("btnaddshipment"));
				wait.until(ExpectedConditions.visibilityOf(BTNAddShipment));
				act.moveToElement(BTNAddShipment).build().perform();
				wait.until(ExpectedConditions.elementToBeClickable(BTNAddShipment));
				js.executeScript("arguments[0].click();", BTNAddShipment);
				logs.info("Click on Add Shipment");
				logs.info("2nd shipment added, Stop 1-3");

				WebElement EditShip = driver.findElement(By.id("btnDownEditStops"));
				js.executeScript("arguments[0].scrollIntoView();", EditShip);
				logs.info("Scrollto Edit STop");
				Thread.sleep(2000);
				wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("gvShipmentDetails")));

			}
			ShipmentDetails = driver.findElement(By.id("module.shipment._header"));
			js.executeScript("arguments[0].scrollIntoView();", ShipmentDetails);
			Thread.sleep(5000);
			logs.info("Scroll to ShipmentDetails");

			// Shipment Details - 3
			// From
			driver.findElement(By.id("txtFromStopSeq")).clear();
			driver.findElement(By.id("txtFromStopSeq")).sendKeys("1");
			logs.info("Enter Fro Stop Seq 1");
			robot.keyPress(KeyEvent.VK_TAB);
			Thread.sleep(2000);
			// TO
			driver.findElement(By.id("txtToStopSeq")).clear();
			driver.findElement(By.id("txtToStopSeq")).sendKeys("4");
			logs.info("Enter To Stop seq 4");
			driver.findElement(By.id("txtToCompany")).sendKeys("HBO");
			logs.info("Enter To Company");
			driver.findElement(By.id("txtToContact")).sendKeys("MR. MICHAEL LOMBARDO");
			logs.info("Enter To Contact");
			driver.findElement(By.id("txtToAddr1")).sendKeys("2500 BROADWAY");
			logs.info("Enter To Address 1");
			driver.findElement(By.id("txtToAddr2")).sendKeys("#400");
			logs.info("Enter To Address 2");
			driver.findElement(By.id("txtToZip")).clear();
			driver.findElement(By.id("txtToZip")).sendKeys("91404");
			logs.info("Enter To Zip");
			robot.keyPress(KeyEvent.VK_TAB);
			driver.findElement(By.id("txtToZip")).sendKeys(Keys.TAB);
			Thread.sleep(2000);
			driver.findElement(By.id("txtToPhone")).sendKeys("(424) 288-2125");
			logs.info("Enter To Phone");
			driver.findElement(By.id("txtDelInst")).sendKeys("Art Work");
			logs.info("Enter To Del Instruction");
			driver.findElement(By.id("txtRecipientEmail")).sendKeys("pdoshi@samyak.com");
			logs.info("Enter To Recipient Email");
			driver.findElement(By.id("chkRecpOrderRcvd")).click();
			logs.info("Click on Recp Order Rcvd");
			driver.findElement(By.id("chkRecpQDTChange")).click();
			logs.info("Click on Recp QDT Change");
			driver.findElement(By.id("chkRecpException")).click();
			logs.info("Click on Recp Exception");

// Package for Shipment-3
			driver.findElement(By.id("pieces")).clear();
			driver.findElement(By.id("pieces")).sendKeys("1");
			logs.info("Enter To Zip");
			driver.findElement(By.id("txtContents")).clear();
			driver.findElement(By.id("txtContents")).sendKeys("Only Chocolate");
			logs.info("Enter To Zip");
			Thread.sleep(500);
			driver.findElement(By.id("txtDimLen0")).clear();
			driver.findElement(By.id("txtDimLen0")).sendKeys(getRandomInteger(st));
			logs.info("Enter Len");
			driver.findElement(By.id("txtDimWid0")).clear();
			driver.findElement(By.id("txtDimWid0")).sendKeys(getRandomInteger(st));
			logs.info("Enter Wt");
			driver.findElement(By.id("txtDimHt0")).clear();
			driver.findElement(By.id("txtDimHt0")).sendKeys(getRandomInteger(st));
			logs.info("Enter Ht");
			driver.findElement(By.id("txtActWt0")).clear();
			driver.findElement(By.id("txtActWt0")).sendKeys(getRandomInteger(st));
			logs.info("Enter Act Wt");
			driver.findElement(By.id("txtRouteWorkRef1")).sendKeys("Ref1 Ship3");
			logs.info("Enter To Ref 1");
			driver.findElement(By.id("txtRouteWorkRef3")).sendKeys("Ref3 Ship3");
			logs.info("Enter Ref 3");
			try {
				driver.findElement(By.id("btnaddshipment")).click();
				logs.info("Click on Add Shipment");
				logs.info("3rd shipment added, Stop 1-4");

				WebElement EditShip = driver.findElement(By.id("btnDownEditStops"));
				js.executeScript("arguments[0].scrollIntoView();", EditShip);
				logs.info("Scrollto Edit STop");
				Thread.sleep(2000);
				wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("gvShipmentDetails")));
			} catch (Exception AddShipmente) {
				logs.error(AddShipmente);
				// --Scroll up
				js.executeScript("window.scrollBy(0,-250)", "");
				js.executeScript("scroll(0, -250);");
				Thread.sleep(2000);
				getScreenshot(driver, "AddShipmentissue1_3");

				// --Scroll Down
				js.executeScript("window.scrollBy(0,250)", "");
				js.executeScript("scroll(0, 250);");
				Thread.sleep(2000);
				getScreenshot(driver, "AddShipmentissue2_3");

				WebElement BTNAddShipment = driver.findElement(By.id("btnaddshipment"));
				wait.until(ExpectedConditions.visibilityOf(BTNAddShipment));
				act.moveToElement(BTNAddShipment).build().perform();
				wait.until(ExpectedConditions.elementToBeClickable(BTNAddShipment));
				js.executeScript("arguments[0].click();", BTNAddShipment);
				logs.info("Click on Add Shipment");
				logs.info("3rd shipment added, Stop 1-4");

				WebElement EditShip = driver.findElement(By.id("btnDownEditStops"));
				js.executeScript("arguments[0].scrollIntoView();", EditShip);
				logs.info("Scrollto Edit STop");
				Thread.sleep(2000);
				wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("gvShipmentDetails")));

			}
			ShipmentDetails = driver.findElement(By.id("module.shipment._header"));
			js.executeScript("arguments[0].scrollIntoView();", ShipmentDetails);
			Thread.sleep(5000);
			logs.info("Scroll to ShipmentDetails");

			// Shipment Details - 4
			logs.info("Shipment Details 4");
			// From
			driver.findElement(By.id("txtFromStopSeq")).clear();
			driver.findElement(By.id("txtFromStopSeq")).sendKeys("1");
			driver.findElement(By.id("txtFromStopSeq")).sendKeys(Keys.TAB);
			logs.info("Enter from stop seq 1");
			robot.keyPress(KeyEvent.VK_TAB);
			Thread.sleep(2000);
			// TO
			driver.findElement(By.id("txtToStopSeq")).clear();
			driver.findElement(By.id("txtToStopSeq")).sendKeys("6");
			logs.info("Enter To Stop seq 6");
			driver.findElement(By.id("txtToCompany")).sendKeys("ORLY ADELSON PRODUCTIONS");
			logs.info("Enter To Compnay");
			driver.findElement(By.id("txtToContact")).sendKeys("SQUINTAR FILMS INC");
			logs.info("Enter To Contact");
			driver.findElement(By.id("txtToAddr1")).sendKeys("2900 OLYMPIC BLVD");
			logs.info("Enter To Address 1");
			driver.findElement(By.id("txtToAddr2")).clear();
			driver.findElement(By.id("txtToZip")).sendKeys("91404");
			logs.info("Enter To Zip");
			robot.keyPress(KeyEvent.VK_TAB);
			driver.findElement(By.id("txtToZip")).sendKeys(Keys.TAB);
			Thread.sleep(2000);
			driver.findElement(By.id("txtToPhone")).sendKeys("(424) 288-2125");
			logs.info("Enter To Phone");
			driver.findElement(By.id("txtDelInst")).sendKeys("Art Work");
			logs.info("Enter To Del Instr");
			driver.findElement(By.id("txtRecipientEmail")).sendKeys("pdoshi@samyak.com");
			logs.info("Enter To Recipient Email");
			driver.findElement(By.id("chkRecpOrderRcvd")).click();
			logs.info("Click on Recp Order Rcvd");

			// Package for Shipment-4
			js.executeScript("window.scrollBy(0,-25)", "");
			String shppcs = formatter.formatCellValue(shCR.getRow(1).getCell(1));
			SHPpcs = Integer.parseInt(shppcs);
			driver.findElement(By.id("pieces")).clear();
			driver.findElement(By.id("pieces")).sendKeys(shppcs);
			logs.info("Enter Pieces");
			WebElement webElementshppcs = driver.findElement(By.id("pieces"));
			webElementshppcs.sendKeys(Keys.TAB);
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath(".//*[@id='rdbNo']")));
			WebElement rdbNo = driver.findElement(By.xpath(".//*[@id='rdbNo']"));
			act.moveToElement(rdbNo).click().perform();
			logs.info("Click on RDB No");
			Thread.sleep(1000);
			driver.findElement(By.id("txtContents")).clear();
			driver.findElement(By.id("txtContents")).sendKeys("Chocolate Voucher");
			logs.info("Enter Contents");
			Thread.sleep(2000);

			for (i = 0; i < SHPpcs; i++) {
				wait.until(ExpectedConditions.elementToBeClickable(By.id("txtQty" + i)));
				WebElement Qty = driver.findElement(By.id("txtQty" + i));
				act.moveToElement(Qty).build().perform();
				driver.findElement(By.id("txtQty" + i)).clear();
				driver.findElement(By.id("txtQty" + i)).sendKeys("1");
				logs.info("Enter Qty");

				driver.findElement(By.id("txtDimLenN" + i)).clear();
				driver.findElement(By.id("txtDimLenN" + i)).sendKeys(getRandomInteger(st));
				logs.info("Enter Len");
				driver.findElement(By.id("txtDimWidN" + i)).clear();
				driver.findElement(By.id("txtDimWidN" + i)).sendKeys(getRandomInteger(st));
				logs.info("Enter Wid");
				driver.findElement(By.id("txtDimHtN" + i)).clear();
				driver.findElement(By.id("txtDimHtN" + i)).sendKeys(getRandomInteger(st));
				logs.info("Enter Ht");
				driver.findElement(By.id("txtActWtNew" + i)).clear();
				driver.findElement(By.id("txtActWtNew" + i)).sendKeys(getRandomInteger(st));
				logs.info("Enter Act Wt");

				int cvl = i + 1;
				int divisor = 10;

				if (cvl % divisor == 0) {
					driver.findElement(By.id("idNext")).click();
					logs.info("Click on Next button");
					Thread.sleep(2000);
				}

			}

			driver.findElement(By.id("txtRouteWorkRef1")).sendKeys("Ref1 Ship4");
			logs.info("Enter Ref 1");
			driver.findElement(By.id("txtRouteWorkRef3")).sendKeys("Ref3 Ship4");
			logs.info("Enter Ref 3");
			try {
				driver.findElement(By.id("btnaddshipment")).click();
				logs.info("Click on Add Shipment");
				logs.info("4th shipment added, Stop 1-6");

				WebElement EditShip = driver.findElement(By.id("btnDownEditStops"));
				js.executeScript("arguments[0].scrollIntoView();", EditShip);
				logs.info("Scrollto Edit STop");
				Thread.sleep(2000);
				wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("gvShipmentDetails")));
			} catch (Exception AddShipmente) {
				logs.error(AddShipmente);
				// --Scroll up
				js.executeScript("window.scrollBy(0,-250)", "");
				js.executeScript("scroll(0, -250);");
				Thread.sleep(2000);
				getScreenshot(driver, "AddShipmentissue1_4");

				// --Scroll Down
				js.executeScript("window.scrollBy(0,250)", "");
				js.executeScript("scroll(0, 250);");
				Thread.sleep(2000);
				getScreenshot(driver, "AddShipmentissue2_4");

				WebElement BTNAddShipment = driver.findElement(By.id("btnaddshipment"));
				wait.until(ExpectedConditions.visibilityOf(BTNAddShipment));
				act.moveToElement(BTNAddShipment).build().perform();
				wait.until(ExpectedConditions.elementToBeClickable(BTNAddShipment));
				js.executeScript("arguments[0].click();", BTNAddShipment);
				logs.info("Click on Add Shipment");
				logs.info("4th shipment added, Stop 1-6");

				WebElement EditShip = driver.findElement(By.id("btnDownEditStops"));
				js.executeScript("arguments[0].scrollIntoView();", EditShip);
				logs.info("Scrollto Edit STop");
				Thread.sleep(2000);
				wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("gvShipmentDetails")));

			}
			// Process4: Check validation on Save for missing shipment sequence

			// Click on Done for validation
			WebElement BtnDone = driver.findElement(By.id("btndone"));
			js.executeScript("arguments[0].scrollIntoView();", BtnDone);
			Thread.sleep(2000);
			logs.info("Scroll down to Done btn");
			act.moveToElement(BtnDone).click().perform();
			logs.info("Click on DOne button");

			wait.until(
					ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[@src=\"images/ajax-loader.gif\"]")));
			try {
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("lblShipmentCountErr")));
				String DoneValidation = driver.findElement(By.id("lblShipmentCountErr")).getText();
				System.out.println("ActualMsg==" + DoneValidation);
				logs.info("ActualMsg==" + DoneValidation);

				String Val1 = "Shipment Stop Sequence is missing. Please include all stops in sequence to generate route.";

				if (DoneValidation.equals(Val1)) {
					System.out.println("Display this validation when seq not proper: " + DoneValidation);
					logs.info("Display this validation when seq not proper: " + DoneValidation);

				} else {
					throw new Error("\nStop sequence validation not proper");

				}
			} catch (Exception ValMsg) {
				System.out.println("Validation for sequence is not proper is not displayed.");
				logs.info("Validation for sequence is not proper is not displayed.");

			}

			// Process5: Create RW with Draft

			// Click SaveforLater for Draft

			try {
				WebElement SaveForLater = driver.findElement(By.id("btnsaveforlater"));
				wait.until(ExpectedConditions.elementToBeClickable(SaveForLater));
				js.executeScript("arguments[0].click();", SaveForLater);
				logs.info("Click on Save For Later button");
				Thread.sleep(5000);

				wait.until(ExpectedConditions
						.invisibilityOfElementLocated(By.xpath("//*[@src=\"images/ajax-loader.gif\"]")));
				wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("currentForm")));

				// Get Generated RWId
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("lmsg")));
			} catch (Exception SaveorLaterE) {
				logs.error(SaveorLaterE);
				// --Scroll up
				js.executeScript("window.scrollBy(0,-250)", "");
				js.executeScript("scroll(0, -250);");
				Thread.sleep(2000);
				getScreenshot(driver, "SaveForLater_1");

				// --Scroll Down
				js.executeScript("window.scrollBy(0,250)", "");
				js.executeScript("scroll(0, 250);");
				Thread.sleep(2000);
				getScreenshot(driver, "SaveForLater_2");

				WebElement SaveForLater = driver.findElement(By.id("btnsaveforlater"));
				wait.until(ExpectedConditions.elementToBeClickable(SaveForLater));
				js.executeScript("arguments[0].click();", SaveForLater);
				logs.info("Click on Save For Later button");
				Thread.sleep(5000);

				wait.until(ExpectedConditions
						.invisibilityOfElementLocated(By.xpath("//*[@src=\"images/ajax-loader.gif\"]")));
				wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("currentForm")));

				// Get Generated RWId
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("lmsg")));
			}

			String RWIdtext = driver.findElement(By.id("lmsg")).getText();
			logs.info("Message==" + RWIdtext);
			getScreenshot(driver, "CreatedRW500");

			String[] RWSplit = RWIdtext.split(" ");
			String RWid = RWSplit[5];

			String RWid1 = RWid.substring(0, 10);

			System.out.println("RouteWorkId: " + RWid1);
			logs.info("RouteWorkId: " + RWid1);

			// Process6: Edit Draft RW + Edit Stop Seq + Add Shipment + Done

			/*
			 * // Search with generated RWId
			 * driver.findElement(By.id("ddlStatus")).sendKeys("All");
			 * logs.info("Select ALL from status dropdown"); Thread.sleep(2000);
			 * driver.findElement(By.id("txtRouteWorkId")).sendKeys(RWid1);
			 * logs.info("Enter RWID"); Thread.sleep(2000); WebElement BTnSearch =
			 * driver.findElement(By.id("btnSearch"));
			 * wait.until(ExpectedConditions.elementToBeClickable(BTnSearch));
			 * js.executeScript("arguments[0].click();", BTnSearch);
			 * logs.info("Click on Search button"); Thread.sleep(5000);
			 * 
			 * // Edit RW try { wait.until(
			 * ExpectedConditions.visibilityOfElementLocated(By.xpath(
			 * ".//*[@id='dgRWList_lbEdit_0']/img"))); } catch (Exception EditVis) {
			 * WebDriverWait wait1 = new WebDriverWait(driver, 60); wait1.until(
			 * ExpectedConditions.visibilityOfElementLocated(By.xpath(
			 * ".//*[@id='dgRWList_lbEdit_0']/img")));
			 * 
			 * } WebElement Edit0 =
			 * driver.findElement(By.xpath(".//*[@id='dgRWList_lbEdit_0']/img"));
			 * act.moveToElement(Edit0).build().perform();
			 * wait.until(ExpectedConditions.elementToBeClickable(Edit0));
			 * js.executeScript("arguments[0].click();", Edit0);
			 * logs.info("Click on Edit button");
			 * 
			 * wait.until( ExpectedConditions.invisibilityOfElementLocated(By.xpath(
			 * "//*[@src=\"images/ajax-loader.gif\"]")));
			 * wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id(
			 * "content1")));
			 * 
			 * WebElement el = driver.findElement(By.id("btnaddshipment"));
			 * js.executeScript("arguments[0].scrollIntoView();", el); Thread.sleep(5000);
			 * logs.info("Scroll to Add Shipment");
			 */

			// Search with generated RWId
			driver.findElement(By.id("ddlStatus")).sendKeys("All");
			Thread.sleep(2000);
			driver.findElement(By.id("txtRouteWorkId")).sendKeys(RWid1);
			Thread.sleep(2000);
			WebElement Search = driver.findElement(By.id("btnSearch"));
			wait.until(ExpectedConditions.elementToBeClickable(Search));
			act.moveToElement(Search).click().build().perform();
			System.out.println("Click on Search button");
			Thread.sleep(5000);
			wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("load")));
			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("currentForm")));
			getScreenshot(driver, "CreatedRW");

			// Edit RW
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(".//*[@id='dgRWList_lbEdit_0']/img")));
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath(".//*[@id='dgRWList_lbEdit_0']/img")));
			driver.findElement(By.xpath(".//*[@id='dgRWList_lbEdit_0']/img")).click();
			System.out.println("Click on Edit button");
			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("content1")));

			// js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
			js.executeScript("window.scrollBy(0,250)");
			Thread.sleep(1000);

			// Change Sequence of shipment-4
			// driver.findElement(By.xpath(".//*[@id='gvShipmentDetails']/tbody/tr[0]/td[5]/a")).click();

			WebElement el = driver.findElement(By.id("btnaddshipment"));
			js.executeScript("arguments[0].scrollIntoView();", el);
			Thread.sleep(5000);
			/*
			 * WebElement Edit = driver.findElement(By.id("btnDownEditStops"));
			 * act.moveToElement(Edit).build().perform(); Thread.sleep(2000);
			 * logs.info("Scroll to Edit STop");
			 */

			// Change Sequence of shipment-4
			// *[@id="gvShipmentDetails_ctl06_lbEdit"]
			try {
				wait.until(ExpectedConditions
						.invisibilityOfElementLocated(By.xpath("//*[@src=\"images/ajax-loader.gif\"]")));
				Thread.sleep(2000);
				WebElement element4 = driver.findElement(By.xpath(".//*[@id='gvShipmentDetails_ctl06_lbEdit']"));
				act.moveToElement(element4).build().perform();
				wait.until(ExpectedConditions.elementToBeClickable(element4));
				act.moveToElement(element4).click().perform();
				logs.info("Click on Edit button of shipment 3");
				wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("content1")));
			} catch (Exception ctl006) {
				wait.until(ExpectedConditions
						.invisibilityOfElementLocated(By.xpath("//*[@src=\"images/ajax-loader.gif\"]")));
				WebElement element4 = driver.findElement(By.xpath(".//*[@id='gvShipmentDetails_ctl06_lbEdit']"));
				act.moveToElement(element4).build().perform();
				wait.until(ExpectedConditions.elementToBeClickable(element4));
				act.moveToElement(element4).click().perform();
				logs.info("Click on Edit button of shipment 3");
				wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("content1")));
			}

			driver.findElement(By.id("txtToStopSeq")).clear();
			driver.findElement(By.id("txtToStopSeq")).sendKeys("5");
			logs.info("Enter To stop seq 5");
			robot.keyPress(KeyEvent.VK_TAB);

			WebElement el2 = driver.findElement(By.id("chkRecpOrderRcvd"));
			js.executeScript("arguments[0].scrollIntoView();", el2);
			Thread.sleep(2000);
			logs.info("Scroll to Recp Order Rcvd");

			try {
				driver.findElement(By.id("btnaddshipment")).click();
				logs.info("Click on Add Shipment");
				logs.info("4th shipment updated, Stop 1-5");

				/*
				 * WebElement EditShip = driver.findElement(By.id("btnDownEditStops"));
				 * js.executeScript("arguments[0].scrollIntoView();", EditShip);
				 * logs.info("Scrollto Edit STop"); Thread.sleep(2000);
				 */
				wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("gvShipmentDetails")));
			} catch (Exception AddShipmente) {
				logs.error(AddShipmente);
				// --Scroll up
				js.executeScript("window.scrollBy(0,-250)", "");
				js.executeScript("scroll(0, -250);");
				Thread.sleep(2000);
				getScreenshot(driver, "UpdateShipmentissue1_4");

				// --Scroll Down
				js.executeScript("window.scrollBy(0,250)", "");
				js.executeScript("scroll(0, 250);");
				Thread.sleep(2000);
				getScreenshot(driver, "UpdateShipmentissue2_4");

				WebElement BTNAddShipment = driver.findElement(By.id("btnaddshipment"));
				wait.until(ExpectedConditions.visibilityOf(BTNAddShipment));
				act.moveToElement(BTNAddShipment).build().perform();
				wait.until(ExpectedConditions.elementToBeClickable(BTNAddShipment));
				js.executeScript("arguments[0].click();", BTNAddShipment);
				logs.info("Click on Add Shipment");
				logs.info("4th shipment updated, Stop 1-5");

				/*
				 * WebElement EditShip = driver.findElement(By.id("btnDownEditStops"));
				 * js.executeScript("arguments[0].scrollIntoView();", EditShip);
				 * logs.info("Scrollto Edit STop"); Thread.sleep(2000);
				 */
				wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("gvShipmentDetails")));

			}
			ShipmentDetails = driver.findElement(By.id("module.shipment._header"));
			js.executeScript("arguments[0].scrollIntoView();", ShipmentDetails);
			Thread.sleep(2000);
			logs.info("Click on Edit button of shipment 3");

			// add new Shipment Details - 5 (1-6)
			logs.info("New Shipment-5");
			// From
			driver.findElement(By.id("txtFromStopSeq")).clear();
			driver.findElement(By.id("txtFromStopSeq")).sendKeys("1");
			logs.info("Enter From stop seq 1");
			robot.keyPress(KeyEvent.VK_TAB);
			// TO
			driver.findElement(By.id("txtToStopSeq")).clear();
			driver.findElement(By.id("txtToStopSeq")).sendKeys("6");
			logs.info("Enter To Stop seq 6");
			driver.findElement(By.id("txtToCompany")).sendKeys("JAVE PRODUCTIONS, INC");
			logs.info("Enter To Company");
			driver.findElement(By.id("txtToContact")).sendKeys("BARRY SIEGAL");
			logs.info("Enter To Contact");
			driver.findElement(By.id("txtToAddr1")).sendKeys("2850 OCEAN PARK BLVD");
			logs.info("Enter To Address 1");
			driver.findElement(By.id("txtToAddr2")).sendKeys("#300");
			logs.info("Enter To Address 2");
			driver.findElement(By.id("txtToZip")).clear();
			driver.findElement(By.id("txtToZip")).sendKeys("90405");
			logs.info("Enter To Zip");
			robot.keyPress(KeyEvent.VK_TAB);
			driver.findElement(By.id("txtToZip")).sendKeys(Keys.TAB);
			Thread.sleep(2000);
			driver.findElement(By.id("txtToPhone")).sendKeys("(424) 288-2125");
			logs.info("Enter To Phone");
			driver.findElement(By.id("txtDelInst")).sendKeys("Art Work");
			logs.info("Enter To Del Instruction");
			driver.findElement(By.id("txtRecipientEmail")).sendKeys("pdoshi@samyak.com");
			logs.info("Enter To Recipient Email");
			driver.findElement(By.id("chkRecpOrderRcvd")).click();
			logs.info("Click on Recp Order Rcvd");
			driver.findElement(By.id("chkRecpPickup")).click();
			logs.info("Click on Recp PickUp");
			driver.findElement(By.id("chkRecpDelivery")).click();
			logs.info("Click on Recp Delivery");

			driver.findElement(By.id("txtRouteWorkRef1")).sendKeys("Ref1 Ship5");
			logs.info("Enter To Ref 1");
			driver.findElement(By.id("txtRouteWorkRef3")).sendKeys("Ref3 Ship5");
			logs.info("Enter To Ref 3");

			/*
			 * try { driver.findElement(By.id("btnaddshipment")).click();
			 * logs.info("Click on Add Shipment");
			 * logs.info("5th shipment added, Stop 1-6");
			 * 
			 * WebElement EditShip = driver.findElement(By.id("btnDownEditStops"));
			 * js.executeScript("arguments[0].scrollIntoView();", EditShip);
			 * logs.info("Scrollto Edit STop"); Thread.sleep(2000);
			 * wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id(
			 * "gvShipmentDetails"))); } catch (Exception AddShipmente) {
			 * logs.error(AddShipmente); // --Scroll up
			 * js.executeScript("window.scrollBy(0,-250)", "");
			 * js.executeScript("scroll(0, -250);"); Thread.sleep(2000);
			 * getScreenshot(driver, "AddShipmentissue1_5");
			 * 
			 * // --Scroll Down js.executeScript("window.scrollBy(0,250)", "");
			 * js.executeScript("scroll(0, 250);"); Thread.sleep(2000);
			 * getScreenshot(driver, "AddShipmentissue2_5");
			 * 
			 * WebElement BTNAddShipment = driver.findElement(By.id("btnaddshipment"));
			 * wait.until(ExpectedConditions.visibilityOf(BTNAddShipment));
			 * act.moveToElement(BTNAddShipment).build().perform();
			 * wait.until(ExpectedConditions.elementToBeClickable(BTNAddShipment));
			 * js.executeScript("arguments[0].click();", BTNAddShipment);
			 * logs.info("Click on Add Shipment");
			 * logs.info("5th shipment added, Stop 1-6");
			 * 
			 * WebElement EditShip = driver.findElement(By.id("btnDownEditStops"));
			 * js.executeScript("arguments[0].scrollIntoView();", EditShip);
			 * logs.info("Scrollto Edit STop"); Thread.sleep(2000);
			 * wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id(
			 * "gvShipmentDetails")));
			 * 
			 * }
			 */
			wait.until(ExpectedConditions.elementToBeClickable(By.id("btnaddshipment")));
			driver.findElement(By.id("btnaddshipment")).click();
			System.out.println("Click on Add Shipment button");

			WebElement EditShip = driver.findElement(By.id("btnDownEditStops"));
			js.executeScript("arguments[0].scrollIntoView();", EditShip);
			Thread.sleep(2000);
			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("gvShipmentDetails")));

			ShipmentDetails = driver.findElement(By.id("module.shipment._header"));
			js.executeScript("arguments[0].scrollIntoView();", ShipmentDetails);
			Thread.sleep(5000);

			BtnDone = driver.findElement(By.id("btndone"));
			js.executeScript("arguments[0].scrollIntoView(true);", BtnDone);
			Thread.sleep(2000);
			wait.until(ExpectedConditions.elementToBeClickable(By.id("btndone")));
			act.moveToElement(BtnDone).click().perform();
			System.out.println("Click on Done button");
			Thread.sleep(2000);
			wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("load")));
			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("currentForm")));

			/*
			 * // Click no Done. BtnDone = driver.findElement(By.id("btndone"));
			 * js.executeScript("arguments[0].scrollIntoView();", BtnDone);
			 * Thread.sleep(2000); logs.info("Scroll to Done");
			 * wait.until(ExpectedConditions.elementToBeClickable(BtnDone));
			 * act.moveToElement(BtnDone).click().perform();
			 * logs.info("Click on Done button");
			 */

			// Search with generated RWId
			driver.findElement(By.id("ddlStatus")).sendKeys("All");
			Thread.sleep(2000);
			driver.findElement(By.id("txtRouteWorkId")).sendKeys(RWid1);
			Thread.sleep(2000);
			Search = driver.findElement(By.id("btnSearch"));
			wait.until(ExpectedConditions.elementToBeClickable(Search));
			act.moveToElement(Search).click().build().perform();
			System.out.println("Click on Search button");
			Thread.sleep(5000);
			wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("load")));
			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("currentForm")));
			getScreenshot(driver, "CreatedRW");
			// Process7: Edit Pending RW + Add new shipment (3-6) + Edit stop as 2-6 from
			// 3-6 + Done

			/*
			 * // Search with generated RWId
			 * driver.findElement(By.id("ddlStatus")).sendKeys("All");
			 * logs.info("Select All as status");
			 * driver.findElement(By.id("txtRouteWorkId")).clear();
			 * driver.findElement(By.id("txtRouteWorkId")).sendKeys(RWid1);
			 * logs.info("Enter RWID"); driver.findElement(By.id("btnSearch")).click();
			 * logs.info("Click on Search button"); Thread.sleep(5000); wait.until(
			 * ExpectedConditions.invisibilityOfElementLocated(By.xpath(
			 * "//*[@src=\"images/ajax-loader.gif\"]")));
			 * wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id(
			 * "currentForm")));
			 */

			// Edit RW
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(".//*[@id='dgRWList_lbEdit_0']/img")));
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath(".//*[@id='dgRWList_lbEdit_0']/img")));
			driver.findElement(By.xpath(".//*[@id='dgRWList_lbEdit_0']/img")).click();
			System.out.println("Click on Edit button");
			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("content1")));

			// Edit RW
			/*
			 * wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
			 * ".//*[@id='dgRWList_lbEdit_0']/img"))); WebElement imgEdit =
			 * driver.findElement(By.xpath(".//*[@id='dgRWList_lbEdit_0']/img"));
			 * act.moveToElement(imgEdit).build().perform();
			 * wait.until(ExpectedConditions.elementToBeClickable(imgEdit));
			 * js.executeScript("arguments[0].click();", imgEdit);
			 * logs.info("Click on Edit button"); wait.until(
			 * ExpectedConditions.invisibilityOfElementLocated(By.xpath(
			 * "//*[@src=\"images/ajax-loader.gif\"]")));
			 */
			/*
			 * try { wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id(
			 * "content1"))); } catch (Exception ContE) { WebDriverWait wait1 = new
			 * WebDriverWait(driver, 50);
			 * wait1.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id(
			 * "content1")));
			 * 
			 * }
			 */

			js.executeScript("window.scrollBy(0,250)");
			Thread.sleep(1000);

			// Change Sequence of shipment-4
			// driver.findElement(By.xpath(".//*[@id='gvShipmentDetails']/tbody/tr[0]/td[5]/a")).click();

			el = driver.findElement(By.id("btnaddshipment"));
			js.executeScript("arguments[0].scrollIntoView();", el);
			Thread.sleep(5000);

			WebElement el1 = driver.findElement(By.id("btnaddshipment"));
			js.executeScript("arguments[0].scrollIntoView();", el1);
			logs.info("Scroll to add shipment");
			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("gvShipmentDetails")));

			// add new Shipment Details - 6 (3-6)
			logs.info("Add new shipment detail-6");
			// From
			driver.findElement(By.id("txtFromStopSeq")).clear();
			driver.findElement(By.id("txtFromStopSeq")).sendKeys("3");
			logs.info("Enter from stop seq 3");
			robot.keyPress(KeyEvent.VK_TAB);
			// TO
			driver.findElement(By.id("txtToStopSeq")).clear();
			driver.findElement(By.id("txtToStopSeq")).sendKeys("6");
			logs.info("Enter to stop seq 6");
			robot.keyPress(KeyEvent.VK_TAB);
			// Package for Shipment-6 (3-6)
			driver.findElement(By.id("pieces")).clear();
			driver.findElement(By.id("pieces")).sendKeys("1");
			logs.info("Enter Pieces");
			driver.findElement(By.id("txtContents")).clear();
			driver.findElement(By.id("txtContents")).sendKeys("Only 26-36 Chocolate");
			logs.info("Enter Contents");
			driver.findElement(By.id("txtDimLen0")).clear();
			driver.findElement(By.id("txtDimLen0")).sendKeys(getRandomInteger(st));
			logs.info("Enter Len");
			driver.findElement(By.id("txtDimWid0")).clear();
			driver.findElement(By.id("txtDimWid0")).sendKeys(getRandomInteger(st));
			logs.info("Enter Wid");
			driver.findElement(By.id("txtDimHt0")).clear();
			driver.findElement(By.id("txtDimHt0")).sendKeys(getRandomInteger(st));
			logs.info("Enter Ht");
			driver.findElement(By.id("txtActWt0")).clear();
			driver.findElement(By.id("txtActWt0")).sendKeys(getRandomInteger(st));
			logs.info("Enter Wt");
			driver.findElement(By.id("txtRouteWorkRef1")).sendKeys("Ref1 Ship6");
			logs.info("Enter Ref 1");
			driver.findElement(By.id("txtRouteWorkRef3")).sendKeys("Ref3 Ship6");
			logs.info("Enter Ref 3");
			try {
				driver.findElement(By.id("btnaddshipment")).click();
				logs.info("Click on Add Shipment");
				logs.info("6th shipment added, Stop 3-6");

				EditShip = driver.findElement(By.id("btnDownEditStops"));
				js.executeScript("arguments[0].scrollIntoView();", EditShip);
				logs.info("Scrollto Edit STop");
				Thread.sleep(2000);
				wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("gvShipmentDetails")));
			} catch (Exception AddShipmente) {
				logs.error(AddShipmente);
				// --Scroll up
				js.executeScript("window.scrollBy(0,-250)", "");
				js.executeScript("scroll(0, -250);");
				Thread.sleep(2000);
				getScreenshot(driver, "AddShipmentissue1_6");

				// --Scroll Down
				js.executeScript("window.scrollBy(0,250)", "");
				js.executeScript("scroll(0, 250);");
				Thread.sleep(2000);
				getScreenshot(driver, "AddShipmentissue2_6");

				WebElement BTNAddShipment = driver.findElement(By.id("btnaddshipment"));
				wait.until(ExpectedConditions.visibilityOf(BTNAddShipment));
				act.moveToElement(BTNAddShipment).build().perform();
				wait.until(ExpectedConditions.elementToBeClickable(BTNAddShipment));
				js.executeScript("arguments[0].click();", BTNAddShipment);
				logs.info("Click on Add Shipment");
				logs.info("6th shipment added, Stop 3-6");

			}
			el = driver.findElement(By.id("btnaddshipment"));
			js.executeScript("arguments[0].scrollIntoView();", el);
			Thread.sleep(2000);
			logs.info("Scroll to Add Shipment");

			/*
			 * EditShip = driver.findElement(By.id("btnDownEditStops"));
			 * js.executeScript("arguments[0].scrollIntoView();", EditShip);
			 * Thread.sleep(2000); logs.info("Scroll to Edit Stop");
			 * 
			 * wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id(
			 * "gvShipmentDetails")));
			 * 
			 * act.moveToElement(EditShip).build().perform(); Thread.sleep(2000);
			 */

			// Change Sequence of shipment-6
			wait.until(ExpectedConditions
					.visibilityOfElementLocated(By.xpath(".//*[@id='gvShipmentDetails_ctl08_lbEdit']")));
			WebElement element6 = driver.findElement(By.xpath(".//*[@id='gvShipmentDetails_ctl08_lbEdit']"));
			act.moveToElement(element6).build().perform();
			act.moveToElement(element6).click().perform();
			logs.info("Click on Edit button of shipment 6");
			Thread.sleep(2000);
			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("content1")));
			Thread.sleep(2000);

			driver.findElement(By.id("txtFromStopSeq")).clear();
			driver.findElement(By.id("txtFromStopSeq")).sendKeys("2");
			logs.info("Enter from stop seq 2");
			robot.keyPress(KeyEvent.VK_TAB);
			WebElement el6 = driver.findElement(By.id("chkRecpOrderRcvd"));
			js.executeScript("arguments[0].scrollIntoView();", el6);
			Thread.sleep(5000);
			logs.info("Scroll to Recp Order Rcvd");
			/*
			 * try { driver.findElement(By.id("btnaddshipment")).click();
			 * logs.info("Click on Add Shipment"); logs.info("update shipment 6, Stop 2-3");
			 * 
			 * EditShip = driver.findElement(By.id("btnDownEditStops"));
			 * js.executeScript("arguments[0].scrollIntoView();", EditShip);
			 * logs.info("Scrollto Edit STop"); Thread.sleep(2000);
			 * wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id(
			 * "gvShipmentDetails"))); } catch (Exception AddShipmente) {
			 * logs.error(AddShipmente); // --Scroll up
			 * js.executeScript("window.scrollBy(0,-250)", "");
			 * js.executeScript("scroll(0, -250);"); Thread.sleep(2000);
			 * getScreenshot(driver, "UpdateShipmentissue1_6");
			 * 
			 * // --Scroll Down js.executeScript("window.scrollBy(0,250)", "");
			 * js.executeScript("scroll(0, 250);"); Thread.sleep(2000);
			 * getScreenshot(driver, "UpdateShipmentissue2_6");
			 * 
			 * WebElement BTNAddShipment = driver.findElement(By.id("btnaddshipment"));
			 * wait.until(ExpectedConditions.visibilityOf(BTNAddShipment));
			 * act.moveToElement(BTNAddShipment).build().perform();
			 * wait.until(ExpectedConditions.elementToBeClickable(BTNAddShipment));
			 * js.executeScript("arguments[0].click();", BTNAddShipment);
			 * logs.info("Click on Add Shipment"); logs.info("update shipment 6, Stop 2-3");
			 * 
			 * wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id(
			 * "gvShipmentDetails")));
			 * 
			 * } // Click on Done BtnDone = driver.findElement(By.id("btndone"));
			 * js.executeScript("arguments[0].scrollIntoView();", BtnDone);
			 * Thread.sleep(2000); logs.info("Scroll to Done button");
			 * act.moveToElement(BtnDone).click().perform();
			 * logs.info("Click on DOne button"); wait.until(
			 * ExpectedConditions.invisibilityOfElementLocated(By.xpath(
			 * "//*[@src=\"images/ajax-loader.gif\"]")));
			 * wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id(
			 * "newcontent")));
			 */

			wait.until(ExpectedConditions.elementToBeClickable(By.id("btnaddshipment")));
			driver.findElement(By.id("btnaddshipment")).click();
			System.out.println("Click on Add Shipment button");

			EditShip = driver.findElement(By.id("btnDownEditStops"));
			js.executeScript("arguments[0].scrollIntoView();", EditShip);
			Thread.sleep(2000);
			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("gvShipmentDetails")));

			ShipmentDetails = driver.findElement(By.id("module.shipment._header"));
			js.executeScript("arguments[0].scrollIntoView();", ShipmentDetails);
			Thread.sleep(5000);

			BtnDone = driver.findElement(By.id("btndone"));
			js.executeScript("arguments[0].scrollIntoView(true);", BtnDone);
			Thread.sleep(2000);
			wait.until(ExpectedConditions.elementToBeClickable(By.id("btndone")));
			act.moveToElement(BtnDone).click().perform();
			System.out.println("Click on Done button");
			Thread.sleep(2000);

			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("newcontent")));
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ddlStatus")));
			driver.findElement(By.id("ddlStatus")).sendKeys("All");
			driver.findElement(By.id("txtRouteWorkId")).sendKeys(RWid1);
			Search = driver.findElement(By.id("btnSearch"));
			wait.until(ExpectedConditions.elementToBeClickable(Search));
			Search.click();
			System.out.println("Click on Search button");
			wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("load")));
			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("currentForm")));
			getScreenshot(driver, "CreatedRWSearch");
			Thread.sleep(2000);

			// Active RW
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath(".//*[@id='dgRWList_lbActivate_0']/img")));
			driver.findElement(By.xpath(".//*[@id='dgRWList_lbActivate_0']/img")).click();
			System.out.println("Click on Activate button");
			Thread.sleep(2000);

			driver.switchTo().alert();
			driver.switchTo().alert().accept();
			Thread.sleep(2000);
			wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("load")));
			// Process8: Active RW + Edit + Add Ship (4-6) + change seq (3-6) + Done +
			// Active
			// Active RW
			/*
			 * wait.until( ExpectedConditions.visibilityOfElementLocated(By.xpath(
			 * ".//*[@id='dgRWList_lbActivate_0']/img")));
			 * driver.findElement(By.xpath(".//*[@id='dgRWList_lbActivate_0']/img")).click()
			 * ; logs.info("Click on Activate button");
			 * 
			 * driver.switchTo().alert(); driver.switchTo().alert().accept();
			 * Thread.sleep(2000); logs.info("Accept the Alert");
			 */

			/*
			 * // Edit RW wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
			 * ".//*[@id='dgRWList_lbEdit_0']/img")));
			 * wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
			 * ".//*[@id='dgRWList_lbEdit_0']/img")));
			 * driver.findElement(By.xpath(".//*[@id='dgRWList_lbEdit_0']/img")).click();
			 * logs.info("Click on Edit button");
			 * wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id(
			 * "content1"))); WebElement el7 = driver.findElement(By.id("btnaddshipment"));
			 * js.executeScript("arguments[0].scrollIntoView();", el7); Thread.sleep(2000);
			 * logs.info("Scroll to Add Shipment");
			 */

			// Edit RW
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(".//*[@id='dgRWList_lbEdit_0']/img")));
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath(".//*[@id='dgRWList_lbEdit_0']/img")));
			driver.findElement(By.xpath(".//*[@id='dgRWList_lbEdit_0']/img")).click();
			System.out.println("Click on Edit button");
			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("content1")));

			// js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
			js.executeScript("window.scrollBy(0,250)");
			Thread.sleep(1000);

			// Change Sequence of shipment-4
			// driver.findElement(By.xpath(".//*[@id='gvShipmentDetails']/tbody/tr[0]/td[5]/a")).click();

			el = driver.findElement(By.id("btnaddshipment"));
			js.executeScript("arguments[0].scrollIntoView();", el);
			Thread.sleep(5000);

			// add ship (4-6)
			logs.info("Add Shipment 7, 4-6");
			// From
			driver.findElement(By.id("txtFromStopSeq")).clear();
			driver.findElement(By.id("txtFromStopSeq")).sendKeys("4");
			logs.info("Enter From Stop sequence 4");
			robot.keyPress(KeyEvent.VK_TAB);
			// TO
			driver.findElement(By.id("txtToStopSeq")).clear();
			driver.findElement(By.id("txtToStopSeq")).sendKeys("6");
			logs.info("Enter To Stop sequence 6");
			robot.keyPress(KeyEvent.VK_TAB);
			driver.findElement(By.id("txtRouteWorkRef1")).sendKeys("Ref1 Ship7");
			logs.info("Enter Ref 1");
			driver.findElement(By.id("txtRouteWorkRef3")).sendKeys("Ref3 Ship7");
			logs.info("Enter Ref 3");
			try {
				driver.findElement(By.id("btnaddshipment")).click();
				logs.info("Click on Add Shipment");
				logs.info("7th shipment added, Stop 4-6");

				EditShip = driver.findElement(By.id("btnDownEditStops"));
				js.executeScript("arguments[0].scrollIntoView();", EditShip);
				logs.info("Scrollto Edit STop");
				Thread.sleep(2000);
				wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("gvShipmentDetails")));
			} catch (Exception AddShipmente) {
				logs.error(AddShipmente);
				// --Scroll up
				js.executeScript("window.scrollBy(0,-250)", "");
				js.executeScript("scroll(0, -250);");
				Thread.sleep(2000);
				getScreenshot(driver, "AddShipmentissue1_7");

				// --Scroll Down
				js.executeScript("window.scrollBy(0,250)", "");
				js.executeScript("scroll(0, 250);");
				Thread.sleep(2000);
				getScreenshot(driver, "AddShipmentissue2_7");

				WebElement BTNAddShipment = driver.findElement(By.id("btnaddshipment"));
				wait.until(ExpectedConditions.visibilityOf(BTNAddShipment));
				act.moveToElement(BTNAddShipment).build().perform();
				wait.until(ExpectedConditions.elementToBeClickable(BTNAddShipment));
				js.executeScript("arguments[0].click();", BTNAddShipment);
				logs.info("Click on Add Shipment");
				logs.info("7th shipment added, Stop 4-6");

				EditShip = driver.findElement(By.id("btnDownEditStops"));
				js.executeScript("arguments[0].scrollIntoView();", EditShip);
				logs.info("Scrollto Edit STop");
				Thread.sleep(2000);
				wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("gvShipmentDetails")));

			} // Change Sequence of shipment-7
				// *[@id="gvShipmentDetails_ctl06_lbEdit"]
			WebElement element7 = driver.findElement(By.xpath(".//*[@id='gvShipmentDetails_ctl09_lbEdit']"));
			act.moveToElement(element7).click().build().perform();
			logs.info("Click on Edit button of shipment 7");
			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("content1")));
			driver.findElement(By.id("txtFromStopSeq")).clear();
			driver.findElement(By.id("txtFromStopSeq")).sendKeys("3");
			logs.info("Enter From Stop sequence 3");
			robot.keyPress(KeyEvent.VK_TAB);
			WebElement el77 = driver.findElement(By.id("chkRecpOrderRcvd"));
			js.executeScript("arguments[0].scrollIntoView();", el77);
			Thread.sleep(2000);
			logs.info("Scroll to Recp Order Rcvd");

			/*
			 * try { driver.findElement(By.id("btnaddshipment")).click();
			 * logs.info("Click on Add Shipment"); logs.info("Update shipment 7, Stop 3-6");
			 * 
			 * EditShip = driver.findElement(By.id("btnDownEditStops"));
			 * js.executeScript("arguments[0].scrollIntoView();", EditShip);
			 * logs.info("Scrollto Edit STop"); Thread.sleep(2000);
			 * wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id(
			 * "gvShipmentDetails"))); } catch (Exception AddShipmente) {
			 * logs.error(AddShipmente); // --Scroll up
			 * js.executeScript("window.scrollBy(0,-250)", "");
			 * js.executeScript("scroll(0, -250);"); Thread.sleep(2000);
			 * getScreenshot(driver, "updateShipmentissue1_7");
			 * 
			 * // --Scroll Down js.executeScript("window.scrollBy(0,250)", "");
			 * js.executeScript("scroll(0, 250);"); Thread.sleep(2000);
			 * getScreenshot(driver, "updateShipmentissue2_7");
			 * 
			 * WebElement BTNAddShipment = driver.findElement(By.id("btnaddshipment"));
			 * wait.until(ExpectedConditions.visibilityOf(BTNAddShipment));
			 * act.moveToElement(BTNAddShipment).build().perform();
			 * wait.until(ExpectedConditions.elementToBeClickable(BTNAddShipment));
			 * js.executeScript("arguments[0].click();", BTNAddShipment);
			 * logs.info("Click on Add Shipment"); logs.info("Update shipment 7, Stop 3-6");
			 * 
			 * EditShip = driver.findElement(By.id("btnDownEditStops"));
			 * js.executeScript("arguments[0].scrollIntoView();", EditShip);
			 * logs.info("Scrollto Edit STop"); Thread.sleep(2000);
			 * wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id(
			 * "gvShipmentDetails")));
			 * 
			 * } // Click on Done WebElement eld7 = driver.findElement(By.id("btndone"));
			 * js.executeScript("arguments[0].scrollIntoView();", eld7); BtnDone =
			 * driver.findElement(By.id("btndone"));
			 * js.executeScript("arguments[0].scrollIntoView();", BtnDone);
			 * logs.info("Scroll to Done button"); Thread.sleep(2000);
			 * act.moveToElement(BtnDone).click().perform();
			 * logs.info("Click on Done button"); wait.until(
			 * ExpectedConditions.invisibilityOfElementLocated(By.xpath(
			 * "//*[@src=\"images/ajax-loader.gif\"]")));
			 * wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id(
			 * "newcontent")));
			 * wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ddlStatus")))
			 * ;
			 */

			wait.until(ExpectedConditions.elementToBeClickable(By.id("btnaddshipment")));
			driver.findElement(By.id("btnaddshipment")).click();
			System.out.println("Click on Add Shipment button");

			EditShip = driver.findElement(By.id("btnDownEditStops"));
			js.executeScript("arguments[0].scrollIntoView();", EditShip);
			Thread.sleep(2000);
			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("gvShipmentDetails")));

			ShipmentDetails = driver.findElement(By.id("module.shipment._header"));
			js.executeScript("arguments[0].scrollIntoView();", ShipmentDetails);
			Thread.sleep(5000);

			BtnDone = driver.findElement(By.id("btndone"));
			js.executeScript("arguments[0].scrollIntoView(true);", BtnDone);
			Thread.sleep(2000);
			wait.until(ExpectedConditions.elementToBeClickable(By.id("btndone")));
			act.moveToElement(BtnDone).click().perform();
			System.out.println("Click on Done button");
			Thread.sleep(2000);

			// BtnDone.click();
			// Active RW
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath(".//*[@id='dgRWList_lbActivate_0']/img")));
			driver.findElement(By.xpath(".//*[@id='dgRWList_lbActivate_0']/img")).click();
			System.out.println("Click on Activate button");
			Thread.sleep(2000);

			driver.switchTo().alert();
			driver.switchTo().alert().accept();
			Thread.sleep(2000);
			wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("load")));

			// Get message after activation of RW
			String NextGen = driver.findElement(By.id("lmsg")).getText();
			System.out.println(NextGen);
			logs.info("Next Gen==" + NextGen);
			getScreenshot(driver, "CreatedRW500After7Pack");

			/*
			 * // Click on search
			 * wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("btnSearch")))
			 * ; Search = driver.findElement(By.id("btnSearch"));
			 * act.moveToElement(Search).build().perform();
			 * wait.until(ExpectedConditions.elementToBeClickable(Search)); Search.click();
			 * logs.info("Click on Search button"); wait.until(
			 * ExpectedConditions.invisibilityOfElementLocated(By.xpath(
			 * "//*[@src=\"images/ajax-loader.gif\"]")));
			 * wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id(
			 * "currentForm")));
			 */

			wait.until(ExpectedConditions.elementToBeClickable(By.id("btnSearch")));
			driver.findElement(By.id("btnSearch")).click();
			System.out.println("Click on Search button");
			Thread.sleep(2000);
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
			System.out.println(ActGen1);
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
				RecMsg = "All Schedule will generate proper as per recurrence set";
				System.out.println(RecMsg);
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
			// msg.append("Process URL : " + baseUrl);
			Env = storage.getProperty("Env");
			String subject = "Selenium Automation Script: " + Env + " : Route Work Details-500Packages";

			try {

				Email.sendMail(
						"ravina.prajapati@samyak.com,asharma@samyak.com,parth.doshi@samyak.com, saurabh.jain@samyak.com, himanshu.dholakia@samyak.com",
						subject, msg.toString(), "");

				// Email.sendMail("ravina.prajapati@samyak.com", subject, msg.toString(), "");

			} catch (Exception ex) {
				logs.error(ex);
			}
		} catch (Exception e) {
			logs.error(e);
			getScreenshot(driver, "RW500Issue");
			System.out.println("Issue in RW500");

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