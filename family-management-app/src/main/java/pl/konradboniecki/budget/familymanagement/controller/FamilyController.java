package pl.konradboniecki.budget.familymanagement.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.konradboniecki.budget.familymanagement.service.FamilyService;
import pl.konradboniecki.budget.openapi.api.FamilyManagementApi;
import pl.konradboniecki.budget.openapi.dto.model.OASCreatedFamily;
import pl.konradboniecki.budget.openapi.dto.model.OASFamily;
import pl.konradboniecki.budget.openapi.dto.model.OASFamilyCreation;
import pl.konradboniecki.budget.openapi.dto.model.OASFamilyModification;

import static pl.konradboniecki.budget.familymanagement.controller.FamilyController.BASE_PATH;

@AllArgsConstructor
@RestController
@RequestMapping(BASE_PATH)
public class FamilyController implements FamilyManagementApi {
    public static final String BASE_PATH = "/api/family-mgt/v1";

    private final FamilyService familyService;

    @Override
    public ResponseEntity<OASCreatedFamily> createFamily(OASFamilyCreation oaSFamilyCreation) throws Exception {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(familyService.saveFamilyOrThrow(oaSFamilyCreation));
    }

    @Override
    public ResponseEntity<OASFamily> modifyFamily(String familyId, OASFamilyModification oaSFamilyModification) throws Exception {
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(familyService.updateFamilyOrThrow(oaSFamilyModification, familyId));
    }

    @Override
    public ResponseEntity<Void> deleteFamily(String familyId) throws Exception {
        familyService.deleteFamilyByIdOrThrow(familyId);
        return ResponseEntity
                .noContent()
                .build();
    }

    @Override
    public ResponseEntity<OASFamily> findFamilyByOwner(String ownerId) throws Exception {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(familyService.findFamilyByOwnerId(ownerId));
    }

    @Override
    public ResponseEntity<OASFamily> findFamily(String familyId) throws Exception {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(familyService.findFamilyByIdOrThrow(familyId));
    }
}
