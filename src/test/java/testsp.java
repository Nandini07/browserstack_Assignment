import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

import java.io.File;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import static java.lang.Thread.sleep;


public class testsp {
    protected WebDriver driver;

    private static final String USERNAME = "xx";  //username and accesskey are not provided
    private static final String ACCESS_KEY = "xx";
    private static final String BROWSERSTACK_URL = "https://" + USERNAME + ":" + ACCESS_KEY + "@hub-cloud.browserstack.com/wd/hub";

    @BeforeMethod
    @Parameters({"browser", "browserVersion", "platformName", "platformVersion", "deviceName"})
    public void setUp(@Optional String browser,
                      @Optional String browserVersion,
                      @Optional String platformName,
                      @Optional String platformVersion,
                      @Optional String deviceName) throws Exception {

        MutableCapabilities caps = new MutableCapabilities();
        caps.setCapability("browserName", browser);

        HashMap<String, Object> bstackOptions = new HashMap<>();
        bstackOptions.put("osVersion", platformVersion);
        bstackOptions.put("deviceName", deviceName);
        bstackOptions.put("realMobile", "true");
        bstackOptions.put("buildName", "Test-" + System.currentTimeMillis());
        bstackOptions.put("sessionName", browser + (deviceName.isEmpty() ? "" : " " + deviceName) + " Test");
        bstackOptions.put("debug", "true");
        bstackOptions.put("consoleLogs", "info");

        caps.setCapability("bstack:options", bstackOptions);

        String capsJson = caps.toJson().toString();
        System.out.println("Caps for " + browser + ": " + capsJson);

        driver = new RemoteWebDriver(new URL(BROWSERSTACK_URL), caps);
        logToBrowserStack("Caps for " + browser + ": " + capsJson);
    }


        @Test
        public void spanishpage() throws InterruptedException {
            //logging off warnings from Google cloud translate
            Logger logger = Logger.getLogger("com.google.cloud.translate");

            logger.setLevel(Level.OFF);
            LogManager.getLogManager().reset();

            // TODO Auto-generated method stub
            //chrome webdriver initiation
            //WebDriver driver= new ChromeDriver();
            driver.get("https://elpais.com/argentina/");

            driver.manage().window().maximize();

            //checking if page is in spanish or not.
            String lang = driver.findElement(By.tagName("html")).getDomAttribute("lang");
            System.out.println(lang);

            if (lang.startsWith("es")) {
                System.out.println("Page is in spanish");
            }

            //accept the popup agreement
            // Handle popup
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            try {
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id=\"didomi-notice\"]")));
                WebElement acceptButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@id=\"didomi-notice-agree-button\"]")));
                acceptButton.click();
            } catch (TimeoutException e) {
                System.out.println("No popup found or timed out");
            }


            //navigate to opinion tab
            driver.findElement(By.xpath("//*[@id=\"btn_open_hamburger\"]")).click();
            WebElement ham = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='me-n _pr']")));
            // Scroll inside the hamburger container (not the entire window)
            JavascriptExecutor js = (JavascriptExecutor) driver;
            Thread.sleep(2000);
            js.executeScript("arguments[0].scrollTop = arguments[0].scrollHeight", ham);

            // Wait for the "opinion" link to be clickable
            WebElement opinionLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"hamburger_container\"]/nav/div[1]/ul/li[2]/a")));
            //Thread.sleep(10000);
            // Click the "opinion" link
            opinionLink.click();





                //fetching  articles in links
                List<WebElement> links = driver.findElements(By.xpath("//article"));
                int i = 1;
                List<WebElement> titles;
                for (WebElement e : links) {
                    System.out.println("Titles and content of 5 Articles");
                    System.out.println("");
                    System.out.println("ARTICLE " + i);
                    System.out.println(" ");
                    //printing the article title and content
                    System.out.println(e.getText());
                    System.out.println(" ");


                    //Downloading the image in the article.
                    try {
                        WebElement imagetag = e.findElement(By.tagName("img"));
                        if (imagetag != null) {
                            String imgurl = imagetag.getDomAttribute("src");
                            System.out.println("Downloading image: " + imgurl);
                            URL imurl = new URL(imgurl);
                            FileUtils.copyURLToFile(imurl, new File("C:\\Users\\nandi\\Downloads\\browserstack\\article" + i + ".png"));

                        }
                    } catch (Exception ignored) {


                    }

                    i++;
                    if (i == 6) {
                        break;
                    }

                }

                //translating all the titles to english
                StringBuilder sb = new StringBuilder();
                System.out.println(" ");
                System.out.println(" List of translated Translated headers");
                System.out.println(" ");
                for (WebElement e1 : links) {
                    titles = e1.findElements(By.tagName("h2"));

                    //Print the title and content of each article in Spanish
                    String title = titles.get(0).getText();
                    String translatedtext = translatesp.translate(title);
                    System.out.println(translatedtext);
                    //combined all the headers into sb
                    sb.append(translatedtext + " ");
                }


                //System.out.println(sb);
                String headersstr = sb.toString();
                //String regex = "[ ]+";
                //headers contains all the words of all headers
                String[] headers = headersstr.split(" ");

                //count the number of times each string repeated in all headers
                HashMap<String, Integer> hm = new HashMap<String, Integer>();
                for (int k = 0; k < headers.length; k++) {

                    hm.put(headers[k], hm.getOrDefault(headers[k], 0) + 1);


                }
                //printing each repeated word along with the count of its occurrences.

                System.out.println("Each repeated word along with the count of its occurrences");
                System.out.println("");

                for (Map.Entry<String, Integer> entry : hm.entrySet()) {
                    if (entry.getValue() > 2) {
                        System.out.println(entry.getKey() + ": " + entry.getValue());
                        logToBrowserStack(entry.getKey() + ": " + entry.getValue());
                    }
                }

            }

    private  void logToBrowserStack(String message) {
        if (driver != null) {
            JavascriptExecutor jse = (JavascriptExecutor) driver;
            jse.executeScript("browserstack_executor: {\"action\": \"annotate\", \"arguments\": {\"data\": \"" + message + "\", \"level\": \"info\"}}");

        }

    }
    @AfterMethod
    public void quit() {
        driver.quit();

    }

}

