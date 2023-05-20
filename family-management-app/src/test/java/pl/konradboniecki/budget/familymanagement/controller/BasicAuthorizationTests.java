package pl.konradboniecki.budget.familymanagement.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.konradboniecki.budget.familymanagement.Application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(SpringExtension.class)
@SpringBootTest(
        classes = Application.class,
        webEnvironment = RANDOM_PORT
)
class BasicAuthorizationTests {
    @Autowired
    private TestRestTemplate rest;

    @LocalServerPort
    private int port;
    private String baseUrl;

    @BeforeAll
    void beforeAll() {
        baseUrl = "http://localhost:" + port;
    }

    @Test
    void return401FromFamilyControllerWhenBAHeaderIsMissing() {
        // Given:
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity httpEntity = new HttpEntity(httpHeaders);
        // When:
        ResponseEntity<String> responseEntity1 = rest.exchange(baseUrl + "/api/family-mgt/v1/families", HttpMethod.POST, httpEntity, String.class);
        ResponseEntity<String> responseEntity2 = rest.exchange(baseUrl + "/api/family-mgt/v1/families", HttpMethod.PUT, httpEntity, String.class);
        ResponseEntity<String> responseEntity3 = rest.exchange(baseUrl + "/api/family-mgt/v1/families/10", HttpMethod.DELETE, httpEntity, String.class);
        ResponseEntity<String> responseEntity4 = rest.exchange(baseUrl + "/api/family-mgt/v1/families/10", HttpMethod.GET, httpEntity, String.class);
        ResponseEntity<String> responseEntity5 = rest.exchange(baseUrl + "/api/family-mgt/v1/families/10/slots", HttpMethod.GET, httpEntity, String.class);
        // Then:
        Assertions.assertAll(
                () -> assertThat(responseEntity1.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED),
                () -> assertThat(responseEntity2.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED),
                () -> assertThat(responseEntity3.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED),
                () -> assertThat(responseEntity4.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED),
                () -> assertThat(responseEntity5.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED)
        );
    }

    @Test
    void return401FromInvitationControllerWhenBAHeaderIsMissing() {
        // Given:
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity httpEntity = new HttpEntity(httpHeaders);
        // When:
        ResponseEntity<String> responseEntity1 = rest.exchange(baseUrl + "/api/family-mgt/v1/family-invitations", HttpMethod.POST, httpEntity, String.class);
        ResponseEntity<String> responseEntity2 = rest.exchange(baseUrl + "/api/family-mgt/v1/family-invitations/10", HttpMethod.DELETE, httpEntity, String.class);
        ResponseEntity<String> responseEntity3 = rest.exchange(baseUrl + "/api/family-mgt/v1/family-invitations/find-one", HttpMethod.GET, httpEntity, String.class);
        ResponseEntity<String> responseEntity4 = rest.exchange(baseUrl + "/api/family-mgt/v1/family-invitations/find-all", HttpMethod.GET, httpEntity, String.class);
        // Then:
        Assertions.assertAll(
                () -> assertThat(responseEntity1.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED),
                () -> assertThat(responseEntity2.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED),
                () -> assertThat(responseEntity3.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED),
                () -> assertThat(responseEntity4.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED)
        );
    }
}
