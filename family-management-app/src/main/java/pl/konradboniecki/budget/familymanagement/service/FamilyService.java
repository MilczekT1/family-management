package pl.konradboniecki.budget.familymanagement.service;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.konradboniecki.budget.familymanagement.exceptions.FamilyNotFoundException;
import pl.konradboniecki.budget.familymanagement.model.Family;
import pl.konradboniecki.budget.openapi.dto.model.OASCreatedFamily;
import pl.konradboniecki.budget.openapi.dto.model.OASFamily;
import pl.konradboniecki.budget.openapi.dto.model.OASFamilyCreation;
import pl.konradboniecki.budget.openapi.dto.model.OASFamilyModification;
import pl.konradboniecki.chassis.exceptions.BadRequestException;

import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Service
public class FamilyService {

    private final FamilyRepository familyRepository;
    private final FamilyMapper familyMapper;

    public OASFamily findFamilyByIdOrThrow(String id) {
        Optional<Family> family = familyRepository.findById(id);
        if (family.isPresent()) {
            return familyMapper.toOASFamily(family.get());
        } else {
            throw new FamilyNotFoundException(String.format("Family with id: %s not found", id));
        }
    }

    public OASFamily findFamilyByOwnerId(String id) {
        Optional<Family> family = familyRepository.findByOwnerId(id);
        if (family.isPresent()) {
            return familyMapper.toOASFamily(family.get());
        } else {
            throw new FamilyNotFoundException("Family not found for owner with id: " + id);
        }
    }

    public void deleteFamilyByIdOrThrow(String id) {
        if (existsById(id)) {
            familyRepository.deleteById(id);
        } else {
            throw new FamilyNotFoundException(String.format("Family with id: %s not found", id));
        }
    }

    private boolean existsById(String id) {
        return familyRepository.existsById(id);
    }

    public OASFamily updateFamilyOrThrow(OASFamilyModification familyModification, String familyId) {
        familyIdInBodyAndPathAreConsistentOrThrow(familyModification, familyId);
        Optional<Family> family = familyRepository.findById(familyModification.getId());
        if (family.isPresent()) {
            Family origin = family.get();
            Family modifiedFamily = familyMapper.toFamily(familyModification);
            Family result = familyRepository.save(origin.mergeFamilies(modifiedFamily));
            return familyMapper.toOASFamily(result);
        } else {
            throw new FamilyNotFoundException("Family with id: " + familyId + " not found");
        }
    }

    public OASCreatedFamily saveFamilyOrThrow(OASFamilyCreation familyCreation) {
        Optional<Family> family = familyRepository.findByOwnerId(familyCreation.getOwnerId());
        if (family.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        } else {
            Family familyToSave = familyMapper.toFamily(familyCreation);
            familyToSave.setId(UUID.randomUUID().toString());
            Family savedFamily = familyRepository.save(familyToSave);
            return familyMapper.toOASCreatedFamily(savedFamily);
        }
    }

    private void familyIdInBodyAndPathAreConsistentOrThrow(OASFamilyModification payload, String familyId) {
        String familyIdFromBody = payload.getId();
        if (!familyIdFromBody.equals(familyId)) {
            throw new BadRequestException("Family id in body and path don't match.");
        }
    }
}
