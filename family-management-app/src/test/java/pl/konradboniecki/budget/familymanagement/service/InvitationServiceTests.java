package pl.konradboniecki.budget.familymanagement.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.konradboniecki.budget.familymanagement.Application;
import pl.konradboniecki.budget.familymanagement.model.Invitation;
import pl.konradboniecki.budget.openapi.dto.model.OASInvitationPage;
import pl.konradboniecki.chassis.exceptions.BadRequestException;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.TestInstance.Lifecycle;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ExtendWith(SpringExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest(
        classes = Application.class,
        webEnvironment = RANDOM_PORT,
        properties = "spring.cloud.config.enabled=false"
)
class InvitationServiceTests {

    @Autowired
    private TestRestTemplate rest;
    @MockBean
    private InvitationRepository invitationRepository;
    @Autowired
    private InvitationService invitationService;

    @LocalServerPort
    private int port;
    private String baseUrl;
    private Pageable defaultPageable = PageRequest.of(0, 100);

    @BeforeAll
    void beforeAll() {
        baseUrl = "http://localhost:" + port;
        assertThat(rest.getForEntity(baseUrl + "/actuator/health", String.class)
                .getStatusCodeValue()).isEqualTo(200);
    }

    //TODO: rework those tests

    @Test
    void given_valid_param_when_findAllBy_email_then_return_invitations() {
        // Given:
        String email = "test@mail.com";
        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        Invitation invitation1 = new Invitation()
                .setId(UUID.randomUUID().toString())
                .setEmail(email);
        Invitation invitation2 = new Invitation()
                .setId(UUID.randomUUID().toString())
                .setEmail(email);
        List<Invitation> list = new ArrayList<>();
        list.add(invitation1);
        list.add(invitation2);
        Page<Invitation> pageWithInvitations = new PageImpl<>(list, defaultPageable, 2);
        // When:
        when(invitationRepository.findAllByEmail(email, defaultPageable))
                .thenReturn(pageWithInvitations);
        OASInvitationPage retrievedList = invitationService.findAllBy(params, defaultPageable);
        // Then:
        assertThat(retrievedList).isNotNull();
        assertThat(retrievedList.getItems()).hasSize(2);
        assertThat(retrievedList.getMeta()).isNotNull();
        assertThat(retrievedList.getMeta().getElements()).isEqualTo(2);
        assertThat(retrievedList.getMeta().getTotalElements()).isEqualTo(2);
        assertThat(retrievedList.getMeta().getPageSize()).isEqualTo(100);
        assertThat(retrievedList.getMeta().getPage()).isZero();
        assertThat(retrievedList.getMeta().getTotalPages()).isEqualTo(1);
    }

    @Test
    void given_valid_param_when_findAllBy_familyId_then_return_invitations() {
        // Given:
        String familyId = UUID.randomUUID().toString();
        Map<String, String> params = new HashMap<>();
        params.put("familyId", familyId);
        Invitation invitation1 = new Invitation()
                .setId(UUID.randomUUID().toString())
                .setFamilyId(familyId)
                .setEmail("test1@mail.com");
        Invitation invitation2 = new Invitation()
                .setId(UUID.randomUUID().toString())
                .setFamilyId(familyId)
                .setEmail("test2@mail.com");
        List<Invitation> list = new ArrayList<>();
        list.add(invitation1);
        list.add(invitation2);
        Page<Invitation> pageWithInvitations = new PageImpl<>(list, defaultPageable, 2);
        // When:
        when(invitationRepository.findAllByFamilyId(familyId, defaultPageable))
                .thenReturn(pageWithInvitations);
        OASInvitationPage retrievedList = invitationService.findAllBy(params, defaultPageable);
        // Then:
        assertThat(retrievedList).isNotNull();
        assertThat(retrievedList.getItems()).hasSize(2);
        assertThat(retrievedList.getMeta()).isNotNull();
        assertThat(retrievedList.getMeta().getElements()).isEqualTo(2);
        assertThat(retrievedList.getMeta().getTotalElements()).isEqualTo(2);
        assertThat(retrievedList.getMeta().getPageSize()).isEqualTo(100);
        assertThat(retrievedList.getMeta().getPage()).isZero();
        assertThat(retrievedList.getMeta().getTotalPages()).isEqualTo(1);
    }

    @Test
    void given_invalid_param_when_findAllBy_familyId_then_throw() {
        // Given:
        Map<String, String> params = new HashMap<>();
        params.put("invalid_param", UUID.randomUUID().toString());
        // When:
        Throwable throwable = catchThrowableOfType(
                () -> invitationService.findAllBy(params, defaultPageable), BadRequestException.class);
        // Then:
        assertThat(throwable).isNotNull()
                .isInstanceOf(BadRequestException.class);
    }
}
