package pl.konradboniecki.budget.familymanagement.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.konradboniecki.budget.familymanagement.Application;
import pl.konradboniecki.budget.familymanagement.service.FamilyRepository;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

//TODO: replace with mockMVC tests for performance
@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(SpringExtension.class)
@SpringBootTest(
        classes = Application.class,
        webEnvironment = RANDOM_PORT
)
class FamilyControllerTests {

    @Autowired
    private TestRestTemplate rest;
    @MockBean
    private FamilyRepository familyRepository;

    @LocalServerPort
    private int port;
    private String baseUrl;

    @BeforeAll
    void beforeAll() {
        baseUrl = "http://localhost:" + port;
        assertThat(rest.getForEntity(baseUrl + "/actuator/health", String.class).getStatusCodeValue()).isEqualTo(200);
        rest = rest.withBasicAuth("testUserName", "testUserPassword");
    }

    @Test
    void givenMissingFamily_whenFindByOwner_thenReturnNotFound() {
        // Given:
        String ownerId = UUID.randomUUID().toString();
        when(familyRepository.findByOwnerId(ownerId))
                .thenReturn(Optional.empty());
        // When:
        ResponseEntity<String> responseEntity = rest.getForEntity(baseUrl + "/api/family-mgt/v1/families/owners/{ownerId}", String.class, ownerId);
        // Then:
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
