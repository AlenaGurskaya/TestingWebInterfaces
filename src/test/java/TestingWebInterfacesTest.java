import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestingWebInterfacesTest {
    private WebDriver driver;

    @BeforeAll
    static void setUpAll() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--headless");
        driver = new ChromeDriver(options);
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
        driver = null;
    }

    @ParameterizedTest
    @CsvSource({
            "Иванов Иван",
            "Иванова Анна-Мария",
            "Иванова Алёна"
    })
    public void shouldBeSuccessMessage(String name) {
        driver.get("http://localhost:9999/");
        WebElement form = driver.findElement(By.cssSelector("[class='form form_size_m form_theme_alfa-on-white']"));
        form.findElement(By.cssSelector("[name='name']")).sendKeys(name);
        form.findElement(By.cssSelector("[name='phone']")).sendKeys("+79649005050");
        form.findElement(By.cssSelector("[data-test-id='agreement']")).click();
        form.findElement(By.cssSelector("button")).click();
        String text = driver.findElement(By.className("Success_successBlock__2L3Cw")).getText();

        assertEquals("Ваша заявка успешно отправлена! Наш менеджер свяжется с вами в ближайшее время.", text.trim());
    }

    @ParameterizedTest
    @CsvSource({
            "'Иванов','Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.'",
            "'Иван Иванов','Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.'",
            "'Иванов Иван Иванович','Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.'",
            "'Ivanov Ivan','Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.'",
            "'Майкл О'Нил','Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.'",
            "'Иванова Анна--Мария','Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.'",
            "'Иванова Анна-','Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.'",
            "'-Иванова Анна','Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.'",
            "'Иванов Иван1','Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.'",
            "'И','Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.'",
            "'','Поле обязательно для заполнения'"
    })
    public void shouldBeMessageAboutInvalidSurnameAndFirstName(String name, String expected) {
        driver.get("http://localhost:9999/");
        WebElement form = driver.findElement(By.cssSelector("[class='form form_size_m form_theme_alfa-on-white']"));
        form.findElement(By.cssSelector("[name='name']")).sendKeys(name);
        form.findElement(By.cssSelector("[name='phone']")).sendKeys("+79649005050");
        form.findElement(By.cssSelector("[data-test-id='agreement']")).click();
        form.findElement(By.cssSelector("button")).click();
        String text = driver.findElement(By.cssSelector("[data-test-id='name'] .input__sub")).getText();

        assertEquals(expected, text.trim());
    }

    @ParameterizedTest
    @CsvSource({
            "'+7964900505','Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.'",
            "'+796490050501','Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.'",
            "'79649005050','Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.'",
            "'+7(964)9005050','Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.'",
            "'+7964900505а','Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.'",
            "'','Поле обязательно для заполнения'"
    })
    public void shouldBeMessageAboutInvalidPhone(String phone, String expected) {
        driver.get("http://localhost:9999/");
        WebElement form = driver.findElement(By.cssSelector("[class='form form_size_m form_theme_alfa-on-white']"));
        form.findElement(By.cssSelector("[name='name']")).sendKeys("Иванов Иван");
        form.findElement(By.cssSelector("[name='phone']")).sendKeys(phone);
        form.findElement(By.cssSelector("[data-test-id='agreement']")).click();
        form.findElement(By.cssSelector("button")).click();
        String text = driver.findElement(By.cssSelector("[data-test-id='phone'] .input__sub")).getText();

        assertEquals(expected, text.trim());
    }

    @Test
    public void shouldBeMessageAboutInvalidCheckbox() {
        driver.get("http://localhost:9999/");
        WebElement form = driver.findElement(By.cssSelector("[class='form form_size_m form_theme_alfa-on-white']"));
        form.findElement(By.cssSelector("[name='name']")).sendKeys("Иванов Иван");
        form.findElement(By.cssSelector("[name='phone']")).sendKeys("+79649005050");
        form.findElement(By.cssSelector("button")).click();

        WebElement error = driver.findElement(By.cssSelector("[data-test-id='agreement']"));
        String actualColor = error.getCssValue("color");

        assertEquals("rgba(255, 92, 92, 1)", actualColor);
    }
}