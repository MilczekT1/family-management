package pl.konradboniecki.budget.familymanagement.contractbases;

import io.restassured.RestAssured;
import io.restassured.config.RedirectConfig;
import io.restassured.config.RestAssuredConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.konradboniecki.budget.familymanagement.Application;
import pl.konradboniecki.budget.familymanagement.model.Family;
import pl.konradboniecki.budget.familymanagement.model.Invitation;
import pl.konradboniecki.budget.familymanagement.service.FamilyRepository;
import pl.konradboniecki.budget.familymanagement.service.InvitationRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        classes = Application.class,
        webEnvironment = WebEnvironment.RANDOM_PORT
)
public class MvcClientBase {

    @LocalServerPort
    int port;

    @MockBean
    private FamilyRepository familyRepository;
    @MockBean
    private InvitationRepository invitationRepository;

    private final Pageable defaultPageable = PageRequest.of(0, 100);

    @BeforeEach
    public void setUpMocks() {
        RestAssured.baseURI = "http://localhost:" + this.port;
        RestAssured.config = RestAssuredConfig.config().redirect(RedirectConfig.redirectConfig().followRedirects(false));

        mock_family_create();
        mock_family_delete();
        mock_family_update();
        mock_family_find();

        mock_invitation_create();
        mock_invitation_delete();
        mock_invitation_find();
        mock_invitation_find_all();
    }

    private void mock_family_delete() {
        String presentFamilyId = "df511b31-0316-476e-8eff-f031692ac670";
        String missingFamilyId = "a31fed2a-8da7-46ea-8256-3b5e746d6fe5";
        doNothing().when(familyRepository).deleteById(presentFamilyId);
        when(familyRepository.existsById(presentFamilyId)).thenReturn(true);
        when(familyRepository.existsById(missingFamilyId)).thenReturn(false);
    }

    private void mock_family_create() {
        String ownerId = "2015e088-f035-47be-b5cd-ae74d22c728d";
        String missingOwnerId = "37369461-0c5f-4240-8ff1-be20fda9c5df";
        when(familyRepository.findByOwnerId(ownerId)).thenReturn(Optional.of(new Family()));
        when(familyRepository.findByOwnerId(missingOwnerId)).thenReturn(Optional.empty());
        String idOfBudget = "09031a3c-2109-46d9-b966-1a51d97ede2e";
        Family family = new Family()
                .setOwnerId(missingOwnerId)
                .setBudgetId(idOfBudget)
                .setTitle("testTitle");
        when(familyRepository.save(refEq(family, "id")))
                .thenReturn(family.setId(UUID.randomUUID().toString()));
    }

    private void mock_family_find() {
        String foundOwnerId = "82e84da2-83db-47d9-b8f1-df44f2971acb";
        String idOfMissingOwner = "336dd389-2a7b-4360-8c13-607c8d126c4f";
        String idOfMissingFamily = "a31fed2a-8da7-46ea-8256-3b5e746d6fe5";
        String idOfFoundFamily = "40320278-4656-4772-bd3a-68fd98ca5921";
        Family familyByOwnerId = new Family()
                .setId(UUID.randomUUID().toString())
                .setOwnerId(foundOwnerId)
                .setBudgetId(UUID.randomUUID().toString())
                .setTitle("testTitle");
        when(familyRepository.findByOwnerId(idOfMissingOwner)).thenReturn(Optional.empty());
        when(familyRepository.findByOwnerId(foundOwnerId)).thenReturn(Optional.of(familyByOwnerId));
        when(familyRepository.findById(idOfMissingFamily)).thenReturn(Optional.empty());
        Family familyById = new Family()
                .setId(idOfFoundFamily)
                .setOwnerId(foundOwnerId)
                .setBudgetId(UUID.randomUUID().toString())
                .setTitle("testTitle");
        when(familyRepository.findById(idOfFoundFamily)).thenReturn(Optional.of(familyById));
    }

    private void mock_family_update() {
        String idOfMissingFamily = "eb44bc19-1b29-444e-bcd2-d9ef9a449bf0";
        String idOfModifiedFamily = "1dac6f28-9a85-407c-8e07-8371f9c2e5d9";
        String budgetIdOfModifiedFamily = "f771e0c9-aea6-4ca3-b580-e81e15ad22a4";
        when(familyRepository.findById(idOfMissingFamily)).thenReturn(Optional.empty());
        Family modifiedFamily = new Family()
                .setId(idOfModifiedFamily)
                .setBudgetId(budgetIdOfModifiedFamily)
                .setOwnerId("6c55e0e1-c847-4ef2-be26-08ad11d7927a")
                .setTitle("testTitle");
        when(familyRepository.findById(idOfModifiedFamily)).thenReturn(Optional.of(modifiedFamily));
        when(familyRepository.save(refEq(modifiedFamily, "id"))).thenReturn(modifiedFamily);
    }

    private void mock_invitation_create() {
        String idOfCreatedInvitation = "a27e1edd-0a55-4531-9926-e74b95926174";
        Invitation invitation = new Invitation()
                .setId(idOfCreatedInvitation)
                .setFamilyId("f75df896-c050-4b55-9950-5ce262925572")
                .setEmail("test@mail2.com")
                .setInvitationCode("ebdd12b3-aedc-4b32-9518-cc71263c0775")
                .setRegistered(false)
                .setCreated(Instant.now());
        when(invitationRepository.save(any(Invitation.class))).thenReturn(invitation);
    }

    private void mock_invitation_delete() {
        String idOfDeletedInvitation = "a27e1edd-0a55-4531-9926-e74b95926174";
        String ifOfMissingInvitation = "0631f6c6-eb39-41dd-9895-da1a9258d3e4";
        when(invitationRepository.existsById(idOfDeletedInvitation)).thenReturn(true);
        doNothing().when(invitationRepository).deleteById(idOfDeletedInvitation);
        when(invitationRepository.existsById(ifOfMissingInvitation)).thenReturn(false);
    }

    private void mock_invitation_find_all() {
        String idOfFamilyWithInvitations = "3ee03003-b049-47fe-9269-e64eaba640e7";
        String idOfFamilyWithoutInvitations = "58701904-4407-4b26-ae77-745c61a49384";
        Invitation invitationByFamilyId1 = new Invitation()
                .setId("ff14b483-f28a-4b6a-a3ba-0d77de4b1682")
                .setFamilyId(idOfFamilyWithInvitations)
                .setEmail("mail_1@mail.com")
                .setInvitationCode("34b7a194-b0d3-47f7-8aef-1d64caefcdf4")
                .setCreated(Instant.parse("2019-06-16T10:22:54.246625Z"))
                .setRegistered(true);
        Invitation invitationByFamilyId2 = new Invitation()
                .setId("1474dffc-360a-4c33-a72d-c58fe72be1f2")
                .setFamilyId(idOfFamilyWithInvitations)
                .setEmail("mail_2@mail.com")
                .setInvitationCode("c04a8005-cb67-46de-a4dc-e4f84d26faf3")
                .setCreated(Instant.parse("2019-06-16T10:22:54.246625Z"))
                .setRegistered(false);
        ArrayList<Invitation> invitations = new ArrayList<>();
        invitations.add(invitationByFamilyId1);
        invitations.add(invitationByFamilyId2);
        Page<Invitation> pageWithInvitations = new PageImpl<>(invitations, defaultPageable, 2);
        Page<Invitation> emptyPage = new PageImpl<>(emptyList(), defaultPageable, 0);
        when(invitationRepository.findAllByFamilyId(eq(idOfFamilyWithInvitations), refEq(defaultPageable)))
                .thenReturn(pageWithInvitations);
        when(invitationRepository.findAllByFamilyId(eq(idOfFamilyWithoutInvitations), refEq(defaultPageable)))
                .thenReturn(emptyPage);

        String emailWithoutInvitations = "email@without-invitations.com";
        String emailWithInvitations = "email@with-invitations.com";
        Invitation invitationByEmail1 = new Invitation()
                .setId("90727450-8e56-4380-90c3-56fc56f4035d")
                .setFamilyId("2db9b8ca-2cc1-4129-b402-cfe75ca08547")
                .setEmail(emailWithInvitations)
                .setInvitationCode("34b7a194-b0d3-47f7-8aef-1d64caefcdf4")
                .setCreated(Instant.parse("2019-06-16T10:22:54.246625Z"))
                .setRegistered(true);
        Invitation invitationByEmail2 = new Invitation()
                .setId("7c28c191-821b-4977-996d-010787a203ee")
                .setFamilyId(null)
                .setEmail(emailWithInvitations)
                .setInvitationCode("c04a8005-cb67-46de-a4dc-e4f84d26faf3")
                .setCreated(Instant.parse("2019-06-16T10:22:54.246625Z"))
                .setRegistered(true);
        ArrayList<Invitation> invitationsByEmail = new ArrayList<>();
        invitationsByEmail.add(invitationByEmail1);
        invitationsByEmail.add(invitationByEmail2);
        Page<Invitation> pageWithInvitationsByEmail = new PageImpl<>(invitationsByEmail, defaultPageable, 2);
        when(invitationRepository.findAllByEmail(eq(emailWithInvitations), refEq(defaultPageable))).thenReturn(pageWithInvitationsByEmail);
        when(invitationRepository.findAllByEmail(eq(emailWithoutInvitations), refEq(defaultPageable))).thenReturn(emptyPage);
    }

    private void mock_invitation_find() {
        String idOfFoundFamily = "091a6799-bce9-444d-982d-8724d4d31588";
        String idOfMissingFamily = "89f066a0-23a4-4e2a-aff5-7d7f920afa48";
        String emailWithInvitations = "email@with-invitations.com";
        Invitation invitationByEmailAndFamilyId = new Invitation()
                .setId("90727450-8e56-4380-90c3-56fc56f4035d")
                .setFamilyId(idOfFoundFamily)
                .setEmail(emailWithInvitations)
                .setRegistered(true)
                .setCreated(Instant.parse("2019-06-16T10:22:54.246625Z"))
                .setInvitationCode("34b7a194-b0d3-47f7-8aef-1d64caefcdf4");
        Page<Invitation> emptyPage = new PageImpl<>(emptyList(), defaultPageable, 0);
        Page<Invitation> pageWithinvitations = new PageImpl<>(singletonList(invitationByEmailAndFamilyId), defaultPageable, 2);

        when(invitationRepository.findAllByEmailAndFamilyId(emailWithInvitations, idOfFoundFamily, defaultPageable))
                .thenReturn(pageWithinvitations);
        when(invitationRepository.findAllByEmailAndFamilyId(
                "email@without-invitations.com",
                idOfMissingFamily, defaultPageable))
                .thenReturn(emptyPage);

        String idOfMissingInvitation = "801bcae9-348a-4cd3-9793-7e6234461d5f";
        String idOfFoundInvitation = "0728df5b-7f7f-42f9-8eae-251e86d8360a";
        Invitation invitationById = new Invitation()
                .setId(idOfFoundInvitation)
                .setFamilyId(UUID.randomUUID().toString())
                .setInvitationCode(UUID.randomUUID().toString())
                .setCreated(Instant.now())
                .setRegistered(true)
                .setEmail("random@email.com");
        when(invitationRepository.findById(idOfFoundInvitation)).thenReturn(Optional.of(invitationById));
        when(invitationRepository.findById(idOfMissingInvitation)).thenReturn(Optional.empty());
    }
}
