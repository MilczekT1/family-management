package pl.konradboniecki.budget.familymanagement.contractbases;

import io.restassured.RestAssured;
import io.restassured.config.RedirectConfig;
import io.restassured.config.RestAssuredConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.konradboniecki.budget.familymanagement.Application;
import pl.konradboniecki.budget.familymanagement.model.Family;
import pl.konradboniecki.budget.familymanagement.service.FamilyRepository;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        classes = Application.class,
        webEnvironment = WebEnvironment.RANDOM_PORT
)
public class AccountManagementClientBase {

    @MockBean
    private FamilyRepository familyRepository;

    @LocalServerPort
    int port;

    @BeforeEach
    public void setUpMocks() {
        RestAssured.baseURI = "http://localhost:" + this.port;
        RestAssured.config = RestAssuredConfig.config().redirect(RedirectConfig.redirectConfig().followRedirects(false));
        mockAbsentFamily();
        mockFamily();
    }

    private void mockAbsentFamily() {
        String idOfMissingFamily = "ddad74b9-8fb3-4195-a999-07c01aaee371";
        when(familyRepository.findById(idOfMissingFamily))
                .thenReturn(Optional.empty());
    }

    private void mockFamily() {
        String idOfFoundFamily = "6e3c8e50-099a-4a44-9f63-0a6704937649";
        String ownerId = "06fc87d5-657f-4299-a93f-c3fa7a0ecd47";
        Family familyToReturn = new Family()
                .setId(idOfFoundFamily)
                .setOwnerId(ownerId)
                .setBudgetId(UUID.randomUUID().toString())
                .setTitle("testTitle");
        when(familyRepository.findById(idOfFoundFamily))
                .thenReturn(Optional.of(familyToReturn));
    }
}
