package pl.konradboniecki.budget.familymanagement.cucumber.steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import pl.konradboniecki.budget.familymanagement.cucumber.commons.SharedData;
import pl.konradboniecki.budget.familymanagement.cucumber.security.Security;
import pl.konradboniecki.budget.openapi.dto.model.OASInvitation;
import pl.konradboniecki.budget.openapi.dto.model.OASInvitationPage;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@RequiredArgsConstructor
public class InvitationSteps {

    private final Security security;
    private final TestRestTemplate testRestTemplate;
    private final SharedData sharedData;

    public final String BASE_PATH = "/api/family-mgt/v1";

    @When("I create invitation with following properties:$")
    public void iCreateInvitationWithFollowingProperties(DataTable dataTable) {
        List<OASInvitation> invitations = dataTable.asList(OASInvitation.class);
        invitations.stream()
                .filter(Objects::nonNull)
                .forEach(this::createInvitation);
    }

    @And("following invitations have been created:")
    public void followingInvitationsHaveBeenCreated(DataTable dataTable) {
        List<OASInvitation> invitations = dataTable.asList(OASInvitation.class);
        invitations.stream()
                .filter(Objects::nonNull)
                .forEach(this::createInvitation);
    }

    @When("I delete invitation with invitation code (.+)$")
    public void iDeleteInvitationWithInvitationCodeInvitCodeId(String invitationCodeRepl) {
        String invitationCode = SharedData.getUuidForReplacement(invitationCodeRepl);
        String invitationId = sharedData.getInvitationIdForInvitationCode(invitationCode);
        deleteInvitation(invitationId);
    }

    @When("I delete not existing invitation")
    public void iDeleteNotExistingInvitation() {
        deleteInvitation(UUID.randomUUID().toString());
    }

    private void createInvitation(OASInvitation invitationToSave) {
        HttpEntity<?> entity = new HttpEntity<>(invitationToSave, security.getSecurityHeaders());
        ResponseEntity<OASInvitation> responseEntity = testRestTemplate
                .exchange(BASE_PATH + "/invitations", HttpMethod.POST, entity, OASInvitation.class);
        sharedData.setLastResponseEntity(responseEntity);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            OASInvitation saved = responseEntity.getBody();
            assertThat(saved).isNotNull();
            assertThat(saved.getId()).isNotNull();
            String id = saved.getId();
            sharedData.addInvitationIdToDelete(id);
            sharedData.addInvitationCodeToInvitationIdEntry(saved.getInvitationCode(), id);
        }
    }

    private void deleteInvitation(String invitationId) {
        HttpEntity<?> entity = new HttpEntity<>(null, security.getSecurityHeaders());
        ResponseEntity<OASInvitation> responseEntity = testRestTemplate
                .exchange(BASE_PATH + "/invitations/{invitationId}", HttpMethod.DELETE, entity, OASInvitation.class, invitationId);
        sharedData.setLastResponseEntity(responseEntity);
    }

    @When("I get invitation for family with invitation code (.+) by id$")
    public void iGetInvitationForFamilyWithInvitationCodeInvitCodeIdById(String invitationCodeRepl) {
        String invitationCode = SharedData.getUuidForReplacement(invitationCodeRepl);
        String invitationId = ObjectUtils.firstNonNull(sharedData.getInvitationIdForInvitationCode(invitationCode), UUID.randomUUID().toString());
        getInvitationById(invitationId);
    }

    @When("I get random invitation")
    public void iGetRandomInvitation() {
        String invitationId = UUID.randomUUID().toString();
        getInvitationById(invitationId);
    }

    private void getInvitationById(String invitationId) {
        HttpEntity<?> entity = new HttpEntity<>(null, security.getSecurityHeaders());
        ResponseEntity<OASInvitation> responseEntity = testRestTemplate
                .exchange(BASE_PATH + "/invitations/{invitationId}", HttpMethod.GET, entity, OASInvitation.class, invitationId);
        sharedData.setLastResponseEntity(responseEntity);
    }

    private void getInvitationsByEmail(String email) {
        HttpEntity<?> entity = new HttpEntity<>(null, security.getSecurityHeaders());
        ResponseEntity<OASInvitationPage> responseEntity = testRestTemplate
                .exchange(BASE_PATH + "/invitations?email={email}", HttpMethod.GET, entity,
                        OASInvitationPage.class, email);
        sharedData.setLastResponseEntity(responseEntity);
    }

    private void getInvitationsByEmailAndFamilyId(String email, String familyId) {
        HttpEntity<?> entity = new HttpEntity<>(null, security.getSecurityHeaders());
        ResponseEntity<OASInvitationPage> responseEntity = testRestTemplate
                .exchange(BASE_PATH + "/invitations?email={email}&familyId={familyId}", HttpMethod.GET, entity, OASInvitationPage.class, email, familyId);
        sharedData.setLastResponseEntity(responseEntity);
    }

    private void getInvitationsByFamilyId(String familyId) {
        HttpEntity<?> entity = new HttpEntity<>(null, security.getSecurityHeaders());
        ResponseEntity<OASInvitationPage> responseEntity = testRestTemplate
                .exchange(BASE_PATH + "/invitations?familyId={familyId}", HttpMethod.GET, entity,
                        OASInvitationPage.class, familyId);
        sharedData.setLastResponseEntity(responseEntity);
    }

    @And("invitation contains following properties:")
    public void invitationContainsFollowingProperties(DataTable dataTable) {
        List<OASInvitation> invitationWithProperties = dataTable.asList(OASInvitation.class);
        OASInvitation expectedInvitation = invitationWithProperties.get(0);
        OASInvitation receivedInvitation = (OASInvitation) sharedData.getLastResponseEntity().getBody();

        assertThat(expectedInvitation)
                .usingRecursiveComparison()
                .ignoringActualNullFields()
                .isEqualTo(receivedInvitation);

    }

    @When("I get invitations to family for user with email (.+)$")
    public void iGetInvitationsToFamilyForUserWithEmailTestMailCom(String email) {
        getInvitationsByEmail(email);
    }

    @And("invitations contains following properties:")
    public void invitationsContainsFollowingProperties(DataTable dataTable) {
        OASInvitation invitationWithProperties = (OASInvitation) dataTable.asList(OASInvitation.class).get(0);
        OASInvitationPage invitationPage = (OASInvitationPage) sharedData.getLastResponseEntity().getBody();
        List<OASInvitation> invitations = invitationPage.getItems();

        invitations.forEach(
                (inv) -> assertThat(invitationWithProperties)
                        .usingRecursiveComparison()
                        .ignoringActualNullFields()
                        .isEqualTo(inv)
        );
    }

    @When("I get invitations to family with id (.+)$")
    public void iGetInvitationsToFamilyWithIdFamilyId(String familyIdReplacement) {
        String familyId = SharedData.getUuidForReplacement(familyIdReplacement);
        getInvitationsByFamilyId(familyId);
    }

    @When("I get invitations by family id (.+) and email (.+)$")
    public void iGetInvitationsByFamilyIdFamilyIdAndEmailTestMailCom(String familyIdReplacement, String email) {
        String familyId = SharedData.getUuidForReplacement(familyIdReplacement);
        getInvitationsByEmailAndFamilyId(email, familyId);
    }
}
