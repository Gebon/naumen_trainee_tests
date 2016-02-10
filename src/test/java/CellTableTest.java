import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CellTableTest extends BaseGoogleWebToolkitTests {
    private static final String cellTableUrl = baseUrl + "#!CwCellTable";

    @Before
    public void SetUp() {
        new WebDriverWait(webDriver, 5).until(ExpectedConditions.presenceOfElementLocated(By.tagName("html")));
        webDriver.get(cellTableUrl);
        new WebDriverWait(webDriver, 5).until(
                ExpectedConditions.presenceOfElementLocated(By.className("GNHGC04CAD"))
        );
    }

    @After
    public void TearDown() {
        webDriver.get("localhost");
    }

    @Test
    public void shouldCorrectlySort() {
        getFirstNameHeader().click();
        Assert.assertTrue(isInAlphabeticalOrder(getFirstNames()));

        getSecondNameHeader().click();
        Assert.assertTrue(isInAlphabeticalOrder(getSecondNames()));

        getAddressHeader().click();
        Assert.assertTrue(isInAlphabeticalOrder(getAddresses()));
    }

    @Test
    public void shouldSaveCheckboxCheckedWhileSwitchingPages() {
        List<WebElement> elements = getNthColumnElements(1);
        for (int i = 0; i < 3; i++) {
            elements.get(i).findElement(By.cssSelector("input")).click();
        }

        getNextPageButton().click();
        getPreviousPageButton().click();

        elements = getNthColumnElements(1);
        for (int i = 0; i < 3; i++) {
            Assert.assertTrue(elements.get(i).findElement(By.cssSelector("input")).isSelected());
        }
    }

    @Test
    public void shouldSaveDropListChoiceWhileSwitchingPages() {
        getFirstSelect().selectByIndex(0);
        String choice = getFirstSelect().getFirstSelectedOption().getText();

        getNextPageButton().click();
        getPreviousPageButton().click();

        Assert.assertTrue(choice.equals(getFirstSelect().getFirstSelectedOption().getText()));

        getLastPageButton().click();

        getFirstSelect().selectByIndex(0);
        choice = getFirstSelect().getFirstSelectedOption().getText();

        getFirstPageButton().click();
        getLastPageButton().click();

        Assert.assertTrue(choice.equals(getFirstSelect().getFirstSelectedOption().getText()));
    }

    @Test
    public void shouldSaveNameChanges() {
        getFirstPageButton().click();
        getNthColumnElements(2).get(0).click();
        getNthColumnElements(2)
                .get(0)
                .findElement(By.cssSelector("input"))
                .sendKeys("Bear", Keys.ENTER);

        Assert.assertTrue(getNthColumnText(2).get(0).equals("Bear"));
    }

    private Select getFirstSelect() {
        return new Select(
                getNthColumnElements(4)
                        .get(0)
                        .findElement(By.tagName("select"))
        );
    }

    private WebElement getNextPageButton() {
        return getNthNavigationButton(2);
    }

    private WebElement getLastPageButton() {
        return getNthNavigationButton(3);
    }

    private WebElement getPreviousPageButton() {
        return getNthNavigationButton(1);
    }

    private WebElement getFirstPageButton() {
        return getNthNavigationButton(0);
    }

    private WebElement getNthNavigationButton(int index) {
        return webDriver.findElements(By.cssSelector(".GNHGC04CLH")).get(index);
    }

    private List<String> getAddresses() {
        return getAllNthColumnText(5);
    }

    private List<String> getAllNthColumnText(int column) {
        getFirstPageButton().click();

        List<String> strings = new ArrayList<>();
        int lastRecordNumber = 0;
        do {
            List<String> texts = getNthColumnText(column);
            for (int i = lastRecordNumber + 1 - getCounterValue(0); i < texts.size(); i++) {
                strings.add(texts.get(i));
            }
            lastRecordNumber = getCounterValue(1);
            getNextPageButton().click();
        } while (lastRecordNumber != getCounterValue(2));

        return strings;
    }

    private Integer getCounterValue(int index) {
        return getCounterValues().get(index);
    }

    private List<Integer> getCounterValues() {
        List<Integer> result = new ArrayList<>();
        for (String value : webDriver.findElement(By.cssSelector(".GNHGC04CNH div")).getText().split("\\D")) {
            if (value.equals(""))
                continue;
            result.add(Integer.parseInt(value));
        }
        return result;
    }

    private List<String> getSecondNames() {
        return getAllNthColumnText(3);
    }

    private List<String> getFirstNames() {
        return getAllNthColumnText(2);
    }

    private List<String> getNthColumnText(int columnNumber) {
        List<String> texts = new ArrayList<>();
        List<WebElement> elements = getNthColumnElements(columnNumber);
        texts.addAll(
                elements
                        .stream()
                        .map(element -> element.findElement(
                                By.cssSelector("div")
                                ).getText()
                        )
                        .collect(Collectors.toList())
        );
        return texts;
    }

    private List<WebElement> getNthColumnElements(int columnNumber) {
        List<WebElement> elements = new ArrayList<>();
        List<WebElement> records = getRecordsFromCurrentPage();
        elements.addAll(
                records.stream()
                        .map(row -> row.findElement(
                                By.cssSelector(
                                        String.format("td:nth-child(%d)", columnNumber)
                                )
                                )
                        )
                        .collect(Collectors.toList())
        );
        return elements;
    }

    private List<WebElement> getRecordsFromCurrentPage() {
        return webDriver.findElements(By.cssSelector(".GNHGC04CIE tbody:nth-child(3) > tr"));
    }

    private WebElement getFirstNameHeader() {
        return getHeaders().get(1);
    }

    private WebElement getAddressHeader() {
        return getHeaders().get(4);
    }

    private WebElement getSecondNameHeader() {
        return getHeaders().get(2);
    }

    private List<WebElement> getHeaders() {
        return webDriver.findElements(By.cssSelector("thead th"));
    }

    private boolean isInAlphabeticalOrder(List<String> strings) {
        if (strings.size() < 2)
            return true;

        int order = 0;

        for (int i = 0; order == 0 && i < strings.size() - 1; i++)
            order = strings.get(i).compareToIgnoreCase(strings.get(i + 1));

        if (order == 0)
            return true;

        order /= Math.abs(order);
        for (int i = 0; i < strings.size() - 1; i++) {
            if (order * strings.get(i).compareToIgnoreCase(strings.get(i + 1)) < 0)
                return false;
        }
        return true;
    }
}
