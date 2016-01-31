import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class CellListTest extends BaseGoogleWebToolkitTests {
    private static final String cellListUrl = baseUrl + "#!CwCellList";

    @Before
    public void SetUp(){
        new WebDriverWait(webDriver, 5).until(ExpectedConditions.presenceOfElementLocated(By.tagName("html")));
        webDriver.get(cellListUrl);
        new WebDriverWait(webDriver, 5).until(
                ExpectedConditions.presenceOfElementLocated(By.className("GNHGC04CCB"))
        );
    }

    @After
    public void TearDown() {
        webDriver.get("localhost");
    }

    @Test
    public void shouldSaveChangesOnUpdate() {
        getFirstContact().click();

        List<WebElement> inputs = getInputFields();

        WebElement firstNameInput = inputs.get(0);
        firstNameInput.clear();
        firstNameInput.sendKeys("Tiffany");

        WebElement lastNameInput = inputs.get(1);
        lastNameInput.clear();
        lastNameInput.sendKeys("Smith");

        WebElement updateButton = getUpdateButton();
        updateButton.click();

        Assert.assertEquals(
                "Tiffany Smith",
                getFirstContact().findElement(
                        By.cssSelector("td:first-child td:last-child")
                ).getText()
        );
    }

    @Test
    public void shouldCreateTheSameContact(){
        getLastContact().click();
        getCreateButton().click();
        List<WebElement> oldFields = getInputFields();

        getLastContact().click();
        List<WebElement> newFields = getInputFields();

        for (int i = 0; i < oldFields.size(); i++) {
            Assert.assertEquals(oldFields.get(i).getText(), newFields.get(i).getText());
        }
    }

    @Test
    public void shouldIgnoreCreationIfContactIsNotChosen(){
        String totalContactsCount = getCounterValues()[2];

        getCreateButton().click();

        Assert.assertEquals(totalContactsCount, getCounterValues()[2]);
    }

    @Test
    public void shouldGenerate50Contacts() {
        Integer totalContactsCount = Integer.parseInt(getCounterValues()[2]);

        getGenerateButton().click();

        Assert.assertEquals(totalContactsCount + 50, Integer.parseInt(getCounterValues()[2]));
    }

    private WebElement getGenerateButton() {
        return webDriver.findElement(By.cssSelector("td.GNHGC04CIJ > button"));
    }

    private WebElement getUpdateButton() {
        return getContactManagingButtons().get(0);
    }

    private WebElement getCreateButton() {
        return getContactManagingButtons().get(1);
    }

    private List<WebElement> getContactManagingButtons() {
        return webDriver.findElements(
                By.cssSelector(".middleCenterInner tr:last-child td:first-child button")
        );
    }

    /**
     * @return input fields from contact's managing area (only inputs, no one other)
     */
    private List<WebElement> getInputFields() {
        return webDriver.findElements(By.cssSelector(".GNHGC04CFK + td > input"));
    }

    private WebElement getFirstContact() {
        WebElement contactsContainer = getContactsContainer();
        WebElement firstContact = getContactsList(contactsContainer).get(0);
        while (!firstContact.isDisplayed())
            contactsContainer.sendKeys(Keys.PAGE_UP);

        return firstContact;
    }

    private WebElement getLastContact() {
        WebElement contactsContainer = getContactsContainer();

        loadAllContacts(contactsContainer);
        List<WebElement> contacts = getContactsList(contactsContainer);
        WebElement lastContact = contacts.get(contacts.size() - 1);

        while (!lastContact.isDisplayed())
            contactsContainer.sendKeys(Keys.PAGE_DOWN);
        return lastContact;
    }

    private List<WebElement> getContactsList(WebElement contactsContainer) {
        return contactsContainer.findElements(By.cssSelector(".GNHGC04CGB > div:first-child > div"));
    }

    private WebElement getContactsContainer() {
        return webDriver.findElement(By.cssSelector(".GNHGC04CJJ"));
    }

    private void loadAllContacts(WebElement contactsContainer) {
        String[] counts;
        contactsContainer.sendKeys(Keys.PAGE_UP);
        do {
            contactsContainer.sendKeys(Keys.PAGE_DOWN);
            counts = getCounterValues();
        } while (!counts[1].equals(counts[2]));
    }

    private String[] getCounterValues() {
        String[] elements = webDriver.findElement(By.cssSelector(".GNHGC04CJJ + div")).getText().split("[\\s:-]");
        String[] result = new String[3];
        result[0] = elements[0];
        result[1] = elements[3];
        result[2] = elements[6];
        return result;
    }
}
