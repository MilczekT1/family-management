package pl.konradboniecki.budget.familymanagement.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
public class FamilyTests {

    @Test
    public void givenNullProperties_whenMergeFamilies_thenDontCopyNulls() {
        // Given:
        String familyId = UUID.randomUUID().toString();
        String budgetId = UUID.randomUUID().toString();
        String ownerId = UUID.randomUUID().toString();
        Family family1 = new Family()
                .setId(familyId)
                .setOwnerId(ownerId)
                .setBudgetId(budgetId)
                .setTitle("testTitle");
        Family familyWithNulls = new Family()
                .setOwnerId(null);

        // When:
        family1.mergeFamilies(familyWithNulls);

        //Then:
        assertAll(
                () -> assertEquals(familyId, family1.getId()),
                () -> assertEquals(ownerId, family1.getOwnerId()),
                () -> assertEquals(budgetId, family1.getBudgetId()),
                () -> assertEquals("testTitle", family1.getTitle())
        );
    }

    @Test
    public void givenValidProperties_whenMergeFamilies_thenOverrideProperties() {
        // Given:
        String familyId1 = UUID.randomUUID().toString();
        String familyId2 = UUID.randomUUID().toString();
        String budgetId1 = UUID.randomUUID().toString();
        String budgetId2 = UUID.randomUUID().toString();
        String ownerId1 = UUID.randomUUID().toString();
        String ownerId2 = UUID.randomUUID().toString();
        Family family1 = new Family()
                .setId(familyId1)
                .setOwnerId(ownerId1)
                .setBudgetId(budgetId1)
                .setTitle("testTitle");
        Family familyWithNulls = new Family()
                .setId(familyId2)
                .setOwnerId(ownerId2)
                .setBudgetId(budgetId2)
                .setTitle("testTitle123");

        // When:
        family1.mergeFamilies(familyWithNulls);

        //Then:
        assertAll(
                () -> assertEquals(familyId2, family1.getId()),
                () -> assertEquals(ownerId2, family1.getOwnerId()),
                () -> assertEquals(budgetId2, family1.getBudgetId()),
                () -> assertEquals("testTitle123", family1.getTitle())
        );
    }
}
