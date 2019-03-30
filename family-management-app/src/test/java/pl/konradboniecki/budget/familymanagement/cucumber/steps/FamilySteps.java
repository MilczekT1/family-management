package pl.konradboniecki.budget.familymanagement.cucumber.steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.konradboniecki.budget.familymanagement.controller.FamilyController;
import pl.konradboniecki.budget.familymanagement.controller.InvitationController;
import pl.konradboniecki.budget.familymanagement.cucumber.commons.SharedData;
import pl.konradboniecki.budget.familymanagement.cucumber.security.Security;
import pl.konradboniecki.budget.familymanagement.model.Family;
import pl.konradboniecki.budget.openapi.dto.model.OASFamily;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
@RequiredArgsConstructor
public class FamilySteps {

    private final Security security;
    private final TestRestTemplate testRestTemplate;
    private final SharedData sharedData;

    @After
    public void scenarioCleanup() {
        security.basicAuthentication();

        sharedData.getFamilyIdsToDelete().stream()
                .filter(Objects::nonNull)
                .forEach(this::cleanFamily);
        sharedData.getInvitationsIdsToDelete().stream()
                .filter(Objects::nonNull)
                .forEach(this::cleanInvitation);
        sharedData.clearAfterScenario();
    }

    private void cleanFamily(String familyId) {
        log.info("SCENARIO CLEANUP: Deleting family with id: {}", familyId);
        HttpEntity<?> entity = new HttpEntity<>(null, security.getSecurityHeaders());
        ResponseEntity<Void> responseEntity =
                testRestTemplate.exchange(FamilyController.BASE_PATH + "/families/{familyId}",
                        HttpMethod.DELETE, entity, Void.class, familyId);
        log.info("SCENARIO CLEANUP: result {}", responseEntity.getStatusCodeValue());
        assertThat(responseEntity.getStatusCode())
                .isIn(HttpStatus.NO_CONTENT, HttpStatus.NOT_FOUND);
    }

    private void cleanInvitation(String invitationId) {
        log.info("SCENARIO CLEANUP: Deleting invitation with id: {}", invitationId);
        HttpEntity<?> entity = new HttpEntity<>(null, security.getSecurityHeaders());
        ResponseEntity<Void> responseEntity =
                testRestTemplate.exchange(InvitationController.BASE_PATH + "/invitations/{invitationId}",
                        HttpMethod.DELETE, entity, Void.class, invitationId);
        log.info("SCENARIO CLEANUP: result {}", responseEntity.getStatusCodeValue());
        assertThat(responseEntity.getStatusCode())
                .isIn(HttpStatus.NO_CONTENT, HttpStatus.NOT_FOUND);
    }

    @And("user (.+) doesn't own a family$")
    public void userDoesnTHaveAFamily(String userName) {
        String ownerId = SharedData.getUserIdForName(userName);
        HttpEntity<?> entity = new HttpEntity<>(null, security.getSecurityHeaders());
        ResponseEntity<?> responseEntity = testRestTemplate
                .exchange(FamilyController.BASE_PATH + "/families/owners/" + ownerId, HttpMethod.GET, entity, OASFamily.class);
        sharedData.setLastResponseEntity(responseEntity);
        responseStatusCodeEquals(HttpStatus.NOT_FOUND);
    }

    @When("I create a family with properties:")
    public void iCreateAFamilyWithProperties(DataTable dataTable) {
        List<OASFamily> families = dataTable.asList(OASFamily.class);
        OASFamily familyToSave = families.get(0);
        HttpEntity<?> entity = new HttpEntity<>(familyToSave, security.getSecurityHeaders());
        ResponseEntity<Family> responseEntity = testRestTemplate
                .exchange(FamilyController.BASE_PATH + "/families", HttpMethod.POST, entity, Family.class);
        sharedData.setLastResponseEntity(responseEntity);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            Family savedFamily = responseEntity.getBody();
            assertThat(savedFamily).isNotNull();
            assertThat(savedFamily.getId()).isNotNull();
            sharedData.addFamilyIdToDelete(savedFamily.getId());
        }
    }

    @And("user (.+) already owns a family$")
    public void userAlreadyHasAFamily(String userName) {
        String userId = SharedData.getUserIdForName(userName);
        OASFamily familyToSave = prepareDefaultFamilyForOwner(userId);

        HttpEntity<?> entity = new HttpEntity<>(familyToSave, security.getSecurityHeaders());
        ResponseEntity<OASFamily> responseEntity = testRestTemplate
                .exchange(FamilyController.BASE_PATH + "/families", HttpMethod.POST, entity, OASFamily.class);
        sharedData.setLastResponseEntity(responseEntity);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        OASFamily savedFamily = responseEntity.getBody();
        assertThat(savedFamily).isNotNull();
        assertThat(savedFamily.getId()).isNotNull();
        sharedData.addUserNameToFamilyIdEntry(userName, savedFamily.getId());
        sharedData.addFamilyIdToDelete(savedFamily.getId());
    }

    private OASFamily prepareDefaultFamilyForOwner(String ownerId) {
        return new OASFamily()
                .ownerId(ownerId)
                .title("default title");
    }

    @Then("the operation is a failure")
    public void theOperationIsAFailure() {
        assertThat(sharedData.getLastResponseEntity().getStatusCode().is4xxClientError()).isTrue();
    }

    @When("I delete a (.+)'s family$")
    public void iDeleteAUserSFamily(String userName) {
        String familyId = sharedData.getFamilyIdForUserName(userName);
        deleteFamily(familyId);
    }

    @When("I delete not existing family")
    public void iDeleteNotExistingFamily() {
        String randomId = UUID.randomUUID().toString();
        deleteFamily(randomId);
    }

    @When("I get (.+)'s family$")
    public void iGetUserSFamily(String userName) {
        String familyId = sharedData.getFamilyIdForUserName(userName);
        getFamilyById(familyId);
    }

    @When("I get random family")
    public void iGetRandomFamily() {
        String familyId = UUID.randomUUID().toString();
        getFamilyById(familyId);
    }

    @When("I get (.+)'s family by owner$")
    public void iGetUserSFamilyByOwner(String userName) {
        String ownerId = SharedData.getUserIdForName(userName);
        getFamilyByOwnerId(ownerId);
    }

    @When("I get random family by owner")
    public void iGetRandomFamilyByOwner() {
        long ownerId = ThreadLocalRandom.current().nextLong(5000, 15000);
        getFamilyByOwnerId(Long.toString(ownerId));
    }

    @When("I update (.+)'s family with properties:$")
    public void iUpdateFamilyOwnerSFamilyWithProperties(String userName, DataTable dataTable) {
        List<OASFamily> families = dataTable.asList(OASFamily.class);
        String familyId = sharedData.getFamilyIdForUserName(userName);
        if (familyId == null) {
            familyId = UUID.randomUUID().toString();
        }
        OASFamily updatePayload = families.get(0);
        if (updatePayload.getId() == null) {
            updatePayload.setId(familyId);
        }
        HttpEntity<OASFamily> entity = new HttpEntity<>(updatePayload, security.getSecurityHeaders());
        ResponseEntity<OASFamily> responseEntity = testRestTemplate
                .exchange(FamilyController.BASE_PATH + "/families/{familyId}", HttpMethod.PUT, entity, OASFamily.class, familyId);
        sharedData.setLastResponseEntity(responseEntity);
    }

    @And("response contains family with following properties:")
    public void responseContainsFamilyWithFollowingProperties(DataTable dataTable) {
        OASFamily expectedFamily = (OASFamily) dataTable.asList(OASFamily.class).get(0);
        OASFamily foundFamily = (OASFamily) sharedData.getLastResponseEntity().getBody();
        assertThat(expectedFamily).isNotNull();
        assertThat(foundFamily).isNotNull();

        assertAll(
                () -> assertThat(foundFamily.getBudgetId()).isEqualTo(expectedFamily.getBudgetId()),
                () -> assertThat(foundFamily.getOwnerId()).isEqualTo(expectedFamily.getOwnerId()),
                () -> assertThat(foundFamily.getTitle()).isEqualTo(expectedFamily.getTitle()));
    }

    private void deleteFamily(String familyId) {
        HttpEntity<?> entity = new HttpEntity<>(null, security.getSecurityHeaders());
        ResponseEntity<OASFamily> responseEntity = testRestTemplate
                .exchange(FamilyController.BASE_PATH + "/families/{familyId}", HttpMethod.DELETE, entity, OASFamily.class, familyId);
        sharedData.setLastResponseEntity(responseEntity);
    }

    private void getFamilyById(String familyId) {
        HttpEntity<OASFamily> entity = new HttpEntity<>(null, security.getSecurityHeaders());
        ResponseEntity<OASFamily> responseEntity = testRestTemplate
                .exchange(FamilyController.BASE_PATH + "/families/{familyId}", HttpMethod.GET, entity, OASFamily.class, familyId);
        sharedData.setLastResponseEntity(responseEntity);
    }

    private void getFamilyByOwnerId(String ownerId) {
        HttpEntity<OASFamily> entity = new HttpEntity<>(null, security.getSecurityHeaders());
        ResponseEntity<OASFamily> responseEntity = testRestTemplate
                .exchange(FamilyController.BASE_PATH + "/families/owners/{ownerId}", HttpMethod.GET, entity, OASFamily.class, ownerId);
        sharedData.setLastResponseEntity(responseEntity);
    }

    private void responseStatusCodeEquals(HttpStatus httpStatus) {
        HttpStatus lastResponseHttpStatus = sharedData.getLastResponseEntity().getStatusCode();
        assertThat(lastResponseHttpStatus).isEqualTo(httpStatus);
    }
}
