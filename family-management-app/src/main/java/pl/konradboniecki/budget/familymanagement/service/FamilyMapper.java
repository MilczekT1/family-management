package pl.konradboniecki.budget.familymanagement.service;

import lombok.NonNull;
import org.springframework.stereotype.Service;
import pl.konradboniecki.budget.familymanagement.model.Family;
import pl.konradboniecki.budget.openapi.dto.model.OASCreatedFamily;
import pl.konradboniecki.budget.openapi.dto.model.OASFamily;
import pl.konradboniecki.budget.openapi.dto.model.OASFamilyCreation;
import pl.konradboniecki.budget.openapi.dto.model.OASFamilyModification;

@Service
public class FamilyMapper {

    Family toFamily(@NonNull OASFamilyCreation familyCreation) {
        return new Family()
                .setBudgetId(familyCreation.getBudgetId())
                .setOwnerId(familyCreation.getOwnerId())
                .setTitle(familyCreation.getTitle());
    }

    public Family toFamily(@NonNull OASFamilyModification oasFamilyModification) {
        return new Family()
                .setId(oasFamilyModification.getId())
                .setOwnerId(oasFamilyModification.getOwnerId())
                .setBudgetId(oasFamilyModification.getBudgetId())
                .setTitle(oasFamilyModification.getTitle());
    }

    public OASFamily toOASFamily(@NonNull Family family) {
        return new OASFamily()
                .id(family.getId())
                .budgetId(family.getBudgetId())
                .ownerId(family.getOwnerId())
                .title(family.getTitle());
    }

    OASCreatedFamily toOASCreatedFamily(@NonNull Family family) {
        return new OASCreatedFamily()
                .id(family.getId())
                .ownerId(family.getOwnerId())
                .budgetId(family.getBudgetId())
                .title(family.getTitle());
    }
}
