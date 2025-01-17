package cucumber;

import com.spring.MainApplication;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/java/cucumber/features",
        glue = {"cucumber.steps", "cucumber.config"},
        plugin = {"pretty", "html:target/cucumber-reports.html"}
)
@SpringBootTest(classes = MainApplication.class)
public class CucumberTest {
}
