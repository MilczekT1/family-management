package pl.konradboniecki.budget.familymanagement.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.konradboniecki.budget.familymanagement.service.InvitationService;
import pl.konradboniecki.budget.openapi.api.FamilyInvitationsApi;
import pl.konradboniecki.budget.openapi.dto.model.OASCreatedInvitation;
import pl.konradboniecki.budget.openapi.dto.model.OASInvitation;
import pl.konradboniecki.budget.openapi.dto.model.OASInvitationCreation;
import pl.konradboniecki.budget.openapi.dto.model.OASInvitationPage;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@AllArgsConstructor
@RestController
public class InvitationController implements FamilyInvitationsApi {

    private final InvitationService invitationService;

    @Override
    public ResponseEntity<OASCreatedInvitation> createInvitation(OASInvitationCreation oaSInvitationCreation) throws Exception {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(invitationService.saveInvitationOrThrow(oaSInvitationCreation));
    }

    @Override
    public ResponseEntity<Void> deleteInvitation(String invitationId) throws Exception {
        invitationService.removeInvitationByIdOrThrow(invitationId);
        return ResponseEntity
                .noContent()
                .build();
    }

    @Override
    public ResponseEntity<OASInvitation> findInvitation(String invitationId) throws Exception {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(invitationService.findById(invitationId));
    }

    @Override
    public ResponseEntity<OASInvitationPage> findInvitations(String familyId, String email, Integer page, Integer limit) throws Exception {
        Map<String, String> params = new HashMap<>();
        if (email != null) params.put("email", email);
        if (familyId != null) params.put("familyId", familyId);
        Pageable pageable = PageRequest.of(page, limit);
        OASInvitationPage invitationPage = invitationService.findAllBy(params, pageable);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(invitationPage);
    }
}
