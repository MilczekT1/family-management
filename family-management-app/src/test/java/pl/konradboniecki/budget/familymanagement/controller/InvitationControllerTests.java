package pl.konradboniecki.budget.familymanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import pl.konradboniecki.budget.familymanagement.Application;
import pl.konradboniecki.budget.familymanagement.model.Invitation;
import pl.konradboniecki.budget.familymanagement.service.InvitationRepository;
import pl.konradboniecki.budget.familymanagement.service.InvitationService;
import pl.konradboniecki.chassis.tools.ChassisSecurityBasicAuthHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.TestInstance.Lifecycle;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(SpringExtension.class)
@SpringBootTest(
        classes = Application.class,
        webEnvironment = WebEnvironment.MOCK,
        properties = "spring.cloud.config.enabled=false"
)
@AutoConfigureMockMvc
class InvitationControllerTests {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private InvitationRepository invitationRepository;
    @Autowired
    private InvitationService invitationService;
    @Autowired
    private ChassisSecurityBasicAuthHelper chassisSecurityBasicAuthHelper;

    private String basicAuthHeaderValue;
    private Pageable defaultPageable = PageRequest.of(0, 100);

    @BeforeAll
    void setUp() {
        basicAuthHeaderValue = chassisSecurityBasicAuthHelper.getBasicAuthHeaderValue();
    }

    // GET /api/family-mgt/v1/invitations

    @Test
    void when_invitation_is_found_by_id_then_response_status_and_headers_are_correct() throws Exception {
        // Given:
        String idOfInvitation = UUID.randomUUID().toString();
        Invitation invitation = new Invitation()
                .setId(idOfInvitation);
        when(invitationRepository.findById(idOfInvitation))
                .thenReturn(Optional.of(invitation));
        // Then:
        mockMvc.perform(get("/api/family-mgt/v1/invitations/{id}", idOfInvitation)
                        .header("Authorization", basicAuthHeaderValue))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON.toString()));
    }

    // GET /api/family-mgt/v1/invitations

    @Test
    void when_invitations_are_found_by_email_then_response_status_and_headers_are_correct() throws Exception {
        // Given:
        String idOfInvitation1 = UUID.randomUUID().toString();
        String idOfInvitation2 = UUID.randomUUID().toString();
        Invitation invitation1 = new Invitation()
                .setEmail("test1@mail.com")
                .setId(idOfInvitation1);
        Invitation invitation2 = new Invitation()
                .setEmail("test1@mail.com")
                .setId(idOfInvitation2);
        List<Invitation> list = new ArrayList<>();
        list.add(invitation1);
        list.add(invitation2);
        Page<Invitation> pageWithInvitations = new PageImpl<>(list, defaultPageable, 2);
        when(invitationRepository.findAllByEmail("test1@mail.com", defaultPageable))
                .thenReturn(pageWithInvitations);
        // Then:
        mockMvc.perform(
                        get("/api/family-mgt/v1/invitations?email=test1@mail.com")
                        .header("Authorization", basicAuthHeaderValue))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON.toString()));
    }

    @Test
    void when_invitations_are_found_by_familyId_then_response_status_and_headers_are_correct() throws Exception {
        // Given:
        String idOfInvitation1 = UUID.randomUUID().toString();
        String idOfInvitation2 = UUID.randomUUID().toString();
        String familyId = UUID.randomUUID().toString();
        Invitation invitation1 = new Invitation()
                .setEmail("test1@mail.com")
                .setId(idOfInvitation1)
                .setFamilyId(familyId);
        Invitation invitation2 = new Invitation()
                .setEmail("test2@mail.com")
                .setId(idOfInvitation2)
                .setFamilyId(familyId);
        List<Invitation> list = new ArrayList<>();
        list.add(invitation1);
        list.add(invitation2);
        Page<Invitation> pageWithInvitations = new PageImpl<>(list, defaultPageable, 2);
        when(invitationRepository.findAllByFamilyId(familyId, defaultPageable))
                .thenReturn(pageWithInvitations);
        // Then:
        mockMvc.perform(
                get("/api/family-mgt/v1/invitations?familyId={familyId}", familyId)
                        .header("Authorization", basicAuthHeaderValue))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON.toString()));
    }

    @Test
    void when_invitations_are_found_by_email_and_familyId_then_response_status_and_headers_are_correct() throws Exception {
        // Given:
        String idOfFamily = UUID.randomUUID().toString();
        String email = "test@mail.com";
        Invitation invitation = new Invitation()
                .setEmail(email)
                .setFamilyId(idOfFamily);
        List<Invitation> list = new ArrayList<>();
        list.add(invitation);
        Page<Invitation> pageWithInvitations = new PageImpl<>(list, defaultPageable, 2);
        when(invitationRepository.findAllByEmailAndFamilyId(email, idOfFamily, defaultPageable))
                .thenReturn(pageWithInvitations);
        // Then:
        mockMvc.perform(
                        get("/api/family-mgt/v1/invitations?email={email}&familyId={familyId}", email, idOfFamily)
                        .header("Authorization", basicAuthHeaderValue))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON.toString()));
    }

    // POST /api/family-mgt/v1/invitations

    @Test
    void when_invitation_is_saved_then_response_status_and_headers_are_correct() throws Exception {
        // Given:
        String idOfInvitation = UUID.randomUUID().toString();
        String familyId = UUID.randomUUID().toString();
        Invitation invitationInRequestBody = new Invitation()
                .setFamilyId(familyId)
                .setEmail("test@mail.com")
                .setInvitationCode("e227de87-1eee-497b-93f7-1e503f4e9875")
                .setRegistered(false);
        Invitation invitationInResponseBody = new Invitation()
                .setFamilyId(familyId)
                .setEmail("test@mail.com")
                .setInvitationCode("e227de87-1eee-497b-93f7-1e503f4e9875")
                .setRegistered(false)
                .setId(idOfInvitation);
        when(invitationRepository.save(
                refEq(invitationInRequestBody, "created", "id")))
                .thenReturn(invitationInResponseBody);
        // Then:
        mockMvc.perform(post("/api/family-mgt/v1/invitations")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", basicAuthHeaderValue)
                .content(new ObjectMapper().writeValueAsString(invitationInResponseBody)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON.toString()));
    }

    // DELETE /api/family-mgt/v1/invitations/{invitationId}

    @Test
    void when_invitation_is_removed_then_response_status_and_headers_are_correct() throws Exception {
        // Given:
        String idOfMissingInvitation = UUID.randomUUID().toString();
        when(invitationRepository.existsById(idOfMissingInvitation)).thenReturn(true);
        doNothing().when(invitationRepository).deleteById(idOfMissingInvitation);
        // Then:
        mockMvc.perform(
                        delete("/api/family-mgt/v1/invitations/{id}", idOfMissingInvitation)
                                .header("Authorization", basicAuthHeaderValue)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
