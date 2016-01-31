import com.gargoylesoftware.htmlunit.BrowserVersion;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class BaseGoogleWebToolkitTests {
    protected static WebDriver webDriver;

    protected static final String baseUrl = "http://samples.gwtproject.org/samples/Showcase/Showcase.html";

    @BeforeClass
    public static void setUp(){
        // When I use HtmlUnitDriver in my PC, I gets too long execution time.
        // Therefore, I decided to use FirefoxDriver
        //webDriver = new HtmlUnitDriver(BrowserVersion.FIREFOX_38, true);
        webDriver = new FirefoxDriver();
        webDriver.manage().window().maximize();
    }

    @AfterClass
    public static void tearDown(){
        webDriver.quit();
    }
}
