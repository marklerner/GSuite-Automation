package home;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.coordinates.WebDriverCoordsProvider;

public class SettingsScraper {
	private FirefoxDriver driver;
	private String saveDirectory;
	private String url;

	public String getSaveDirectory() {
		return saveDirectory;
	}

	public void setSaveDirectory(String saveDirectory) {
		this.saveDirectory = saveDirectory;
		File directory = new File(saveDirectory);
		if (!directory.exists()) {
			System.out.printf("Creating directory %s...", saveDirectory);
			try {
				Files.createDirectories(directory.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.printf("Saving to %s\n", saveDirectory);
	}

	public SettingsScraper(String url) {
		this(url, "C:/Users/mwilkins/Pictures/");
	}

	public SettingsScraper(String url, String directory) {
		FirefoxBinary firefoxBinary = new FirefoxBinary();
//	    firefoxBinary.addCommandLineOptions("--headless");
		FirefoxOptions firefoxOptions = new FirefoxOptions();
		firefoxOptions.setBinary(firefoxBinary);
		driver = new FirefoxDriver(firefoxOptions);
		Date date = new Date();
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMddhhmmss");
		setSaveDirectory(directory + dateFormatter.format(date) + "/");
		this.url = url;
	}

	public void scrollPlease() {
		Actions act = new Actions(driver);
		act.moveToElement(driver.findElement(By.xpath("/html/body/div[2]/div[2]/div/div[1]/div[5]"))).click();
		act.sendKeys(Keys.PAGE_DOWN).perform();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * use my credentials to login
	 */
	public void login() {
		Scanner scan = new Scanner(System.in);
		driver.get(url);
		driver.manage().window().maximize();
		WebDriverWait wait = new WebDriverWait(driver, 5);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("identifierId")));
		WebElement username = driver.findElement(By.id("identifierId"));
		System.out.println("Enter Username:");
		String user = scan.nextLine();
		if (user.equals("default"))
			user = "madison-admin@beta.dhsraptor.com";
		username.sendKeys(user);
		username.sendKeys("\n");
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"passwordNext\"]")));
		WebElement pwd = driver.findElement(By.name("password"));
		System.out.println("Enter Password:");
		String pass = scan.nextLine();
		if (pass.equals("default"))
			pass = "H3rbie!!";
		pwd.sendKeys(pass);
		click("//*[@id=\"passwordNext\"]");
		wait.until(ExpectedConditions.elementToBeClickable(By.id("totpNext")));
		WebElement factor = driver.findElement(By.name("totpPin"));
		System.out.println("Enter 2-Factor Code:");
		String code = scan.nextLine();
		factor.sendKeys(code);
		driver.findElement(By.id("totpNext")).click();
		wait = new WebDriverWait(driver, 120);
		wait.until(ExpectedConditions.elementToBeClickable(By.id("dashboard-icon-1")));
		System.out.println("Login success");
		scan.close();
	}

	/**
	 * navigates to the dashboard
	 */
	private void goToDashboard() {
		driver.get("https://admin.google.com/AdminHome?hl=en");
		System.out.println("Loaded home page.");
	}

	/**
	 * opens and screenshots the important parts of the apps panel
	 */
	public void openApps() {
		driver.get("https://admin.google.com/ac/appslist/core");
		System.out.println("Loaded apps panel.");
		// G Suite apps
//		click("/html/body/div[2]/div[2]/div/div[1]/div[5]/div/div/table/tbody/tr/td[2]/div/div[1]/div");
		wait("/html/body/div[8]/c-wiz/div/div[1]/div/div[2]/div/div/div/div/div[2]/div/div/div/content/div[2]/div/div[2]/div[2]/div[2]/div/div[2]");
		pageshot(
				"/html/body/div[8]/c-wiz/div/div[1]/div/div[2]/div/div/div/div/div[2]/div/div/div/content/div[2]/div/div[2]",
				"G Suite",
				"/html/body/div[8]/c-wiz/div/div[1]/div/div[2]/div/div/div/div/div[2]/div/div/div/content/div[2]/div/div[2]/div[2]/div[2]/div/div[2]",
				"/html/body/div[8]/c-wiz/div/div[1]/div/div[2]/div/div/div/div/div[2]/div/div/div/content/div[2]/div/div[2]/div[2]/div[2]/div/div[4]");
		// Additional google services
		driver.get("https://admin.google.com/ac/appslist/additional");
//		click("/html/body/div[2]/div[2]/div/div[1]/div[5]/div/div/table/tbody/tr/td[2]/div/div[2]/div");
		wait("/html/body/div[8]/c-wiz/div/div[1]/div/div[2]/div/div/div/div/div[2]/div/div/div/content/div[2]/div/div[2]/div[2]/div[2]/div");
		pageshot(
				"/html/body/div[8]/c-wiz/div/div[1]/div/div[2]/div/div/div/div/div[2]/div/div/div/content/div[2]/div[2]/div[2]/div[1]/table",
				"Additional Google Services",
				"/html/body/div[8]/c-wiz/div/div[1]/div/div[2]/div/div/div/div/div[2]/div/div/div/content/div[2]/div[2]/div[2]/div[2]/div[2]/div/div[2]",
				"/html/body/div[8]/c-wiz/div/div[1]/div/div[2]/div/div/div/div/div[2]/div/div/div/content/div[2]/div[2]/div[2]/div[2]/div[2]/div/div[4]");
		// Marketplace Apps
		driver.get("https://admin.google.com/AdminHome?hl=en#AppsList:serviceType=MARKETPLACE");
//		click("/html/body/div[2]/div[2]/div/div[1]/div[5]/div/div/table/tbody/tr/td[2]/div/div[3]/div");
		screenshot("Marketplace Apps");
		// SAML Apps
		driver.get("https://admin.google.com/AdminHome?hl=en#AppsList:serviceType=SAML_APPS");
//		click("/html/body/div[2]/div[2]/div/div[1]/div[5]/div/div/table/tbody/tr/td[2]/div/div[4]/div");
		screenshot("SAML Apps");
		goToDashboard();
	}

	/**
	 * screenshots multiple pages
	 */
	private void pageshot(String path, String name, String pages, String next) {
		WebElement pagenums = driver.findElement(By.xpath(pages));
		String[] nums = pagenums.getText().split(" ");
		// this is the number of pages
		int end = Integer.parseInt(nums[3]);
		for (int id = 1; id <= end; id++) {
			spotshot(path, name + Integer.toString(id));
			if (id == end)
				break;
			click(next);
			int page = id;
			while (page != id + 1) {
				try {
					nums = pagenums.getText().split(" ");
					page = Integer.parseInt(nums[1]);
				} catch (StaleElementReferenceException e) {
					break;
				}
			}
		}
	}

	/**
	 * opens and screenshots the important parts of the security panel
	 */
	public void openSecurity() {
		// open security panel
		driver.get("https://admin.google.com/AdminHome?hl=en#SecuritySettings:");
		// open basic settings
		click("/html/body/div[2]/div[2]/div/div[1]/div[5]/div/div/div[3]/div/div/div/div[3]/div/div/div/div[1]/div[1]/div[1]/div");
		wait("/html/body/div[2]/div[2]/div/div[1]/div[5]/div/div/div[3]/div/div/div/div[3]/div/div/div/div[1]/div[1]/div[2]/div[2]/div[2]/div/div/div/div/table");
		spotshot(
				"/html/body/div[2]/div[2]/div/div[1]/div[5]/div/div/div[3]/div/div/div/div[3]/div/div/div/div[1]/div[1]/div[2]",
				"Basic Settings");
		// open password monitoring
		click("/html/body/div[2]/div[2]/div/div[1]/div[5]/div/div/div[3]/div/div/div/div[3]/div/div/div/div[1]/div[4]");
		wait("/html/body/div[2]/div[2]/div/div[1]/div[5]/div/div/div[3]/div/div/div/div[3]/div/div/div/div[1]/div[4]/div[2]/div[2]/div[2]/div/div[3]/div/div[2]/div/div/table/tbody/tr[11]/td[1]/div/div");
		spotshot(
				"/html/body/div[2]/div[2]/div/div[1]/div[5]/div/div/div[3]/div/div/div/div[3]/div/div/div/div[1]/div[4]/div[2]",
				"Password Monitoring");
		// login challenges
		click("/html/body/div[2]/div[2]/div/div[1]/div[5]/div/div/div[3]/div/div/div/div[3]/div/div/div/div[1]/div[5]");
		wait("/html/body/div[2]/div[2]/div/div[1]/div[5]/div/div/div[3]/div/div/div/div[3]/div/div/div/div[1]/div[5]/div[2]/div[2]/div[2]/div/div/div/div[2]/table/tbody/tr/td[3]");
		spotshot(
				"/html/body/div[2]/div[2]/div/div[1]/div[5]/div/div/div[3]/div/div/div/div[3]/div/div/div/div[1]/div[5]/div[2]/div[2]/div[2]/div",
				"Login Challenges");
		// api reference
		click("/html/body/div[2]/div[2]/div/div[1]/div[5]/div/div/div[3]/div/div/div/div[3]/div/div/div/div[1]/div[6]");
		wait("/html/body/div[2]/div[2]/div/div[1]/div[5]/div/div/div[3]/div/div/div/div[3]/div/div/div/div[1]/div[6]/div[2]/div[2]/div[2]/div/div/div/div/table/tbody/tr[2]/td[1]");
		spotshot(
				"/html/body/div[2]/div[2]/div/div[1]/div[5]/div/div/div[3]/div/div/div/div[3]/div/div/div/div[1]/div[6]/div[2]/div[2]/div[2]",
				"API Reference");
		// sso
		click("/html/body/div[2]/div[2]/div/div[1]/div[5]/div/div/div[3]/div/div/div/div[3]/div/div/div/div[1]/div[7]");
		wait("/html/body/div[2]/div[2]/div/div[1]/div[5]/div/div/div[3]/div/div/div/div[3]/div/div/div/div[1]/div[7]/div[2]/div[2]/div[2]/div/div/div/div[3]/div/div[5]/div/div[2]/div[2]/button");
		spotshot(
				"/html/body/div[2]/div[2]/div/div[1]/div[5]/div/div/div[3]/div/div/div/div[3]/div/div/div/div[1]/div[7]/div[2]/div[2]",
				"SSO");
		// session Control
		click("/html/body/div[2]/div[2]/div/div[1]/div[5]/div/div/div[3]/div/div/div/div[3]/div/div/div/div[1]/div[9]");
		wait("/html/body/div[2]/div[2]/div/div[1]/div[5]/div/div/div[3]/div/div/div/div[3]/div/div/div/div[1]/div[9]/div[2]/div[2]/div[2]/div/div/div/div[2]/table/tbody/tr/td[2]/div/div/div[3]/div/div[2]");
		spotshot(
				"/html/body/div[2]/div[2]/div/div[1]/div[5]/div/div/div[3]/div/div/div/div[3]/div/div/div/div[1]/div[9]/div[2]/div[2]/div[2]/div",
				"Session Control");
		// api permissions
		click("/html/body/div[2]/div[2]/div/div[1]/div[5]/div/div/div[3]/div/div/div/div[3]/div/div/div/div[1]/div[11]");
		wait("/html/body/div[2]/div[2]/div/div[1]/div[5]/div/div/div[3]/div/div/div/div[3]/div/div/div/div[1]/div[11]/div[2]/div[2]/div[2]/div/div/div/div[2]/table/tbody/tr[3]/td[2]/div");
		spotshot(
				"/html/body/div[2]/div[2]/div/div[1]/div[5]/div/div/div[3]/div/div/div/div[3]/div/div/div/div[1]/div[11]/div[2]/div[2]",
				"API Permissions");
		System.out.println("Saved all security settings.");
	}

	/**
	 * performs necessary waiting, then clicks the element indicated by xpath
	 */
	private WebElement click(String xpath) {
		WebDriverWait wait = new WebDriverWait(driver, 20);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
		WebElement element = driver.findElement(By.xpath(xpath));
		element.click();
		return element;
	}

	/**
	 * waits for the object given by path to be visible
	 */
	private void wait(String path) {
		WebDriverWait wait = new WebDriverWait(driver, 20);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(path)));
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * screenshots just the element in question
	 */
	private void spotshot(String path, String name) {
		WebElement ele = driver.findElement(By.xpath(path));
		JavascriptExecutor js = (JavascriptExecutor) driver;
		// move element into view as much as possible
		js.executeScript("arguments[0].scrollIntoView(true);", ele);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		int i = 0;
		do {
			croppedShot(ele, i, name);
			// check if more shots need to be taken
			Point loc = ele.getLocation();
			Dimension size = ele.getSize();
			Dimension win = driver.manage().window().getSize();
			int endOfElement = loc.getY() + size.getHeight() + 30;
			if (endOfElement < win.getHeight()) {
				break;
			} else {
				System.out.printf("%s is more than one page.\n", name);
				scrollPlease();
				i++;
			}
		} while (true);
		System.out.printf("Saved %s.\n", name);
	}

	/**
	 * takes a cropped screenshot of the given element
	 * 
	 * @param ele
	 * @param i
	 * @param name
	 */
	private void croppedShot(WebElement ele, int i, String name) {
		File output = new File(name + ".png");
		if (i == 0) {
			// take picture
			BufferedImage eleScreenshot = new AShot().coordsProvider(new WebDriverCoordsProvider())
					.takeScreenshot(driver, ele).getImage();
			try {
				ImageIO.write(eleScreenshot, "png", output);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			// Get entire page screenshot
			File screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
			BufferedImage fullImg = null;
			try {
				fullImg = ImageIO.read(screenshot);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Get the location of element on the page
			Point point = ele.getLocation();

			// Get width and height of the element
			int eleWidth = ele.getSize().getWidth();
			int realHeight = driver.findElement(By.xpath("/html/body/div[2]/div[2]/div/div[1]/div[5]/div/div/div[3]")).getSize().getHeight();

			// Crop the entire page screenshot to get only element screenshot
			BufferedImage eleScreenshot = fullImg.getSubimage(point.getX(), 97,
			    eleWidth, realHeight);
			try {
				ImageIO.write(eleScreenshot, "png", output);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// move picture to correct location
		File screenshotLocation;
		if (i == 0)
			screenshotLocation = new File(saveDirectory + "\\" + name + ".png");
		else
			screenshotLocation = new File(saveDirectory + "\\" + name + Integer.toString(i) + ".png");
		try {
			FileUtils.copyFile(output, screenshotLocation);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * opens and dumps the users panel
	 */
	public void openUsers() {
		Date date = new Date();
		File settings = new File("savedsettings" + date.getTime() + ".txt");
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(settings, "UTF-8");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		WebDriverWait wait = new WebDriverWait(driver, 10);
		// wait until table of users is visible, then get it
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("i3WFpf")));
		WebElement table = driver.findElement(By.className("i3WFpf"));
		// get the list of users to click
		System.out.println("Found Table.");
		while (true) {
//			List<WebElement> users = table.findElements(By.tagName("tr"));
			String username;
			WebElement user;
			int i = 1;
			// add the loop back so it gets all users
			// for (int i = 1; i < users.size(); i++) {
			wait.until(ExpectedConditions.elementToBeClickable(table));
			user = table.findElement(By.xpath("./tbody/tr[" + i + "]/td[2]/span/div/div[2]/div"));
			user.click();
			// wait for username to appear
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[8]/c-wiz[2]")));
			username = driver.findElement(By.xpath(
					"/html/body/div[8]/c-wiz[2]/div/div[1]/div/div[2]/div/div/div[1]/div/div/content/c-wiz/div/div[1]/div[2]/div[2]/div[1]/content"))
					.getText();
			// screenshot the page
//				screenshot(username);
			captureSettings(writer);
			System.out.printf("Captured %s's settings.\n", username);
			// go back
			driver.navigate().back();
			// }
			wait.until(ExpectedConditions.visibilityOf(table));
//			WebElement nextButton = driver.findElement(By.xpath("/html/body/div[8]/c-wiz/div/div[1]/div/div[2]/div/div/div/div/div/div/div[3]/div[3]/div[3]/div[2]/div/div[4]"));
			// fix multiple pages support - add branch back with loop
//			if (nextButton.getAttribute("aria-disabled").equals("false"))
//				nextButton.click();
//			else
			break;
		}
		System.out.println("Collected all data.");
		writer.close();
	}

	/**
	 * scrapes the actual settings from the current page, assuming it is a user page
	 */
	private void captureSettings(PrintWriter writer) {
		// prints the users name in the file
		writer.println(driver.findElement(By.xpath(
				"/html/body/div[8]/c-wiz[2]/div/div[1]/div/div[2]/div/div/div[1]/div/div/content/c-wiz/div/div[1]/div[2]/div[2]/div[1]/content"))
				.getText());

		// open the user information panel
		System.out.print("Getting User Information...");
		scrape("/html/body/div[8]/c-wiz[2]/div/div[1]/div/div[2]/div/div/div[2]/div/div/div/content/div/div[2]/div",
				"/html/body/div[8]/c-wiz[3]/div/div[1]/div/div[2]/div/div/div[2]/div/div/div/content/div/div",
				"/html/body/div[8]/c-wiz[3]/div/div[1]/div/div[2]/div/div/div[2]/div/div/div/content/div/div/div[2]/div/div",
				writer);

		// open the security panel
		System.out.print("Getting Security...");
		scrape("/html/body/div[8]/c-wiz[2]/div/div[1]/div/div[2]/div/div/div[2]/div/div/div/content/div/c-wiz[2]/div",
				"/html/body/div[8]/c-wiz[5]/div/div[1]/div/div[2]/div/div/div[2]/div/div/div/content/div/div",
				"/html/body/div[8]/c-wiz[5]/div/div[1]/div/div[2]/div/div/div[2]/div/div/div/content/div/div/div[2]/div",
				writer);
	}

	/**
	 * pulls the data from the element and dumps it to the printwriter
	 * 
	 * @param clickPath
	 * @param attribute
	 * @param panelPath
	 * @param writer
	 */
	private void scrape(String clickPath, String attribute, String panelPath, PrintWriter writer) {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		// get the open clicky john
		WebElement open = driver.findElement(By.xpath(clickPath));
		// wait till you see the john
		wait.until(ExpectedConditions.elementToBeClickable(open));
		wait.until(ExpectedConditions.visibilityOf(open));
		open.click();
		// let the damn thing expand
		wait.until(ExpectedConditions.attributeToBe(By.xpath(attribute), "aria-expanded", "true"));
		WebElement panel = driver.findElement(By.xpath(panelPath));
		// dump the text into the file
		writer.println(panel.getText());
		// on to da nex one
		driver.navigate().back();
		System.out.println("done.");
	}

	/**
	 * takes a screenshot of the current page and saves to the saveDirectory as a
	 * png file
	 */
	private void screenshot(String name) {
		TakesScreenshot shooter = ((TakesScreenshot) driver);
		File srcFile = shooter.getScreenshotAs(OutputType.FILE);
		File destination = new File(saveDirectory + "\\" + name + ".png");
		try {
			FileUtils.copyFile(srcFile, destination);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
