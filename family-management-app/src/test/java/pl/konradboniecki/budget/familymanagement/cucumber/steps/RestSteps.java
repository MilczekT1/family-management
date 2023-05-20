package pl.konradboniecki.budget.familymanagement.cucumber.steps;

import io.cucumber.java.en.Then;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import pl.konradboniecki.budget.familymanagement.cucumber.commons.SharedData;
import pl.konradboniecki.budget.openapi.dto.model.OASInvitationPage;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@RequiredArgsConstructor
public class RestSteps {

    private final SharedData sharedData;

    @Then("(family|invitation) is created$")
    public void familyIsCreated(String nvm) {
        responseStatusCodeEquals(HttpStatus.CREATED);
    }

    @Then("(family|invitation) is found$")
    public void familyIsFound(String nvm) {
        responseStatusCodeEquals(HttpStatus.OK);
    }

    @Then("{int} invitations are found")
    public void invitationsAreFound(int numberOfInvitations) {
        responseStatusCodeEquals(HttpStatus.OK);
        OASInvitationPage invitationPage = (OASInvitationPage) sharedData.getLastResponseEntity().getBody();
        Assertions.assertThat(invitationPage.getItems()).hasSize(numberOfInvitations);
        Assertions.assertThat(invitationPage.getMeta().getTotalElements()).isEqualTo(numberOfInvitations);

    }

    @Then("(family|invitation) is deleted$")
    public void familyIsDeleted(String nvm) {
        responseStatusCodeEquals(HttpStatus.NO_CONTENT);
    }

    @Then("(family|invitation) is not found$")
    public void familyIsNotFound(String nvm) {
        responseStatusCodeEquals(HttpStatus.NOT_FOUND);
    }

    @Then("family is updated")
    public void familyIsUpdated() {
        responseStatusCodeEquals(HttpStatus.OK);
    }

    private void responseStatusCodeEquals(HttpStatus httpStatus) {
        HttpStatusCode lastResponseHttpStatus = sharedData.getLastResponseEntity().getStatusCode();
        assertThat(lastResponseHttpStatus).isEqualTo(httpStatus);
    }
}
