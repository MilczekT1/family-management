package pl.konradboniecki.budget.familymanagement.cucumber.commons;

import io.cucumber.java.DataTableType;
import pl.konradboniecki.budget.openapi.dto.model.OASFamily;

import java.util.Map;
import java.util.UUID;

public class FamilyDataTableConverter {

    private static final String ID_KEY = "id";
    private static final String OWNER_ID_KEY = "ownerId";
    private static final String TITLE_KEY = "title";
    private static final String BUDGET_ID_KEY = "budgetId";

    /*
    | ownerId | title | budgetId                             |
    | 1       | title | 2eb31cdb-2375-4f51-b109-4895c63abe52 |
     */
    @DataTableType
    public OASFamily familyRow(Map<String, String> entry) {
        String familyId = processId(entry);
        String budgetId = processBudgetId(entry);
        String ownerId = processOwnerId(entry);
        String title = processTitle(entry);
        return new OASFamily()
                .id(familyId)
                .ownerId(ownerId)
                .budgetId(budgetId)
                .title(title);
    }


    private String processBudgetId(Map<String, String> entry) {
        return entry.getOrDefault(BUDGET_ID_KEY, null);
    }

    private String processId(Map<String, String> entry) {
        if (entry.containsKey(ID_KEY)) {
            if (entry.get(ID_KEY).equals("{{random}}")) {
                return UUID.randomUUID().toString();
            } else {
                return entry.getOrDefault(ID_KEY, null);
            }
        } else {
            return null;
        }
    }

    private String processOwnerId(Map<String, String> entry) {
        if (entry.containsKey(OWNER_ID_KEY)) {
            String ownerIdEntry = entry.get(OWNER_ID_KEY);
            ownerIdEntry = removeBracketsIfReplacement(ownerIdEntry);
            return String.valueOf(SharedData.getUserIdForName(ownerIdEntry));
        } else {
            return null;
        }
    }

    private String processTitle(Map<String, String> entry) {
        return entry.getOrDefault(TITLE_KEY, null);
    }

    private String removeBracketsIfReplacement(String value) {
        if (value.startsWith("{{") && value.endsWith("}}")) {
            return value.substring(2, value.length() - 2);
        } else {
            return value;
        }
    }

}
